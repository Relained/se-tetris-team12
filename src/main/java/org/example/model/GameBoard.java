package org.example.model;

public class GameBoard {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    public static final int BUFFER_ZONE = 4; // Extra rows above visible area

    private final int[][] board;

    public GameBoard() {
        this.board = new int[HEIGHT + BUFFER_ZONE][WIDTH];
    }

    public boolean isValidPosition(TetrominoPosition position) {
        int[][] shape = position.getCurrentShape();
        int startX = position.getX();
        int startY = position.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int boardX = startX + col;
                    int boardY = startY + row;

                    if (boardX < 0 || boardX >= WIDTH ||
                        boardY >= HEIGHT + BUFFER_ZONE ||
                        (boardY >= 0 && board[boardY][boardX] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placeTetromino(TetrominoPosition position) {
        int[][] shape = position.getCurrentShape();
        int startX = position.getX();
        int startY = position.getY();
        int color = position.getType().getColorIndex();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int boardX = startX + col;
                    int boardY = startY + row;
                    if (boardY >= 0 && boardY < HEIGHT + BUFFER_ZONE &&
                        boardX >= 0 && boardX < WIDTH) {
                        board[boardY][boardX] = color;
                    }
                }
            }
        }
    }

    public int clearLines() {
        int linesCleared = 0;
        for (int row = HEIGHT + BUFFER_ZONE - 1; row >= 0; row--) {
            if (isLineFull(row)) {
                clearLine(row);
                linesCleared++;
                row++; // Check the same row again
            }
        }
        return linesCleared;
    }

    private boolean isLineFull(int row) {
        for (int col = 0; col < WIDTH; col++) {
            if (board[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    private void clearLine(int lineIndex) {
        for (int row = lineIndex; row > 0; row--) {
            System.arraycopy(board[row - 1], 0, board[row], 0, WIDTH);
        }
        // Clear the top line
        for (int col = 0; col < WIDTH; col++) {
            board[0][col] = 0;
        }
    }

    public int[][] getVisibleBoard() {
        int[][] visible = new int[HEIGHT][WIDTH];
        System.arraycopy(board, BUFFER_ZONE, visible, 0, HEIGHT);
        return visible;
    }

    public int getCellColor(int row, int col) {
        if (row >= 0 && row < HEIGHT + BUFFER_ZONE && col >= 0 && col < WIDTH) {
            return board[row][col];
        }
        return 0;
    }

    public boolean isGameOver() {
        // Check if any blocks exist in the buffer zone (invisible top area)
        for (int row = 0; row < BUFFER_ZONE; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clear() {
        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            for (int col = 0; col < WIDTH; col++) {
                board[row][col] = 0;
            }
        }
    }
}