package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.example.service.NetworkUtility;
import org.example.view.ClientConnectionView;

/**
 * Client Waiting Room 화면의 입력을 처리하는 Controller
 */
public class ClientConnectionController extends BaseController {

    private final ClientConnectionView view;
    private Thread connectionThread;
    private Thread broadcastThread;
    private AtomicBoolean isConnecting;
    private AtomicBoolean isBroadcasting;
    private List<String> connectionHistory;
    private long lastConnectionAttempt;
    private static final String MSG_CONNECTION_TIMEOUT = "Connection timed out. Please try again.";
    private static final String MSG_CONNECTION_FAILED = "The IP address is incorrect or your Internet connection is unstable.";
    private static final String MSG_INVALID_IP = "Invalid IP address. Please try again.";
    private static final String MSG_CONNECTING = "Trying to connect...";
    private static final String CONNECTION_HISTORY_FILE = "tetris_connection_history.txt";

    public ClientConnectionController() {
        this.view = new ClientConnectionView();
        this.isConnecting = new AtomicBoolean(false);
        this.isBroadcasting = new AtomicBoolean(false);
        this.lastConnectionAttempt = 0;
        this.connectionHistory = loadConnectionHistory();
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            this::handleSearchedUserSelect,
            this::handleRefresh,
            this::handleHistorySelect,
            this::handleIpSubmit,
            this::handleGoBack,
            this::handleClearHistory
        );
        createDefaultScene(root);
        view.setConnectionHistoryItems(connectionHistory);
        handleRefresh();
        return scene;
    }

    public void handleGoBack() {
        popState();
    }

    private void handleSearchedUserSelect(String ipAddress) {
        ipAddress = ipAddress.trim();
        view.setIpAddressField(ipAddress);
        handleIpSubmit(ipAddress);
    }

    private void handleRefresh() {
        if (!isBroadcasting.getAndSet(true)) {
            broadcastDiscovery();
        }
    }

    private void handleHistorySelect(String ipAddress) {
        view.setIpAddressField(ipAddress);
    }

    private void handleClearHistory() {
        connectionHistory.clear();
        view.setConnectionHistoryItems(connectionHistory);
    }

    private void handleIpSubmit(String ipAddress) {
        if (!NetworkUtility.isValidIPv4(ipAddress)) {
            view.setTitleText(MSG_INVALID_IP);
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastConnectionAttempt < 1000) {
            return;
        }
        if (isConnecting.getAndSet(true))
            return;
        
        lastConnectionAttempt = currentTime;
        view.setTitleText(MSG_CONNECTING);
        startConnection(ipAddress);
    }

    private void startConnection(String ipAddress) {
        connectionThread = Thread.startVirtualThread(() -> {
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(ipAddress, 54673), 3000);
                
                if (!connectionHistory.contains(ipAddress)) {
                    connectionHistory.add(ipAddress);
                }
                Platform.runLater(() -> swapState(new WaitingRoomController(socket, false)));
            }
            catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.err.println("[connection Thread interrupted - graceful shutdown]");
                    return;
                }
                System.err.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
                try {
                    socket.close(); 
                } catch (Exception ignore) {}

                Platform.runLater(() -> {
                    view.setTitleText(e instanceof SocketTimeoutException ?
                         MSG_CONNECTION_TIMEOUT : MSG_CONNECTION_FAILED);
                });
                isConnecting.set(false);
            }
        });
    }

    private void broadcastDiscovery() {
        view.resetSearchedUsersItems();
        view.setRefreshButtonText("...");
        broadcastThread = Thread.startVirtualThread(() -> {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setSoTimeout(3000);

                // 브로드캐스트 주소 계산
                InetAddress broadcastAddress = NetworkUtility.getBroadcastAddress();
                if (broadcastAddress == null) {
                    System.err.println("Failed to calculate broadcast address");
                    return;
                }

                // 브로드캐스트 패킷 전송
                byte[] sendData = "TETRIS_DISCOVERY".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(
                    sendData, 
                    sendData.length, 
                    broadcastAddress, 
                    54652
                );
                socket.send(sendPacket);
                System.out.println("Broadcast sent to: " + broadcastAddress.getHostAddress());

                // 응답 대기
                byte[] receiveData = new byte[512];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String responseAddress = receivePacket.getAddress().getHostAddress();
                System.out.println("Received response from " + responseAddress + ": " + response);

                if ("TETRIS_RESPONSE".equals(response)) {
                    Platform.runLater(() -> {
                        view.addSearchedUsersItems(responseAddress);
                    });
                }
            } catch (SocketTimeoutException e) {
                System.err.println("Broadcast discovery timed out - no response received");
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.err.println("[broadcast Thread interrupted - graceful shutdown]");
                    return;
                }
                System.err.println("Broadcast exception: " + e.getClass().getName() + " - " + e.getMessage());
            } finally {
                if (socket != null) {
                    socket.close();
                }
                isBroadcasting.set(false);
                Platform.runLater(() -> view.setRefreshButtonText("↻"));
            }
        });
    }

    @Override
    protected void exit() {
        if (connectionThread != null && connectionThread.isAlive()) {
            connectionThread.interrupt();
        }
        if (broadcastThread != null && broadcastThread.isAlive()) {
            broadcastThread.interrupt();
        }
        saveConnectionHistory(connectionHistory);
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            handleGoBack();
        }
    }

    /**
     * 연결 기록을 파일에서 로드합니다.
     * @return IP 주소 목록
     */
    private List<String> loadConnectionHistory() {
        List<String> history = new ArrayList<>();
        Path filePath = Paths.get(System.getProperty("user.home"), CONNECTION_HISTORY_FILE);
        
        if (!Files.exists(filePath)) {
            return history;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    history.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load connection history: " + e.getMessage());
        }
        
        return history;
    }

    /**
     * 연결 기록을 파일에 저장합니다.
     * @param history 저장할 IP 주소 목록
     */
    private void saveConnectionHistory(List<String> history) {
        Path filePath = Paths.get(System.getProperty("user.home"), CONNECTION_HISTORY_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (String ip : history) {
                writer.write(ip);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save connection history: " + e.getMessage());
        }
    }
}
