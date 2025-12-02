package org.example.view;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PauseView 클래스의 단위 테스트
 * TestFX를 사용하여 JavaFX UI 컴포넌트를 테스트합니다.
 */
@ExtendWith(ApplicationExtension.class)
class PauseViewTest {

    private PauseView pauseView;

    @Start
    void start(Stage stage) throws Exception {
        // JavaFX 스레드에서 초기화
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
        pauseView = new PauseView();
    }

    @Test
    void testPauseViewCreation() {
        assertNotNull(pauseView);
        assertNotNull(pauseView.getButtonSystem());
    }

    @Test
    void testCreateViewWithAllCallbacks() {
        AtomicInteger resumeCount = new AtomicInteger(0);
        AtomicInteger restartCount = new AtomicInteger(0);
        AtomicInteger settingsCount = new AtomicInteger(0);
        AtomicInteger mainMenuCount = new AtomicInteger(0);
        AtomicInteger exitCount = new AtomicInteger(0);

        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                resumeCount::incrementAndGet,
                restartCount::incrementAndGet,
                settingsCount::incrementAndGet,
                mainMenuCount::incrementAndGet,
                exitCount::incrementAndGet
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(result[0]);
    }

    @Test
    void testCreateViewReturnsVBox() {
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(result[0] instanceof VBox);
    }

    @Test
    void testViewHasTitleText() {
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        VBox vbox = (VBox) result[0];
        assertFalse(vbox.getChildren().isEmpty());
        assertTrue(vbox.getChildren().get(0) instanceof Text);
        
        Text titleText = (Text) vbox.getChildren().get(0);
        assertEquals("PAUSED", titleText.getText());
    }

    @Test
    void testViewHasCorrectNumberOfChildren() {
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        VBox vbox = (VBox) result[0];
        // Title text (1) + 5 buttons (5) = 6 children
        assertEquals(6, vbox.getChildren().size());
    }

    @Test
    void testResumeCallback() {
        AtomicInteger callCount = new AtomicInteger(0);
        
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                callCount::incrementAndGet,
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Runnable action = (Runnable) pauseView.getButtonSystem().getButtons().get(0).getUserData();
            action.run();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, callCount.get());
    }

    @Test
    void testRestartCallback() {
        AtomicInteger callCount = new AtomicInteger(0);
        
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                callCount::incrementAndGet,
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Runnable action = (Runnable) pauseView.getButtonSystem().getButtons().get(1).getUserData();
            action.run();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, callCount.get());
    }

    @Test
    void testSettingsCallback() {
        AtomicInteger callCount = new AtomicInteger(0);
        
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                callCount::incrementAndGet,
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Runnable action = (Runnable) pauseView.getButtonSystem().getButtons().get(2).getUserData();
            action.run();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, callCount.get());
    }

    @Test
    void testMainMenuCallback() {
        AtomicInteger callCount = new AtomicInteger(0);
        
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                callCount::incrementAndGet,
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Runnable action = (Runnable) pauseView.getButtonSystem().getButtons().get(3).getUserData();
            action.run();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, callCount.get());
    }

    @Test
    void testExitCallback() {
        AtomicInteger callCount = new AtomicInteger(0);
        
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                callCount::incrementAndGet
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Runnable action = (Runnable) pauseView.getButtonSystem().getButtons().get(4).getUserData();
            action.run();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, callCount.get());
    }

    @Test
    void testButtonSystemHasCorrectNumberOfButtons() {
        Platform.runLater(() -> {
            pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(5, pauseView.getButtonSystem().getButtons().size());
    }

    @Test
    void testButtonLabels() {
        Platform.runLater(() -> {
            pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        var buttons = pauseView.getButtonSystem().getButtons();
        assertEquals("Resume", buttons.get(0).getText());
        assertEquals("Restart", buttons.get(1).getText());
        assertEquals("Settings", buttons.get(2).getText());
        assertEquals("Main Menu", buttons.get(3).getText());
        assertEquals("Exit", buttons.get(4).getText());
    }

    @Test
    void testVBoxSpacing() {
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        VBox vbox = (VBox) result[0];
        assertEquals(30, vbox.getSpacing());
    }

    @Test
    void testMultipleCallbackInvocations() {
        AtomicInteger resumeCount = new AtomicInteger(0);
        
        Platform.runLater(() -> {
            pauseView.createView(
                resumeCount::incrementAndGet,
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        // 같은 버튼을 여러 번 실행
        Platform.runLater(() -> {
            Runnable action = (Runnable) pauseView.getButtonSystem().getButtons().get(0).getUserData();
            action.run();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Runnable action = (Runnable) pauseView.getButtonSystem().getButtons().get(0).getUserData();
            action.run();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Runnable action = (Runnable) pauseView.getButtonSystem().getButtons().get(0).getUserData();
            action.run();
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(3, resumeCount.get());
    }

    @Test
    void testAllCallbacksCanBeInvoked() {
        AtomicInteger resumeCount = new AtomicInteger(0);
        AtomicInteger restartCount = new AtomicInteger(0);
        AtomicInteger settingsCount = new AtomicInteger(0);
        AtomicInteger mainMenuCount = new AtomicInteger(0);
        AtomicInteger exitCount = new AtomicInteger(0);

        Platform.runLater(() -> {
            pauseView.createView(
                resumeCount::incrementAndGet,
                restartCount::incrementAndGet,
                settingsCount::incrementAndGet,
                mainMenuCount::incrementAndGet,
                exitCount::incrementAndGet
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        // 모든 버튼 실행
        var buttons = pauseView.getButtonSystem().getButtons();
        for (int i = 0; i < buttons.size(); i++) {
            int index = i;
            Platform.runLater(() -> {
                Runnable action = (Runnable) buttons.get(index).getUserData();
                action.run();
            });
            WaitForAsyncUtils.waitForFxEvents();
        }

        assertEquals(1, resumeCount.get());
        assertEquals(1, restartCount.get());
        assertEquals(1, settingsCount.get());
        assertEquals(1, mainMenuCount.get());
        assertEquals(1, exitCount.get());
    }

    @Test
    void testViewBackgroundColor() {
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        VBox vbox = (VBox) result[0];
        assertNotNull(vbox.getBackground());
    }

    @Test
    void testTitleTextStyle() {
        Pane[] result = new Pane[1];
        Platform.runLater(() -> {
            result[0] = pauseView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
        });
        WaitForAsyncUtils.waitForFxEvents();

        VBox vbox = (VBox) result[0];
        Text titleText = (Text) vbox.getChildren().get(0);
        assertNotNull(titleText.getFill());
        assertNotNull(titleText.getFont());
    }
}
