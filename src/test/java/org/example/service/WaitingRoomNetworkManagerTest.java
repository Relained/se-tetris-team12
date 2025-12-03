package org.example.service;

import javafx.application.Platform;
import org.example.model.GameMode;
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
 * WaitingRoomNetworkManager의 통합 테스트
 * localhost 소켓으로 서버/클라이언트 인스턴스를 생성하여 네트워크 통신을 검증합니다.
 */
@Timeout(15) // 전체 테스트 15초 타임아웃
class WaitingRoomNetworkManagerTest {

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
    private WaitingRoomNetworkManager serverManager;
    private WaitingRoomNetworkManager clientManager;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // 동적 포트로 서버 소켓 생성 (포트 충돌 방지)
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
        
        // 소켓이 정상적으로 연결되었는지 확인
        assertNotNull(serverSideSocket);
        assertTrue(serverSideSocket.isConnected());
        assertTrue(clientSideSocket.isConnected());
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
        Thread.sleep(100);
    }

    @Test
    @DisplayName("서버에서 클라이언트로 게임 모드 변경 메시지 전송 테스트")
    void testGameModeChangeFromServerToClient() {
        AtomicReference<GameMode> receivedMode = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        // 네트워크 매니저 생성
        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> {},
            () -> {},
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> {},
            () -> {},
            mode -> {
                receivedMode.set(mode);
                latch.countDown();
            },
            ready -> {},
            msg -> {},
            diff -> {}
        );

        // 서버에서 게임 모드 변경 전송
        serverManager.sendGameModeChange(GameMode.ITEM);

        // Awaitility로 비동기 검증
        await()
            .atMost(3, TimeUnit.SECONDS)
            .until(() -> receivedMode.get() == GameMode.ITEM);

        assertEquals(GameMode.ITEM, receivedMode.get());
    }

    @Test
    @DisplayName("양방향 Ready 상태 동기화 테스트")
    void testReadyStateSynchronization() {
        AtomicBoolean serverReceivedReady = new AtomicBoolean(false);
        AtomicBoolean clientReceivedReady = new AtomicBoolean(false);

        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> {},
            () -> {},
            mode -> {},
            ready -> serverReceivedReady.set(ready),
            msg -> {},
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> {},
            () -> {},
            mode -> {},
            ready -> clientReceivedReady.set(ready),
            msg -> {},
            diff -> {}
        );

        // 클라이언트 -> 서버로 Ready 전송
        clientManager.sendReadyState(true);
        await()
            .atMost(3, TimeUnit.SECONDS)
            .until(serverReceivedReady::get);

        // 서버 -> 클라이언트로 Ready 전송
        serverManager.sendReadyState(true);
        await()
            .atMost(3, TimeUnit.SECONDS)
            .until(clientReceivedReady::get);

        assertTrue(serverReceivedReady.get());
        assertTrue(clientReceivedReady.get());
    }

    @Test
    @DisplayName("양측 Ready 시 게임 시작 신호 자동 전송 테스트")
    void testGameStartWhenBothReady() {
        AtomicBoolean serverGameStarted = new AtomicBoolean(false);
        AtomicBoolean clientGameStarted = new AtomicBoolean(false);

        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> {},
            () -> serverGameStarted.set(true),
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> {},
            () -> clientGameStarted.set(true),
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        // 클라이언트 먼저 Ready
        clientManager.sendReadyState(true);
        
        // 잠시 대기 (클라이언트 Ready 상태가 서버에 도착할 시간)
        await()
            .pollDelay(100, TimeUnit.MILLISECONDS)
            .atMost(2, TimeUnit.SECONDS)
            .until(() -> true);

        // 서버도 Ready - 이때 자동으로 게임 시작 신호 전송
        serverManager.setServerReady(true);

        // 양측 모두 게임 시작 콜백 호출되어야 함
        await()
            .atMost(5, TimeUnit.SECONDS)
            .until(() -> serverGameStarted.get() && clientGameStarted.get());

        assertTrue(serverGameStarted.get());
        assertTrue(clientGameStarted.get());
    }

    @Test
    @DisplayName("하트비트 메시지 정상 전송 및 연결 유지 테스트")
    void testHeartbeatKeepsConnectionAlive() throws InterruptedException {
        AtomicBoolean disconnected = new AtomicBoolean(false);

        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> disconnected.set(true),
            () -> {},
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> disconnected.set(true),
            () -> {},
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        // 5초 동안 대기 (하트비트는 2초마다 전송, 타임아웃은 3.5초)
        Thread.sleep(5000);

        // 연결이 끊기지 않아야 함
        assertFalse(disconnected.get(), "하트비트가 정상 작동하면 연결이 유지되어야 합니다");
    }

    @Test
    @DisplayName("양방향 채팅 메시지 전송 테스트")
    void testChatMessageExchange() {
        AtomicReference<String> serverReceivedMessage = new AtomicReference<>();
        AtomicReference<String> clientReceivedMessage = new AtomicReference<>();

        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> {},
            () -> {},
            mode -> {},
            ready -> {},
            msg -> serverReceivedMessage.set(msg),
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> {},
            () -> {},
            mode -> {},
            ready -> {},
            msg -> clientReceivedMessage.set(msg),
            diff -> {}
        );

        // 클라이언트 -> 서버 채팅 메시지
        String clientMessage = "Hello from client!";
        clientManager.sendChatMessage(clientMessage);

        await()
            .atMost(3, TimeUnit.SECONDS)
            .until(() -> clientMessage.equals(serverReceivedMessage.get()));

        // 서버 -> 클라이언트 채팅 메시지
        String serverMessage = "Hello from server!";
        serverManager.sendChatMessage(serverMessage);

        await()
            .atMost(3, TimeUnit.SECONDS)
            .until(() -> serverMessage.equals(clientReceivedMessage.get()));

        assertEquals(clientMessage, serverReceivedMessage.get());
        assertEquals(serverMessage, clientReceivedMessage.get());
    }

    @Test
    @DisplayName("서버에서 클라이언트로 난이도 변경 메시지 전송 테스트")
    void testDifficultyChangeFromServerToClient() {
        AtomicInteger receivedDifficulty = new AtomicInteger(-1);

        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> {},
            () -> {},
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> {},
            () -> {},
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> receivedDifficulty.set(diff)
        );

        // 서버에서 난이도 변경 전송
        int expectedDifficulty = 5;
        serverManager.sendDifficultyChange(expectedDifficulty);

        await()
            .atMost(3, TimeUnit.SECONDS)
            .until(() -> receivedDifficulty.get() == expectedDifficulty);

        assertEquals(expectedDifficulty, receivedDifficulty.get());
    }

    @Test
    @DisplayName("다중 게임 모드 변경 순차 전송 테스트")
    void testMultipleGameModeChanges() {
        AtomicReference<GameMode> receivedMode = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(3);

        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> {},
            () -> {},
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> {},
            () -> {},
            mode -> {
                receivedMode.set(mode);
                latch.countDown();
            },
            ready -> {},
            msg -> {},
            diff -> {}
        );

        // 순차적으로 게임 모드 변경
        serverManager.sendGameModeChange(GameMode.NORMAL);
        await().pollDelay(100, TimeUnit.MILLISECONDS).atMost(1, TimeUnit.SECONDS)
            .until(() -> receivedMode.get() == GameMode.NORMAL);

        serverManager.sendGameModeChange(GameMode.ITEM);
        await().pollDelay(100, TimeUnit.MILLISECONDS).atMost(1, TimeUnit.SECONDS)
            .until(() -> receivedMode.get() == GameMode.ITEM);

        serverManager.sendGameModeChange(GameMode.TIME_ATTACK);
        await().pollDelay(100, TimeUnit.MILLISECONDS).atMost(1, TimeUnit.SECONDS)
            .until(() -> receivedMode.get() == GameMode.TIME_ATTACK);

        assertEquals(GameMode.TIME_ATTACK, receivedMode.get());
    }

    @Test
    @DisplayName("원격 IP 주소 확인 테스트")
    void testGetRemoteIPAddress() {
        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> {},
            () -> {},
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> {},
            () -> {},
            mode -> {},
            ready -> {},
            msg -> {},
            diff -> {}
        );

        String serverSeesClientIP = serverManager.getRemoteIPAddress();
        String clientSeesServerIP = clientManager.getRemoteIPAddress();

        // localhost 연결이므로 127.0.0.1 또는 localhost여야 함
        assertTrue(serverSeesClientIP.equals("127.0.0.1") || 
                   serverSeesClientIP.equals("localhost") ||
                   serverSeesClientIP.equals("0:0:0:0:0:0:0:1"), // IPv6 loopback
            "서버가 본 클라이언트 IP: " + serverSeesClientIP);
        
        assertTrue(clientSeesServerIP.equals("127.0.0.1") || 
                   clientSeesServerIP.equals("localhost") ||
                   clientSeesServerIP.equals("0:0:0:0:0:0:0:1"),
            "클라이언트가 본 서버 IP: " + clientSeesServerIP);
    }

    @Test
    @DisplayName("복합 시나리오: 게임 모드 변경 + Ready + 게임 시작")
    void testCompleteWaitingRoomFlow() {
        AtomicReference<GameMode> clientReceivedMode = new AtomicReference<>();
        AtomicBoolean serverReceivedReady = new AtomicBoolean(false);
        AtomicBoolean serverGameStarted = new AtomicBoolean(false);
        AtomicBoolean clientGameStarted = new AtomicBoolean(false);

        serverManager = new WaitingRoomNetworkManager(
            serverSideSocket,
            true,
            () -> {},
            () -> serverGameStarted.set(true),
            mode -> {},
            ready -> serverReceivedReady.set(ready),
            msg -> {},
            diff -> {}
        );

        clientManager = new WaitingRoomNetworkManager(
            clientSideSocket,
            false,
            () -> {},
            () -> clientGameStarted.set(true),
            clientReceivedMode::set,
            ready -> {},
            msg -> {},
            diff -> {}
        );

        // 1. 서버가 게임 모드 설정
        serverManager.sendGameModeChange(GameMode.ITEM);
        await().atMost(2, TimeUnit.SECONDS)
            .until(() -> clientReceivedMode.get() == GameMode.ITEM);

        // 2. 클라이언트가 Ready
        clientManager.sendReadyState(true);
        await().atMost(2, TimeUnit.SECONDS)
            .until(serverReceivedReady::get);

        // 3. 서버도 Ready -> 자동으로 게임 시작
        serverManager.setServerReady(true);
        await().atMost(3, TimeUnit.SECONDS)
            .until(() -> serverGameStarted.get() && clientGameStarted.get());

        // 최종 검증
        assertEquals(GameMode.ITEM, clientReceivedMode.get());
        assertTrue(serverReceivedReady.get());
        assertTrue(serverGameStarted.get());
        assertTrue(clientGameStarted.get());
    }
}
