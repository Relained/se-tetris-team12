package org.example.view;

import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import static org.junit.jupiter.api.Assertions.*;

class PauseViewTest extends ApplicationTest {
    
    private PauseView pauseView;
    
    @BeforeEach
    void setUp() {
        pauseView = new PauseView();
    }
    
    @Test
    @DisplayName("PauseView 생성 테스트")
    void testConstructor() {
        assertNotNull(pauseView);
        assertNotNull(pauseView.getButtonSystem());
    }
    
    @Test
    @DisplayName("createView - 5개 버튼 생성")
    void testCreateViewButtons() {
        javafx.application.Platform.runLater(() -> {
            VBox root = pauseView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertNotNull(root);
            assertEquals(5, pauseView.getButtonSystem().getButtons().size());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 버튼 텍스트 확인")
    void testCreateViewButtonTexts() {
        javafx.application.Platform.runLater(() -> {
            pauseView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            var buttons = pauseView.getButtonSystem().getButtons();
            assertEquals("Resume", buttons.get(0).getText());
            assertEquals("Restart", buttons.get(1).getText());
            assertEquals("Settings", buttons.get(2).getText());
            assertEquals("Main Menu", buttons.get(3).getText());
            assertEquals("Exit", buttons.get(4).getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - VBox 자식 요소 개수")
    void testCreateViewChildren() {
        javafx.application.Platform.runLater(() -> {
            VBox root = pauseView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            // Title + 5 buttons = 6개
            assertEquals(6, root.getChildren().size());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 타이틀 텍스트")
    void testCreateViewTitle() {
        javafx.application.Platform.runLater(() -> {
            VBox root = pauseView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            Text title = (Text) root.getChildren().get(0);
            assertEquals("PAUSED", title.getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Resume 액션")
    void testResumeAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] resumed = {false};
            pauseView.createView(() -> resumed[0] = true, () -> {}, () -> {}, () -> {}, () -> {});
            
            Button resumeButton = pauseView.getButtonSystem().getButtons().get(0);
            ((Runnable) resumeButton.getUserData()).run();
            
            assertTrue(resumed[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Restart 액션")
    void testRestartAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] restarted = {false};
            pauseView.createView(() -> {}, () -> restarted[0] = true, () -> {}, () -> {}, () -> {});
            
            Button restartButton = pauseView.getButtonSystem().getButtons().get(1);
            ((Runnable) restartButton.getUserData()).run();
            
            assertTrue(restarted[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Settings 액션")
    void testSettingsAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] settingsOpened = {false};
            pauseView.createView(() -> {}, () -> {}, () -> settingsOpened[0] = true, () -> {}, () -> {});
            
            Button settingsButton = pauseView.getButtonSystem().getButtons().get(2);
            ((Runnable) settingsButton.getUserData()).run();
            
            assertTrue(settingsOpened[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Main Menu 액션")
    void testMainMenuAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] mainMenuOpened = {false};
            pauseView.createView(() -> {}, () -> {}, () -> {}, () -> mainMenuOpened[0] = true, () -> {});
            
            Button mainMenuButton = pauseView.getButtonSystem().getButtons().get(3);
            ((Runnable) mainMenuButton.getUserData()).run();
            
            assertTrue(mainMenuOpened[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Exit 액션")
    void testExitAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] exited = {false};
            pauseView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> exited[0] = true);
            
            Button exitButton = pauseView.getButtonSystem().getButtons().get(4);
            ((Runnable) exitButton.getUserData()).run();
            
            assertTrue(exited[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
