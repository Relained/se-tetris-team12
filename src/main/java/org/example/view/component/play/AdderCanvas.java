package org.example.view.component.play;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.example.service.ColorManager;
import org.example.model.AdderBoard;

/**
 * AdderBoard의 내용을 표시하는 캔버스 컴포넌트입니다.
 * 10x10 크기의 고정 영역을 표시합니다.
 * getLines()가 아래부터 채워서 반환하므로 그대로 그립니다.
 * - result[0] = 화면 위쪽 (빈 줄일 수 있음)
 * - result[9] = 화면 아래쪽 (가장 최근 라인)
 */
public class AdderCanvas extends Canvas {
    private double cellSize = 30;
    private static final Color BORDER_COLOR = Color.DARKGRAY;
    private final ColorManager colorManager;
    
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;  // 최대 10줄
    private static final double BORDER_PADDING = 4; // 테두리 안쪽 패딩
    
    private int[][] adderBoardLines;
    
    public AdderCanvas() {
        super(WIDTH * 30, HEIGHT * 30);
        this.colorManager = ColorManager.getInstance();
        
        // 너비 변경 시 자동으로 cell size 재계산
        widthProperty().addListener((_, _, newWidth) -> {
            cellSize = newWidth.doubleValue() / WIDTH;
            setHeight(HEIGHT * cellSize);
            draw();
        });
    }
    
    public void setCanvasHeight(double height) {
        setHeight(height);
    }
    
    /**
     * 캔버스 크기를 설정합니다.
     * 10x10 그리드이므로 정사각형 비율(1:1)을 유지합니다.
     * width를 기준으로 cellSize를 계산하고, height도 동일하게 설정합니다.
     */
    public void setCanvasSize(double width, double height) {
        cellSize = width / WIDTH;
        double squareSize = WIDTH * cellSize; // 10x10이므로 정사각형
        setWidth(squareSize);
        setHeight(squareSize);
        draw();
    }
    
    public void updateBoard(int[][] adderBoardLines) {
        this.adderBoardLines = adderBoardLines;
        draw();
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        
        // 배경 및 테두리 그리기
        gc.setFill(Color.web("#222"));
        gc.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        gc.setStroke(Color.web("#666"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 5, 5);

        if (adderBoardLines == null) 
            return;
                
        // 테두리 패딩을 고려한 셀 크기 계산
        double usableWidth = getWidth() - (BORDER_PADDING * 2);
        double usableHeight = getHeight() - (BORDER_PADDING * 2);
        double drawCellSize = Math.min(usableWidth / WIDTH, usableHeight / HEIGHT);
        
        // Draw AdderBoard blocks
        // getLines()가 아래부터 채워서 반환하므로 그대로 그림
        // result[0] = 화면 위쪽, result[9] = 화면 아래쪽 (가장 최근 라인)
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                int colorIndex = adderBoardLines[row][col];
                if (colorIndex != 0) {
                    drawCell(gc, row, col, colorIndex, drawCellSize);
                }
            }
        }
    }
    
    private void drawCell(GraphicsContext gc, int row, int col, int colorIndex, double drawCellSize) {
        double x = BORDER_PADDING + col * drawCellSize;
        double y = BORDER_PADDING + row * drawCellSize;
        
        // Get color from ColorManager
        Color color = colorManager.getColorFromIndex(colorIndex);
        
        // Fill cell
        gc.setFill(color);
        gc.fillRect(x, y, drawCellSize, drawCellSize);
        
        // Draw border
        gc.setStroke(BORDER_COLOR);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, drawCellSize, drawCellSize);
    }
}