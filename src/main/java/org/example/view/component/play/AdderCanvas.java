package org.example.view.component.play;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.example.service.ColorManager;
import org.example.model.AdderBoard;

/**
 * AdderBoard의 내용을 표시하는 캔버스 컴포넌트입니다.
 * 10x10 크기의 고정 영역을 표시합니다.
 * 새로운 라인은 위쪽에 쌓이고, 오래된 라인은 아래쪽에 있습니다.
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
        
        // 높이 변경 시 자동으로 cell size 재계산
        heightProperty().addListener((_, _, newHeight) -> {
            cellSize = newHeight.doubleValue() / HEIGHT;
            setWidth(WIDTH * cellSize);
            draw();
        });
    }
    
    public void setCanvasHeight(double height) {
        setHeight(height);
    }
    
    public void setCanvasSize(double width, double height) {
        // 테트리스 비율 (가로:세로 = 1:2) 유지
        double widthBasedHeight = width * 2;
        double heightBasedWidth = height * 0.5;
        
        // 더 작은 값으로 조정하여 비율 유지
        if (widthBasedHeight <= height) {
            setWidth(width);
            setHeight(widthBasedHeight);
            cellSize = width / WIDTH;
        } else {
            setWidth(heightBasedWidth);
            setHeight(height);
            cellSize = height / HEIGHT;
        }
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
        // lines[0] = 가장 오래된 라인 (GameBoard에 가장 먼저 추가될 라인) -> 화면 아래
        // lines[lineCount-1] = 가장 최근 라인 -> 화면 위
        int[][] lines = adderBoard.getLines();
        int lineCount = adderBoard.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            // lines[i]를 화면의 (HEIGHT - lineCount + i) 행에 그림
            // 즉, 오래된 라인은 아래에, 최근 라인은 위에
            int drawRow = HEIGHT - lineCount + i;
            for (int col = 0; col < WIDTH; col++) {
                int colorIndex = lines[i][col];
                if (colorIndex != 0) {
                    drawCell(gc, drawRow, col, colorIndex);
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