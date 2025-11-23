package org.example.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
    private final BlockingQueue<byte[]> sendQueue;
    private Runnable onDisconnect;
    private Runnable onGameStart;
    private Consumer<GameMode> onGameModeChange;

    private Thread sendThread;
    private Thread receiveThread;
    private volatile boolean clientReady = false;
    private volatile boolean serverReady = false;
    private final boolean isServer;

    private Thread heartbeatThread;
    private volatile long lastHeartbeatTime;
    private static final long HEARTBEAT_INTERVAL = 2500; // 2.5초마다 heartbeat 전송
    private static final long HEARTBEAT_TIMEOUT = 5000; // 5초 동안 응답 없으면 끊김으로 판단

    public WaitingRoomNetworkManager(Socket socket, boolean isServer, Runnable onDisconnect, Runnable onGameStart, Consumer<GameMode> onGameModeChange) {
        this.socket = socket;
        this.isServer = isServer;
        this.sendQueue = new LinkedBlockingQueue<>();
        this.onDisconnect = onDisconnect;
        this.onGameStart = onGameStart;
        this.onGameModeChange = onGameModeChange;
        this.lastHeartbeatTime = System.currentTimeMillis();
        
        receiveThread = Thread.startVirtualThread(this::receiveLoop);
        sendThread = Thread.startVirtualThread(this::sendLoop);
        heartbeatThread = Thread.startVirtualThread(this::heartbeatLoop);
    }

    public String getRemoteIPAddress() {
        return socket.getInetAddress().getHostAddress();
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
     * Heartbeat 전송 및 타임아웃 체크
     */
    private void heartbeatLoop() {
        try {
            while (true) {
                Thread.sleep(HEARTBEAT_INTERVAL);
                
                // Heartbeat 전송
                byte[] message = new byte[1];
                message[0] = 0x04; // Heartbeat 메시지 타입
                sendQueue.put(message);
                
                // 타임아웃 체크
                if (System.currentTimeMillis() - lastHeartbeatTime > HEARTBEAT_TIMEOUT) {
                    System.err.println("[Heartbeat timeout - connection lost]");
                    releaseResources(true);
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.err.println("[Heartbeat thread interrupted - graceful shutdown]");
        }
    }

    /**
     * 전송 큐에서 메시지를 가져와 전송하는 루프
     */
    private void sendLoop() {
        DataOutputStream outputStream;
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("[Failed to initialize output stream]");
            e.printStackTrace();
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
            System.err.println("[Send thread interrupted - graceful shutdown]");
        } catch (IOException e) {
            System.err.println("[Send failed - connection lost]");
            releaseResources(true);
        }
    }

    /**
     * 게임 모드 변경 메시지를 수신하는 루프
     */
    private void receiveLoop() {
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("[Failed to initialize input stream]");
            e.printStackTrace();
            releaseResources(true);
            return;
        }
        try {
            while (true) {
                int length = inputStream.readInt(); // blocking - 데이터 올 때까지 대기
                byte type = inputStream.readByte();
                byte[] data = inputStream.readNBytes(length - 1);

                if (type == 0x01) { // 게임 모드 변경 (클라이언트만 수신)
                    if (isServer) continue;
                    GameMode mode = GameMode.valueOf(new String(data));
                    Platform.runLater(() -> onGameModeChange.accept(mode));
                } 
                else if (type == 0x02) { // Ready 상태 변경 (서버만 수신)
                    if (!isServer) continue;
                    boolean ready = data[0] == 1;
                    clientReady = ready;
                    // 서버: 클라이언트와 서버 모두 Ready면 게임 시작
                    if (clientReady && serverReady) {
                        sendGameStart();
                        Platform.runLater(onGameStart);
                    }
                }
                else if (type == 0x03) { // 게임 시작 (클라이언트만 수신)
                    if (isServer) continue;
                    Platform.runLater(onGameStart);
                    //게임 시작할 때 WaitingRoom 리소스 어떻게 할건지 생각해봐야함
                }
                else if (type == 0x04) { // Heartbeat (양방향)
                    lastHeartbeatTime = System.currentTimeMillis();
                }
            }
        } catch (EOFException e) {
            System.err.println("[Remote disconnected - graceful shutdown]");
            releaseResources(true);
        }
        catch (IOException e) {
            if (Thread.currentThread().isInterrupted()) {
                System.err.println("[Receive thread interrupted - graceful shutdown]");
            } else {
                System.err.println("[Receive failed - connection lost]");
                releaseResources(true);
            }
        }
    }

    private void releaseResources(boolean remoteDisconnected) {
        if (remoteDisconnected) {
            Platform.runLater(onDisconnect);
        }
        sendThread.interrupt();
        receiveThread.interrupt();
        heartbeatThread.interrupt();
        try {
            if (!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            System.err.println("[Error while closing socket]");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        releaseResources(false);
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
