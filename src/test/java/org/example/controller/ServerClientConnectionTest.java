package org.example.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for server-client connection process
 * Validates TCP connection establishment and UDP broadcast server discovery scenarios.
 */
@Timeout(15)
class ServerClientConnectionTest {

    private ServerSocket serverSocket;
    private Socket acceptedClient;
    private Thread serverAcceptThread;
    private Thread udpResponderThread;
    private Thread clientConnectionThread;
    private Thread clientBroadcastThread;
    private DatagramSocket udpServerSocket;

    @AfterEach
    void tearDown() throws InterruptedException, IOException {
        // Thread cleanup
        if (serverAcceptThread != null && serverAcceptThread.isAlive()) {
            serverAcceptThread.interrupt();
        }
        if (udpResponderThread != null && udpResponderThread.isAlive()) {
            udpResponderThread.interrupt();
        }
        if (clientConnectionThread != null && clientConnectionThread.isAlive()) {
            clientConnectionThread.interrupt();
        }
        if (clientBroadcastThread != null && clientBroadcastThread.isAlive()) {
            clientBroadcastThread.interrupt();
        }

        // Socket cleanup
        if (acceptedClient != null && !acceptedClient.isClosed()) {
            acceptedClient.close();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        if (udpServerSocket != null && !udpServerSocket.isClosed()) {
            udpServerSocket.close();
        }

        Thread.sleep(200);
    }

    @Test
    @DisplayName("TCP Connection Establishment: Server accepts client connection scenario")
    void testTCPConnectionEstablishment() throws Exception {
        // Create server socket with dynamic port
        serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();

        AtomicReference<Socket> serverSideSocket = new AtomicReference<>();
        CountDownLatch serverAcceptLatch = new CountDownLatch(1);

        // Server: Wait for client connection (ServerConnectionController.startAccept() logic)
        serverAcceptThread = Thread.ofVirtual().start(() -> {
            try {
                System.err.println("[SERVER] Waiting for client connection on port " + port + "...");
                Socket client = serverSocket.accept();
                serverSideSocket.set(client);
                serverAcceptLatch.countDown();
                System.err.println("[SERVER] Client connection accepted from " + 
                    client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });

        // Client: Attempt to connect to server (ClientConnectionController.startConnection() logic)
        AtomicReference<Socket> clientSocket = new AtomicReference<>();
        CountDownLatch clientConnectLatch = new CountDownLatch(1);

        clientConnectionThread = Thread.ofVirtual().start(() -> {
            try {
                Socket socket = new Socket();
                System.err.println("[CLIENT] Attempting to connect to localhost:" + port + "...");
                socket.connect(new InetSocketAddress("localhost", port), 3000);
                clientSocket.set(socket);
                clientConnectLatch.countDown();
                System.err.println("[CLIENT] Connection successful!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Wait for connection to complete
        assertTrue(serverAcceptLatch.await(5, TimeUnit.SECONDS), "Server failed to accept connection");
        assertTrue(clientConnectLatch.await(5, TimeUnit.SECONDS), "Client failed to connect");

        // Verification
        assertNotNull(serverSideSocket.get());
        assertNotNull(clientSocket.get());
        assertTrue(serverSideSocket.get().isConnected());
        assertTrue(clientSocket.get().isConnected());

        // Verify connected socket IP
        String clientIP = serverSideSocket.get().getInetAddress().getHostAddress();
        assertTrue(clientIP.equals("127.0.0.1") || clientIP.equals("0:0:0:0:0:0:0:1"),
            "Server-side client IP: " + clientIP);

        // Cleanup
        acceptedClient = serverSideSocket.get();
        clientSocket.get().close();
    }

    @Test
    @DisplayName("TCP Connection Timeout: Timeout when attempting to connect to unreachable server")
    void testTCPConnectionTimeout() {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        CountDownLatch timeoutLatch = new CountDownLatch(1);

        // Attempt to connect to nonexistent server
        clientConnectionThread = Thread.ofVirtual().start(() -> {
            try {
                Socket socket = new Socket();
                // Attempt to connect to unused port (1 second timeout)
                socket.connect(new InetSocketAddress("192.0.2.1", 9999), 1000);
            } catch (IOException e) {
                caughtException.set(e);
                timeoutLatch.countDown();
            }
        });

        // Confirm timeout exception occurred
        await()
            .atMost(3, TimeUnit.SECONDS)
            .until(() -> caughtException.get() != null);

        assertNotNull(caughtException.get());
        assertTrue(caughtException.get() instanceof SocketTimeoutException ||
                   caughtException.get().getMessage().contains("timed out"),
            "Exception type: " + caughtException.get().getClass().getName());
    }

    @Test
    @DisplayName("UDP Broadcast Server Discovery: Client discovers server scenario")
    void testUDPBroadcastDiscovery() throws Exception {
        // Create UDP server socket with dynamic port
        udpServerSocket = new DatagramSocket(0);
        int udpPort = udpServerSocket.getLocalPort();

        AtomicReference<String> discoveredServerIP = new AtomicReference<>();
        CountDownLatch discoveryLatch = new CountDownLatch(1);

        // Server: UDP broadcast response (ServerConnectionController.startUDPResponder() logic)
        udpResponderThread = Thread.ofVirtual().start(() -> {
            try {
                byte[] buf = new byte[512];
                System.err.println("[SERVER] Waiting for UDP broadcast on port " + udpPort + "...");
                
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    udpServerSocket.receive(packet);
                    
                    String received = new String(packet.getData(), 0, packet.getLength());
                    System.err.println("[SERVER] UDP received - " + received + 
                        " from " + packet.getAddress() + ":" + packet.getPort());
                    
                    if ("TETRIS_DISCOVERY".equals(received)) {
                        // Send response
                        byte[] response = "TETRIS_RESPONSE".getBytes();
                        DatagramPacket responsePacket = new DatagramPacket(
                            response, response.length, packet.getAddress(), packet.getPort()
                        );
                        udpServerSocket.send(responsePacket);
                        System.err.println("[SERVER] TETRIS_RESPONSE sent successfully");
                    }
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });

        // Time for server socket to be ready
        Thread.sleep(200);

        // Client: Send UDP broadcast and receive response
        // (ClientConnectionController.broadcastDiscovery() logic)
        clientBroadcastThread = Thread.ofVirtual().start(() -> {
            DatagramSocket clientSocket = null;
            try {
                clientSocket = new DatagramSocket();
                clientSocket.setSoTimeout(3000);
                
                // Broadcast to localhost (test environment)
                InetAddress targetAddress = InetAddress.getByName("127.0.0.1");
                
                // Send broadcast packet
                byte[] sendData = "TETRIS_DISCOVERY".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, targetAddress, udpPort
                );
                clientSocket.send(sendPacket);
                System.err.println("[CLIENT] Broadcast sent to " + targetAddress + ":" + udpPort);
                
                // Wait for response
                byte[] receiveData = new byte[512];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String responseAddress = receivePacket.getAddress().getHostAddress();
                System.err.println("[CLIENT] Response received from " + responseAddress + ": " + response);
                
                if ("TETRIS_RESPONSE".equals(response)) {
                    discoveredServerIP.set(responseAddress);
                    discoveryLatch.countDown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            }
        });

        // Wait for server discovery
        assertTrue(discoveryLatch.await(5, TimeUnit.SECONDS), 
            "Client failed to discover server");

        // Verification
        assertNotNull(discoveredServerIP.get());
        assertTrue(discoveredServerIP.get().equals("127.0.0.1") || 
                   discoveredServerIP.get().equals("0:0:0:0:0:0:0:1"),
            "Discovered server IP: " + discoveredServerIP.get());
    }

    @Test
    @DisplayName("UDP Broadcast Timeout: Timeout when no server responds")
    void testUDPBroadcastTimeout() {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        CountDownLatch timeoutLatch = new CountDownLatch(1);

        // Send broadcast without responding server
        clientBroadcastThread = Thread.ofVirtual().start(() -> {
            DatagramSocket clientSocket = null;
            try {
                clientSocket = new DatagramSocket();
                clientSocket.setSoTimeout(1000); // 1 second timeout
                
                // Send to unused port
                InetAddress targetAddress = InetAddress.getByName("127.0.0.1");
                byte[] sendData = "TETRIS_DISCOVERY".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, targetAddress, 9999
                );
                clientSocket.send(sendPacket);
                
                // Wait for response (timeout expected)
                byte[] receiveData = new byte[512];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
            } catch (IOException e) {
                caughtException.set(e);
                timeoutLatch.countDown();
            } finally {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            }
        });

        // Confirm timeout exception occurred
        await()
            .atMost(3, TimeUnit.SECONDS)
            .until(() -> caughtException.get() instanceof SocketTimeoutException);

        assertNotNull(caughtException.get());
        assertInstanceOf(SocketTimeoutException.class, caughtException.get());
    }

    @Test
    @DisplayName("Complete Connection Scenario: UDP discovery followed by TCP connection")
    void testCompleteConnectionFlow() throws Exception {
        // 1. Start TCP server
        serverSocket = new ServerSocket(0);
        int tcpPort = serverSocket.getLocalPort();

        // 2. Start UDP response server
        udpServerSocket = new DatagramSocket(0);
        int udpPort = udpServerSocket.getLocalPort();

        AtomicReference<Socket> serverSideSocket = new AtomicReference<>();
        AtomicReference<String> discoveredIP = new AtomicReference<>();
        CountDownLatch udpDiscoveryLatch = new CountDownLatch(1);
        CountDownLatch tcpConnectionLatch = new CountDownLatch(1);

        // UDP response thread
        udpResponderThread = Thread.ofVirtual().start(() -> {
            try {
                byte[] buf = new byte[512];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                udpServerSocket.receive(packet);
                
                String received = new String(packet.getData(), 0, packet.getLength());
                if ("TETRIS_DISCOVERY".equals(received)) {
                    byte[] response = "TETRIS_RESPONSE".getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                        response, response.length, packet.getAddress(), packet.getPort()
                    );
                    udpServerSocket.send(responsePacket);
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });

        // TCP connection accept thread
        serverAcceptThread = Thread.ofVirtual().start(() -> {
            try {
                Socket client = serverSocket.accept();
                serverSideSocket.set(client);
                tcpConnectionLatch.countDown();
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });

        Thread.sleep(200); // Wait for server to be ready

        // 3. Client: Discover server via UDP broadcast
        clientBroadcastThread = Thread.ofVirtual().start(() -> {
            try (DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(3000);
                
                InetAddress targetAddress = InetAddress.getByName("127.0.0.1");
                byte[] sendData = "TETRIS_DISCOVERY".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, targetAddress, udpPort
                );
                clientSocket.send(sendPacket);
                
                byte[] receiveData = new byte[512];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if ("TETRIS_RESPONSE".equals(response)) {
                    discoveredIP.set(receivePacket.getAddress().getHostAddress());
                    udpDiscoveryLatch.countDown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Wait for UDP discovery to complete
        assertTrue(udpDiscoveryLatch.await(5, TimeUnit.SECONDS), "UDP server discovery failed");
        assertNotNull(discoveredIP.get());

        // 4. Client: Connect to TCP using discovered IP
        clientConnectionThread = Thread.ofVirtual().start(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(discoveredIP.get(), tcpPort), 3000);
                acceptedClient = socket; // Save for cleanup
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Wait for TCP connection to complete
        assertTrue(tcpConnectionLatch.await(5, TimeUnit.SECONDS), "TCP connection failed");

        // Final verification
        assertNotNull(serverSideSocket.get());
        assertTrue(serverSideSocket.get().isConnected());
        System.err.println("[SUCCESS] Complete connection flow: UDP discovery -> TCP connection");
    }
}
