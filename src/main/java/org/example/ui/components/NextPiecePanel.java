package org.example.ui.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.model.Tetromino;

import java.util.List;

public class NextPiecePanel extends VBox {
    private static final int CELL_SIZE = 20;
    private static final int SMALL_CELL_SIZE = 12;
    private final Canvas[] nextCanvases;

    public NextPiecePanel() {
        super(10);
        this.nextCanvases = new Canvas[5];

        Text title = new Text("Next");
        title.setFill(Color.WHITE);
        title.setFont(Font.font(16));
        getChildren().add(title);

        // 첫 번째 캔버스는 크게, 나머지는 작게 생성
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                // 첫 번째 피스는 크기 그대로 + 창틀 효과를 위해 약간 더 크게
                nextCanvases[i] = new Canvas(4 * CELL_SIZE + 8, 4 * CELL_SIZE + 8);
                nextCanvases[i].setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.3), 3, 0.5, 0, 0); " +
                                       "-fx-background-color: #444; -fx-background-radius: 5;");
            } else {
                // 나머지 피스들은 작게, 하지만 충분히 크게 설정하여 짤리지 않도록
                nextCanvases[i] = new Canvas(4 * SMALL_CELL_SIZE + 4, 4 * SMALL_CELL_SIZE + 4);
            }
            getChildren().add(nextCanvases[i]);
        }

        setStyle("-fx-background-color: #333; -fx-padding: 10;");
    }

    public void updateNextPieces(List<Tetromino> nextPieces) {
        for (int i = 0; i < nextCanvases.length && i < nextPieces.size(); i++) {
            drawTetromino(nextCanvases[i], nextPieces.get(i), i);
        }
    }

    private void drawTetromino(Canvas canvas, Tetromino tetromino, int index) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 첫 번째 피스인 경우 창틀 효과를 위한 배경 그리기
        if (index == 0) {
            gc.setFill(Color.web("#444"));
            gc.fillRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 5, 5);
            gc.setStroke(Color.web("#666"));
            gc.setLineWidth(2);
            gc.strokeRoundRect(1, 1, canvas.getWidth() - 2, canvas.getHeight() - 2, 5, 5);
        }

        int[][] shape = tetromino.getShape(0);
        Color color = tetromino.getColor();

        // 인덱스에 따라 다른 셀 크기 사용
        double cellSize = (index == 0) ? CELL_SIZE : SMALL_CELL_SIZE;
        
        // Center the piece in the canvas
        double offsetX = (canvas.getWidth() - shape[0].length * cellSize) / 2;
        double offsetY = (canvas.getHeight() - shape.length * cellSize) / 2;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    double x = offsetX + col * cellSize;
                    double y = offsetY + row * cellSize;

                    gc.setFill(color);
                    gc.fillRect(x, y, cellSize, cellSize);

                    gc.setStroke(Color.DARKGRAY);
                    gc.setLineWidth(1);
                    gc.strokeRect(x, y, cellSize, cellSize);
                }
            }
        }
    }
}