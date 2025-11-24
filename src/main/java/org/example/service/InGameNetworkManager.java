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

    public InGameNetworkManager(Socket socket, Runnable onDisconnect) {
        this.socket = socket;
        this.onDisconnect = onDisconnect;
    }

    /**
     * 게임 중 실시간 데이터 수신 시작
     */
    public void startReceiving() {
        receiveThread = new Thread(this::receiveLoop);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    /**
     * 게임 중 실시간 데이터를 수신하는 루프 (20ms tick)
     */
    private void receiveLoop() {
        int expectedTick = 0;
        long lastPacketTime = System.currentTimeMillis();
        DataInputStream inputStream;

        try {
            socket.setSoTimeout(20);
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("[Connection lost]");
            releaseResources(true);
            return;
        }

        while (true) {
            long loopStart = System.currentTimeMillis();
            Pair<Integer, byte[]> payload;
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

            // 지연이 100ms를 넘으면 끊김으로 간주
            if (System.currentTimeMillis() - lastPacketTime > 100) {
                System.err.println("[excessive delay (over 100ms)]");
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

            // 20ms 틱 속도 유지
            long sleep = 20 - (System.currentTimeMillis() - loopStart);
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    System.err.println("[Receive thread interrupted - graceful shutdown]");
                    break;
                }
            }
        }
    }

    /**
     * 한 스텝의 데이터를 읽음 (timeout 시 null 반환)
     */
    private Pair<Integer, byte[]> readStep(DataInputStream in) throws IOException {
        try {
            int length = in.readInt(); // 페이로드 길이
            int tick = in.readInt();   // 패킷 타임스탬프

            byte[] data = new byte[length];
            int totalRead = 0;
            while (totalRead < length) {
                int bytesRead = in.read(data, totalRead, length - totalRead);
                if (bytesRead == -1) {
                    throw new IOException("[EOF: packet body incomplete]");
                }
                totalRead += bytesRead;
            }

            return new Pair<>(tick, data);

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
}
