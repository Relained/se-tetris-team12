package org.example.view.component.play;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.model.GameBoard;
import org.example.service.ColorManager;

/**
 * 상대방이 두 줄 이상 지웠을 때 내 보드에 추가될 줄을 미리 보여주는 캔버스
 * GameBoard의 adderBoard 정보를 사용하여 표시합니다.
 */
public class AdderCanvas extends Canvas {
    private double cellSize = 20;
    private static final Color BORDER_COLOR = Color.DARKGRAY;
    private static final Color BACKGROUND_COLOR = Color.rgb(40, 40, 40);
    private final ColorManager colorManager;
    
    private GameBoard gameBoard; // GameBoard 참조
    
    public AdderCanvas() {
        super(GameBoard.WIDTH * 30, 0); // 초기 높이는 0
        this.colorManager = ColorManager.getInstance();
        
        // 너비 변경 시 자동으로 cell size 재계산
        widthProperty().addListener((_, _, newWidth) -> {
            cellSize = newWidth.doubleValue() / GameBoard.WIDTH;
            draw();
        });
    }
    
    /**
     * GameBoard를 설정합니다.
     * @param gameBoard 추가될 줄 정보를 가진 GameBoard
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        updateDisplay();
    }
    
    /**
     * 화면을 업데이트합니다.
     */
    public void updateDisplay() {
        if (gameBoard == null) {
            setHeight(0);
            draw();
            return;
        }
        
        int lineCount = gameBoard.getPendingAdderLineCount();
        setHeight(lineCount * cellSize);
        draw();
    }
    
    /**
     * 캔버스 너비를 설정합니다.
     */
    public void setCanvasWidth(double width) {
        setWidth(width);
        cellSize = width / GameBoard.WIDTH;
        updateDisplay();
    }
    
    /**
     * 캔버스를 초기화합니다 (추가될 줄 없음).
     */
    public void clear() {
        if (gameBoard != null) {
            gameBoard.clearPendingAdderLines();
        }
        updateDisplay();
    }
    
    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        
        if (gameBoard == null) return;
        
        int[][] pendingLines = gameBoard.getPendingAdderLines();
        int lineCount = pendingLines.length;
        
        if (lineCount == 0) return;
        
        // 배경 그리기
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, getWidth(), getHeight());
        
        // 추가될 줄들 그리기
        for (int row = 0; row < lineCount; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                int cellValue = pendingLines[row][col];
                if (cellValue != 0) {
                    Color color = colorManager.getColorFromIndex(cellValue);
                    drawCell(gc, col, row, color);
                }
            }
        }
        
        // 그리드 그리기
        drawGrid(gc, lineCount);
        
        // 경고 표시 (반투명 빨간색 오버레이)
        gc.setFill(Color.rgb(255, 0, 0, 0.2));
        gc.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void drawCell(GraphicsContext gc, int x, int y, Color color) {
        double pixelX = x * cellSize;
        double pixelY = y * cellSize;
        
        gc.setFill(color);
        gc.fillRect(pixelX, pixelY, cellSize, cellSize);
        
        gc.setStroke(BORDER_COLOR);
        gc.setLineWidth(1);
        gc.strokeRect(pixelX, pixelY, cellSize, cellSize);
    }
    
    private void drawGrid(GraphicsContext gc, int lineCount) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(0.5);
        
        // 세로 선
        for (int i = 0; i <= GameBoard.WIDTH; i++) {
            double x = i * cellSize;
            gc.strokeLine(x, 0, x, getHeight());
        }
        
        // 가로 선
        for (int i = 0; i <= lineCount; i++) {
            double y = i * cellSize;
            gc.strokeLine(0, y, getWidth(), y);
        }
    }
    
    /**
     * 현재 추가 예정인 줄의 개수를 반환합니다.
     */
    public int getPendingLineCount() {
        if (gameBoard == null) return 0;
        return gameBoard.getPendingAdderLineCount();
    }
}
