package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.example.model.GameMode;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LocalMultiPlayController integration tests with JavaFX
 * Tests controller with full UI environment
 */
@ExtendWith(ApplicationExtension.class)
class LocalMultiPlayControllerIntegrationTest {

    private LocalMultiPlayController controller;
    private Stage testStage;

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
        
        controller = new LocalMultiPlayController(GameMode.NORMAL, 2);
    }

    @Test
    @DisplayName("LocalMultiPlayController initializes correctly")
    void testInitialization() {
        assertNotNull(controller, "Controller should be initialized");
    }

    @ParameterizedTest
    @EnumSource(GameMode.class)
    @DisplayName("LocalMultiPlayController initializes for all game modes")
    void testInitializationAllModes(GameMode mode) {
        LocalMultiPlayController testController = new LocalMultiPlayController(mode, 2);
        assertNotNull(testController, "Controller should be initialized for mode " + mode);
        assertNotNull(testController.getPlayer1System(), "Player 1 system should exist for " + mode);
        assertNotNull(testController.getPlayer2System(), "Player 2 system should exist for " + mode);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("LocalMultiPlayController initializes with different difficulty levels")
    void testInitializationDifferentDifficulties(int difficulty) {
        LocalMultiPlayController testController = new LocalMultiPlayController(GameMode.NORMAL, difficulty);
        assertNotNull(testController, "Controller should be initialized for difficulty " + difficulty);
        assertEquals(difficulty, testController.getPlayer1System().getDifficulty(), 
            "Player 1 difficulty should be " + difficulty);
        assertEquals(difficulty, testController.getPlayer2System().getDifficulty(), 
            "Player 2 difficulty should be " + difficulty);
    }

    @Test
    @DisplayName("LocalMultiPlayController creates scene successfully")
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
    @DisplayName("LocalMultiPlayController has independent player systems")
    void testIndependentPlayerSystems() {
        assertNotNull(controller.getPlayer1System(), "Player 1 system should be initialized");
        assertNotNull(controller.getPlayer2System(), "Player 2 system should be initialized");
        assertNotSame(controller.getPlayer1System(), controller.getPlayer2System(), 
            "Player systems should be independent instances");
    }

    @Test
    @DisplayName("LocalMultiPlayController has AdderBoards initialized")
    void testAdderBoardsInitialized() {
        assertNotNull(controller.getPlayer1AdderBoard(), "Player 1 AdderBoard should be initialized");
        assertNotNull(controller.getPlayer2AdderBoard(), "Player 2 AdderBoard should be initialized");
        assertEquals(0, controller.getPlayer1AdderBoard().getLineCount(), 
            "Player 1 AdderBoard should start empty");
        assertEquals(0, controller.getPlayer2AdderBoard().getLineCount(), 
            "Player 2 AdderBoard should start empty");
    }

    @Test
    @DisplayName("LocalMultiPlayController handles pause")
    void testHandlePause() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> controller.handlePause());
            WaitForAsyncUtils.waitForFxEvents();
        }, "Handle pause should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController handles resume after pause")
    void testResume() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            controller.handlePause();
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> controller.resume());
            WaitForAsyncUtils.waitForFxEvents();
        }, "Resume should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController handles exit")
    void testExit() {
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> controller.exit());
            WaitForAsyncUtils.waitForFxEvents();
        }, "Exit should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController update method works")
    void testUpdate() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> controller.update(0.016));
            WaitForAsyncUtils.waitForFxEvents();
        }, "Update should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController handles Player 1 arrow key inputs")
    void testHandleKeyPressedPlayer1() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                controller.handleKeyPressed(KeyCode.LEFT);
                controller.handleKeyPressed(KeyCode.RIGHT);
                controller.handleKeyPressed(KeyCode.DOWN);
                controller.handleKeyPressed(KeyCode.UP);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Player 1 key handling should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController handles Player 2 WASD key inputs")
    void testHandleKeyPressedPlayer2() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                controller.handleKeyPressed(KeyCode.A);
                controller.handleKeyPressed(KeyCode.D);
                controller.handleKeyPressed(KeyCode.S);
                controller.handleKeyPressed(KeyCode.W);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Player 2 key handling should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController handles key release")
    void testHandleKeyReleased() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                controller.handleKeyPressed(KeyCode.LEFT);
                controller.handleKeyReleased(KeyCode.LEFT);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Key release handling should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController TIME_ATTACK mode shows timer")
    void testTimeAttackModeTimer() {
        LocalMultiPlayController timeController = new LocalMultiPlayController(GameMode.TIME_ATTACK, 2);
        
        final Scene[] sceneHolder = {null};
        javafx.application.Platform.runLater(() -> {
            Scene scene = timeController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "TIME_ATTACK scene should be created");
    }

    @Test
    @DisplayName("LocalMultiPlayController multiple updates work correctly")
    void testMultipleUpdates() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                for (int i = 0; i < 10; i++) {
                    controller.update(0.016);
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Multiple updates should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController pause and resume cycle")
    void testPauseResumeCycle() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                controller.handlePause();
                controller.resume();
                controller.handlePause();
                controller.resume();
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Multiple pause/resume cycles should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController handles both players key input simultaneously")
    void testSimultaneousKeyInput() {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                // Player 1 and Player 2 press keys at the same time
                controller.handleKeyPressed(KeyCode.LEFT);
                controller.handleKeyPressed(KeyCode.A);
                controller.handleKeyPressed(KeyCode.DOWN);
                controller.handleKeyPressed(KeyCode.S);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Simultaneous key input should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController ITEM mode creates ItemTetrisSystem")
    void testItemModeSystem() {
        LocalMultiPlayController itemController = new LocalMultiPlayController(GameMode.ITEM, 2);
        
        assertNotNull(itemController.getPlayer1System(), "ITEM mode should create player 1 system");
        assertNotNull(itemController.getPlayer2System(), "ITEM mode should create player 2 system");
        
        final Scene[] sceneHolder = {null};
        javafx.application.Platform.runLater(() -> {
            Scene scene = itemController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "ITEM mode scene should be created");
    }
}
