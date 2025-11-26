package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import org.example.service.NetworkUtility;
import org.example.view.ServerConnectionView;

/**
 * Server Connection 화면의 입력을 처리하는 Controller
 */
public class ServerConnectionController extends BaseController {

    private final ServerConnectionView view;
    private Thread acceptThread;
    private Thread broadcastThread;

    private static final String MSG_IP_ERROR = "IP address Not Found";
    private static final String MSG_PORT_BINDING_ERROR = "Port binding error! please try again";
    private static final String MSG_ERROR = "Failed to accept client connection";

    public ServerConnectionController() {
        this.view = new ServerConnectionView();
    }

    @Override
    protected Scene createScene() {
        var ip = NetworkUtility.getLocalIPAddress();

        boolean IPNotFound = ip.isEmpty();
        if (IPNotFound) {
            ip = "Unable to detect IP";
        }

        var root = view.createView(ip, this::handleGoBack);
        createDefaultScene(root);

        if (IPNotFound) {
            view.setTitle(MSG_IP_ERROR);
        } else {
            startAccept();
            startUDPResponder();
        }

        return scene;
    }

    public void handleGoBack() {
        popState();
    }

    public void startUDPResponder() {
        broadcastThread = Thread.startVirtualThread(() -> {
            try (DatagramSocket socket = new DatagramSocket(54777)) {
                byte[] buf = new byte[512];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Received UDP: " + received + " from " + packet.getAddress() + ":" + packet.getPort());
                    // 응답
                    byte[] response = "TETRIS_RESPONSE".getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                        response, response.length, packet.getAddress(), packet.getPort()
                    );
                    socket.send(responsePacket);
                }
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("close UDP responder");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startAccept() {
        acceptThread = Thread.startVirtualThread(() -> {
            try {
                System.out.println("waiting for client connection in port 54673...");

                ServerSocket serverSocket = new ServerSocket(54673);
                Socket client = serverSocket.accept(); // 클라이언트 접속 대기
                serverSocket.close();

                System.out.println("Client connected from " + client.getInetAddress().getHostAddress());
                
                Platform.runLater(() -> swapState(new WaitingRoomController(client, true)));

            } catch (BindException be) {
                System.err.println("[Port Binding Error]");
                System.err.println(be.getClass().getName() + " - " + be.getMessage());
                Platform.runLater(() -> view.setTitle(MSG_PORT_BINDING_ERROR));
            }
            catch (IOException ie) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Closed Server");
                    return;
                }
                Platform.runLater(() -> view.setTitle(MSG_ERROR));
                System.err.println("Exception: " + ie.getClass().getName() + " - " + ie.getMessage());
            }
        });
    }

    private void closeThread() {
        if (acceptThread != null && acceptThread.isAlive()) {
            acceptThread.interrupt();
        }
        if (broadcastThread != null && broadcastThread.isAlive()) {
            broadcastThread.interrupt();
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
