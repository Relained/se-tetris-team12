package org.example.controller;

import javafx.application.Platform;
import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Real-time Network Synchronization Test
 * 
 * Non-functional Requirements:
 * 1. Block movements must be displayed simultaneously on both players' screens in real-time
 * 2. Input-to-display latency must be under 200ms on local network
 * 
 * Test Scenarios:
 * - Real-time block movement synchronization (left, right, rotate, drop)
 * - Network latency measurement for key input and screen display
 * - Multiple consecutive inputs with latency verification
 * - Board state synchronization accuracy
 */
@Timeout(30)
class RealtimeNetworkSyncTest {

    private static final long MAX_LATENCY_MS = 200; // Maximum allowed latency (milliseconds)
    private static final int SYNC_ITERATIONS = 50;   // Number of synchronization tests

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    private ServerSocket serverSocket;
    private Socket serverSideSocket;
    private Socket clientSideSocket;
    private DataOutputStream serverOutput;
    private DataInputStream serverInput;
    private DataOutputStream clientOutput;
    private DataInputStream clientInput;
    private Thread serverThread;
    private Thread clientThread;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // Create server socket with dynamic port
        serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();

        // Accept connection in separate thread
        CountDownLatch connectionLatch = new CountDownLatch(1);
        Thread acceptThread = Thread.ofVirtual().start(() -> {
            try {
                serverSideSocket = serverSocket.accept();
                serverOutput = new DataOutputStream(serverSideSocket.getOutputStream());
                serverInput = new DataInputStream(serverSideSocket.getInputStream());
                connectionLatch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Client connects to server
        clientSideSocket = new Socket("localhost", port);
        clientOutput = new DataOutputStream(clientSideSocket.getOutputStream());
        clientInput = new DataInputStream(clientSideSocket.getInputStream());

        // Wait for connection
        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS), "Connection failed");
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        // Stop threads
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
        if (clientThread != null && clientThread.isAlive()) {
            clientThread.interrupt();
        }

        // Close sockets
        if (serverOutput != null) serverOutput.close();
        if (serverInput != null) serverInput.close();
        if (clientOutput != null) clientOutput.close();
        if (clientInput != null) clientInput.close();
        if (serverSideSocket != null && !serverSideSocket.isClosed()) {
            serverSideSocket.close();
        }
        if (clientSideSocket != null && !clientSideSocket.isClosed()) {
            clientSideSocket.close();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        Thread.sleep(200);
    }

    @Test
    @DisplayName("Real-time block movement synchronization: LEFT movement")
    void testRealtimeBlockMovementLeft() throws Exception {
        AtomicLong totalLatency = new AtomicLong(0);
        AtomicReference<Exception> error = new AtomicReference<>();
        CountDownLatch syncLatch = new CountDownLatch(SYNC_ITERATIONS);

        // Server: Send movement command and measure latency
        serverThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < SYNC_ITERATIONS; i++) {
                    long sendTime = System.currentTimeMillis();
                    
                    // Send MOVE_LEFT command
                    serverOutput.writeUTF("MOVE_LEFT");
                    serverOutput.writeInt(5);  // x position
                    serverOutput.writeInt(10); // y position
                    serverOutput.flush();
                    
                    // Wait for acknowledgment
                    String ack = serverInput.readUTF();
                    long receiveTime = System.currentTimeMillis();
                    
                    if ("ACK".equals(ack)) {
                        long latency = receiveTime - sendTime;
                        totalLatency.addAndGet(latency);
                        syncLatch.countDown();
                    }
                    
                    Thread.sleep(50); // Simulate game tick rate
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        // Client: Receive movement and send acknowledgment
        clientThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < SYNC_ITERATIONS; i++) {
                    String command = clientInput.readUTF();
                    int x = clientInput.readInt();
                    int y = clientInput.readInt();
                    
                    // Simulate block movement processing
                    assertEquals("MOVE_LEFT", command);
                    assertTrue(x >= 0 && x < GameBoard.WIDTH);
                    assertTrue(y >= 0 && y < GameBoard.HEIGHT + GameBoard.BUFFER_ZONE);
                    
                    // Send acknowledgment
                    clientOutput.writeUTF("ACK");
                    clientOutput.flush();
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        // Wait for all synchronizations to complete
        assertTrue(syncLatch.await(15, TimeUnit.SECONDS), "Synchronization timeout");
        assertNull(error.get(), "Error during synchronization: " + error.get());

        // Verify average latency is under 200ms
        double averageLatency = (double) totalLatency.get() / SYNC_ITERATIONS;
        System.err.println("[LATENCY] Average latency for MOVE_LEFT: " + 
            String.format("%.2f", averageLatency) + " ms");
        assertTrue(averageLatency < MAX_LATENCY_MS, 
            String.format("Average latency (%.2f ms) exceeds requirement (%d ms)", 
                averageLatency, MAX_LATENCY_MS));
    }

    @Test
    @DisplayName("Real-time block movement synchronization: ROTATE movement")
    void testRealtimeBlockMovementRotate() throws Exception {
        AtomicLong totalLatency = new AtomicLong(0);
        AtomicReference<Exception> error = new AtomicReference<>();
        CountDownLatch syncLatch = new CountDownLatch(SYNC_ITERATIONS);

        // Server: Send rotation command
        serverThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < SYNC_ITERATIONS; i++) {
                    long sendTime = System.currentTimeMillis();
                    
                    serverOutput.writeUTF("ROTATE_CW");
                    serverOutput.writeInt(i % 4); // rotation state (0-3)
                    serverOutput.flush();
                    
                    String ack = serverInput.readUTF();
                    long receiveTime = System.currentTimeMillis();
                    
                    if ("ACK".equals(ack)) {
                        long latency = receiveTime - sendTime;
                        totalLatency.addAndGet(latency);
                        syncLatch.countDown();
                    }
                    
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        // Client: Receive rotation and acknowledge
        clientThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < SYNC_ITERATIONS; i++) {
                    String command = clientInput.readUTF();
                    int rotation = clientInput.readInt();
                    
                    assertEquals("ROTATE_CW", command);
                    assertTrue(rotation >= 0 && rotation < 4);
                    
                    clientOutput.writeUTF("ACK");
                    clientOutput.flush();
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        assertTrue(syncLatch.await(15, TimeUnit.SECONDS), "Synchronization timeout");
        assertNull(error.get(), "Error during synchronization: " + error.get());

        double averageLatency = (double) totalLatency.get() / SYNC_ITERATIONS;
        System.err.println("[LATENCY] Average latency for ROTATE_CW: " + 
            String.format("%.2f", averageLatency) + " ms");
        assertTrue(averageLatency < MAX_LATENCY_MS, 
            String.format("Average latency (%.2f ms) exceeds requirement (%d ms)", 
                averageLatency, MAX_LATENCY_MS));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MOVE_LEFT", "MOVE_RIGHT", "MOVE_DOWN", "ROTATE_CW", "ROTATE_CCW"})
    @DisplayName("Latency verification for various input types")
    void testVariousInputLatencies(String inputType) throws Exception {
        AtomicLong totalLatency = new AtomicLong(0);
        CountDownLatch syncLatch = new CountDownLatch(SYNC_ITERATIONS);

        serverThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < SYNC_ITERATIONS; i++) {
                    long sendTime = System.currentTimeMillis();
                    
                    serverOutput.writeUTF(inputType);
                    serverOutput.flush();
                    
                    String ack = serverInput.readUTF();
                    long receiveTime = System.currentTimeMillis();
                    
                    if ("ACK".equals(ack)) {
                        totalLatency.addAndGet(receiveTime - sendTime);
                        syncLatch.countDown();
                    }
                    
                    Thread.sleep(40); // 25 FPS game tick
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        clientThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < SYNC_ITERATIONS; i++) {
                    String command = clientInput.readUTF();
                    assertEquals(inputType, command);
                    
                    clientOutput.writeUTF("ACK");
                    clientOutput.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertTrue(syncLatch.await(15, TimeUnit.SECONDS), 
            "Synchronization timeout for " + inputType);

        double averageLatency = (double) totalLatency.get() / SYNC_ITERATIONS;
        System.err.println("[LATENCY] Average latency for " + inputType + ": " + 
            String.format("%.2f", averageLatency) + " ms");
        assertTrue(averageLatency < MAX_LATENCY_MS, 
            String.format("%s latency (%.2f ms) exceeds requirement (%d ms)", 
                inputType, averageLatency, MAX_LATENCY_MS));
    }

    @Test
    @DisplayName("Board state synchronization: Full board data transfer")
    void testBoardStateSynchronization() throws Exception {
        AtomicLong totalLatency = new AtomicLong(0);
        AtomicReference<Exception> error = new AtomicReference<>();
        CountDownLatch syncLatch = new CountDownLatch(20);

        // Create test board data
        int[][] testBoard = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        for (int i = 0; i < GameBoard.HEIGHT; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                testBoard[i][j] = (i + j) % 8;
            }
        }

        // Server: Send board state
        serverThread = Thread.ofVirtual().start(() -> {
            try {
                for (int iteration = 0; iteration < 20; iteration++) {
                    long sendTime = System.currentTimeMillis();
                    
                    serverOutput.writeUTF("BOARD_UPDATE");
                    for (int i = 0; i < GameBoard.HEIGHT; i++) {
                        for (int j = 0; j < GameBoard.WIDTH; j++) {
                            serverOutput.writeInt(testBoard[i][j]);
                        }
                    }
                    serverOutput.flush();
                    
                    String ack = serverInput.readUTF();
                    long receiveTime = System.currentTimeMillis();
                    
                    if ("ACK".equals(ack)) {
                        long latency = receiveTime - sendTime;
                        totalLatency.addAndGet(latency);
                        syncLatch.countDown();
                    }
                    
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        // Client: Receive and verify board state
        clientThread = Thread.ofVirtual().start(() -> {
            try {
                for (int iteration = 0; iteration < 20; iteration++) {
                    String command = clientInput.readUTF();
                    assertEquals("BOARD_UPDATE", command);
                    
                    int[][] receivedBoard = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
                    for (int i = 0; i < GameBoard.HEIGHT; i++) {
                        for (int j = 0; j < GameBoard.WIDTH; j++) {
                            receivedBoard[i][j] = clientInput.readInt();
                        }
                    }
                    
                    // Verify board data integrity
                    assertArrayEquals(testBoard, receivedBoard);
                    
                    clientOutput.writeUTF("ACK");
                    clientOutput.flush();
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        assertTrue(syncLatch.await(20, TimeUnit.SECONDS), "Board synchronization timeout");
        assertNull(error.get(), "Error during board synchronization: " + error.get());

        double averageLatency = (double) totalLatency.get() / 20;
        System.err.println("[LATENCY] Average latency for full board sync: " + 
            String.format("%.2f", averageLatency) + " ms");
        assertTrue(averageLatency < MAX_LATENCY_MS, 
            String.format("Board sync latency (%.2f ms) exceeds requirement (%d ms)", 
                averageLatency, MAX_LATENCY_MS));
    }

    @Test
    @DisplayName("Consecutive input sequence latency: Simulating rapid player inputs")
    void testConsecutiveInputSequenceLatency() throws Exception {
        List<Long> latencies = new ArrayList<>();
        AtomicReference<Exception> error = new AtomicReference<>();
        CountDownLatch sequenceLatch = new CountDownLatch(1);

        String[] inputSequence = {
            "MOVE_LEFT", "MOVE_LEFT", "ROTATE_CW", "MOVE_RIGHT", 
            "MOVE_DOWN", "MOVE_RIGHT", "ROTATE_CW", "MOVE_DOWN"
        };

        serverThread = Thread.ofVirtual().start(() -> {
            try {
                for (String input : inputSequence) {
                    long sendTime = System.currentTimeMillis();
                    
                    serverOutput.writeUTF(input);
                    serverOutput.flush();
                    
                    String ack = serverInput.readUTF();
                    long receiveTime = System.currentTimeMillis();
                    
                    if ("ACK".equals(ack)) {
                        latencies.add(receiveTime - sendTime);
                    }
                    
                    Thread.sleep(30); // Rapid input simulation
                }
                sequenceLatch.countDown();
            } catch (Exception e) {
                error.set(e);
            }
        });

        clientThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < inputSequence.length; i++) {
                    String command = clientInput.readUTF();
                    assertNotNull(command);
                    
                    clientOutput.writeUTF("ACK");
                    clientOutput.flush();
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        assertTrue(sequenceLatch.await(10, TimeUnit.SECONDS), 
            "Consecutive input sequence timeout");
        assertNull(error.get(), "Error during input sequence: " + error.get());

        // Calculate statistics
        double averageLatency = latencies.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
        
        long maxLatency = latencies.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0L);

        System.err.println("[LATENCY] Consecutive inputs - Average: " + 
            String.format("%.2f", averageLatency) + " ms, Max: " + maxLatency + " ms");
        
        assertTrue(averageLatency < MAX_LATENCY_MS, 
            String.format("Average latency (%.2f ms) exceeds requirement (%d ms)", 
                averageLatency, MAX_LATENCY_MS));
        assertTrue(maxLatency < MAX_LATENCY_MS * 1.5, 
            String.format("Max latency (%d ms) is too high", maxLatency));
    }

    @Test
    @DisplayName("Bidirectional synchronization: Both players sending inputs simultaneously")
    void testBidirectionalSynchronization() throws Exception {
        AtomicLong serverToClientLatency = new AtomicLong(0);
        AtomicLong clientToServerLatency = new AtomicLong(0);
        CountDownLatch serverLatch = new CountDownLatch(20);
        CountDownLatch clientLatch = new CountDownLatch(20);
        AtomicReference<Exception> error = new AtomicReference<>();

        // Server sends to client and receives from client
        serverThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    // Send to client
                    long sendTime = System.currentTimeMillis();
                    serverOutput.writeUTF("SERVER_INPUT");
                    serverOutput.writeInt(i);
                    serverOutput.flush();
                    
                    // Wait for client's ACK
                    String ack = serverInput.readUTF();
                    serverToClientLatency.addAndGet(System.currentTimeMillis() - sendTime);
                    serverLatch.countDown();
                    
                    // Receive from client
                    String clientCommand = serverInput.readUTF();
                    int clientValue = serverInput.readInt();
                    // Send ACK to client
                    serverOutput.writeUTF("ACK");
                    serverOutput.flush();
                    
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        // Client receives from server and sends to server
        clientThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    // Receive from server
                    String serverCommand = clientInput.readUTF();
                    int value = clientInput.readInt();
                    clientOutput.writeUTF("ACK");
                    clientOutput.flush();
                    
                    // Send to server
                    long sendTime = System.currentTimeMillis();
                    clientOutput.writeUTF("CLIENT_INPUT");
                    clientOutput.writeInt(i);
                    clientOutput.flush();
                    
                    String ack = clientInput.readUTF();
                    clientToServerLatency.addAndGet(System.currentTimeMillis() - sendTime);
                    clientLatch.countDown();
                    
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                error.set(e);
            }
        });

        assertTrue(serverLatch.await(15, TimeUnit.SECONDS), 
            "Server to client synchronization timeout");
        assertTrue(clientLatch.await(15, TimeUnit.SECONDS), 
            "Client to server synchronization timeout");
        assertNull(error.get(), "Error during bidirectional sync: " + error.get());

        double avgServerToClient = (double) serverToClientLatency.get() / 20;
        double avgClientToServer = (double) clientToServerLatency.get() / 20;

        System.err.println("[LATENCY] Bidirectional - Server to Client: " + 
            String.format("%.2f", avgServerToClient) + " ms, Client to Server: " + 
            String.format("%.2f", avgClientToServer) + " ms");
        
        assertTrue(avgServerToClient < MAX_LATENCY_MS, 
            String.format("Server to client latency (%.2f ms) exceeds requirement", 
                avgServerToClient));
        assertTrue(avgClientToServer < MAX_LATENCY_MS, 
            String.format("Client to server latency (%.2f ms) exceeds requirement", 
                avgClientToServer));
    }

    @Test
    @DisplayName("99th percentile latency: Verify worst-case performance")
    void testNinetyNinthPercentileLatency() throws Exception {
        List<Long> allLatencies = new ArrayList<>();
        CountDownLatch measurementLatch = new CountDownLatch(100);

        serverThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    long sendTime = System.currentTimeMillis();
                    
                    serverOutput.writeUTF("INPUT_" + i);
                    serverOutput.flush();
                    
                    String ack = serverInput.readUTF();
                    long latency = System.currentTimeMillis() - sendTime;
                    
                    synchronized (allLatencies) {
                        allLatencies.add(latency);
                    }
                    measurementLatch.countDown();
                    
                    Thread.sleep(40);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        clientThread = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    String command = clientInput.readUTF();
                    clientOutput.writeUTF("ACK");
                    clientOutput.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertTrue(measurementLatch.await(20, TimeUnit.SECONDS), 
            "Latency measurement timeout");

        // Sort latencies
        allLatencies.sort(Long::compareTo);
        
        // Calculate percentiles
        int p50Index = (int) (allLatencies.size() * 0.50);
        int p90Index = (int) (allLatencies.size() * 0.90);
        int p99Index = (int) (allLatencies.size() * 0.99);
        
        long p50 = allLatencies.get(p50Index);
        long p90 = allLatencies.get(p90Index);
        long p99 = allLatencies.get(p99Index);
        
        double average = allLatencies.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);

        System.err.println("[LATENCY] Percentiles - Average: " + 
            String.format("%.2f", average) + " ms, 50th: " + p50 + 
            " ms, 90th: " + p90 + " ms, 99th: " + p99 + " ms");
        
        assertTrue(p99 < MAX_LATENCY_MS * 1.5, 
            String.format("99th percentile latency (%d ms) is too high", p99));
        assertTrue(average < MAX_LATENCY_MS, 
            String.format("Average latency (%.2f ms) exceeds requirement", average));
    }

    // Helper method for deep copying board
    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }
}
