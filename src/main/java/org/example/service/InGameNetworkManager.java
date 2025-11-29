package org.example.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.example.model.GameBoard;

import javafx.application.Platform;

/**
 * 게임 중 실시간 데이터를 송수신하는 네트워크 매니저
 */

public class InGameNetworkManager {

    private final Socket tcpSocket;
    private DatagramSocket udpSocket;

    private Thread boardSyncSendThread;
    private Thread boardSyncReceiveThread;
    private Thread gameDataSendThread;
    private Thread gameDataReceiveThread;
    private Runnable onDisconnect;
    private Consumer<int[][]> onDataReceived;
    private Supplier<int[][]> dataProvider;
    private final boolean isServer;

    // ----------- 수신 관련 -----------
    

    // ----------- 송신 관련 -----------
    private final BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<>();

    // ----------- 상수 -----------
    private static final int TICK_TIME = 40;
    private static final int CONNECTION_LOST_TIME = 3000;
    private static final int CONNECTION_DELAY_TIME = 500;
    private static final int MAX_PACKET_SIZE = 1024; // 1KB

    public InGameNetworkManager(
        Socket socket,
        boolean isServer,
        Runnable onDisconnect, 
        Consumer<int[][]> onDataReceived, 
        Supplier<int[][]> dataProvider
    )
    {
        this.tcpSocket = socket;
        this.isServer = isServer;
        this.onDisconnect = onDisconnect;
        this.onDataReceived = onDataReceived;
        this.dataProvider = dataProvider;

        try {
            udpSocket = new DatagramSocket(tcpSocket.getLocalSocketAddress());
            udpSocket.setSoTimeout(TICK_TIME);
            startNetworking();
        } catch (IOException e) {
            System.err.println("[Error while creating UDP socket]");
            System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            udpSocket = null;
        }
    }

    private void startNetworking() {
        boardSyncSendThread = new Thread(this::boardSyncSendLoop);
        boardSyncSendThread.setDaemon(true);
        boardSyncSendThread.start();
        boardSyncReceiveThread = new Thread(this::boardSyncReceiveLoop);
        boardSyncReceiveThread.setDaemon(true);
        boardSyncReceiveThread.start();
        gameDataSendThread = Thread.startVirtualThread(this::gameDataSendLoop);
        gameDataReceiveThread = Thread.startVirtualThread(this::gameDataReceiveLoop);
    }

    public void sendAdderBoard(int[][] adderBoard) {
        int height = adderBoard.length;
        int width = GameBoard.WIDTH;
        int dataLen = height * width * 4;
        ByteBuffer buffer = ByteBuffer.allocate(1 + dataLen);
        buffer.put((byte) 0x01); // type
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                buffer.putInt(adderBoard[i][j]);
            }
        }
        sendQueue.offer(buffer.array());
    }

    public void sendGoWaitingRoom() {
        byte[] msg = new byte[1];
        msg[0] = (byte) 0x02; // type
        sendQueue.offer(msg);
    }

    public void sendGameOver() {
        byte[] msg = new byte[1];
        msg[0] = (byte) 0x03; // type
        sendQueue.offer(msg);
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

                if (type == 0x01) { 
                   
                } 
                else if (type == 0x02) { 

                }
                else if (type == 0x03) {

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

    //UDP 보드 동기화 송신 루프
    private void boardSyncSendLoop() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("(BoardSync)[Send thread interrupted - graceful shutdown]");
            return;
        }

        int[][] data;
        int sendTick = 0;
        final ByteBuffer sendDataBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
        final DatagramPacket packet = new DatagramPacket(sendDataBuffer.array(), 0, tcpSocket.getRemoteSocketAddress());

        while (true) {
            data = dataProvider.get();
            sendDataBuffer.clear();
            sendDataBuffer.putInt(sendTick++);
            for (int i = 0; i < GameBoard.HEIGHT; i++) {
                for (int j = 0; j < GameBoard.WIDTH; j++) {
                    sendDataBuffer.putInt(data[i][j]);
                }
            }
            packet.setData(sendDataBuffer.array(), 0, sendDataBuffer.position());
            try {
                udpSocket.send(packet);
            } catch (IOException e) {
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

    // UDP 보드 동기화 수신 루프
    private void boardSyncReceiveLoop() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("(BoardSync)[Receive thread interrupted - graceful shutdown]");
            return;
        }

        int lastReceivedTick = -1;
        int expectedLength = GameBoard.HEIGHT * GameBoard.WIDTH * 4 + 4;
        long lastPacketTime = System.currentTimeMillis();
        final int[][] decodeBuffer = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        final byte[] receiveRawBuffer = new byte[MAX_PACKET_SIZE];
        final ByteBuffer receiveByteBuffer = ByteBuffer.wrap(receiveRawBuffer);
        final DatagramPacket packet = new DatagramPacket(receiveRawBuffer, receiveRawBuffer.length);

        while (true) {
            long loopStart = System.currentTimeMillis();
            if (loopStart - lastPacketTime > CONNECTION_DELAY_TIME 
                && CONNECTION_DELAY_TIME + TICK_TIME > loopStart - lastPacketTime) {
                System.err.println("[Connection delay detected (>500ms)]");
            }
            else if (loopStart - lastPacketTime > CONNECTION_LOST_TIME) {
                System.err.println("[Connection lost detected (>3000ms)]");
                releaseResources(true);
                return;
            }

            try {
                udpSocket.receive(packet);
                if (packet.getLength() < expectedLength) {
                    continue;
                }
                receiveByteBuffer.limit(packet.getLength());
                receiveByteBuffer.position(0);
                int receivedTick = receiveByteBuffer.getInt();
                if (receivedTick <= lastReceivedTick) {
                    continue;
                }
                lastReceivedTick = receivedTick;
                lastPacketTime = System.currentTimeMillis();
                for (int i = 0; i < GameBoard.HEIGHT; i++) {
                    for (int j = 0; j < GameBoard.WIDTH; j++) {
                        decodeBuffer[i][j] = receiveByteBuffer.getInt();
                    }
                }
                onDataReceived.accept(decodeBuffer);

            } catch (SocketTimeoutException e) {
                // 타임아웃이면 다음 tick으로
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
            udpSocket.close();
        } catch (IOException e) {
            System.err.println("[Error while closing socket]");
        }
    }

    public void disconnect() {
        releaseResources(false);
    }

    public Socket getSocket() {
        return tcpSocket;
    }
}
