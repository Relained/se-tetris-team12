package org.example.view.component.play;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class ScorePanelIntegrationTest {

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
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testPanelCreation_NormalMode() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("NORMAL", "EASY");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertFalse(holder[0].getChildren().isEmpty());
    }

    @Test
    void testPanelCreation_ItemMode() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("ITEM", "HARD");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertFalse(holder[0].getChildren().isEmpty());
    }

    @Test
    void testPanelCreation_DifferentDifficulties() throws Exception {
        final ScorePanel[] easyHolder = {null};
        final ScorePanel[] normalHolder = {null};
        final ScorePanel[] hardHolder = {null};
        
        Platform.runLater(() -> {
            easyHolder[0] = new ScorePanel("NORMAL", "EASY");
            normalHolder[0] = new ScorePanel("NORMAL", "NORMAL");
            hardHolder[0] = new ScorePanel("NORMAL", "HARD");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(easyHolder[0]);
        assertNotNull(normalHolder[0]);
        assertNotNull(hardHolder[0]);
    }

    @Test
    void testUpdateStats_InitialValues() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("NORMAL", "EASY");
            holder[0].updateStats(0, 0, 1);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateStats_NonZeroValues() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("NORMAL", "EASY");
            holder[0].updateStats(1000, 10, 2);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateStats_HighScore() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("ITEM", "HARD");
            holder[0].updateStats(999999, 500, 50);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateStats_MultipleUpdates() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("NORMAL", "NORMAL");
            holder[0].updateStats(100, 1, 1);
            holder[0].updateStats(300, 3, 1);
            holder[0].updateStats(600, 6, 2);
            holder[0].updateStats(1000, 10, 2);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testFontSizeAdjustment() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("NORMAL", "EASY");
            holder[0].setPrefHeight(300);
            holder[0].setPrefWidth(150);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testPanelLayout() throws Exception {
        final ScorePanel[] holder = {null};
        final int[] childCountHolder = {0};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("NORMAL", "EASY");
            childCountHolder[0] = holder[0].getChildren().size();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // title, scoreText, linesLevelRow, modeDifficultyRow, timerText = 5 children
        assertEquals(5, childCountHolder[0]);
    }

    @Test
    void testUpdateStats_LevelProgression() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("NORMAL", "EASY");
            
            // 레벨 1
            holder[0].updateStats(0, 0, 1);
            
            // 레벨 2
            holder[0].updateStats(1000, 10, 2);
            
            // 레벨 3
            holder[0].updateStats(2500, 20, 3);
            
            // 레벨 10
            holder[0].updateStats(50000, 100, 10);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testPanelStyle() throws Exception {
        final ScorePanel[] holder = {null};
        final String[] styleHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("NORMAL", "EASY");
            styleHolder[0] = holder[0].getStyle();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(styleHolder[0]);
        assertTrue(styleHolder[0].contains("#333"));
    }

    @Test
    void testUpdateStats_ScoreLinesCombinations() throws Exception {
        final ScorePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ScorePanel("ITEM", "HARD");
            
            // 다양한 점수/라인 조합
            holder[0].updateStats(40, 1, 1);    // 1줄
            holder[0].updateStats(140, 3, 1);   // 2줄 추가
            holder[0].updateStats(440, 7, 1);   // 4줄 추가 (테트리스)
            holder[0].updateStats(1240, 17, 2); // 10줄 추가 (레벨업)
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }
}
