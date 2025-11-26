package org.example.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import org.example.model.GameBoard;

import javafx.application.Platform;
import javafx.util.Pair;

/**
 * 게임 중 실시간 데이터를 송수신하는 네트워크 매니저
 */

public class InGameNetworkManager {

    private final Socket socket;

    private Thread receiveThread;
    private Thread sendThread;
    private Runnable onDisconnect;
    private Consumer<int[][]> onDataReceived;
    private Supplier<int[][]> dataProvider;

    // ----------- 수신 관련 -----------

    private final byte[] receiveDataBuffer = new byte[MAX_PACKET_SIZE];

    // readStep 진행 상태 저장용 멤버 변수
    private int stepLength = -1;
    private int stepTick = -1;
    private int stepTotalRead = 0;

    // ----------- 송신 관련 -----------

    private final ByteBuffer sendDataBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
    private final BlockingQueue<int[][]> sendQueue = new LinkedBlockingQueue<>();

    // ----------- 상수 -----------
    private static final int TICK_TIME = 40;
    private static final int CONNECTION_LOST_TIME = 200;
    private static final int MAX_PACKET_SIZE = 1024; // 1KB

    public InGameNetworkManager(Socket socket, Runnable onDisconnect, Consumer<int[][]> onDataReceived, Supplier<int[][]> dataProvider) {
        this.socket = socket;
        this.onDisconnect = onDisconnect;
        this.onDataReceived = onDataReceived;
        this.dataProvider = dataProvider;
    }

    public void startReceiving() {
        receiveThread = new Thread(this::receiveLoop);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    public void startSending() {
        sendThread = new Thread(this::sendLoop);
        sendThread.setDaemon(true);
        sendThread.start();
    }

    public void sendBoardData(int[][] board) {
        sendQueue.add(board);
    }

    private void sendLoop() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {}

        int sendTick = 0;
        int[][] data;
        OutputStream outputStream;

        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println("[Error while initializing output stream]");
            System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            releaseResources(false);
            return;
        }

        while (true) {
            data = dataProvider.get();
            sendDataBuffer.clear();
            sendDataBuffer.putInt(GameBoard.HEIGHT * GameBoard.WIDTH * 4);
            sendDataBuffer.putInt(sendTick);
            for (int i = 0; i < GameBoard.HEIGHT; i++) {
                for (int j = 0; j < GameBoard.WIDTH; j++) {
                    sendDataBuffer.putInt(data[i][j]);
                }
            }
            sendTick++;
            try {
                outputStream.write(sendDataBuffer.array(), 0, sendDataBuffer.position());
            } catch (IOException e) {
                System.err.println("[Error while sending data]");
                releaseResources(true);
                return;
            }

            try {
                Thread.sleep(TICK_TIME);
            } catch (InterruptedException e) {
                System.err.println("[Send thread interrupted - graceful shutdown]");
                return;
            }
        }
        // while (!sendQueue.isEmpty()) {
        //     data = sendQueue.poll();
        
