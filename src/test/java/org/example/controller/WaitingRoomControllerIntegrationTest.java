package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.model.GameMode;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WaitingRoomController integration tests with JavaFX
 * Tests controller with real Socket connections and reflection-based method testing
 */
@ExtendWith(ApplicationExtension.class)
class WaitingRoomControllerIntegrationTest {

    private Stage testStage;
    private ServerSocket serverSocket;
    private Socket serverSideSocket;
    private Socket clientSideSocket;
    private WaitingRoomController serverController;
    private WaitingRoomController clientController;

    @Start
    void start(Stage stage) {
        this.testStage = stage;
    }
    
    private void setupSocketConnection() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        
        CountDownLatch connectionLatch = new CountDownLatch(1);
        
        Thread.ofVirtual().start(() -> {
            try {
                serverSideSocket = serverSocket.accept();
                connectionLatch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        clientSideSocket = new Socket("localhost", port);
        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS));
    }

    @BeforeEach
    void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        
        Platform.runLater(() -> {
            BaseView.Initialize(ColorManager.getInstance());
            BaseController.Initialize(testStage, new SettingManager());
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Clear state stack
        Field stackField = BaseController.class.getDeclaredField("stateStack");
        stackField.setAccessible(true);
        Stack<BaseController> stack = (Stack<BaseController>) stackField.get(null);
        stack.clear();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        if (serverSideSocket != null && !serverSideSocket.isClosed()) {
            serverSideSocket.close();
        }
        if (clientSideSocket != null && !clientSideSocket.isClosed()) {
            clientSideSocket.close();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        Thread.sleep(300);
    }

    // Functional tests with real Socket connections
    
    @Test
    @DisplayName("Server controller initializes correctly")
    void testServerInitialization() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(500);
        
        assertNotNull(serverController);
    }

    @Test
    @DisplayName("Client controller initializes correctly")
    void testClientInitialization() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            clientController = new WaitingRoomController(clientSideSocket, false);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(500);
        
        assertNotNull(clientController);
    }
    
    @Test
    @DisplayName("Controller createScene works after network stabilization")
    void testCreateSceneDelayed() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(800); // Wait for network to stabilize
        
