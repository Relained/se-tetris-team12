package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.example.service.NetworkManager;
import org.example.view.ServerConnectionView;

/**
 * Server Connection 화면의 입력을 처리하는 Controller
 */
public class ServerConnectionController extends BaseController {

    private final ServerConnectionView view;
    private Thread acceptThread;

    public ServerConnectionController() {
        this.view = new ServerConnectionView();
    }

    @Override
    protected Scene createScene() {
        var ip = NetworkManager.getLocalIPAddress();

        var root = view.createView(ip, this::handleGoBack);
        createDefaultScene(root);

        startAccept();

        return scene;
    }

    public void handleGoBack() {
        popState();
    }

    private void startAccept() {
        //already bind 에러 핸들링 추가필요
        acceptThread = Thread.startVirtualThread(() -> {
            try {
                System.out.println("waiting for client connection in port 54673...");

                ServerSocket serverSocket = new ServerSocket(54673);
                Socket client = serverSocket.accept(); // 클라이언트 접속 대기
                serverSocket.close();

                System.out.println("Client connected from " + client.getInetAddress().getHostAddress());
                
                Platform.runLater(() -> swapState(new WaitingRoomController(client, true)));

            } catch (IOException ie) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Closed Server");
                } else {
                    ie.printStackTrace();
                }
            }
        });
    }

    private void closeThread() {
        if (acceptThread != null && acceptThread.isAlive()) {
            acceptThread.interrupt();
        }
    }

    @Override
    protected void exit() {
        closeThread();
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
