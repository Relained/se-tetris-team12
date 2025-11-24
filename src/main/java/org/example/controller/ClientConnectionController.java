package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.example.service.NetworkManager;
import org.example.view.ClientConnectionView;

/**
 * Client Waiting Room 화면의 입력을 처리하는 Controller
 */
public class ClientConnectionController extends BaseController {

    private final ClientConnectionView view;
    private Thread connectionThread;
    private AtomicBoolean isConnecting;
    private long lastConnectionAttempt;
    private static final String MSG_CONNECTION_TIMEOUT = "Connection timed out. Please try again.";
    private static final String MSG_CONNECTION_FAILED = "The IP address is incorrect or your Internet connection is unstable.";
    private static final String MSG_INVALID_IP = "Invalid IP address. Please try again.";
    private static final String MSG_CONNECTING = "Trying to connect...";

    public ClientConnectionController() {
        this.view = new ClientConnectionView();
        this.isConnecting = new AtomicBoolean(false);
        this.lastConnectionAttempt = 0;
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            this::handleSearchedUserSelect,
            this::handleRefresh,
            this::handleHistorySelect,
            this::handleIpSubmit,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }

    public void handleGoBack() {
        popState();
    }

    private void handleSearchedUserSelect(String ipAddress) {
        System.out.println("Searched user selected: " + ipAddress);
        view.setIpAddressField(ipAddress);
    }

    private void handleRefresh() {
        System.out.println("Refresh button clicked");
        // TODO: Implement refresh logic
    }

    private void handleHistorySelect(String ipAddress) {
        System.out.println("History selected: " + ipAddress);
        view.setIpAddressField(ipAddress);
    }

    private void handleIpSubmit(String ipAddress) {
        if (!NetworkManager.isValidIPv4(ipAddress)) {
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

    void startConnection(String ipAddress) {
        connectionThread = Thread.startVirtualThread(() -> {
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(ipAddress, 54673), 3000);
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
                return;
            }
            Platform.runLater(() -> {
                swapState(new WaitingRoomController(socket, false));
            });
        });
    }

    @Override
    protected void exit() {
        if (connectionThread != null && connectionThread.isAlive()) {
            connectionThread.interrupt();
        }
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            handleGoBack();
        }
    }
}
