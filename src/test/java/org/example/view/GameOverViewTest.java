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

class GameOverViewTest extends ApplicationTest {
    
    private GameOverView gameOverView;
    
    @BeforeEach
    void setUp() {
        gameOverView = new GameOverView();
    }
    
    @Test
    @DisplayName("GameOverView 생성 테스트")
    void testConstructor() {
        assertNotNull(gameOverView);
        assertNotNull(gameOverView.getButtonSystem());
    }
    
    @Test
    @DisplayName("createView - 4개 버튼 생성")
    void testCreateViewButtons() {
        javafx.application.Platform.runLater(() -> {
            VBox root = gameOverView.createView(1000, 50, 5, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertNotNull(root);
            assertEquals(4, gameOverView.getButtonSystem().getButtons().size());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 버튼 텍스트")
    void testCreateViewButtonTexts() {
        javafx.application.Platform.runLater(() -> {
            gameOverView.createView(1000, 50, 5, () -> {}, () -> {}, () -> {}, () -> {});
            
            var buttons = gameOverView.getButtonSystem().getButtons();
            assertEquals("Play Again", buttons.get(0).getText());
            assertEquals("View Scoreboard", buttons.get(1).getText());
            assertEquals("Main Menu", buttons.get(2).getText());
            assertEquals("Exit Game", buttons.get(3).getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 점수 표시")
    void testCreateViewScoreDisplay() {
        javafx.application.Platform.runLater(() -> {
            VBox root = gameOverView.createView(12345, 67, 8, () -> {}, () -> {}, () -> {}, () -> {});
            
            // Title, Score, Lines, Level, 4 buttons = 8개
            assertEquals(8, root.getChildren().size());
            
            Text scoreText = (Text) root.getChildren().get(1);
            assertTrue(scoreText.getText().contains("12345"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 라인 수 표시")
    void testCreateViewLinesDisplay() {
        javafx.application.Platform.runLater(() -> {
            VBox root = gameOverView.createView(1000, 99, 5, () -> {}, () -> {}, () -> {}, () -> {});
            
            Text linesText = (Text) root.getChildren().get(2);
            assertTrue(linesText.getText().contains("99"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 레벨 표시")
    void testCreateViewLevelDisplay() {
        javafx.application.Platform.runLater(() -> {
            VBox root = gameOverView.createView(1000, 50, 10, () -> {}, () -> {}, () -> {}, () -> {});
            
            Text levelText = (Text) root.getChildren().get(3);
            assertTrue(levelText.getText().contains("10"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Play Again 액션")
    void testPlayAgainAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] played = {false};
            gameOverView.createView(1000, 50, 5, () -> played[0] = true, () -> {}, () -> {}, () -> {});
            
            Button playButton = gameOverView.getButtonSystem().getButtons().get(0);
            ((Runnable) playButton.getUserData()).run();
            
            assertTrue(played[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - View Scoreboard 액션")
    void testViewScoreboardAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] viewed = {false};
            gameOverView.createView(1000, 50, 5, () -> {}, () -> viewed[0] = true, () -> {}, () -> {});
            
            Button scoreboardButton = gameOverView.getButtonSystem().getButtons().get(1);
            ((Runnable) scoreboardButton.getUserData()).run();
            
            assertTrue(viewed[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Main Menu 액션")
    void testMainMenuAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] mainMenuOpened = {false};
            gameOverView.createView(1000, 50, 5, () -> {}, () -> {}, () -> mainMenuOpened[0] = true, () -> {});
            
            Button mainMenuButton = gameOverView.getButtonSystem().getButtons().get(2);
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
            gameOverView.createView(1000, 50, 5, () -> {}, () -> {}, () -> {}, () -> exited[0] = true);
            
            Button exitButton = gameOverView.getButtonSystem().getButtons().get(3);
            ((Runnable) exitButton.getUserData()).run();
            
            assertTrue(exited[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 0 점수 표시")
    void testCreateViewZeroScore() {
        javafx.application.Platform.runLater(() -> {
            VBox root = gameOverView.createView(0, 0, 1, () -> {}, () -> {}, () -> {}, () -> {});
            
            Text scoreText = (Text) root.getChildren().get(1);
            assertTrue(scoreText.getText().contains("0"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
