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

public class HoldPanel extends VBox {
    private Canvas holdCanvas;
    private Text title;
    private double cellSize = 20;

    public HoldPanel() {
        super(10);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10));
        
        this.title = new Text("Hold");
        title.setFill(Color.WHITE);
        title.setFont(Font.font(16));
        
        this.holdCanvas = new Canvas(4 * cellSize, 4 * cellSize);
        
        getChildren().addAll(title, holdCanvas);
        setStyle("-fx-background-color: #333;");
        
        // 가로 크기 변경 감지 - 가로 크기 기준으로 비율 조정
        widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            if (newWidth > 0) {
                adjustCanvasSizeByWidth(newWidth);
            }
        });
    }
    
    /**
     * 가로 크기를 기준으로 캔버스 크기를 조정합니다.
     * HoldPanel은 다른 위젯보다 조금 더 작은 크기를 유지합니다.
     */
    private void adjustCanvasSizeByWidth(double containerWidth) {
        double padding = 20; // 좌우 패딩
        double availableWidth = containerWidth - padding;
        
        if (availableWidth <= 0) return;
        
        // 정사각형 캔버스 크기 계산 (가로 크기의 70%로 더 작게 유지)
        double canvasSize = Math.max(50, availableWidth * 0.7);
        
        holdCanvas.setWidth(canvasSize);
        holdCanvas.setHeight(canvasSize);
        cellSize = canvasSize / 4;
        
        // 폰트 크기도 비례하여 조정
        double fontSize = Math.max(10, Math.min(18, canvasSize / 4));
        title.setFont(Font.font(fontSize));
    }

    private static final double BORDER_PADDING = 4; // 테두리 안쪽 패딩

    public void updateHoldPiece(TetrominoPosition holdPiece) {
        GraphicsContext gc = holdCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, holdCanvas.getWidth(), holdCanvas.getHeight());

        // 창틀 효과를 위한 배경
        gc.setFill(Color.web("#444"));
        gc.fillRoundRect(0, 0, holdCanvas.getWidth(), holdCanvas.getHeight(), 5, 5);
        gc.setStroke(Color.web("#666"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(1, 1, holdCanvas.getWidth() - 2, holdCanvas.getHeight() - 2, 5, 5);

        if (holdPiece == null) return;

        int[][] shape = holdPiece.getCurrentShape();
        Color color = holdPiece.getDisplayColor(org.example.service.ColorManager.getInstance());

        // 테두리 패딩을 고려한 사용 가능 영역
        double usableSize = holdCanvas.getWidth() - (BORDER_PADDING * 2);
        double drawCellSize = usableSize / 4;

        // Center the piece in the canvas (패딩 고려)
        double offsetX = BORDER_PADDING + (usableSize - shape[0].length * drawCellSize) / 2;
        double offsetY = BORDER_PADDING + (usableSize - shape.length * drawCellSize) / 2;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    double x = offsetX + col * drawCellSize;
                    double y = offsetY + row * drawCellSize;

                    gc.setFill(color);
                    gc.fillRect(x, y, drawCellSize, drawCellSize);

                    gc.setStroke(Color.DARKGRAY);
                    gc.setLineWidth(1);
                    gc.strokeRect(x, y, drawCellSize, drawCellSize);
                    
                    // 아이템 글자 표시
                    org.example.model.ItemBlock item = holdPiece.getItemAt(row, col);
                    if (item != null && item.isItem()) {
                        gc.setFill(Color.WHITE);
                        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, drawCellSize * 0.8));
                        gc.fillText(String.valueOf(item.getSymbol()), 
                                  x + drawCellSize * 0.2, 
                                  y + drawCellSize * 0.8);
                    }
                }
            }
        }
    }
}