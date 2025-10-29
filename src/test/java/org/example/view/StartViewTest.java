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

class StartViewTest extends ApplicationTest {
    
    private StartView startView;
    
    @BeforeEach
    void setUp() {
        startView = new StartView();
    }
    
    @Test
    @DisplayName("StartView 생성 테스트")
    void testConstructor() {
        assertNotNull(startView);
        assertNotNull(startView.getButtonSystem());
        assertNotNull(startView.getColorManager());
    }
    
    @Test
    @DisplayName("createView - 4개 버튼이 생성됨")
    void testCreateViewButtons() {
        javafx.application.Platform.runLater(() -> {
            boolean[] flags = {false, false, false, false};
            
            VBox root = startView.createView(
                () -> flags[0] = true,
                () -> flags[1] = true,
                () -> flags[2] = true,
                () -> flags[3] = true
            );
            
            assertNotNull(root);
            assertEquals(4, startView.getButtonSystem().getButtons().size());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 버튼 텍스트 확인")
    void testCreateViewButtonTexts() {
        javafx.application.Platform.runLater(() -> {
            startView.createView(() -> {}, () -> {}, () -> {}, () -> {});
            
            var buttons = startView.getButtonSystem().getButtons();
            assertEquals("Start Game", buttons.get(0).getText());
            assertEquals("View Scoreboard", buttons.get(1).getText());
            assertEquals("Setting", buttons.get(2).getText());
            assertEquals("Exit", buttons.get(3).getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - VBox 자식 요소 개수")
    void testCreateViewChildren() {
        javafx.application.Platform.runLater(() -> {
            VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {});
            
            // Title, Subtitle, 4 buttons, Controls = 7개
            assertEquals(7, root.getChildren().size());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 타이틀 텍스트 확인")
    void testCreateViewTitle() {
        javafx.application.Platform.runLater(() -> {
            VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {});
            
            Text title = (Text) root.getChildren().get(0);
            assertEquals("TETRIS", title.getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 서브타이틀 텍스트 확인")
    void testCreateViewSubtitle() {
        javafx.application.Platform.runLater(() -> {
            VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {});
            
            Text subtitle = (Text) root.getChildren().get(1);
            assertEquals("Team 12 Edition", subtitle.getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Start Game 버튼 액션")
    void testStartGameAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] started = {false};
            
            startView.createView(
                () -> started[0] = true,
                () -> {},
                () -> {},
                () -> {}
            );
            
            Button startButton = startView.getButtonSystem().getButtons().get(0);
            Runnable action = (Runnable) startButton.getUserData();
            action.run();
            
            assertTrue(started[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - View Scoreboard 버튼 액션")
    void testViewScoreboardAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] viewed = {false};
            
            startView.createView(
                () -> {},
                () -> viewed[0] = true,
                () -> {},
                () -> {}
            );
            
            Button scoreboardButton = startView.getButtonSystem().getButtons().get(1);
            Runnable action = (Runnable) scoreboardButton.getUserData();
            action.run();
            
            assertTrue(viewed[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Setting 버튼 액션")
    void testSettingAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] settingOpened = {false};
            
            startView.createView(
                () -> {},
                () -> {},
                () -> settingOpened[0] = true,
                () -> {}
            );
            
            Button settingButton = startView.getButtonSystem().getButtons().get(2);
            Runnable action = (Runnable) settingButton.getUserData();
            action.run();
            
            assertTrue(settingOpened[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Exit 버튼 액션")
    void testExitAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] exited = {false};
            
            startView.createView(
                () -> {},
                () -> {},
                () -> {},
                () -> exited[0] = true
            );
            
            Button exitButton = startView.getButtonSystem().getButtons().get(3);
            Runnable action = (Runnable) exitButton.getUserData();
            action.run();
            
            assertTrue(exited[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 여러 번 호출 시 독립적인 View 생성")
    void testCreateViewMultipleTimes() {
        javafx.application.Platform.runLater(() -> {
            VBox root1 = startView.createView(() -> {}, () -> {}, () -> {}, () -> {});
            VBox root2 = startView.createView(() -> {}, () -> {}, () -> {}, () -> {});
            
            assertNotSame(root1, root2);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
