package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class GameBoard {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    public static final int BUFFER_ZONE = 4; // Extra rows above visible area

    private final int[][] board;
    private final Map<Integer, ItemBlock> itemBoard; // (row * WIDTH + col) -> ItemBlock (보드 절대 좌표)

    public GameBoard() {
        this.board = new int[HEIGHT + BUFFER_ZONE][WIDTH];
        this.itemBoard = new HashMap<>();
    }
    
    /**
     * 행과 열을 하나의 int 키로 변환합니다.
     * 
     * @param row 행 인덱스
     * @param col 열 인덱스
     * @return 변환된 키 값 (row * WIDTH + col)
     */
    private int toKey(int row, int col) {
        return row * WIDTH + col;
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
                        
                        // 아이템이 있으면 itemBoard에 저장
                        ItemBlock item = position.getItemAt(row, col);
                        if (item != null && item.isItem()) {
                            itemBoard.put(toKey(boardY, boardX), item);
                        }
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
    
    /**
     * 아이템 모드용 줄 삭제 메서드
     * - 꽉 찬 줄 삭제 (기본)
     * - LINE_CLEAR 아이템이 있는 줄 삭제 (아이템 모드 특성)
     * 
     * 삭제 시 아이템 보드도 함께 업데이트되며, 아래에서 위로 반복 검사하여
     * 여러 줄이 연쇄적으로 삭제될 수 있습니다.
     * 
     * @return 삭제된 줄 수
     */
    public int clearLinesWithItems() {
        int linesCleared = 0;
        
        // 아래에서 위로 검사하며 삭제 (기존 clearLines와 동일한 방식)
        for (int row = HEIGHT + BUFFER_ZONE - 1; row >= 0; row--) {
            boolean shouldClear = false;
            
            // 꽉 찬 줄인지 확인
            if (isLineFull(row)) {
                shouldClear = true;
            }
            
            // LINE_CLEAR 아이템이 있는지 확인
            if (!shouldClear) {
                for (int col = 0; col < WIDTH; col++) {
                    ItemBlock item = itemBoard.get(toKey(row, col));
                    if (item == ItemBlock.LINE_CLEAR) {
                        shouldClear = true;
                        break;
                    }
                }
            }
            
            // 줄 삭제
            if (shouldClear) {
                clearLineWithItems(row);
                linesCleared++;
                row++; // 같은 줄을 다시 검사 (위에서 내려온 줄)
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
    
    /**
     * 특정 줄을 삭제하고 아이템 보드도 함께 업데이트합니다.
     * - 삭제된 줄의 아이템 제거
     * - 위쪽 줄들을 한 칸씩 아래로 이동
     * 
     * @param lineIndex 삭제할 줄의 인덱스
     */
    private void clearLineWithItems(int lineIndex) {
        // 아이템 보드 처리: 해당 줄의 아이템 제거 및 위 줄들 이동
        Map<Integer, ItemBlock> newItemBoard = new HashMap<>();
        for (Map.Entry<Integer, ItemBlock> entry : itemBoard.entrySet()) {
            int key = entry.getKey();
            int row = key / WIDTH;
            int col = key % WIDTH;
            
            if (row < lineIndex) {
                // 삭제된 줄 위의 아이템들은 한 칸 아래로
                newItemBoard.put(toKey(row + 1, col), entry.getValue());
            } else if (row > lineIndex) {
                // 삭제된 줄 아래의 아이템들은 그대로
                newItemBoard.put(key, entry.getValue());
            }
            // row == lineIndex인 경우는 제거 (추가하지 않음)
        }
        itemBoard.clear();
        itemBoard.putAll(newItemBoard);
        
        // 일반 보드 처리
        for (int row = lineIndex; row > 0; row--) {
            System.arraycopy(board[row - 1], 0, board[row], 0, WIDTH);
        }
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
        itemBoard.clear();
    }
        
    /**
     * 보드의 특정 위치에 있는 아이템 정보를 반환합니다.
     * 
     * @param row 행 (절대 좌표)
     * @param col 열 (절대 좌표)
     * @return 해당 위치의 아이템 (없으면 ItemBlock.NONE)
     */
    public ItemBlock getItemAt(int row, int col) {
        return itemBoard.getOrDefault(toKey(row, col), ItemBlock.NONE);
    }
}