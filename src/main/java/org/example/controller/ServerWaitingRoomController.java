package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.example.view.ServerWaitingRoomView;

/**
 * Server Waiting Room 화면의 입력을 처리하는 Controller
 */
public class ServerWaitingRoomController extends BaseController {

    private final ServerWaitingRoomView view;
    private Thread serverThread;
    private ServerSocket serverSocket;

    public ServerWaitingRoomController() {
        this.view = new ServerWaitingRoomView();
    }

    @Override
    protected Scene createScene() {
        var ip = getLocalIPAddress();

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

                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

            } catch (IOException ie) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Stopping Server...");
                } else {
                    ie.printStackTrace();
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // 루프백이 아니고 활성화된 인터페이스만 확인
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    System.out.println(addr.getHostAddress());
                    // IPv4 주소만 선택
                    if (addr.getHostAddress().contains(":")) {
                        continue; // IPv6 주소는 스킵
                    }
                    return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Unable to detect IP";
    }

    @Override
    protected void exit() {
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
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("thread: " + (serverThread.isAlive() ? "alive" : "stopped"));
                System.out.println("socket: " + (serverSocket.isClosed() ? "closed" : "open"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
