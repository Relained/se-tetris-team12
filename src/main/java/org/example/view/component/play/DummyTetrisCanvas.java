package org.example.view.component.play;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.model.GameBoard;

public class DummyTetrisCanvas extends TetrisCanvas {
    // Magic Number (TetrisSystem과 같아야함)
    private static final int WEIGHT_MARK = 200;
    private static final int BOMB_MARK = 201;
    private static final int GHOST_MARK = -2;

    int[][] compressedBoard;

    public DummyTetrisCanvas() {
        super();
    }

    public void updateBoard(int[][] compressedBoard) {
        this.compressedBoard = compressedBoard;
        Platform.runLater(this::draw);
    }

    @Override
    protected void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, getWidth(), getHeight());

        if (compressedBoard == null) return;

        for (int row = 0; row < GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                int value = compressedBoard[row][col];
                if (value == 0) continue;

                if (value == GameBoard.CLEAR_MARK) {
                    drawCell(gc, col, row, Color.WHITE);
                } 
                else if (value == GHOST_MARK) {
                    drawGhostCell(gc, col, row);
                } 
                else if (value == WEIGHT_MARK) {
                    drawCell(gc, col, row, Color.GOLD);
                    drawItemMark(gc, col, row, 'W');
                } 
                else if (value == BOMB_MARK) {
                    drawCell(gc, col, row, Color.ORANGERED);
                    drawItemMark(gc, col, row, 'B');
                } 
                else if ((value >>> 16) != 0) { // 아이템 블록: 상위 16비트에 symbol
                    char symbol = (char)(value >>> 16);
                    int colorIdx = value & 0xFF;
                    Color color = colorManager.getColorFromIndex(colorIdx);
                    drawCell(gc, col, row, color);
                    drawItemMark(gc, col, row, symbol);
                }
                else if (value >= 1 && value <= 7) {
                    Color color = colorManager.getColorFromIndex(value);
                    drawCell(gc, col, row, color);
                }
            }
        }
        drawGrid(gc);
    }
}
