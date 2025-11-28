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
    
    private AdderBoard adderBoard;
    
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
    
    public void setCanvasSize(double width, double height) {
        setWidth(width);
        setHeight(height);
        cellSize = width / WIDTH;
        draw();
    }
    
    public void updateBoard(AdderBoard adderBoard) {
        this.adderBoard = adderBoard;
        draw();
    }
    
    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        
        if (adderBoard == null) return;
        
        // Draw AdderBoard blocks
        // getLines()가 아래부터 채워서 반환하므로 그대로 그림
        // result[0] = 화면 위쪽, result[9] = 화면 아래쪽 (가장 최근 라인)
        int[][] lines = adderBoard.getLines();
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                int colorIndex = lines[row][col];
                if (colorIndex != 0) {
                    drawCell(gc, row, col, colorIndex);
                }
            }
        }
    }
    
    private void drawCell(GraphicsContext gc, int row, int col, int colorIndex) {
        double x = col * cellSize;
        double y = row * cellSize;
        
        // Get color from ColorManager
        Color color = colorManager.getColorFromIndex(colorIndex);
        
        // Fill cell
        gc.setFill(color);
        gc.fillRect(x, y, cellSize, cellSize);
        
        // Draw border
        gc.setStroke(BORDER_COLOR);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, cellSize, cellSize);
    }
}