package org.example.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
    private Consumer<String> onChatMessageReceived;
    private Consumer<Boolean> onOpponentReadyChanged;

    private Thread sendThread;
    private Thread receiveThread;
    private volatile boolean clientReady = false;
    private volatile boolean serverReady = false;
    private final boolean isServer;

    private Thread heartbeatThread;
    private volatile long lastHeartbeatTime;
    private static final long HEARTBEAT_INTERVAL = 2000;
    private static final long HEARTBEAT_TIMEOUT = 3500;

    public WaitingRoomNetworkManager(Socket socket, boolean isServer, 
                                    Runnable onDisconnect, 
                                    Runnable onGameStart, 
                                    Consumer<GameMode> onGameModeChange, 
                                    Consumer<Boolean> onOpponentReadyChanged,
                                    Consumer<String> onChatMessageReceived) 
    {
        this.socket = socket;
        this.isServer = isServer;
        this.sendQueue = new LinkedBlockingQueue<>();
        this.onDisconnect = onDisconnect;
        this.onGameStart = onGameStart;
        this.onGameModeChange = onGameModeChange;
        this.onOpponentReadyChanged = onOpponentReadyChanged;
        this.onChatMessageReceived = onChatMessageReceived;
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
            byte[] modeData = mode.name().getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.allocate(1 + modeData.length);
            buffer.put((byte) 0x01); // 게임 모드 변경 메시지 타입
            buffer.put(modeData);
            sendQueue.put(buffer.array());
        } catch (InterruptedException e) {
            System.err.println("[Failed to queue game mode change]");
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
        }
    }

    /**
     * 게임 시작 신호 전송
     */
    public void sendGameStart() {
        try {
            byte[] message = new byte[1];
            message[0] = 0x03; // 게임 시작 메시지 타입
            sendQueue.put(message);
        } catch (InterruptedException e) {
            System.err.println("[Failed to queue game start]");
        }
    }

    /**
     * 채팅 메시지를 상대에게 전송
     */
    public void sendChatMessage(String chatMessage) {
        try {
            byte[] messageData = chatMessage.getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.allocate(1 + messageData.length);
            buffer.put((byte) 0x05); // 채팅 메시지 타입
            buffer.put(messageData);
            sendQueue.put(buffer.array());
        } catch (InterruptedException e) {
            System.err.println("[Failed to queue chat message]");
        }
    }

    /**
     * Heartbeat 전송 및 타임아웃 체크
     */
    private void heartbeatLoop() {
        try {
            while (true) {
                Thread.sleep(HEARTBEAT_INTERVAL);

                // 타임아웃 체크
                if (System.currentTimeMillis() - lastHeartbeatTime > HEARTBEAT_TIMEOUT) {
                    System.err.println("[Heartbeat timeout - connection lost]");
                    releaseResources(true);
                    break;
                }

                // Heartbeat 전송
                byte[] message = new byte[1];
                message[0] = 0x04; // Heartbeat 메시지 타입
                sendQueue.put(message);
            }
        } catch (InterruptedException e) {
            System.err.println("(WaitingRoom)[Heartbeat thread interrupted - graceful shutdown]");
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
            releaseResources(true);
            return;
        }
        try {
            while (true) {
                byte[] message = sendQueue.take(); // blocking - 메시지 올 때까지 대기
                outputStream.writeInt(message.length);
                outputStream.write(message);
                outputStream.flush();
                if (message[0] == 0x03) { // 게임 시작 메시지 전송 후 종료
                    System.err.println("(WaitingRoom)[Game start message sent - send loop shutdown]");
                    return;
                }
            }
        } catch (InterruptedException e) {
            System.err.println("(WaitingRoom)[Send thread interrupted - graceful shutdown]");
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
                else if (type == 0x02) { // Ready 상태 변경
                    boolean ready = data[0] == 1;
                    Platform.runLater(() -> onOpponentReadyChanged.accept(ready));
                    if (isServer) {
                        clientReady = ready;
                        // 클라이언트와 서버 모두 Ready면 게임 시작
                        if (clientReady && serverReady) {
                            heartbeatThread.interrupt();
                            sendGameStart();
                        }
                    }
                }
                else if (type == 0x03) { // 게임 시작
                    // 서버는 ready 조건을 파악하고 게임 시작 신호만 보냄
                    // 신호를 보내는 동시에 heartbeatThread 종료시킴
                    // 게임 시작 신호가 보내지면 sendThread도 종료됨
                    // 클라이언트는 시작 신호를 받으면 다시 서버에게 시작 신호를 보내고,
                    // 본인은 모든 스레드를 자연스럽게 종료시킴
                    // 이후 클라이언트의 시작 신호를 서버가 다시 받으면,
                    // 그때 서버의 리시브 스레드가 마지막으로 정지됨
                    // 이는 리시브 스레드를 정상 정지하기 위한 조치임
                    if (isServer) {
                        Platform.runLater(onGameStart);
                    }
                    else {
                        sendGameStart();
                        heartbeatThread.interrupt();
                        Platform.runLater(onGameStart);
                    }
                    System.err.println("(WaitingRoom)[Game start signal received - receive loop shutdown]");
                    return;
                }
                else if (type == 0x04) { // Heartbeat (양방향)
                    lastHeartbeatTime = System.currentTimeMillis();
                }
                else if (type == 0x05) { // 채팅 메시지 (양방향)
                    String chatMessage = new String(data, StandardCharsets.UTF_8);
                    Platform.runLater(() -> onChatMessageReceived.accept(chatMessage));
                }
            }
        } catch (EOFException e) {
            System.err.println("[Remote disconnected - graceful shutdown]");
            releaseResources(true);
        }
        catch (IOException e) {
            if (Thread.currentThread().isInterrupted()) {
                System.err.println("(WaitingRoom)[Receive thread interrupted - graceful shutdown]");
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
            socket.close();
        } catch (IOException e) {
            System.err.println("[Error while closing socket]");
        }
    }

    public void disconnect() {
        releaseResources(false);
    }

    /**
     * 서버의 Ready 상태 설정 (서버 전용)
     */
    public void setServerReady(boolean ready) {
        this.serverReady = ready;
        // 클라이언트와 서버 모두 Ready면 게임 시작
        if (clientReady && serverReady) {
            heartbeatThread.interrupt();
            sendGameStart();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
