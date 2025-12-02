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
class AdderCanvasIntegrationTest {

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
    void testCanvasCreation() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertEquals(10 * 30, holder[0].getWidth());
        assertEquals(10 * 30, holder[0].getHeight());
    }

    @Test
    void testSetCanvasHeight() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            holder[0].setCanvasHeight(400);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(400, holder[0].getHeight());
    }

    @Test
    void testSetCanvasSize_MaintainsRatio() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            holder[0].setCanvasSize(200, 500);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // 정사각형 비율 (1:1) 유지
        assertEquals(200, holder[0].getWidth());
        assertEquals(200, holder[0].getHeight());
    }

    @Test
    void testSetCanvasSize_HeightLimited() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            holder[0].setCanvasSize(300, 400);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // 정사각형 비율
        assertEquals(300, holder[0].getWidth());
        assertEquals(300, holder[0].getHeight());
    }

    @Test
    void testUpdateBoard_NullBoard() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            holder[0].updateBoard(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_EmptyBoard() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            int[][] emptyLines = new int[10][10];
            holder[0].updateBoard(emptyLines);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithSingleLine() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            
            // 한 줄 추가 (맨 아래줄, 모두 1로 채움)
            int[][] lines = new int[10][10];
            for (int i = 0; i < 10; i++) {
                lines[9][i] = 1; // 색상 인덱스 1
            }
            
            holder[0].updateBoard(lines);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithMultipleLines() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            
            // 여러 줄 추가 (아래 3줄)
            int[][] lines = new int[10][10];
            for (int lineIdx = 7; lineIdx < 10; lineIdx++) {
                for (int i = 0; i < 10; i++) {
                    lines[lineIdx][i] = ((lineIdx - 7) % 7) + 1; // 색상 인덱스 1~7 순환
                }
            }
            
            holder[0].updateBoard(lines);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithPartialLine() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            
            // 부분적으로 채워진 줄 (맨 아래줄)
            int[][] lines = new int[10][10];
            lines[9][0] = 1;
            lines[9][2] = 2;
            lines[9][4] = 3;
            lines[9][9] = 7;
            // 나머지는 0 (빈 칸)
            
            holder[0].updateBoard(lines);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_AllColors() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            
            // 모든 색상 인덱스 (1~7) 테스트
            int[][] lines = new int[10][10];
            for (int colorIdx = 1; colorIdx <= 7; colorIdx++) {
                for (int i = 0; i < 10; i++) {
                    lines[colorIdx + 2][i] = colorIdx;
                }
            }
            
            holder[0].updateBoard(lines);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testCanvasSizeAfterMultipleAdjustments() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            holder[0].setCanvasSize(200, 500);
            holder[0].setCanvasSize(300, 400);
            holder[0].setCanvasSize(150, 300);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(150, holder[0].getWidth());
        assertEquals(150, holder[0].getHeight());
    }

    @Test
    void testMultipleUpdates() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            
            // 첫 번째 업데이트 - 빈 보드
            int[][] emptyLines = new int[10][10];
            holder[0].updateBoard(emptyLines);
            
            // 두 번째 업데이트 - 1줄 추가
            int[][] lines1 = new int[10][10];
            for (int i = 0; i < 10; i++) {
                lines1[9][i] = 1;
            }
            holder[0].updateBoard(lines1);
            
            // 세 번째 업데이트 - 2줄
            int[][] lines2 = new int[10][10];
            for (int i = 0; i < 10; i++) {
                lines2[8][i] = 1;
                lines2[9][i] = 2;
            }
            holder[0].updateBoard(lines2);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_MaxLines() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            
            // 최대 10줄까지 추가 (HEIGHT = 10)
            int[][] lines = new int[10][10];
            for (int lineIdx = 0; lineIdx < 10; lineIdx++) {
                for (int i = 0; i < 10; i++) {
                    lines[lineIdx][i] = (lineIdx % 7) + 1;
                }
            }
            
            holder[0].updateBoard(lines);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testWidthPropertyListener() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            // widthProperty listener가 트리거되도록 너비 변경
            holder[0].setWidth(500);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // cellSize가 재계산되고 height가 조정되었는지 확인
        assertEquals(500, holder[0].getWidth());
        // cellSize = 500 / 10 = 50
        // height = 10 * 50 = 500 (정사각형)
        assertEquals(500, holder[0].getHeight());
    }

    @Test
    void testUpdateBoard_WithZeroColorIndex() throws Exception {
        final AdderCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new AdderCanvas();
            
            // 색상 인덱스 0인 셀들 (빈 칸, 그려지지 않아야 함)
            int[][] lines = new int[10][10];
            // 모두 0으로 초기화됨
            
            holder[0].updateBoard(lines);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }
}
