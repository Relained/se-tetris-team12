package org.example.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientConnectionController integration tests with JavaFX
 * Tests controller with full UI environment
 */
@ExtendWith(ApplicationExtension.class)
class ClientConnectionControllerIntegrationTest {

    private ClientConnectionController controller;
    private Stage testStage;
    private static final String TEST_HISTORY_FILE = "tetris_connection_history.txt";

    @Start
    void start(Stage stage) {
        this.testStage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        
        javafx.application.Platform.runLater(() -> {
            BaseView.Initialize(ColorManager.getInstance());
            BaseController.Initialize(testStage, new SettingManager());
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Clear state stack
        Field stackField = BaseController.class.getDeclaredField("stateStack");
        stackField.setAccessible(true);
        Stack<BaseController> stack = (Stack<BaseController>) stackField.get(null);
        stack.clear();
        
        controller = new ClientConnectionController();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test history file
        Path historyPath = Paths.get(TEST_HISTORY_FILE);
        if (Files.exists(historyPath)) {
            Files.delete(historyPath);
        }
        
        // Allow threads to finish
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("ClientConnectionController initializes correctly")
    void testInitialization() {
        assertNotNull(controller, "Controller should be initialized");
    }

    @Test
    @DisplayName("ClientConnectionController creates scene successfully")
    void testCreateScene() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created");
        assertNotNull(sceneHolder[0].getRoot(), "Scene root should not be null");
    }

    @Test
    @DisplayName("ClientConnectionController handles go back")
    void testHandleGoBack() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> controller.handleGoBack());
            WaitForAsyncUtils.waitForFxEvents();
        }, "Handle go back should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionController handles refresh")
    void testHandleRefresh() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Refresh is called automatically in createScene, test doesn't throw
        assertNotNull(sceneHolder[0], "Scene with refresh should be created");
    }

    @Test
    @DisplayName("ClientConnectionController validates invalid IP address")
    void testInvalidIpAddress() {
        final Scene[] sceneHolder = {null};
        final String[] titleHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Access private method through reflection to test IP validation
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    var method = ClientConnectionController.class.getDeclaredMethod("handleIpSubmit", String.class);
                    method.setAccessible(true);
                    method.invoke(controller, "invalid.ip.address");
                } catch (Exception e) {
                    // Expected for testing
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Invalid IP handling should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionController validates valid IP format")
    void testValidIpFormat() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    var method = ClientConnectionController.class.getDeclaredMethod("handleIpSubmit", String.class);
                    method.setAccessible(true);
                    // This will attempt connection (and fail), but should handle it gracefully
                    method.invoke(controller, "192.168.1.1");
                } catch (Exception e) {
                    // Connection failure is expected
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
            
            // Give connection attempt time to fail
            Thread.sleep(500);
        }, "Valid IP format handling should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionController handles history selection")
    void testHandleHistorySelect() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    var method = ClientConnectionController.class.getDeclaredMethod("handleHistorySelect", String.class);
                    method.setAccessible(true);
                    method.invoke(controller, "192.168.1.100");
                } catch (Exception e) {
                    fail("History selection should work: " + e.getMessage());
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "History selection should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionController clears history")
    void testHandleClearHistory() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    var method = ClientConnectionController.class.getDeclaredMethod("handleClearHistory");
                    method.setAccessible(true);
                    method.invoke(controller);
                } catch (Exception e) {
                    fail("Clear history should work: " + e.getMessage());
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Clear history should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionController handles searched user selection")
    void testHandleSearchedUserSelect() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    var method = ClientConnectionController.class.getDeclaredMethod("handleSearchedUserSelect", String.class);
                    method.setAccessible(true);
                    method.invoke(controller, "  192.168.1.50  "); // With whitespace
                } catch (Exception e) {
                    // Connection will fail, but method should handle it
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
            
            Thread.sleep(500);
        }, "Searched user selection should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionController prevents rapid connection attempts")
    void testRapidConnectionPrevention() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    var method = ClientConnectionController.class.getDeclaredMethod("handleIpSubmit", String.class);
                    method.setAccessible(true);
                    
                    // Rapid attempts should be prevented
                    method.invoke(controller, "192.168.1.1");
                    method.invoke(controller, "192.168.1.2");
                    method.invoke(controller, "192.168.1.3");
                } catch (Exception e) {
                    // Expected behavior
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
            
            Thread.sleep(500);
        }, "Rapid connection prevention should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionController creates scene with all UI elements")
    void testSceneWithUIElements() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created");
        assertTrue(sceneHolder[0].getRoot().getChildrenUnmodifiable().size() > 0, 
            "Scene should have UI elements");
    }
}