        Platform.runLater(() -> {
            Scene scene = serverController.createScene();
            assertNotNull(scene);
            assertNotNull(scene.getRoot());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("handleReadyToggle changes ready state via reflection")
    void testHandleReadyToggleReflection() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);
        
        Platform.runLater(() -> {
            try {
                // Set lastToggleTime to past to bypass cooldown
                Field lastToggleField = WaitingRoomController.class.getDeclaredField("lastToggleTime");
                lastToggleField.setAccessible(true);
                lastToggleField.setLong(serverController, 0L);
                
                // Get initial ready state
                Field isReadyField = WaitingRoomController.class.getDeclaredField("isReady");
                isReadyField.setAccessible(true);
                boolean initialReady = isReadyField.getBoolean(serverController);
                
                // Call handleReadyToggle
                var method = WaitingRoomController.class.getDeclaredMethod("handleReadyToggle");
                method.setAccessible(true);
                method.invoke(serverController);
                
                // Verify state changed
                boolean newReady = isReadyField.getBoolean(serverController);
                assertNotEquals(initialReady, newReady);
            } catch (Exception e) {
                fail("handleReadyToggle should work: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("handleReadyToggle respects cooldown")
    void testHandleReadyToggleCooldown() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);
        
        Platform.runLater(() -> {
            try {
                Field isReadyField = WaitingRoomController.class.getDeclaredField("isReady");
                isReadyField.setAccessible(true);
                
                Field lastToggleField = WaitingRoomController.class.getDeclaredField("lastToggleTime");
                lastToggleField.setAccessible(true);
                
                // Set time to past for first toggle
                lastToggleField.setLong(serverController, 0L);
                
                var method = WaitingRoomController.class.getDeclaredMethod("handleReadyToggle");
                method.setAccessible(true);
                method.invoke(serverController);
                
                boolean afterFirstToggle = isReadyField.getBoolean(serverController);
                
                // Set time to current (within cooldown)
                lastToggleField.setLong(serverController, System.currentTimeMillis());
                
                // Try to toggle again immediately
                method.invoke(serverController);
                
                // State should not change due to cooldown
                boolean afterSecondToggle = isReadyField.getBoolean(serverController);
                assertEquals(afterFirstToggle, afterSecondToggle);
            } catch (Exception e) {
                fail("Cooldown test should work: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"Normal", "Item", "Time Attack"})
    @DisplayName("handleGameModeChange works for all modes")
    void testHandleGameModeChange(String mode) throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);
        
        Platform.runLater(() -> {
            try {
                var method = WaitingRoomController.class.getDeclaredMethod("handleGameModeChange", String.class);
                method.setAccessible(true);
                method.invoke(serverController, mode);
                
                Field gameModeField = WaitingRoomController.class.getDeclaredField("selectedGameMode");
                gameModeField.setAccessible(true);
                GameMode selectedMode = (GameMode) gameModeField.get(serverController);
                
                assertNotNull(selectedMode);
            } catch (Exception e) {
                fail("handleGameModeChange should work: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("handleDifficultyChange works for all levels")
    void testHandleDifficultyChange(int difficulty) throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);
        
        Platform.runLater(() -> {
            try {
                var method = WaitingRoomController.class.getDeclaredMethod("handleDifficultyChange", int.class);
                method.setAccessible(true);
                method.invoke(serverController, difficulty);
                
                Field difficultyField = WaitingRoomController.class.getDeclaredField("selectedDifficulty");
                difficultyField.setAccessible(true);
                int selectedDifficulty = difficultyField.getInt(serverController);
                
                assertEquals(difficulty, selectedDifficulty);
            } catch (Exception e) {
                fail("handleDifficultyChange should work: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("handleKeyInput processes key events")
    void testHandleKeyInputReflection() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(800);
        
        // Create scene first to initialize view
        Platform.runLater(() -> {
            serverController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);
        
        Platform.runLater(() -> {
            try {
                // Test various key codes - DOWN and UP navigate the view
                KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
                KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
                
                // Should not throw exceptions
                serverController.handleKeyInput(downEvent);
                serverController.handleKeyInput(upEvent);
            } catch (Exception e) {
                fail("handleKeyInput should work: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("handleGameModeReceived updates game mode")
    void testHandleGameModeReceivedReflection() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);
        
        Platform.runLater(() -> {
            try {
                var method = WaitingRoomController.class.getDeclaredMethod("handleGameModeReceived", GameMode.class);
                method.setAccessible(true);
                method.invoke(serverController, GameMode.ITEM);
                
                Field gameModeField = WaitingRoomController.class.getDeclaredField("selectedGameMode");
                gameModeField.setAccessible(true);
                GameMode selectedMode = (GameMode) gameModeField.get(serverController);
                
                assertEquals(GameMode.ITEM, selectedMode);
            } catch (Exception e) {
                fail("handleGameModeReceived should work: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("handleDifficultyReceived updates difficulty")
    void testHandleDifficultyReceivedReflection() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);
        
        Platform.runLater(() -> {
            try {
                var method = WaitingRoomController.class.getDeclaredMethod("handleDifficultyReceived", int.class);
                method.setAccessible(true);
                method.invoke(serverController, 3);
                
                Field difficultyField = WaitingRoomController.class.getDeclaredField("selectedDifficulty");
                difficultyField.setAccessible(true);
                int selectedDifficulty = difficultyField.getInt(serverController);
                
                assertEquals(3, selectedDifficulty);
            } catch (Exception e) {
                fail("handleDifficultyReceived should work: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getDifficultyName returns correct strings")
    void testGetDifficultyNameReflection() throws Exception {
        setupSocketConnection();
        
        Platform.runLater(() -> {
            serverController = new WaitingRoomController(serverSideSocket, true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);
        
        Platform.runLater(() -> {
            try {
                var method = WaitingRoomController.class.getDeclaredMethod("getDifficultyName", int.class);
                method.setAccessible(true);
                
                String easy = (String) method.invoke(serverController, 1);
                String normal = (String) method.invoke(serverController, 2);
                String hard = (String) method.invoke(serverController, 3);
                
                assertEquals("Easy", easy);
                assertEquals("Normal", normal);
                assertEquals("Hard", hard);
            } catch (Exception e) {
                fail("getDifficultyName should work: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}

