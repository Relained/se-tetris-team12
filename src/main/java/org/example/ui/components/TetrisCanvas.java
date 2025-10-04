package org.example.ui.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.model.GameBoard;
import org.example.model.TetrominoPosition;

/**
 * 테트리스 메인 컴포넌트입니다.
 * 창의 높이에 따라 동적으로 크기가 조정됩니다.
 */
public class TetrisCanvas extends Canvas {
    private double cellSize = 30;
    private static final Color BORDER_COLOR = Color.DARKGRAY;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color GHOST_COLOR = Color.GRAY;

    private GameBoard board;
    private TetrominoPosition currentPiece;
    private TetrominoPosition ghostPiece;

    public TetrisCanvas() {
        super(GameBoard.WIDTH * 30, GameBoard.HEIGHT * 30);
        
        // 높이 변경 시 자동으로 cell size 재계산
        heightProperty().addListener((_, _, newHeight) -> {
            cellSize = newHeight.doubleValue() / GameBoard.HEIGHT;
            setWidth(GameBoard.WIDTH * cellSize);
            draw(); // 크기 변경 시 다시 그리기
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
            cellSize = width / GameBoard.WIDTH;
        } else {
            setWidth(heightBasedWidth);
            setHeight(height);
            cellSize = height / GameBoard.HEIGHT;
        }
        draw();
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
                if (visibleBoard[row][col] != 0) {
                    drawCell(gc, col, row, Color.web(String.format("#%06X", visibleBoard[row][col])));
                }
            }
        }

        // Draw ghost piece
        if (ghostPiece != null) {
            drawPiece(gc, ghostPiece, GHOST_COLOR, true);
        }

        // Draw current piece
        if (currentPiece != null) {
            Color pieceColor = Color.web(String.format("#%06X", currentPiece.getType().getColor()));
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
        double pixelX = x * cellSize;
        double pixelY = y * cellSize;

        gc.setFill(color);
        gc.fillRect(pixelX, pixelY, cellSize, cellSize);

        gc.setStroke(BORDER_COLOR);
        gc.setLineWidth(1);
        gc.strokeRect(pixelX, pixelY, cellSize, cellSize);
    }

    private void drawGhostCell(GraphicsContext gc, int x, int y) {
        double pixelX = x * cellSize;
        double pixelY = y * cellSize;

        gc.setStroke(GHOST_COLOR);
        gc.setLineWidth(2);
        gc.strokeRect(pixelX + 2, pixelY + 2, cellSize - 4, cellSize - 4);
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);

        for (int i = 0; i <= GameBoard.WIDTH; i++) {
            double x = i * cellSize;
            gc.strokeLine(x, 0, x, getHeight());
        }

        for (int i = 0; i <= GameBoard.HEIGHT; i++) {
            double y = i * cellSize;
            gc.strokeLine(0, y, getWidth(), y);
        }
    }
}
