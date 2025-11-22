package org.example.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.example.model.GameMode;

import javafx.application.Platform;

/**
 * 대기실에서 게임 모드 변경을 송수신하는 네트워크 매니저
 */
public class WaitingRoomNetworkManager {

    private final Socket socket;
    private Runnable onDisconnect;
    private Runnable onGameStart;
    private DataOutputStream outputStream;
    private final BlockingQueue<byte[]> sendQueue;
    private Thread sendThread;
    private Thread receiveThread;
    private volatile boolean clientReady = false;
    private volatile boolean serverReady = false;
    private final boolean isServer;

    public WaitingRoomNetworkManager(Socket socket, boolean isServer, Runnable onDisconnect, Runnable onGameStart) {
        this.socket = socket;
        this.isServer = isServer;
        this.sendQueue = new LinkedBlockingQueue<>();
        this.onDisconnect = onDisconnect;
        this.onGameStart = onGameStart;
        try {
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("[Failed to initialize output stream]");
            e.printStackTrace();
            onDisconnect.run();
        }
    }

    public String getRemoteIPAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public void startReceiving(Consumer<GameMode> onGameModeChange, Consumer<Boolean> onReadyChange) {
        receiveThread = Thread.startVirtualThread(() -> receiveLoop(onGameModeChange, onReadyChange));
    }

    public void startSending() {
        sendThread = Thread.startVirtualThread(this::sendLoop);
    }

    /**
     * 게임 모드 변경을 상대에게 전송
     */
    public void sendGameModeChange(GameMode mode) {
        try {
            byte[] modeData = mode.name().getBytes();
            byte[] message = new byte[1 + modeData.length];
            message[0] = 0x01; // 게임 모드 변경 메시지 타입
            System.arraycopy(modeData, 0, message, 1, modeData.length);
            sendQueue.put(message);
        } catch (InterruptedException e) {
            System.err.println("[Failed to queue game mode change]");
            e.printStackTrace();
        }
    }

    /**
     * Ready 상태를 상대에게 전송
     */
    public void sendReadyState(boolean ready) {
        try {
            byte[] message = new byte[2];
            message[0] = 0x02; // Ready 상태 메시지 타입
            message[1] = (byte) (ready ? 1 : 0);
            sendQueue.put(message);
        } catch (InterruptedException e) {
            System.err.println("[Failed to queue ready state]");
            e.printStackTrace();
        }
    }

    /**
     * 게임 시작 신호를 클라이언트에게 전송 (서버 전용)
     */
    public void sendGameStart() {
        if (!isServer) {
            System.err.println("[Only server can send game start signal]");
            return;
        }
        try {
            byte[] message = new byte[1];
            message[0] = 0x03; // 게임 시작 메시지 타입
            sendQueue.put(message);
        } catch (InterruptedException e) {
            System.err.println("[Failed to queue game start]");
            e.printStackTrace();
        }
    }

    /**
     * 연결 종료 메시지를 전송
     */
    private void sendDisconnect() {
        try {
            sendQueue.clear();
            byte[] message = new byte[1];
            message[0] = (byte) 0xFF; // 연결 종료 메시지 타입
            outputStream.writeInt(message.length);
            outputStream.write(message);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("[Failed to send disconnect message]");
        }
    }

    /**
     * 전송 큐에서 메시지를 가져와 전송하는 루프 (blocking - 데이터 없으면 대기)
     */
    private void sendLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] message = sendQueue.take(); // blocking - 메시지 올 때까지 대기
                outputStream.writeInt(message.length);
                outputStream.write(message);
                outputStream.flush();
            }
        } catch (InterruptedException e) {
            System.err.println("[Send thread interrupted]");
        } catch (IOException e) {
            System.err.println("[Send failed - connection lost]");
            closeConnection(true);
        }
    }

    /**
     * 게임 모드 변경 메시지를 수신하는 루프 (blocking - 데이터 없으면 대기)
     */
    private void receiveLoop(Consumer<GameMode> onGameModeChange, Consumer<Boolean> onReadyChange) {
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("[Failed to initialize input stream]");
            closeConnection(true);
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                int length = inputStream.readInt(); // blocking - 데이터 올 때까지 대기
                byte type = inputStream.readByte();
                byte[] data = inputStream.readNBytes(length - 1);

                // 메시지 타입 확인
                if (type == 0x01) { // 게임 모드 변경
                    GameMode mode = GameMode.valueOf(new String(data));
                    Platform.runLater(() -> onGameModeChange.accept(mode));
                } else if (type == 0x02) { // Ready 상태
                    boolean ready = data[0] == 1;
                    if (isServer) {
                        clientReady = ready;
                        // 서버: 클라이언트와 서버 모두 Ready면 게임 시작
                        if (clientReady && serverReady) {
                            sendGameStart();
                            if (onGameStart != null) {
                                Platform.runLater(onGameStart);
                            }
                        }
                    } else {
                        // 클라이언트: UI 업데이트
                        Platform.runLater(() -> onReadyChange.accept(ready));
                    }
                } else if (type == 0x03) { // 게임 시작 (클라이언트만 수신)
                    if (!isServer && onGameStart != null) {
                        Platform.runLater(onGameStart);
                    }
                } else if (type == (byte) 0xFF) { // 연결 종료
                    System.err.println("[Received disconnect message from remote]");
                    closeConnection(true);
                    break;
                }
            } catch (IOException e) {
                System.err.println("[Receive failed - connection lost]");
                closeConnection(true);
                break;
            }
        }
    }

    private void closeConnection(boolean remoteDisconnected) {
        if (remoteDisconnected) {
            Platform.runLater(onDisconnect);
        }
        if (sendThread != null) {
            sendThread.interrupt();
        }
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
        try {
            if (!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            System.err.println("[Error while closing socket]");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        sendDisconnect();
        Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            closeConnection(false);
        });
    }

    /**
     * 서버의 Ready 상태 설정 (서버 전용)
     */
    public void setServerReady(boolean ready) {
        if (!isServer) {
            System.err.println("[Only server can set server ready state]");
            return;
        }
        this.serverReady = ready;
        // 클라이언트와 서버 모두 Ready면 게임 시작
        if (clientReady && serverReady) {
            sendGameStart();
            Platform.runLater(onGameStart);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
