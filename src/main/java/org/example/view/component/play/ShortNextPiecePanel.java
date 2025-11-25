package org.example.view.component.play;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.model.TetrominoPosition;
import org.example.service.ColorManager;

/**
 * NextPiecePanel의 간략화 버전
 * 다음에 올 피스 하나만 보여줍니다.
 */
public class ShortNextPiecePanel extends VBox {
    private Canvas nextCanvas;
    private Text title;
    private double cellSize = 20;
    private final ColorManager colorManager;
    
    public ShortNextPiecePanel() {
        super(10);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10));
        
        this.colorManager = ColorManager.getInstance();
        
        // 타이틀
        this.title = new Text("Next");
        title.setFill(Color.WHITE);
        title.setFont(Font.font(16));
        getChildren().add(title);
        
        // 캔버스 초기화
        this.nextCanvas = new Canvas(4 * cellSize + 8, 4 * cellSize + 8);
        nextCanvas.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.3), 3, 0.5, 0, 0); " +
                          "-fx-background-color: #444; -fx-background-radius: 5;");
        getChildren().add(nextCanvas);
        
        setStyle("-fx-background-color: #333;");
        
        // 크기 변경 감지
        widthProperty().addListener((obs, oldVal, newVal) -> adjustCanvasSize());
        heightProperty().addListener((obs, oldVal, newVal) -> adjustCanvasSize());
    }
    
    /**
     * 선호하는 크기를 설정합니다.
     */
    public void setPreferredCanvasSize(double size) {
        setPrefWidth(size + 20);
        setPrefHeight(size + 50);
        adjustCanvasSize();
    }
    
    private void adjustCanvasSize() {
        double availableWidth = getPrefWidth() > 0 ? getPrefWidth() - 20 : getWidth() - 20;
        double availableHeight = getPrefHeight() > 0 ? getPrefHeight() - 60 : getHeight() - 60;
        
        if (availableWidth <= 0 || availableHeight <= 0) return;
        
        double canvasSize = Math.min(availableWidth, availableHeight);
        canvasSize = Math.max(60, canvasSize); // 최소 크기 60
        
        nextCanvas.setWidth(canvasSize);
        nextCanvas.setHeight(canvasSize);
        cellSize = (canvasSize - 8) / 4;
        
        // 폰트 크기도 조정
        double fontSize = Math.max(12, canvasSize / 6);
        title.setFont(Font.font(fontSize));
    }
    
    /**
     * 다음 피스를 업데이트합니다.
     * @param nextPiece 표시할 다음 피스 (null이면 빈 캔버스)
     */
    public void updateNextPiece(TetrominoPosition nextPiece) {
        GraphicsContext gc = nextCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, nextCanvas.getWidth(), nextCanvas.getHeight());
        
        // 창틀 효과를 위한 배경
        gc.setFill(Color.web("#444"));
        gc.fillRoundRect(0, 0, nextCanvas.getWidth(), nextCanvas.getHeight(), 5, 5);
        gc.setStroke(Color.web("#666"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(1, 1, nextCanvas.getWidth() - 2, nextCanvas.getHeight() - 2, 5, 5);
        
        if (nextPiece == null) return;
        
        drawTetromino(gc, nextPiece);
    }
    
    private void drawTetromino(GraphicsContext gc, TetrominoPosition piece) {
        int[][] shape = piece.getCurrentShape();
        Color color = piece.getDisplayColor(colorManager);
        
        // 피스를 캔버스 중앙에 배치
        double offsetX = (nextCanvas.getWidth() - shape[0].length * cellSize) / 2;
        double offsetY = (nextCanvas.getHeight() - shape.length * cellSize) / 2;
        
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    double x = offsetX + col * cellSize;
                    double y = offsetY + row * cellSize;
                    
                    // 블록 그리기
                    gc.setFill(color);
                    gc.fillRect(x, y, cellSize, cellSize);
                    
                    gc.setStroke(Color.DARKGRAY);
                    gc.setLineWidth(1);
                    gc.strokeRect(x, y, cellSize, cellSize);
                    
                    // 아이템 표시
                    org.example.model.ItemBlock item = piece.getItemAt(row, col);
                    if (item != null && item.isItem()) {
                        drawItemMark(gc, x, y, item.getSymbol());
                    }
                    
                    // 특수 조각 표시
                    var special = piece.getSpecialKind();
                    if (special == TetrominoPosition.SpecialKind.WEIGHT) {
                        drawItemMark(gc, x, y, 'W');
                    } else if (special == TetrominoPosition.SpecialKind.BOMB) {
                        drawItemMark(gc, x, y, 'B');
                    }
                }
            }
        }
    }
    
    private void drawItemMark(GraphicsContext gc, double x, double y, char symbol) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        
        double fontSize = cellSize * 0.7;
        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize));
        
        // 텍스트 중앙 정렬
        javafx.scene.text.Text text = new javafx.scene.text.Text(String.valueOf(symbol));
        text.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize));
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        
        double textX = x + (cellSize - textWidth) / 2;
        double textY = y + (cellSize + textHeight) / 2 - 2;
        
        // 테두리와 글자
        gc.strokeText(String.valueOf(symbol), textX, textY);
        gc.fillText(String.valueOf(symbol), textX, textY);
    }
}
