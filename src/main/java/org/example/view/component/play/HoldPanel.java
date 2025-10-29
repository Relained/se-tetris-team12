package org.example.view.component.play;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.model.TetrominoPosition;

public class HoldPanel extends VBox {
    private static final int CELL_SIZE = 20;
    private final Canvas holdCanvas;

    public HoldPanel() {
        super(10);
        this.holdCanvas = new Canvas(4 * CELL_SIZE, 4 * CELL_SIZE);

        Text title = new Text("Hold");
        title.setFill(Color.WHITE);
        title.setFont(Font.font(16));
        getChildren().addAll(title, holdCanvas);

        setStyle("-fx-background-color: #333; -fx-padding: 10;");
    }

    public void updateHoldPiece(TetrominoPosition holdPiece) {
        GraphicsContext gc = holdCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, holdCanvas.getWidth(), holdCanvas.getHeight());

        if (holdPiece == null) return;

        // getCurrentShape()를 사용하여 customShape도 반영
        int[][] shape = holdPiece.getCurrentShape();
        // getDisplayColor()를 사용하여 customColor도 반영
        Color color = holdPiece.getDisplayColor(org.example.service.ColorManager.getInstance());

        // Center the piece in the canvas
        double offsetX = (holdCanvas.getWidth() - shape[0].length * CELL_SIZE) / 2;
        double offsetY = (holdCanvas.getHeight() - shape.length * CELL_SIZE) / 2;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    double x = offsetX + col * CELL_SIZE;
                    double y = offsetY + row * CELL_SIZE;

                    gc.setFill(color);
                    gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                    gc.setStroke(Color.DARKGRAY);
                    gc.setLineWidth(1);
                    gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
                    
                    // 아이템 글자 표시
                    org.example.model.ItemBlock item = holdPiece.getItemAt(row, col);
                    if (item != null && item.isItem()) {
                        gc.setFill(Color.WHITE);
                        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, CELL_SIZE * 0.8));
                        gc.fillText(String.valueOf(item.getSymbol()), 
                                  x + CELL_SIZE * 0.2, 
                                  y + CELL_SIZE * 0.8);
                    }
                }
            }
        }
    }
}