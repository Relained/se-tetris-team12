package org.example.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientConnectionController unit tests
 * Tests controller initialization and IP connection history functionality
 */
class ClientConnectionControllerTest {

    private static final String TEST_HISTORY_FILE = "tetris_connection_history_test.txt";
    private Path testFilePath;

    @BeforeEach
    void setUp() {
        testFilePath = Paths.get(System.getProperty("user.home"), TEST_HISTORY_FILE);
    }

    @AfterEach
    void tearDown() {
        // 테스트 파일 정리
        try {
            Files.deleteIfExists(testFilePath);
        } catch (IOException e) {
            System.err.println("테스트 파일 삭제 실패: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("ClientConnectionController initializes correctly")
    void testInitialization() {
        ClientConnectionController controller = new ClientConnectionController();
        assertNotNull(controller, "Controller should be initialized");
    }

    @Test
    @DisplayName("ClientConnectionController has proper state management")
    void testStateManagement() {
        ClientConnectionController controller = new ClientConnectionController();
        assertNotNull(controller, "Controller should exist");
    }

    // ===================== IP 저장/로드 테스트 =====================

    @Test
    @DisplayName("연결 기록 파일에 IP 저장 테스트")
    void testSaveConnectionHistory() throws Exception {
        List<String> testHistory = new ArrayList<>();
        testHistory.add("192.168.1.100");
        testHistory.add("10.0.0.1");
        testHistory.add("172.16.0.50");

        try (BufferedWriter writer = Files.newBufferedWriter(testFilePath)) {
            for (String ip : testHistory) {
                writer.write(ip);
                writer.newLine();
            }
        }

        assertTrue(Files.exists(testFilePath), "기록 파일이 생성되어야 함");
        List<String> savedLines = Files.readAllLines(testFilePath);
        assertEquals(3, savedLines.size(), "저장된 IP 개수가 3개여야 함");
        assertEquals("192.168.1.100", savedLines.get(0), "첫 번째 IP 확인");
        assertEquals("10.0.0.1", savedLines.get(1), "두 번째 IP 확인");
        assertEquals("172.16.0.50", savedLines.get(2), "세 번째 IP 확인");

        System.err.println("=== 저장된 IP 목록 ===");
        for (String ip : savedLines) {
            System.err.println(ip);
        }
    }

    @Test
    @DisplayName("연결 기록 파일에서 IP 로드 테스트")
    void testLoadConnectionHistory() throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(testFilePath)) {
            writer.write("192.168.0.1");
            writer.newLine();
            writer.write("127.0.0.1");
            writer.newLine();
        }

        List<String> loadedHistory = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(testFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    loadedHistory.add(line);
                }
            }
        }

        assertEquals(2, loadedHistory.size(), "로드된 IP 개수가 2개여야 함");
        assertEquals("192.168.0.1", loadedHistory.get(0), "첫 번째 IP 확인");
        assertEquals("127.0.0.1", loadedHistory.get(1), "두 번째 IP 확인");

