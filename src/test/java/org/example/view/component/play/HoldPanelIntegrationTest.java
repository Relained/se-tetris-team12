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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class HoldPanelIntegrationTest {

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
        final HoldPanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertFalse(holder[0].getChildren().isEmpty());
    }

    @Test
    void testUpdateHoldPiece_Null() throws Exception {
        final HoldPanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            holder[0].updateHoldPiece(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateHoldPiece_IPiece() throws Exception {
        final HoldPanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.I, 0, 0, 0);
            holder[0].updateHoldPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateHoldPiece_OPiece() throws Exception {
        final HoldPanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.O, 0, 0, 0);
            holder[0].updateHoldPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateHoldPiece_TPiece() throws Exception {
        final HoldPanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.T, 0, 0, 0);
            holder[0].updateHoldPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateHoldPiece_AllTypes() throws Exception {
        final HoldPanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            
            // 모든 테트로미노 타입 테스트
            for (Tetromino type : Tetromino.values()) {
                TetrominoPosition piece = new TetrominoPosition(type, 0, 0, 0);
                holder[0].updateHoldPiece(piece);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateHoldPiece_WithItem() throws Exception {
        final HoldPanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.T, 0, 0, 0);
            
            // 아이템 추가 - 첫 번째 블록에 LINE_CLEAR 아이템 부착
            pieceHolder[0].setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
            
            holder[0].updateHoldPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateHoldPiece_MultipleItems() throws Exception {
        final HoldPanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.I, 0, 0, 0);
            
            // 아이템 추가 - 첫 번째 블록에만 아이템 부착 (한 번에 하나만 가능)
            pieceHolder[0].setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
            
            holder[0].updateHoldPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateHoldPiece_MultipleUpdates() throws Exception {
        final HoldPanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            
            // 여러 번 업데이트
            holder[0].updateHoldPiece(new TetrominoPosition(Tetromino.I, 0, 0, 0));
            holder[0].updateHoldPiece(new TetrominoPosition(Tetromino.O, 0, 0, 0));
            holder[0].updateHoldPiece(new TetrominoPosition(Tetromino.T, 0, 0, 0));
            holder[0].updateHoldPiece(null);
            holder[0].updateHoldPiece(new TetrominoPosition(Tetromino.L, 0, 0, 0));
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testPanelLayout() throws Exception {
        final HoldPanel[] holder = {null};
        final int[] childCountHolder = {0};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            childCountHolder[0] = holder[0].getChildren().size();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // title + canvas = 2 children
        assertEquals(2, childCountHolder[0]);
    }

    @Test
    void testPanelStyle() throws Exception {
        final HoldPanel[] holder = {null};
        final String[] styleHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            styleHolder[0] = holder[0].getStyle();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(styleHolder[0]);
        assertTrue(styleHolder[0].contains("#333"));
    }

    @Test
    void testCanvasSizeAdjustment() throws Exception {
        final HoldPanel[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            holder[0].setPrefWidth(200);
            holder[0].setPrefHeight(200);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateHoldPiece_RotatedPiece() throws Exception {
        final HoldPanel[] holder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new HoldPanel();
            pieceHolder[0] = new TetrominoPosition(Tetromino.I, 0, 0, 1);  // 90도 회전
            holder[0].updateHoldPiece(pieceHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }
}
