package org.example.view.component.play;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;


import static org.junit.jupiter.api.Assertions.*;

class ScorePanelTest extends ApplicationTest {
    
    @Test
    @DisplayName("ScorePanel 생성 시 초기화됨")
    void testScorePanelCreation() {
        Platform.runLater(() -> {
            ScorePanel panel = new ScorePanel();
            
            assertNotNull(panel);
            assertFalse(panel.getChildren().isEmpty());
            assertEquals(4, panel.getChildren().size()); // title + score + lines + level
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("통계 업데이트 시 텍스트가 변경됨")
    void testUpdateStats() {
        Platform.runLater(() -> {
            ScorePanel panel = new ScorePanel();
            
            panel.updateStats(100, 10, 2);
            
            // 텍스트가 업데이트되었는지 확인
            Text scoreText = (Text) panel.getChildren().get(1);
            Text linesText = (Text) panel.getChildren().get(2);
            Text levelText = (Text) panel.getChildren().get(3);
            
            assertEquals("Score: 100", scoreText.getText());
            assertEquals("Lines: 10", linesText.getText());
            assertEquals("Level: 2", levelText.getText());
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("0점으로 통계 업데이트")
    void testUpdateStatsWithZero() {
        Platform.runLater(() -> {
            ScorePanel panel = new ScorePanel();
            
            panel.updateStats(0, 0, 1);
            
            Text scoreText = (Text) panel.getChildren().get(1);
            Text linesText = (Text) panel.getChildren().get(2);
            Text levelText = (Text) panel.getChildren().get(3);
            
            assertEquals("Score: 0", scoreText.getText());
            assertEquals("Lines: 0", linesText.getText());
            assertEquals("Level: 1", levelText.getText());
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("높은 점수로 통계 업데이트")
    void testUpdateStatsWithHighValues() {
        Platform.runLater(() -> {
            ScorePanel panel = new ScorePanel();
            
            panel.updateStats(999999, 5000, 99);
            
            Text scoreText = (Text) panel.getChildren().get(1);
            Text linesText = (Text) panel.getChildren().get(2);
            Text levelText = (Text) panel.getChildren().get(3);
            
            assertEquals("Score: 999999", scoreText.getText());
            assertEquals("Lines: 5000", linesText.getText());
            assertEquals("Level: 99", levelText.getText());
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("반복적인 통계 업데이트 테스트")
    void testMultipleStatsUpdates() {
        Platform.runLater(() -> {
            ScorePanel panel = new ScorePanel();
            
            panel.updateStats(100, 5, 1);
            panel.updateStats(200, 10, 2);
            panel.updateStats(300, 15, 3);
            
            Text scoreText = (Text) panel.getChildren().get(1);
            Text linesText = (Text) panel.getChildren().get(2);
            Text levelText = (Text) panel.getChildren().get(3);
            
            assertEquals("Score: 300", scoreText.getText());
            assertEquals("Lines: 15", linesText.getText());
            assertEquals("Level: 3", levelText.getText());
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("음수 값으로 통계 업데이트")
    void testUpdateStatsWithNegativeValues() {
        Platform.runLater(() -> {
            ScorePanel panel = new ScorePanel();
            
            // 음수 값도 허용됨 (경계 케이스 테스트)
            assertDoesNotThrow(() -> panel.updateStats(-100, -10, -1));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
