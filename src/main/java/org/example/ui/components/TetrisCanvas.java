package org.example.ui.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.example.model.ColorModel;
import org.example.model.GameBoard;
import org.example.model.TetrominoPosition;

public class TetrisCanvas extends Canvas {
    private static final int CELL_SIZE = 30;
    private static final Color BORDER_COLOR = Color.DARKGRAY;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color GHOST_COLOR = Color.GRAY;

    private GameBoard board;
    private TetrominoPosition currentPiece;
    private TetrominoPosition ghostPiece;

    public TetrisCanvas() {
        super(GameBoard.WIDTH * CELL_SIZE, GameBoard.HEIGHT * CELL_SIZE);
    }

    public void updateBoard(GameBoard board, TetrominoPosition currentPiece, TetrominoPosition ghostPiece) {
        this.board = board;
        this.currentPiece = currentPiece;
        this.ghostPiece = ghostPiece;
        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Draw background
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, getWidth(), getHeight());

        if (board == null) return;

        // Draw placed blocks
        int[][] visibleBoard = board.getVisibleBoard();
        for (int row = 0; row < GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                if (visibleBoard[row][col] == 0) 
                    continue;
                Color color = ColorModel.getColorFromIndex(visibleBoard[row][col]);
                drawCell(gc, col, row, color);
            }
        }

        // Draw ghost piece
        if (ghostPiece != null) {
            drawPiece(gc, ghostPiece, GHOST_COLOR, true);
        }

        // Draw current piece
        if (currentPiece != null) {
            Color pieceColor = currentPiece.getType().getColor();
            drawPiece(gc, currentPiece, pieceColor, false);
        }

        // Draw grid
        drawGrid(gc);
    }

    private void drawPiece(GraphicsContext gc, TetrominoPosition piece, Color color, boolean isGhost) {
        int[][] shape = piece.getCurrentShape();
        int startX = piece.getX();
        int startY = piece.getY() - GameBoard.BUFFER_ZONE; // Adjust for buffer zone

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int x = startX + col;
                    int y = startY + row;
                    if (x >= 0 && x < GameBoard.WIDTH && y >= 0 && y < GameBoard.HEIGHT) {
                        if (isGhost) {
                            drawGhostCell(gc, x, y);
                        } else {
                            drawCell(gc, x, y, color);
                        }
                    }
                }
            }
        }
    }

    private void drawCell(GraphicsContext gc, int x, int y, Color color) {
        double pixelX = x * CELL_SIZE;
        double pixelY = y * CELL_SIZE;

        gc.setFill(color);
        gc.fillRect(pixelX, pixelY, CELL_SIZE, CELL_SIZE);

        gc.setStroke(BORDER_COLOR);
        gc.setLineWidth(1);
        gc.strokeRect(pixelX, pixelY, CELL_SIZE, CELL_SIZE);
    }

    private void drawGhostCell(GraphicsContext gc, int x, int y) {
        double pixelX = x * CELL_SIZE;
        double pixelY = y * CELL_SIZE;

        gc.setStroke(GHOST_COLOR);
        gc.setLineWidth(2);
        gc.strokeRect(pixelX + 2, pixelY + 2, CELL_SIZE - 4, CELL_SIZE - 4);
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);

        for (int i = 0; i <= GameBoard.WIDTH; i++) {
            double x = i * CELL_SIZE;
            gc.strokeLine(x, 0, x, getHeight());
        }

        for (int i = 0; i <= GameBoard.HEIGHT; i++) {
            double y = i * CELL_SIZE;
            gc.strokeLine(0, y, getWidth(), y);
        }
    }
}
