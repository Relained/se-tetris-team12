package org.example.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.example.model.GameBoard;

import javafx.application.Platform;

/**
 * 게임 중 실시간 데이터를 송수신하는 네트워크 매니저
 */

public class InGameNetworkManager {

    public static final byte SIGNAL_ADDER_BOARD = 0x01;
    public static final byte SIGNAL_GO_WAITING_ROOM = 0x02;
    public static final byte SIGNAL_GAME_OVER = 0x03;
    public static final byte SIGNAL_ENDING = 0x04;

    private final Socket tcpSocket;
    private DatagramChannel udpChannel;
    private Selector selector;

    private Thread boardSyncSendThread;
    private Thread boardSyncReceiveThread;
    private Thread gameDataSendThread;
    private Thread gameDataReceiveThread;
    private Runnable onDisconnect;
    private Runnable onGoWaitingRoom;
    private BiConsumer<Integer, Boolean> onGameOver;
    private Consumer<int[][]> onAdderBoardReceived;
    private Consumer<int[][]> onBoardDataReceived;
    private Supplier<int[][]> boardDataProvider;
    private IntSupplier scoreProvider;
    private LongConsumer displayDelay;
    private final BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean released = new AtomicBoolean(false);

    // ----------- 상수 -----------
    private static final int TICK_TIME = 40;
    private static final int CONNECTION_LOST_TIME = 5000;
    private static final int MAX_PACKET_SIZE = 1024; // 1KB

    public InGameNetworkManager(
        Socket socket,
        Runnable onDisconnect,
        Runnable onGoWaitingRoom,
        BiConsumer<Integer, Boolean> onGameOver,
        Consumer<int[][]> onAdderBoardReceived,
        Consumer<int[][]> onBoardDataReceived, 
        Supplier<int[][]> boardDataProvider,
        IntSupplier scoreProvider,
        LongConsumer displayDelay
    )
    {
        this.tcpSocket = socket;
        this.onDisconnect = onDisconnect;
        this.onGoWaitingRoom = onGoWaitingRoom;
        this.onGameOver = onGameOver;
        this.onAdderBoardReceived = onAdderBoardReceived;
        this.onBoardDataReceived = onBoardDataReceived;
        this.boardDataProvider = boardDataProvider;
        this.scoreProvider = scoreProvider;
        this.displayDelay = displayDelay;
        try {
            // NIO DatagramChannel 설정
            udpChannel = DatagramChannel.open();
            udpChannel.configureBlocking(false);
            udpChannel.bind(tcpSocket.getLocalSocketAddress());
            udpChannel.connect(tcpSocket.getRemoteSocketAddress()); // 상대방 주소 고정
            
            // Selector 설정 (수신 대기용)
            selector = Selector.open();
            udpChannel.register(selector, SelectionKey.OP_READ);
            
            Thread.startVirtualThread(this::startNetworking);
        } catch (IOException e) {
            System.err.println("[Error while creating UDP channel]");
            System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            releaseResources(true);
            udpChannel = null;
        }
    }
    
