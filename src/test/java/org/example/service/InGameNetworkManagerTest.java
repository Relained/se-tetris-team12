package org.example.service;

import javafx.application.Platform;
import org.example.model.GameBoard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InGameNetworkManager의 통합 테스트
 * UDP 보드 동기화와 TCP 게임 이벤트 전송을 검증합니다.
 */
@Timeout(20) // 전체 테스트 20초 타임아웃
class InGameNetworkManagerTest {

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // JavaFX Platform 초기화 (이미 초기화되어 있으면 건너뛰기)
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화되어 있음
        }
    }

    private ServerSocket serverSocket;
    private Socket serverSideSocket;
    private Socket clientSideSocket;
    private InGameNetworkManager serverManager;
    private InGameNetworkManager clientManager;

    // 테스트용 보드 데이터
    private int[][] serverBoardData;
    private int[][] clientBoardData;
    private int serverScore = 0;
    private int clientScore = 0;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // 동적 포트로 서버 소켓 생성
        serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();

        // 클라이언트 연결을 별도 스레드에서 수락
        CountDownLatch connectionLatch = new CountDownLatch(1);
        Thread acceptThread = Thread.ofVirtual().start(() -> {
            try {
                serverSideSocket = serverSocket.accept();
                connectionLatch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 클라이언트 소켓 연결
        clientSideSocket = new Socket("localhost", port);
        
        // 연결 완료 대기
        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS), "서버-클라이언트 연결 실패");

        // 테스트용 보드 데이터 초기화
        serverBoardData = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        clientBoardData = new int[GameBoard.HEIGHT][GameBoard.WIDTH];

        // 간단한 패턴으로 초기화 (구분 가능하도록)
        for (int i = 0; i < GameBoard.HEIGHT; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                serverBoardData[i][j] = (i + j) % 8; // 서버 보드: 0-7 패턴
                clientBoardData[i][j] = (i * j) % 8; // 클라이언트 보드: 다른 패턴
            }
        }
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        // 네트워크 매니저 정리
        if (serverManager != null) {
            serverManager.disconnect();
        }
        if (clientManager != null) {
            clientManager.disconnect();
        }

        // 소켓 정리
        if (serverSideSocket != null && !serverSideSocket.isClosed()) {
            serverSideSocket.close();
        }
        if (clientSideSocket != null && !clientSideSocket.isClosed()) {
            clientSideSocket.close();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        // 스레드가 정리될 시간 부여
        Thread.sleep(300);
    }

    @Test
    @DisplayName("UDP Board Synchronization: Server and client exchange board data")
    void testBoardSynchronization() throws InterruptedException {
        AtomicReference<int[][]> serverReceivedBoard = new AtomicReference<>();
        AtomicReference<int[][]> clientReceivedBoard = new AtomicReference<>();
        CountDownLatch serverReceiveLatch = new CountDownLatch(1);
        CountDownLatch clientReceiveLatch = new CountDownLatch(1);

        // Create network managers
        serverManager = new InGameNetworkManager(
            serverSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> {
                serverReceivedBoard.set(deepCopy(board));
                serverReceiveLatch.countDown();
            },
            () -> serverBoardData,
            () -> serverScore
        );

        clientManager = new InGameNetworkManager(
            clientSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> {
                clientReceivedBoard.set(deepCopy(board));
                clientReceiveLatch.countDown();
            },
            () -> clientBoardData,
            () -> clientScore
        );

        // Wait for UDP board synchronization to start (40ms tick)
        Thread.sleep(500);

        // Verify board data reception
        assertTrue(serverReceiveLatch.await(3, TimeUnit.SECONDS), 
            "Server did not receive client board");
        assertTrue(clientReceiveLatch.await(3, TimeUnit.SECONDS), 
            "Client did not receive server board");

        // Verify received boards
        assertNotNull(serverReceivedBoard.get());
        assertNotNull(clientReceivedBoard.get());

        // Server should receive client's board
        assertArrayEquals(clientBoardData, serverReceivedBoard.get());
        
        // Client should receive server's board
        assertArrayEquals(serverBoardData, clientReceivedBoard.get());
    }

    @Test
    @DisplayName("TCP Line Clear: Adder Board transmission and reception")
    void testAdderBoardTransmission() throws InterruptedException {
        AtomicReference<int[][]> receivedAdderBoard = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        serverManager = new InGameNetworkManager(
            serverSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> {},
            () -> serverBoardData,
            () -> serverScore
        );

        clientManager = new InGameNetworkManager(
            clientSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {
                receivedAdderBoard.set(deepCopy(adderBoard));
                latch.countDown();
            },
            board -> {},
            () -> clientBoardData,
            () -> clientScore
        );

        // Send Adder Board from server to client (added board when line is cleared)
        int[][] adderBoard = new int[2][GameBoard.WIDTH]; // Add 2 lines
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                adderBoard[i][j] = (i + 1) * 10 + j; // Test pattern
            }
        }

        serverManager.sendAdderBoard(adderBoard);

        // Wait for reception with robust timeout
        boolean received = latch.await(5, TimeUnit.SECONDS);
        assertTrue(received, "Adder board should be received within timeout");

        // Verify
        assertNotNull(receivedAdderBoard.get());
        assertEquals(2, receivedAdderBoard.get().length);
        assertEquals(GameBoard.WIDTH, receivedAdderBoard.get()[0].length);
        assertArrayEquals(adderBoard, receivedAdderBoard.get());
    }

    @Test
    @DisplayName("TCP Game Over: Send score and timeover flag")
    void testGameOverTransmission() throws InterruptedException {
        AtomicInteger receivedScore = new AtomicInteger(-1);
        AtomicBoolean receivedTimeover = new AtomicBoolean(false);
        CountDownLatch gameOverLatch = new CountDownLatch(1);

        serverManager = new InGameNetworkManager(
            serverSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {
                receivedScore.set(score);
                receivedTimeover.set(timeover);
                gameOverLatch.countDown();
            },
            adderBoard -> {},
            board -> {},
            () -> serverBoardData,
            () -> serverScore
        );

        clientManager = new InGameNetworkManager(
            clientSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> {},
            () -> clientBoardData,
            () -> clientScore
        );

        // Wait for network managers to fully initialize
        Thread.sleep(500);

        // Send game over signal from client
        int finalScore = 1234;
        boolean isTimeover = true;
        clientManager.sendGameOverAndShutDown(finalScore, isTimeover);

        // Wait for server to receive with robust timeout
        boolean received = gameOverLatch.await(10, TimeUnit.SECONDS);
        assertTrue(received, "Game over signal should be received within timeout");

        // Verify
        assertEquals(finalScore, receivedScore.get());
        assertTrue(receivedTimeover.get());
    }

    @Test
    @DisplayName("Return to waiting room signal transmission and reception")
    void testGoWaitingRoomSignal() throws InterruptedException {
        AtomicBoolean serverGoWaitingRoom = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        serverManager = new InGameNetworkManager(
            serverSideSocket,
            () -> {},
            () -> {
                serverGoWaitingRoom.set(true);
                latch.countDown();
            },
            (score, timeover) -> {},
            adderBoard -> {},
            board -> {},
            () -> serverBoardData,
            () -> serverScore
        );

        clientManager = new InGameNetworkManager(
            clientSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> {},
            () -> clientBoardData,
            () -> clientScore
        );

        // Wait for network managers to fully initialize
        Thread.sleep(500);

        // Send waiting room return signal from client
        clientManager.sendGoWaitingRoomAndShutDown();

        // Wait for server to receive with robust timeout
        boolean received = latch.await(10, TimeUnit.SECONDS);
        assertTrue(received, "Waiting room signal should be received within timeout");
        assertTrue(serverGoWaitingRoom.get());
    }

    @Test
    @DisplayName("Continuous board synchronization update test")
    void testContinuousBoardUpdates() throws InterruptedException {
        AtomicInteger clientUpdateCount = new AtomicInteger(0);
        AtomicInteger serverUpdateCount = new AtomicInteger(0);

        serverManager = new InGameNetworkManager(
            serverSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> serverUpdateCount.incrementAndGet(),
            () -> serverBoardData,
            () -> serverScore
        );

        clientManager = new InGameNetworkManager(
            clientSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> clientUpdateCount.incrementAndGet(),
            () -> clientBoardData,
            () -> clientScore
        );

        // 1초 동안 대기 (40ms 틱이므로 약 25개의 업데이트 예상)
        Thread.sleep(1000);

        // 보드 업데이트가 여러 번 발생했는지 확인
        assertTrue(clientUpdateCount.get() >= 10, 
            "클라이언트 업데이트 횟수: " + clientUpdateCount.get());
        assertTrue(serverUpdateCount.get() >= 10, 
            "서버 업데이트 횟수: " + serverUpdateCount.get());

        System.out.println("서버 업데이트 횟수: " + serverUpdateCount.get());
        System.out.println("클라이언트 업데이트 횟수: " + clientUpdateCount.get());
    }

    @Test
    @DisplayName("Board data change synchronization test")
    void testBoardDataChangeSync() throws InterruptedException {
        AtomicReference<int[][]> clientReceivedBoard = new AtomicReference<>();

        serverManager = new InGameNetworkManager(
            serverSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> {},
            () -> serverBoardData,
            () -> serverScore
        );

        clientManager = new InGameNetworkManager(
            clientSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> clientReceivedBoard.set(deepCopy(board)),
            () -> clientBoardData,
            () -> clientScore
        );

        // Wait for initial board reception
        Thread.sleep(200);

        // Change server board data
        for (int i = 0; i < GameBoard.HEIGHT; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                serverBoardData[i][j] = 99; // Change all to 99
            }
        }

        // Wait for changed board reception with robust timeout
        await()
            .atMost(3, TimeUnit.SECONDS)
            .pollDelay(10, TimeUnit.MILLISECONDS)
            .until(() -> {
                int[][] board = clientReceivedBoard.get();
                return board != null && board[0][0] == 99;
            });

        // Verify: all cells should be 99
        int[][] receivedBoard = clientReceivedBoard.get();
        for (int i = 0; i < GameBoard.HEIGHT; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                assertEquals(99, receivedBoard[i][j],
                    "Value at position [" + i + "][" + j + "] does not match");
            }
        }
    }

    @Test
    @DisplayName("Complex scenario: Board sync + Adder Board + Game Over")
    void testCompleteGameFlow() throws InterruptedException {
        AtomicReference<int[][]> clientReceivedBoard = new AtomicReference<>();
        AtomicReference<int[][]> serverReceivedAdderBoard = new AtomicReference<>();
        AtomicInteger clientReceivedScore = new AtomicInteger(-1);
        AtomicBoolean clientReceivedTimeover = new AtomicBoolean(false);

        serverManager = new InGameNetworkManager(
            serverSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {
                clientReceivedScore.set(score);
                clientReceivedTimeover.set(timeover);
            },
            adderBoard -> serverReceivedAdderBoard.set(deepCopy(adderBoard)),
            board -> {},
            () -> serverBoardData,
            () -> 5000 // 서버 점수
        );

        clientManager = new InGameNetworkManager(
            clientSideSocket,
            () -> {},
            () -> {},
            (score, timeover) -> {},
            adderBoard -> {},
            board -> clientReceivedBoard.set(deepCopy(board)),
            () -> clientBoardData,
            () -> 3000 // 클라이언트 점수
        );

        // 1. 보드 동기화 확인
        await()
            .atMost(3, TimeUnit.SECONDS)
            .pollInterval(100, TimeUnit.MILLISECONDS)
            .until(() -> clientReceivedBoard.get() != null);
        
        assertNotNull(clientReceivedBoard.get(), "보드 동기화가 되지 않았습니다");

        // 2. 클라이언트에서 Adder Board 전송
        int[][] adderBoard = new int[3][GameBoard.WIDTH];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                adderBoard[i][j] = 100 + i;
            }
        }
        clientManager.sendAdderBoard(adderBoard);

        await()
            .atMost(3, TimeUnit.SECONDS)
            .pollInterval(100, TimeUnit.MILLISECONDS)
            .until(() -> serverReceivedAdderBoard.get() != null);

        assertArrayEquals(adderBoard, serverReceivedAdderBoard.get());

        // 3. 클라이언트 게임 오버
        clientManager.sendGameOverAndShutDown(3000, false);

        await()
            .atMost(3, TimeUnit.SECONDS)
            .pollInterval(100, TimeUnit.MILLISECONDS)
            .until(() -> clientReceivedScore.get() != -1);

        assertEquals(3000, clientReceivedScore.get());
        assertFalse(clientReceivedTimeover.get());

        System.out.println("복합 시나리오 테스트 성공: 보드 동기화 → Adder Board → 게임 오버");
    }

    /**
     * 2차원 배열 깊은 복사 (테스트용 헬퍼 메서드)
     */
    private int[][] deepCopy(int[][] source) {
        if (source == null) return null;
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }
}
