package org.example.view.component.play;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.model.TetrominoPosition;
import org.example.model.Tetromino;
import org.example.model.ItemBlock;
import org.example.service.ColorManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class NextPiecePanelIntegrationTest {

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
        final NextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertFalse(holder[0].getChildren().isEmpty());
    }

    @Test
    void testPanelCreation_VerticalMode() throws Exception {
        final NextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            holder[0].setHorizontalMode(false);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testPanelCreation_HorizontalMode() throws Exception {
        final NextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            holder[0].setHorizontalMode(true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testToggleHorizontalMode() throws Exception {
        final NextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            holder[0].setHorizontalMode(false);
            holder[0].setHorizontalMode(true);
            holder[0].setHorizontalMode(false);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPieces_OnePiece() throws Exception {
        final NextPiecePanel[] holder = {null};
        final List<TetrominoPosition> nextPieces = new ArrayList<>();
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            nextPieces.add(new TetrominoPosition(Tetromino.I, 0, 0, 0));
            holder[0].updateNextPieces(nextPieces);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPieces_FivePieces() throws Exception {
        final NextPiecePanel[] holder = {null};
        final List<TetrominoPosition> nextPieces = new ArrayList<>();
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            nextPieces.add(new TetrominoPosition(Tetromino.I, 0, 0, 0));
            nextPieces.add(new TetrominoPosition(Tetromino.O, 0, 0, 0));
            nextPieces.add(new TetrominoPosition(Tetromino.T, 0, 0, 0));
            nextPieces.add(new TetrominoPosition(Tetromino.L, 0, 0, 0));
            nextPieces.add(new TetrominoPosition(Tetromino.J, 0, 0, 0));
            holder[0].updateNextPieces(nextPieces);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPieces_MoreThanFive() throws Exception {
        final NextPiecePanel[] holder = {null};
        final List<TetrominoPosition> nextPieces = new ArrayList<>();
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            for (Tetromino type : Tetromino.values()) {
                nextPieces.add(new TetrominoPosition(type, 0, 0, 0));
            }
            holder[0].updateNextPieces(nextPieces);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPieces_WithItems() throws Exception {
        final NextPiecePanel[] holder = {null};
        final List<TetrominoPosition> nextPieces = new ArrayList<>();
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            
            TetrominoPosition piece = new TetrominoPosition(Tetromino.T, 0, 0, 0);
            // 아이템 추가 - 첫 번째 블록에 LINE_CLEAR 아이템 부착
            piece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
            nextPieces.add(piece);
            
            holder[0].updateNextPieces(nextPieces);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPieces_HorizontalMode() throws Exception {
        final NextPiecePanel[] holder = {null};
        final List<TetrominoPosition> nextPieces = new ArrayList<>();
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            holder[0].setHorizontalMode(true);
            
            nextPieces.add(new TetrominoPosition(Tetromino.I, 0, 0, 0));
            nextPieces.add(new TetrominoPosition(Tetromino.O, 0, 0, 0));
            nextPieces.add(new TetrominoPosition(Tetromino.T, 0, 0, 0));
            
            holder[0].updateNextPieces(nextPieces);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPieces_MultipleUpdates() throws Exception {
        final NextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            
            List<TetrominoPosition> nextPieces1 = new ArrayList<>();
            nextPieces1.add(new TetrominoPosition(Tetromino.I, 0, 0, 0));
            holder[0].updateNextPieces(nextPieces1);
            
            List<TetrominoPosition> nextPieces2 = new ArrayList<>();
            nextPieces2.add(new TetrominoPosition(Tetromino.O, 0, 0, 0));
            nextPieces2.add(new TetrominoPosition(Tetromino.T, 0, 0, 0));
            holder[0].updateNextPieces(nextPieces2);
            
            List<TetrominoPosition> nextPieces3 = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                nextPieces3.add(new TetrominoPosition(Tetromino.values()[i % 7], 0, 0, 0));
            }
            holder[0].updateNextPieces(nextPieces3);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testPanelLayout_VerticalMode() throws Exception {
        final NextPiecePanel[] holder = {null};
        final int[] childCountHolder = {0};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            holder[0].setHorizontalMode(false);
            childCountHolder[0] = holder[0].getChildren().size();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // title + 5 canvases = 6 children in vertical mode
        assertEquals(6, childCountHolder[0]);
    }

    @Test
    void testPanelLayout_HorizontalMode() throws Exception {
        final NextPiecePanel[] holder = {null};
        final int[] childCountHolder = {0};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            holder[0].setHorizontalMode(true);
            childCountHolder[0] = holder[0].getChildren().size();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // title + canvasContainer = 2 children in horizontal mode
        assertEquals(2, childCountHolder[0]);
    }

    @Test
    void testPanelStyle() throws Exception {
        final NextPiecePanel[] holder = {null};
        final String[] styleHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertTrue(holder[0].getStyleClass().contains("panel-next-piece"));
    }

    @Test
    void testCanvasSizeAdjustment() throws Exception {
        final NextPiecePanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            holder[0].setPrefWidth(200);
            holder[0].setPrefHeight(600);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPieces_AllPieceTypes() throws Exception {
        final NextPiecePanel[] holder = {null};
        final List<TetrominoPosition> nextPieces = new ArrayList<>();
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            
            for (Tetromino type : Tetromino.values()) {
                nextPieces.add(new TetrominoPosition(type, 0, 0, 0));
            }
            
            holder[0].updateNextPieces(nextPieces);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateNextPieces_RotatedPieces() throws Exception {
        final NextPiecePanel[] holder = {null};
        final List<TetrominoPosition> nextPieces = new ArrayList<>();
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            
            TetrominoPosition piece1 = new TetrominoPosition(Tetromino.I, 0, 0, 1);
            nextPieces.add(piece1);
            
            TetrominoPosition piece2 = new TetrominoPosition(Tetromino.T, 0, 0, 2);
            nextPieces.add(piece2);
            
            holder[0].updateNextPieces(nextPieces);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testModeSwitch_WithPieces() throws Exception {
        final NextPiecePanel[] holder = {null};
        final List<TetrominoPosition> nextPieces = new ArrayList<>();
        
        Platform.runLater(() -> {
            holder[0] = new NextPiecePanel();
            
            nextPieces.add(new TetrominoPosition(Tetromino.I, 0, 0, 0));
            nextPieces.add(new TetrominoPosition(Tetromino.O, 0, 0, 0));
            nextPieces.add(new TetrominoPosition(Tetromino.T, 0, 0, 0));
            
            holder[0].setHorizontalMode(false);
            holder[0].updateNextPieces(nextPieces);
            
            holder[0].setHorizontalMode(true);
            holder[0].updateNextPieces(nextPieces);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }
}
