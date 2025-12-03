package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.example.view.BaseView;
import org.example.view.PauseView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PauseController 클래스의 단위 테스트
 * TestFX를 사용하여 JavaFX UI 컴포넌트를 테스트합니다.
 */
@ExtendWith(ApplicationExtension.class)
class PauseControllerTest {

    private PauseController pauseController;

    @Start
    void start(Stage stage) throws Exception {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
        AtomicInteger resetCount = new AtomicInteger(0);
        pauseController = new PauseController(resetCount::incrementAndGet);
    }

    @Test
    void testPauseControllerCreation() {
        assertNotNull(pauseController);
        assertNotNull(pauseController.pauseView);
        assertTrue(pauseController.pauseView instanceof PauseView);
    }

    @Test
    void testCreateSceneReturnsScene() {
        Scene[] result = new Scene[1];
        Platform.runLater(() -> {
            result[0] = pauseController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(result[0]);
        assertTrue(result[0] instanceof Scene);
    }

    @Test
    void testHandleResumeMethodExists() throws Exception {
        Method method = PauseController.class.getDeclaredMethod("handleResume");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void testHandleRestartMethodExists() throws Exception {
        Method method = PauseController.class.getDeclaredMethod("handleRestart");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void testHandleSettingsMethodExists() throws Exception {
        Method method = PauseController.class.getDeclaredMethod("handleSettings");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void testHandleMainMenuMethodExists() throws Exception {
        Method method = PauseController.class.getDeclaredMethod("handleMainMenu");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void testHandleExitMethodExists() throws Exception {
        Method method = PauseController.class.getDeclaredMethod("handleExit");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void testHandleKeyInputMethodExists() throws Exception {
        Method method = PauseController.class.getDeclaredMethod("handleKeyInput", KeyEvent.class);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void testHandleKeyInputProcessesEvent() {
        Platform.runLater(() -> {
            pauseController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();

        KeyEvent downEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.DOWN,
            false, false, false, false
        );

        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                pauseController.handleKeyInput(downEvent);
            });
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testHandleKeyInputWithUpKey() {
        Platform.runLater(() -> {
            pauseController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();

        KeyEvent upEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.UP,
            false, false, false, false
        );

        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                pauseController.handleKeyInput(upEvent);
            });
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testDefaultConstructorCreatesView() {
        PauseController controller = new PauseController();
        assertNotNull(controller);
        assertNotNull(controller.pauseView);
    }

    @Test
    void testParameterizedConstructorCreatesView() {
        AtomicInteger count = new AtomicInteger(0);
        PauseController controller = new PauseController(count::incrementAndGet);
        assertNotNull(controller);
        assertNotNull(controller.pauseView);
    }

    @Test
    void testMultipleKeyInputs() {
        Platform.runLater(() -> {
            pauseController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();

        KeyEvent[] events = {
            new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false),
            new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false),
            new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false)
        };

        assertDoesNotThrow(() -> {
            for (KeyEvent event : events) {
                Platform.runLater(() -> {
                    pauseController.handleKeyInput(event);
                });
                WaitForAsyncUtils.waitForFxEvents();
            }
        });
    }

    @Test
    void testButtonSystemIsAccessible() {
        Platform.runLater(() -> {
            pauseController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(pauseController.pauseView.getButtonSystem());
        assertEquals(5, pauseController.pauseView.getButtonSystem().getButtons().size());
    }

    @Test
    void testAllButtonsHaveCorrectLabels() {
        Platform.runLater(() -> {
            pauseController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();

        var buttons = pauseController.pauseView.getButtonSystem().getButtons();
        assertEquals("Resume", buttons.get(0).getText());
        assertEquals("Restart", buttons.get(1).getText());
        assertEquals("Settings", buttons.get(2).getText());
        assertEquals("Main Menu", buttons.get(3).getText());
        assertEquals("Exit", buttons.get(4).getText());
    }
}
