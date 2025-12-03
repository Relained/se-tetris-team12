package org.example.view;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.model.AdderBoard;
import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LocalMultiPlayView integration tests with JavaFX
 * Tests view creation and update methods
 */
@ExtendWith(ApplicationExtension.class)
class LocalMultiPlayViewIntegrationTest {

    private LocalMultiPlayView view;
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
        
        view = new LocalMultiPlayView();
    }

    @Test
    @DisplayName("LocalMultiPlayView initializes correctly")
    void testInitialization() {
        assertNotNull(view, "View should be initialized");
    }

    @Test
    @DisplayName("LocalMultiPlayView creates view with NORMAL mode")
    void testCreateViewNormalMode() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Easy");
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0], "View pane should be created");
        assertTrue(rootHolder[0].getChildren().size() > 0, "View should have children");
    }

    @Test
    @DisplayName("LocalMultiPlayView creates view with ITEM mode")
    void testCreateViewItemMode() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Item", "Normal");
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0], "Item mode view should be created");
        assertTrue(rootHolder[0].getChildren().size() > 0, "View should have children");
    }

    @Test
    @DisplayName("LocalMultiPlayView creates view with TIME_ATTACK mode")
    void testCreateViewTimeAttackMode() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Time Attack", "Hard");
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0], "Time Attack mode view should be created");
        assertTrue(rootHolder[0].getChildren().size() > 0, "View should have children");
    }

    @Test
    @DisplayName("LocalMultiPlayView creates view with all difficulty levels")
    void testCreateViewAllDifficulties() {
        String[] difficulties = {"Easy", "Normal", "Hard"};
        
        for (String difficulty : difficulties) {
            final HBox[] rootHolder = {null};
            
            javafx.application.Platform.runLater(() -> {
                HBox root = view.createView("Normal", difficulty);
                rootHolder[0] = root;
            });
            WaitForAsyncUtils.waitForFxEvents();
            
            assertNotNull(rootHolder[0], difficulty + " difficulty view should be created");
        }
    }

    @Test
    @DisplayName("LocalMultiPlayView updates canvas size")
    void testUpdateCanvasSize() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.updateCanvasSize(testStage.getScene());
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Update canvas size should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayView updates Player 1 display")
    void testUpdatePlayer1Display() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                GameBoard board = new GameBoard();
                TetrominoPosition current = new TetrominoPosition(Tetromino.I, 0, 0, 0);
                TetrominoPosition ghost = new TetrominoPosition(Tetromino.I, 0, 10, 0);
                TetrominoPosition hold = new TetrominoPosition(Tetromino.O, 0, 0, 0);
                TetrominoPosition next = new TetrominoPosition(Tetromino.T, 0, 0, 0);
                AdderBoard adderBoard = new AdderBoard();
                
                view.updatePlayer1Display(board, current, ghost, hold, next, adderBoard, 1000, 10, 1, 180);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Player 1 display update should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayView updates Player 2 display")
    void testUpdatePlayer2Display() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                GameBoard board = new GameBoard();
                TetrominoPosition current = new TetrominoPosition(Tetromino.I, 0, 0, 0);
                TetrominoPosition ghost = new TetrominoPosition(Tetromino.I, 0, 10, 0);
                TetrominoPosition hold = new TetrominoPosition(Tetromino.O, 0, 0, 0);
                TetrominoPosition next = new TetrominoPosition(Tetromino.T, 0, 0, 0);
                AdderBoard adderBoard = new AdderBoard();
                
                view.updatePlayer2Display(board, current, ghost, hold, next, adderBoard, 2000, 20, 2, 150);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Player 2 display update should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayView updates Player 1 AdderBoard")
    void testUpdatePlayer1AdderBoard() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                AdderBoard adderBoard = new AdderBoard();
                view.updatePlayer1AdderBoard(adderBoard);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Player 1 AdderBoard update should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayView updates Player 2 AdderBoard")
    void testUpdatePlayer2AdderBoard() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                AdderBoard adderBoard = new AdderBoard();
                view.updatePlayer2AdderBoard(adderBoard);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Player 2 AdderBoard update should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayView sets show timer")
    void testSetShowTimer() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Time Attack", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.setShowTimer(true);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Set show timer should not throw exception");
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.setShowTimer(false);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Set hide timer should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayView handles null parameters in Player 1 update")
    void testUpdatePlayer1WithNullParameters() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.updatePlayer1Display(null, null, null, null, null, null, 0, 0, 1, 180);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Null parameters should be handled gracefully");
    }

    @Test
    @DisplayName("LocalMultiPlayView handles null parameters in Player 2 update")
    void testUpdatePlayer2WithNullParameters() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.updatePlayer2Display(null, null, null, null, null, null, 0, 0, 1, 180);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Null parameters should be handled gracefully");
    }

    @Test
    @DisplayName("LocalMultiPlayView multiple updates work correctly")
    void testMultipleUpdates() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                GameBoard board = new GameBoard();
                TetrominoPosition current = new TetrominoPosition(Tetromino.I, 0, 0, 0);
                
                for (int i = 0; i < 10; i++) {
                    view.updatePlayer1Display(board, current, null, null, null, null, i * 100, i, 1, 180);
                    view.updatePlayer2Display(board, current, null, null, null, null, i * 200, i * 2, 2, 150);
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Multiple updates should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayView canvas size adapts to scene resize")
    void testCanvasSizeAdaptation() {
        final HBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            HBox root = view.createView("Normal", "Normal");
            Scene scene = new Scene(root, 800, 600);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                testStage.setWidth(1200);
                testStage.setHeight(900);
                view.updateCanvasSize(testStage.getScene());
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Canvas should adapt to scene resize");
    }
}
