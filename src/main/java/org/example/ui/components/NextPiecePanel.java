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
    private final Canvas[] nextCanvases;

    public NextPiecePanel() {
        super(10);
        this.nextCanvases = new Canvas[5];

        Text title = new Text("Next");
        title.setFill(Color.WHITE);
        title.setFont(Font.font(16));
        getChildren().add(title);

        for (int i = 0; i < 5; i++) {
            nextCanvases[i] = new Canvas(4 * CELL_SIZE, 4 * CELL_SIZE);
            getChildren().add(nextCanvases[i]);
        }

        setStyle("-fx-background-color: #333; -fx-padding: 10;");
    }

    public void updateNextPieces(List<Tetromino> nextPieces) {
        for (int i = 0; i < nextCanvases.length && i < nextPieces.size(); i++) {
            drawTetromino(nextCanvases[i], nextPieces.get(i));
        }
    }

    private void drawTetromino(Canvas canvas, Tetromino tetromino) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int[][] shape = tetromino.getShape(0);
        Color color = tetromino.getColor();

        // Center the piece in the canvas
        double offsetX = (canvas.getWidth() - shape[0].length * CELL_SIZE) / 2;
        double offsetY = (canvas.getHeight() - shape.length * CELL_SIZE) / 2;

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
                }
            }
        }
    }
}