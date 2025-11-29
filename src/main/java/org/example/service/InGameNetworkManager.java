package org.example.service;

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

    private Thread receiveThread;
    private Thread sendThread;
    private Runnable onDisconnect;
    private Consumer<int[][]> onDataReceived;
    private Supplier<int[][]> dataProvider;

    // ----------- 수신 관련 -----------
    

    // ----------- 송신 관련 -----------
    private final BlockingQueue<int[][]> sendQueue = new LinkedBlockingQueue<>();

    // ----------- 상수 -----------
    private static final int TICK_TIME = 40;
    private static final int CONNECTION_LOST_TIME = 3000;
    private static final int CONNECTION_DELAY_TIME = 500;
    private static final int MAX_PACKET_SIZE = 1024; // 1KB

    public InGameNetworkManager(
        Socket socket, 
        Runnable onDisconnect, 
        Consumer<int[][]> onDataReceived, 
        Supplier<int[][]> dataProvider
    )
    {
        this.tcpSocket = socket;
        this.onDisconnect = onDisconnect;
        this.onDataReceived = onDataReceived;
        this.dataProvider = dataProvider;

        try {
            udpSocket = new DatagramSocket(tcpSocket.getLocalSocketAddress());
            udpSocket.setSoTimeout(TICK_TIME);
            startSending();
            startReceiving();
        } catch (IOException e) {
            System.err.println("[Error while creating UDP socket]");
            System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            udpSocket = null;
        }
    }

    private void startSending() {
        sendThread = new Thread(this::sendLoop);
        sendThread.setDaemon(true);
        sendThread.start();
    }

    private void startReceiving() {
        receiveThread = new Thread(this::receiveLoop);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    // public void sendBoardData(int[][] board) {
    //     sendQueue.add(board);
    // }

    private void sendLoop() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("[Send thread interrupted - graceful shutdown]");
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
                System.err.println("[Send thread interrupted - graceful shutdown]");
                return;
            }
        }
    }

    // 데이터 수신 루프
    private void receiveLoop() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("[Receive thread interrupted - graceful shutdown]");
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
                    System.err.println("[Receive thread interrupted - graceful shutdown]");
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
                System.err.println("[Receive thread interrupted - graceful shutdown]");
                break;
            }
        }
    }

    private void releaseResources(boolean remoteDisconnected) {
        if (remoteDisconnected) {
            Platform.runLater(onDisconnect);
        }
        if (receiveThread != null)
            receiveThread.interrupt();
        if (sendThread != null)
            sendThread.interrupt();
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
}
