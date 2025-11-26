package org.example.model;

import java.util.Arrays;

public class GameBoard {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    public static final int BUFFER_ZONE = 4; // Extra rows above visible area
    public static final int CLEAR_MARK = -1; // pending clear mark

    protected final int[][] board;
    protected long pendingClearDueMs = 0L;

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
        long now = System.currentTimeMillis();
        for (int row = HEIGHT + BUFFER_ZONE - 1; row >= 0; row--) {
            if (isLineFull(row)) {
                // mark entire row for clear and schedule
                for (int col = 0; col < WIDTH; col++) {
                    board[row][col] = CLEAR_MARK;
                }
                linesCleared++;
                schedulePendingClear(now + 500);
            }
        }
        return linesCleared;
    }
    
    public void playClearLineEffect(int min_x, int min_y, int max_x, int max_y) {
        // Clamp to board bounds and mark cells for white flash
        int clampedMinX = Math.max(0, Math.min(WIDTH - 1, min_x));
        int clampedMaxX = Math.max(0, Math.min(WIDTH - 1, max_x));
        int clampedMinY = Math.max(0, Math.min(HEIGHT + BUFFER_ZONE - 1, min_y));
        int clampedMaxY = Math.max(0, Math.min(HEIGHT + BUFFER_ZONE - 1, max_y));
        for (int y = clampedMinY; y <= clampedMaxY; y++) {
            for (int x = clampedMinX; x <= clampedMaxX; x++) {
                board[y][x] = CLEAR_MARK;
            }
        }
        schedulePendingClear(System.currentTimeMillis() + 500);
    }

    private void schedulePendingClear(long dueMs) {
        if (pendingClearDueMs == 0L || dueMs < pendingClearDueMs) {
            pendingClearDueMs = dueMs;
        }
    }

    public void processPendingClearsIfDue() {
        if (pendingClearDueMs == 0L) return;
        long now = System.currentTimeMillis();
        if (now < pendingClearDueMs) return;

        // 1) Clear fully marked rows (bottom-up for correct shifting)
        for (int row = HEIGHT + BUFFER_ZONE - 1; row >= 0; row--) {
            boolean fullMarked = true;
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != CLEAR_MARK) { fullMarked = false; break; }
            }
            if (fullMarked) {
                clearLine(row);
                row++; // re-check same index after shift
            }
        }

        // 2) Clear any remaining marked cells (rect/segments)
        for (int y = 0; y < HEIGHT + BUFFER_ZONE; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (board[y][x] == CLEAR_MARK) {
                    board[y][x] = 0;
                }
            }
        }

        pendingClearDueMs = 0L;
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

    public int[][] getCompressedBoard() {
        int[][] compressed = new int[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            compressed[i] = Arrays.copyOf(board[i + BUFFER_ZONE], board[i + BUFFER_ZONE].length);
        }
        return compressed;
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
    
    public void setCellColor(int row, int col, int color) {
        if (row >= 0 && row < HEIGHT + BUFFER_ZONE && col >= 0 && col < WIDTH) {
            board[row][col] = color;
        }
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

    /**
     * 보드의 특정 위치에 있는 아이템 정보를 반환합니다.
     * 
     * @param row 행 (절대 좌표)
     * @param col 열 (절대 좌표)
     * @return 해당 위치의 아이템 (없으면 ItemBlock.NONE)
     */
    public ItemBlock getItemAt(int row, int col) {
        return ItemBlock.NONE;
    }
}