package org.example.view.component.play;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.model.TetrominoPosition;
import org.example.model.Tetromino;
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
class ShortNextPiecePanelIntegrationTest {

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
    void testPanelCreation() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertFalse(holder[0].getChildren().isEmpty());
    }

    @Test
    void testUpdateNextPiece_Null() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            holder[0].updateNextPiece(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPiece_IPiece() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.I, 0, 0, 0);
            holder[0].updateNextPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPiece_OPiece() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.O, 0, 0, 0);
            holder[0].updateNextPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPiece_AllTypes() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            
            for (Tetromino type : Tetromino.values()) {
                TetrominoPosition piece = new TetrominoPosition(type, 0, 0, 0);
                holder[0].updateNextPiece(piece);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPiece_WeightPiece() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            pieceHolder[0] = TetrominoPosition.createWeightPiece(0, 0);
            holder[0].updateNextPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPiece_BombPiece() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            pieceHolder[0] = TetrominoPosition.createBombPiece(0, 0);
            holder[0].updateNextPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPiece_MultipleUpdates() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            
            holder[0].updateNextPiece(new TetrominoPosition(Tetromino.I, 0, 0, 0));
            holder[0].updateNextPiece(new TetrominoPosition(Tetromino.O, 0, 0, 0));
            holder[0].updateNextPiece(null);
            holder[0].updateNextPiece(new TetrominoPosition(Tetromino.T, 0, 0, 0));
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testPanelLayout() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        final int[] childCountHolder = {0};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            childCountHolder[0] = holder[0].getChildren().size();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // title + canvas = 2 children
        assertEquals(2, childCountHolder[0]);
    }

    @Test
    void testPanelStyle() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        final String[] styleHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertTrue(holder[0].getStyleClass().contains("panel-hold"));
    }

    @Test
    void testSetPreferredCanvasSize() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            holder[0].setPreferredCanvasSize(100);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testCanvasSizeAdjustment() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            holder[0].setPrefWidth(150);
            holder[0].setPrefHeight(150);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPiece_RotatedPiece() throws Exception {
        final ShortNextPiecePanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new ShortNextPiecePanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.I, 0, 0, 1);
            holder[0].updateNextPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }
}
