package org.example.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javafx.application.Platform;
import javafx.util.Pair;

/**
 * 게임 중 실시간 데이터를 송수신하는 네트워크 매니저
 */

public class InGameNetworkManager {

    private final Socket socket;

    private Runnable onDisconnect;
    private Thread receiveThread;

    private static final int TICK_TIME = 40;
    private static final int MAX_PACKET_SIZE = 1024; // 1KB

    // readStep 진행 상태 저장용 멤버 변수
    private int stepLength = -1;
    private int stepTick = -1;
    private int stepTotalRead = 0;
    private final byte[] dataBuffer = new byte[MAX_PACKET_SIZE];

    public InGameNetworkManager(Socket socket, Runnable onDisconnect) {
        this.socket = socket;
        this.onDisconnect = onDisconnect;
    }

    private void startReceiving() {
        receiveThread = new Thread(this::receiveLoop);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    // 데이터 수신 루프
    private void receiveLoop() {
        int expectedTick = 0;
        long lastPacketTime = System.currentTimeMillis();
        DataInputStream inputStream;

        try {
            socket.setSoTimeout(TICK_TIME);
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("[Connection lost]");
            releaseResources(true);
            return;
        }

        while (true) {
            // 지연이 200ms를 넘으면 끊김으로 간주
            if (System.currentTimeMillis() - lastPacketTime > 200) {
                System.err.println("[excessive delay (over 200ms)]");
                releaseResources(true);
                break;
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
                System.err.println("[Connection lost]");
                releaseResources(true);
                break;
            }

            expectedTick++;
            if (payload != null) {
                lastPacketTime = System.currentTimeMillis();
                
                // tick이 맞지 않으면 맞을 때까지 consume
                while (payload.getKey() != expectedTick) {
                    System.err.println("[Tick mismatch: expected " + expectedTick + ", got " + payload.getKey() + "]");
                    try {
                        payload = readStep(inputStream);
                        if (payload == null) {
                            break;
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
                
                if (payload != null && payload.getKey() == expectedTick) {
                    // TODO: payload 처리 로직 추가
                }
            }

            long sleep = TICK_TIME - (System.currentTimeMillis() - loopStart);
            if (sleep <= 0) continue;

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.err.println("[Receive thread interrupted - graceful shutdown]");
                break;
            }
        }
    }

    /**
     * 한 스텝의 데이터를 읽음 (timeout 시 null 반환)
     */
    private Pair<Integer, Integer> readStep(DataInputStream in) throws IOException {
        long start = System.currentTimeMillis();
        try {
            // 1. length 읽기
            if (stepLength == -1) {
                int remain = (int) (TICK_TIME - (System.currentTimeMillis() - start));
                socket.setSoTimeout(remain);
                stepLength = in.readInt();
                if (stepLength > MAX_PACKET_SIZE) {
                    throw new IOException("[Packet too large: " + stepLength + "]");
                }
            }
            // 2. tick 읽기
            if (stepTick == -1) {
                int remain = (int) (TICK_TIME - (System.currentTimeMillis() - start));
                if (remain <= 0) return null;
                socket.setSoTimeout(remain);
                stepTick = in.readInt();
            }
            // 3. 데이터 읽기
            while (stepTotalRead < stepLength) {
                int remain = (int) (TICK_TIME - (System.currentTimeMillis() - start));
                if (remain <= 0) return null;
                socket.setSoTimeout(remain);
                int bytesRead = in.read(dataBuffer, stepTotalRead, stepLength - stepTotalRead);
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

    private void releaseResources(boolean remoteDisconnected) {
        if (remoteDisconnected) {
            Platform.runLater(onDisconnect);
        }
        receiveThread.interrupt();
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
