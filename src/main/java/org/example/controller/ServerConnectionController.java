package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.example.service.NetworkManager;
import org.example.view.ServerConnectionView;

/**
 * Server Connection 화면의 입력을 처리하는 Controller
 */
public class ServerConnectionController extends BaseController {

    private final ServerConnectionView view;
    private Thread serverThread;
    private ServerSocket serverSocket;

    public ServerConnectionController() {
        this.view = new ServerConnectionView();
    }

    @Override
    protected Scene createScene() {
        var ip = NetworkManager.getLocalIPAddress();

        var root = view.createView(ip, this::handleGoBack);
        createDefaultScene(root);

        startServer();

        return scene;
    }

    public void handleGoBack() {
        popState();
    }

    private void startServer() {
        serverThread = new Thread(() -> {
            try {
                System.out.println("waiting for client connection in port 54673...");
                serverSocket = new ServerSocket(54673);
                
                Socket client = serverSocket.accept(); // 클라이언트 접속 대기

                System.out.println("Client connected from " + client.getInetAddress().getHostAddress());
                swapState(new WaitingRoomController(client.getInetAddress().getHostAddress(), true));

            } catch (IOException ie) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Closed Server");
                } else {
                    ie.printStackTrace();
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private void closeServer() {
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void exit() {
        closeServer();
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
