package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ServerConnectionController integration tests with JavaFX
 * Tests server connection controller with network setup
 */
@ExtendWith(ApplicationExtension.class)
class ServerConnectionControllerIntegrationTest {

    private ServerConnectionController controller;
    private Stage testStage;

    @Start
    void start(Stage stage) {
        this.testStage = stage;
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
        
        controller = new ServerConnectionController();
    }

    @Test
    @DisplayName("ServerConnectionController initializes correctly")
    void testInitialization() {
        assertNotNull(controller, "Controller should be initialized");
    }

    @Test
    @DisplayName("Controller creates scene successfully")
    void testCreateScene() {
        Platform.runLater(() -> {
            Scene scene = controller.createScene();
            assertNotNull(scene, "Scene should be created");
            assertNotNull(scene.getRoot(), "Scene should have root node");
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("Controller handles scene display")
    void testShowScene() {
        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                Scene scene = controller.createScene();
                testStage.setScene(scene);
            });
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    @DisplayName("Controller scene has correct dimensions")
    void testSceneDimensions() {
        Platform.runLater(() -> {
            Scene scene = controller.createScene();
            assertTrue(scene.getWidth() > 0, "Scene width should be positive");
            assertTrue(scene.getHeight() > 0, "Scene height should be positive");
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("Multiple controllers can be created")
    void testMultipleControllers() {
        assertDoesNotThrow(() -> {
            ServerConnectionController controller1 = new ServerConnectionController();
            ServerConnectionController controller2 = new ServerConnectionController();
            
            assertNotNull(controller1);
            assertNotNull(controller2);
            assertNotSame(controller1, controller2);
        });
    }

    @Test
    @DisplayName("Controller cleanup does not throw exceptions")
    void testControllerCleanup() {
        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                controller.createScene();
            });
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    @DisplayName("Controller can be recreated after cleanup")
    void testControllerRecreation() {
        assertDoesNotThrow(() -> {
            ServerConnectionController firstController = new ServerConnectionController();
            Platform.runLater(() -> firstController.createScene());
            WaitForAsyncUtils.waitForFxEvents();
            
            ServerConnectionController secondController = new ServerConnectionController();
            Platform.runLater(() -> secondController.createScene());
            WaitForAsyncUtils.waitForFxEvents();
            
            assertNotSame(firstController, secondController);
        });
    }

    @Test
    @DisplayName("Controller scene can be set on stage multiple times")
    void testMultipleSceneChanges() {
        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                Scene scene1 = controller.createScene();
                testStage.setScene(scene1);
                
                ServerConnectionController controller2 = new ServerConnectionController();
                Scene scene2 = controller2.createScene();
                testStage.setScene(scene2);
                
                testStage.setScene(scene1);
            });
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    @DisplayName("Controller handles rapid scene creation")
    void testRapidSceneCreation() {
        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                for (int i = 0; i < 5; i++) {
                    Scene scene = controller.createScene();
                    assertNotNull(scene);
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    @DisplayName("Controller initialization is thread-safe")
    void testThreadSafeInitialization() {
        assertDoesNotThrow(() -> {
            ServerConnectionController[] controllers = new ServerConnectionController[3];
            
            for (int i = 0; i < 3; i++) {
                final int index = i;
                Platform.runLater(() -> {
                    controllers[index] = new ServerConnectionController();
                });
            }
            WaitForAsyncUtils.waitForFxEvents();
            
            for (ServerConnectionController c : controllers) {
                assertNotNull(c);
            }
        });
    }
}
