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
    private Socket socket;
    private AtomicBoolean isConnecting;
    private long lastConnectionAttempt;

    public ClientConnectionController() {
        this.view = new ClientConnectionView();
        this.socket = new Socket();
        this.isConnecting = new AtomicBoolean(false);
        this.lastConnectionAttempt = 0;
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(this::handleIpSubmit);
        createDefaultScene(root);
        return scene;
    }

    public void handleGoBack() {
        popState();
    }

    private void handleIpSubmit(String ipAddress) {
        if (!NetworkManager.isValidIPv4(ipAddress)) {
            view.setTitleToInvalidIP();
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastConnectionAttempt < 1000) {
            return;
        }
        if (isConnecting.getAndSet(true))
            return;
        
        lastConnectionAttempt = currentTime;
        System.out.println("Attempting to connect to server at " + ipAddress);
        view.setTitleToConnecting();
        startConnection(ipAddress);
    }

    void startConnection(String ipAddress) {
        connectionThread = Thread.startVirtualThread(() -> {
            try {
                socket.connect(new InetSocketAddress(ipAddress, 54673), 3000);
            }
            catch (SocketTimeoutException ste) {
                Platform.runLater(() -> {
                    view.setTitleText("Connection timed out. Please try again.");
                });
                socket = new Socket(); //이거 안하면 오류남
                isConnecting.set(false);
                return;
            }
            catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    view.setTitleText("The IP address is incorrect or your Internet connection is unstable.");
                });
                isConnecting.set(false);
                return;
            }
            System.out.println("Connection successful: " + ipAddress);
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
