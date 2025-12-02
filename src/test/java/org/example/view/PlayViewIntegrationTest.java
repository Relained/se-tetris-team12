package org.example.view;

import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.example.service.ColorManager;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(ApplicationExtension.class)
class PlayViewIntegrationTest {

    private PlayView view;
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
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        view = new PlayView();
    }

    @Test
    void testCreateViewNormalMode() throws Exception {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("NORMAL", "Easy");
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0]);
        assertEquals(2, rootHolder[0].getChildren().size());
    }

    @Test
    void testCreateViewItemMode() throws Exception {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("ITEM", "Hard");
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0]);
    }

    @Test
    void testGetComponentsAfterCreate() throws Exception {
        javafx.application.Platform.runLater(() -> {
            view.createView("NORMAL", "Normal");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(view.getGameCanvas());
        assertNotNull(view.getHoldPanel());
        assertNotNull(view.getNextPanel());
        assertNotNull(view.getScorePanel());
    }

    @Test
    void testUpdateDisplay() throws Exception {
        javafx.application.Platform.runLater(() -> {
            view.createView("NORMAL", "Normal");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        GameBoard board = new GameBoard();
        TetrominoPosition current = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        List<TetrominoPosition> nextQueue = new ArrayList<>();
        nextQueue.add(new TetrominoPosition(Tetromino.I, 0, 0, 0));
        
        javafx.application.Platform.runLater(() -> {
            view.updateDisplay(board, current, null, null, nextQueue, 1000, 5, 3, -1L);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(view.getGameCanvas());
    }

    @Test
    void testUpdateDisplayMultipleTimes() throws Exception {
        javafx.application.Platform.runLater(() -> {
            view.createView("ITEM", "Normal");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        GameBoard board = new GameBoard();
        TetrominoPosition piece = new TetrominoPosition(Tetromino.Z, 4, 12, 0);
        List<TetrominoPosition> queue = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            final int score = i * 100;
            javafx.application.Platform.runLater(() -> {
                view.updateDisplay(board, piece, piece, null, queue, score, 0, 1, -1L);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }
        
        assertNotNull(view);
    }

    @Test
    void testDifferentModes() throws Exception {
        javafx.application.Platform.runLater(() -> {
            view.createView("NORMAL", "Easy");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(view.getScorePanel());
        
        PlayView itemView = new PlayView();
        javafx.application.Platform.runLater(() -> {
            itemView.createView("ITEM", "Hard");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(itemView.getScorePanel());
    }
}