    private void startNetworking() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("(InGame)[Networking start interrupted - graceful shutdown]");
            return;
        }
        boardSyncSendThread = new Thread(this::boardSyncSendLoop);
        boardSyncSendThread.setDaemon(true);
        boardSyncSendThread.start();
        boardSyncReceiveThread = new Thread(this::boardSyncReceiveLoop);
        boardSyncReceiveThread.setDaemon(true);
        boardSyncReceiveThread.start();
        gameDataSendThread = Thread.startVirtualThread(this::gameDataSendLoop);
        gameDataReceiveThread = Thread.startVirtualThread(this::gameDataReceiveLoop);
        System.err.println("-----------------In Game Network manager initialized------------------");
    }

    public void sendAdderBoard(int[][] adderBoard) {
        int height = adderBoard.length;
        int width = GameBoard.WIDTH;
        int dataLen = height * width * 4;
        ByteBuffer buffer = ByteBuffer.allocate(1 + dataLen);
        buffer.put(SIGNAL_ADDER_BOARD);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                buffer.putInt(adderBoard[i][j]);
            }
        }
        sendQueue.offer(buffer.array());
    }

    public void sendGoWaitingRoomAndShutDown() {
        byte[] msg = new byte[1];
        msg[0] = SIGNAL_GO_WAITING_ROOM;
        sendQueue.offer(msg);
        stopUDPthread();
    }

    public void sendGameOverAndShutDown(int score, boolean timeover) {
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.put(SIGNAL_GAME_OVER);
        buffer.putInt(score);
        buffer.put((byte)(timeover ? 1 : 0));
        sendQueue.offer(buffer.array());
        stopUDPthread();
    }

    private void sendEndingMsgAndShutDown(int score) {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put(SIGNAL_ENDING);
        buffer.putInt(score);
        sendQueue.offer(buffer.array());
        stopUDPthread();
    }

    // 보드 동기화를 제외한 기타 게임 데이터 송신 루프
    private void gameDataSendLoop() {
        DataOutputStream outputStream;
        try {
            outputStream = new DataOutputStream(tcpSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("[Failed to initialize output stream]");
            releaseResources(true);
            return;
        }
        try {
            while (true) {
                byte[] message = sendQueue.take(); // blocking - 메시지 올 때까지 대기
                outputStream.writeInt(message.length);
                outputStream.write(message);
                outputStream.flush();
                if (SIGNAL_GO_WAITING_ROOM <= message[0] && message[0] <= SIGNAL_ENDING) {
                    System.err.println("(InGame)[Send thread stopped after sending ending message]");
                    return ;
                }
            }
        } catch (InterruptedException e) {
            System.err.println("(InGame)[Send thread interrupted - graceful shutdown]");
        } catch (IOException e) {
            System.err.println("[Send failed - connection lost]");
            releaseResources(true);
        }
    }

    // 보드 동기화를 제외한 기타 게임 데이터 수신 루프
    private void gameDataReceiveLoop() {
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(tcpSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("[Failed to initialize input stream]");
            releaseResources(true);
            return;
        }
        try {
            while (true) {
                int length = inputStream.readInt(); // blocking - 데이터 올 때까지 대기
                byte type = inputStream.readByte();
                byte[] data = inputStream.readNBytes(length - 1);

                if (type == SIGNAL_ADDER_BOARD) { //Adder Board
                    int height = data.length / (4 * GameBoard.WIDTH);
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    int[][] adderBoard = new int[height][GameBoard.WIDTH];
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < GameBoard.WIDTH; j++) {
                            adderBoard[i][j] = buffer.getInt();
                        }
                    }
                    onAdderBoardReceived.accept(adderBoard);
                }
                else if (type == SIGNAL_GO_WAITING_ROOM) { // Go Waiting Room
                    sendEndingMsgAndShutDown(-1); // WaitingRoom 표시
                    Platform.runLater(onGoWaitingRoom); 
                    System.err.println("(InGame)[shutdown receive thread by remote go waiting room]");
                    return ;
                }
                else if (type == SIGNAL_GAME_OVER) { // Game Over
                    ByteBuffer scoreBuffer = ByteBuffer.wrap(data);
                    int opponentScore = scoreBuffer.getInt();
                    boolean timeover = scoreBuffer.get() == 1;
                    sendEndingMsgAndShutDown(scoreProvider.getAsInt());
                    Platform.runLater(() -> onGameOver.accept(opponentScore, timeover));
                    System.err.println("(InGame)[shutdown receive thread by remote game over]");
                    return ;
                }
                else if (type == SIGNAL_ENDING) { // stop receive data
                    ByteBuffer scoreBuffer = ByteBuffer.wrap(data);
                    int opponentScore = scoreBuffer.getInt();
                    if (opponentScore != -1) {
                        Platform.runLater(() -> onGameOver.accept(opponentScore, false));
                    }
                    System.err.println("(InGame)[shutdown receive thread by remote request]");
                    return ;
                }
            }
        }
        catch (IOException e) {
            if (Thread.currentThread().isInterrupted()) {
                System.err.println("(InGame)[Receive thread interrupted - graceful shutdown]");
            } else {
                System.err.println("[Receive failed - connection lost]");
                releaseResources(true);
            }
        }
    }

    //UDP 보드 동기화 송신 루프 (NIO)
    private void boardSyncSendLoop() {
        int[][] data;
        final ByteBuffer sendDataBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);

        while (true) {
            data = boardDataProvider.get();
            sendDataBuffer.clear();
            sendDataBuffer.putLong(System.currentTimeMillis());
            for (int i = 0; i < GameBoard.HEIGHT; i++) {
                for (int j = 0; j < GameBoard.WIDTH; j++) {
                    sendDataBuffer.putInt(data[i][j]);
                }
            }
            sendDataBuffer.flip();
            try {
                udpChannel.write(sendDataBuffer);
            } catch (java.net.PortUnreachableException e) {
                // 상대방 UDP 닫힘 or NAT 문제 - 무시하고 계속 (타임아웃으로 최종 판단)
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.err.println("(BoardSync)[Send thread interrupted - graceful shutdown]");
                    return;
                }
                System.err.println("[Error while sending board data]");
                releaseResources(true);
                return;
            }

            try {
                Thread.sleep(TICK_TIME);
            } catch (InterruptedException e) {
                System.err.println("(BoardSync)[Send thread interrupted - graceful shutdown]");
                return;
            }
        }
    }
    
    // UDP 보드 동기화 수신 루프 (NIO)
    private void boardSyncReceiveLoop() {
        final int expectedLength = GameBoard.HEIGHT * GameBoard.WIDTH * 4 + 8;
        final int[][] decodeBuffer = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        final ByteBuffer receiveByteBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
        
        long delaySum = 0;
        int delayCount = 0;
        long lastDelayDisplayTime = System.currentTimeMillis();
        long lastPacketTransmitTime = System.currentTimeMillis() - TICK_TIME;

        while (true) {
            long loopStart = System.currentTimeMillis();

            try {
                int readyChannels = selector.select(TICK_TIME);
                
                if (readyChannels > 0) {
                    selector.selectedKeys().clear();
                    
                    while (true) {
                        receiveByteBuffer.clear();
                        if (udpChannel.read(receiveByteBuffer) <= 0)
                            break;
                        receiveByteBuffer.flip();
                        
                        if (receiveByteBuffer.remaining() < expectedLength) {
                            continue;
                        }
                        
                        long receivedTimestamp = receiveByteBuffer.getLong();
                        if (receivedTimestamp <= lastPacketTransmitTime) {
                            continue;
                        }
                        lastPacketTransmitTime = receivedTimestamp;
                        
                        for (int i = 0; i < GameBoard.HEIGHT; i++) {
                            for (int j = 0; j < GameBoard.WIDTH; j++) {
                                decodeBuffer[i][j] = receiveByteBuffer.getInt();
                            }
                        }
                        onBoardDataReceived.accept(decodeBuffer);
                    }
                }
            } catch (java.net.PortUnreachableException e) {
                // 상대방 UDP 닫힘 or NAT 문제 - 무시하고 계속 (타임아웃으로 최종 판단)
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.err.println("(BoardSync)[Receive thread interrupted - graceful shutdown]");
                    return;
                }
                System.err.println("[Error while reading data]");
                System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
                releaseResources(true);
                return;
            }

            long currentTime = System.currentTimeMillis();
            // 딜레이 계산:
            // 현재 시간 - 마지막으로 패킷이 전송될 때의 시간
            // 만약 패킷이 통째로 밀린다면 밀린 시간만큼 차이가 생기고,
            // 패킷이 유실되면 마지막으로 제대로 받은 패킷의 전송 시점의 시간이 들어가므로 
            // 유실에 대한 딜레이도 구할 수 있음.
            long delay = currentTime - lastPacketTransmitTime;
            delaySum += delay;
            delayCount++;
            
            if (currentTime - lastDelayDisplayTime >= 1000) {
                long avgDelay = delayCount > 0 ? delaySum / delayCount : 0;
                Platform.runLater(() -> displayDelay.accept(avgDelay));
                delaySum = 0;
                delayCount = 0;
                lastDelayDisplayTime = currentTime;
            }
            
            if (delay > CONNECTION_LOST_TIME) {
                System.err.println("[Connection lost detected (>5000ms)]");
                releaseResources(true);
                return;
            }

            long sleep = TICK_TIME - (System.currentTimeMillis() - loopStart);
            if (sleep <= 0)
                continue;

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.err.println("(BoardSync)[Receive thread interrupted - graceful shutdown]");
                break;
            }
        }
    }

    private void releaseResources(boolean remoteDisconnected) {
        if (!released.compareAndSet(false, true)) {
            return; // 이미 해제됨
        }
        if (remoteDisconnected) {
            Platform.runLater(onDisconnect);
        }
        if (boardSyncSendThread != null)
            boardSyncSendThread.interrupt();
        if (boardSyncReceiveThread != null)
            boardSyncReceiveThread.interrupt();
        if (gameDataSendThread != null)
            gameDataSendThread.interrupt();
        if (gameDataReceiveThread != null)
            gameDataReceiveThread.interrupt();
        try {
            tcpSocket.close();
            if (selector != null) selector.close();
            if (udpChannel != null) udpChannel.close();
        } catch (IOException e) {
            System.err.println("[Error while closing socket]");
        }
    }

    private void stopUDPthread() {
        if (boardSyncSendThread != null)
            boardSyncSendThread.interrupt();
        if (boardSyncReceiveThread != null)
            boardSyncReceiveThread.interrupt();
        try {
            if (selector != null) selector.close();
            if (udpChannel != null) udpChannel.close();
        } catch (Exception e) {
            System.err.println("[Error while closing UDP channel]");
        }
    }

    public void disconnect() {
        releaseResources(false);
    }

    public Socket getSocket() {
        return tcpSocket;
    }
}
