package org.example.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private int stepTotalRead = 0;

    // ----------- 송신 관련 -----------

    private final ByteBuffer sendDataBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
    private final BlockingQueue<int[][]> sendQueue = new LinkedBlockingQueue<>();

    // ----------- 상수 -----------
    private static final int TICK_TIME = 40;
    private static final int CONNECTION_LOST_TIME = 3000;
    private static final int CONNECTION_DELAY_TIME = 500;
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
            for (int i = 0; i < GameBoard.HEIGHT; i++) {
                for (int j = 0; j < GameBoard.WIDTH; j++) {
                    sendDataBuffer.putInt(data[i][j]);
                }
            }
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
            // 
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
            if (System.currentTimeMillis() - lastPacketTime > CONNECTION_DELAY_TIME) {
                System.err.println("[Connection delay detected (>500ms)]");
            }
            if (System.currentTimeMillis() - lastPacketTime > CONNECTION_LOST_TIME) {
                System.err.println("[Connection lost detected (>3000ms)]");
                releaseResources(true);
                return;
            }

            long loopStart = System.currentTimeMillis();
            int readLength = -1;

            try {
                readLength = readStep(inputStream);
                while (inputStream.available() != 0) {
                    readLength = readStep(inputStream);
                }
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

            if (readLength != -1) {
                lastPacketTime = System.currentTimeMillis();
                var decodeData = decodeReceivedData(readLength);
                onDataReceived.accept(decodeData);
            }

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

    // 한 덩어리의 패킷을 읽고 길이를 반환함
    // 데이터는 receiveDataBuffer에 저장됨
    // 이 함수의 실행 시간은 절대 1 tick을 넘기지 않음. 넘기게 되면 즉시 null 반환
    private int readStep(DataInputStream in) throws IOException {
        long start = System.currentTimeMillis();
        try {
            // length 읽기
            if (stepLength == -1) {
                int remain = (int) (TICK_TIME - (System.currentTimeMillis() - start));
                socket.setSoTimeout(remain);
                stepLength = in.readInt();
            }
            // 데이터 읽기
            while (stepTotalRead < stepLength) {
                int remain = (int) (TICK_TIME - (System.currentTimeMillis() - start));
                if (remain <= 0)
                    return -1;
                socket.setSoTimeout(remain);
                int bytesRead = in.read(receiveDataBuffer, stepTotalRead, stepLength - stepTotalRead);
                if (bytesRead == -1) {
                    throw new IOException("[EOF: packet body incomplete]");
                }
                stepTotalRead += bytesRead;
            }
            int ret = stepLength;
            stepLength = -1;
            stepTotalRead = 0;
            return ret;
        } catch (SocketTimeoutException e) {
            return -1;
        }
    }

    private int[][] decodeReceivedData(int length) {
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