            // sendDataBuffer.clear();
            // sendDataBuffer.putInt(GameBoard.HEIGHT * GameBoard.WIDTH * 4);
            // sendDataBuffer.putInt(sendTick);
            // for (int i = 0; i < GameBoard.HEIGHT; i++) {
            //     for (int j = 0; j < GameBoard.WIDTH; j++) {
            //         sendDataBuffer.putInt(data[i][j]);
            //     }
            // }
            // sendTick++;
            // try {
            //     socket.getOutputStream().write(sendDataBuffer.array(), 0, sendDataBuffer.position());
            // } catch (IOException e) {
            //     System.err.println("[Error while sending data]");
            //     releaseResources(false);
            // }
        // }
    }

    // 데이터 수신 루프
    private void receiveLoop() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {}

        int expectedTick = 0;
        long lastPacketTime = System.currentTimeMillis();
        DataInputStream inputStream;

        try {
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("[Error while initializing input stream]");
            System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            releaseResources(true);
            return;
        }

        while (true) {
            if (System.currentTimeMillis() - lastPacketTime > CONNECTION_LOST_TIME) {
                System.err.println("[excessive delay (over 200ms)]");
                releaseResources(true);
                return;
            }

            long loopStart = System.currentTimeMillis();
            Pair<Integer, Integer> payload;
            try {
                payload = readStep(inputStream);
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.err.println("[Receive thread interrupted - graceful shutdown]");
                    return;
                }
                System.err.println("[Error while reading data]");
                System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
                releaseResources(true);
                return;
            }

            if (payload != null) {
                lastPacketTime = System.currentTimeMillis();

                // 상대가 보낸 틱이 예상 틱보다 작으면:
                // 송신 지연이 생겼다는 의미이고, 나중에 한 번에 들어와서 버퍼에 쌓일 수 있음
                // 또는 수신 인터벌이 송신 인터벌보다 짧다는 의미인데, 이 경우에는 어쩔 수 없음. (문제가 생기진 않음)

                // 상대가 보낸 틱이 예상 틱보다 크면:
                // 송신 인터벌이 수신 인터벌보다 짧다는 의미이고,
                // expected tick은 맞는데 inputStream이 계속 쌓이고 지연이 늘어나게 됨.
                if (payload.getValue() != expectedTick) {
                    System.err.println("[Tick mismatch: expected " + expectedTick + ", got " + payload.getValue() + "]");
                    try {
                        while (inputStream.available() != 0) {
                            payload = readStep(inputStream);
                            if (payload == null) break;
                        }
                    } catch (IOException e) {
                        if (Thread.currentThread().isInterrupted()) {
                            System.err.println("[Receive thread interrupted - graceful shutdown]");
                            return;
                        }
                        System.err.println("[Connection lost while consuming]");
                        releaseResources(true);
                        return;
                    }
                }
                if (payload != null) {
                    if (payload.getValue() > expectedTick) {
                        //expectedTick을 업데이트해줌
                        expectedTick = payload.getValue();
                    }
                    var decodeData = decode(payload.getKey());
                    onDataReceived.accept(decodeData);
                }
            }
            expectedTick++;

            long sleep = TICK_TIME - (System.currentTimeMillis() - loopStart);
            if (sleep <= 0)
                continue;

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.err.println("[Receive thread interrupted - graceful shutdown]");
                break;
            }
        }
    }

    // 한 덩어리의 패킷을 읽고 <길이, tick> 반환함
    // 데이터는 dataBuffer에 저장됨
    // 이 함수의 실행 시간은 절대 1tick을 넘기지 않음. 넘기게 되면 즉시 null 반환
    private Pair<Integer, Integer> readStep(DataInputStream in) throws IOException {
        long start = System.currentTimeMillis();
        try {
            // 1. length 읽기
            if (stepLength == -1) {
                int remain = (int) (TICK_TIME - (System.currentTimeMillis() - start));
                socket.setSoTimeout(remain);
                stepLength = in.readInt();
            }
            // 2. tick 읽기
            if (stepTick == -1) {
                int remain = (int) (TICK_TIME - (System.currentTimeMillis() - start));
                if (remain <= 0)
                    return null;
                socket.setSoTimeout(remain);
                stepTick = in.readInt();
            }
            // 3. 데이터 읽기
            while (stepTotalRead < stepLength) {
                int remain = (int) (TICK_TIME - (System.currentTimeMillis() - start));
                if (remain <= 0)
                    return null;
                socket.setSoTimeout(remain);
                int bytesRead = in.read(receiveDataBuffer, stepTotalRead, stepLength - stepTotalRead);
                if (bytesRead == -1) {
                    throw new IOException("[EOF: packet body incomplete]");
                }
                stepTotalRead += bytesRead;
            }
            var ret = new Pair<Integer, Integer>(stepLength, stepTick);
            stepLength = -1;
            stepTick = -1;
            stepTotalRead = 0;
            return ret;
        } catch (SocketTimeoutException e) {
            return null;
        }
    }

    private int[][] decode(int length) {
        ByteBuffer buffer = ByteBuffer.wrap(receiveDataBuffer, 0, length);
        int[][] arr = new int[GameBoard.HEIGHT][GameBoard.WIDTH];

        for (int i = 0; i < GameBoard.HEIGHT; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                arr[i][j] = buffer.getInt();
            }
        }

        return arr;
    }

    private void releaseResources(boolean remoteDisconnected) {
        if (remoteDisconnected) {
            Platform.runLater(onDisconnect);
        }
        receiveThread.interrupt();
        sendThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("[Error while closing socket]");
        }
    }

    public void disconnect() {
        releaseResources(false);
    }
}