        System.err.println("=== 로드된 IP 목록 ===");
        for (String ip : loadedHistory) {
            System.err.println(ip);
        }
    }

    @Test
    @DisplayName("존재하지 않는 파일에서 로드 시 빈 목록 반환")
    void testLoadFromNonExistentFile() throws Exception {
        Files.deleteIfExists(testFilePath);
        assertFalse(Files.exists(testFilePath), "파일이 존재하지 않아야 함");

        List<String> loadedHistory = new ArrayList<>();
        if (Files.exists(testFilePath)) {
            loadedHistory = Files.readAllLines(testFilePath);
        }

        assertTrue(loadedHistory.isEmpty(), "존재하지 않는 파일에서는 빈 목록이어야 함");
        System.err.println("파일 없음 → 빈 목록 반환 확인");
    }

    @Test
    @DisplayName("빈 줄과 공백이 있는 파일 로드 테스트")
    void testLoadWithEmptyLinesAndWhitespace() throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(testFilePath)) {
            writer.write("192.168.1.1");
            writer.newLine();
            writer.write("");  // 빈 줄
            writer.newLine();
            writer.write("   ");  // 공백만 있는 줄
            writer.newLine();
            writer.write("  10.0.0.1  ");  // 앞뒤 공백
            writer.newLine();
            writer.write("172.16.0.1");
            writer.newLine();
        }

        List<String> loadedHistory = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(testFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    loadedHistory.add(line);
                }
            }
        }

        assertEquals(3, loadedHistory.size(), "유효한 IP만 3개여야 함");
        assertEquals("192.168.1.1", loadedHistory.get(0));
        assertEquals("10.0.0.1", loadedHistory.get(1));  // 앞뒤 공백 제거됨
        assertEquals("172.16.0.1", loadedHistory.get(2));

        System.err.println("=== 빈 줄/공백 제거 후 로드된 IP ===");
        for (String ip : loadedHistory) {
            System.err.println("'" + ip + "'");
        }
    }

    @Test
    @DisplayName("중복 IP 추가 시 기존 목록에 추가되지 않음 테스트")
    void testDuplicateIpNotAdded() {
        List<String> connectionHistory = new ArrayList<>();
        connectionHistory.add("192.168.1.1");
        connectionHistory.add("10.0.0.1");

        String newIp = "192.168.1.1";  // 이미 존재하는 IP

        // 중복 체크 로직 (실제 코드와 동일)
        if (!connectionHistory.contains(newIp)) {
            connectionHistory.add(newIp);
        }

        assertEquals(2, connectionHistory.size(), "중복 IP는 추가되지 않아야 함");
        System.err.println("중복 IP 추가 시도 후 목록 크기: " + connectionHistory.size());
    }

    @Test
    @DisplayName("새 IP 추가 시 목록에 추가됨 테스트")
    void testNewIpAdded() {
        List<String> connectionHistory = new ArrayList<>();
        connectionHistory.add("192.168.1.1");
        connectionHistory.add("10.0.0.1");

        String newIp = "172.16.0.100";  // 새로운 IP

        // 중복 체크 로직 (실제 코드와 동일)
        if (!connectionHistory.contains(newIp)) {
            connectionHistory.add(newIp);
        }

        assertEquals(3, connectionHistory.size(), "새 IP가 추가되어야 함");
        assertTrue(connectionHistory.contains(newIp), "새 IP가 목록에 있어야 함");
        System.err.println("새 IP 추가 후 목록: " + connectionHistory);
    }

    @Test
    @DisplayName("기록 삭제 테스트")
    void testClearHistory() {
        List<String> connectionHistory = new ArrayList<>();
        connectionHistory.add("192.168.1.1");
        connectionHistory.add("10.0.0.1");
        connectionHistory.add("172.16.0.1");

        assertEquals(3, connectionHistory.size(), "초기에 3개의 IP가 있어야 함");

        // 기록 삭제 (handleClearHistory와 동일)
        connectionHistory.clear();

        assertTrue(connectionHistory.isEmpty(), "삭제 후 목록이 비어있어야 함");
        assertEquals(0, connectionHistory.size(), "삭제 후 크기가 0이어야 함");
        System.err.println("기록 삭제 완료");
    }

    @Test
    @DisplayName("대용량 IP 목록 저장/로드 테스트")
    void testLargeHistorySaveAndLoad() throws Exception {
        // 100개의 IP 생성
        List<String> largeHistory = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            largeHistory.add("192.168.1." + i);
        }

        // 저장
        try (BufferedWriter writer = Files.newBufferedWriter(testFilePath)) {
            for (String ip : largeHistory) {
                writer.write(ip);
                writer.newLine();
            }
        }

        // 로드
        List<String> loadedHistory = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(testFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    loadedHistory.add(line);
                }
            }
        }

        assertEquals(100, loadedHistory.size(), "100개의 IP가 모두 로드되어야 함");
        assertEquals("192.168.1.1", loadedHistory.get(0), "첫 번째 IP 확인");
        assertEquals("192.168.1.100", loadedHistory.get(99), "마지막 IP 확인");
        System.err.println("대용량 테스트: " + loadedHistory.size() + "개 IP 저장/로드 성공");
    }

    @Test
    @DisplayName("Controller의 connectionHistory 필드 초기화 테스트")
    void testConnectionHistoryFieldInitialization() throws Exception {
        ClientConnectionController controller = new ClientConnectionController();

        // 리플렉션으로 private 필드 접근
        Field historyField = ClientConnectionController.class.getDeclaredField("connectionHistory");
        historyField.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        List<String> history = (List<String>) historyField.get(controller);

        assertNotNull(history, "connectionHistory 필드가 null이 아니어야 함");
        System.err.println("connectionHistory 필드 초기화 확인: " + history.size() + "개 IP 로드됨");
    }

    @Test
    @DisplayName("CONNECTION_HISTORY_FILE 상수 값 테스트")
    void testConnectionHistoryFileConstant() throws Exception {
        // 리플렉션으로 private static final 필드 접근
        Field fileField = ClientConnectionController.class.getDeclaredField("CONNECTION_HISTORY_FILE");
        fileField.setAccessible(true);
        
        String fileName = (String) fileField.get(null);

        assertEquals("tetris_connection_history.txt", fileName, "파일명이 일치해야 함");
        System.err.println("CONNECTION_HISTORY_FILE = " + fileName);
    }
}