package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class ItemGameBoard extends GameBoard {
    // 아이템 블록 정보를 저장하는 맵 (보드 절대 좌표 -> ItemBlock)
    private final Map<Integer, ItemBlock> itemBoard;

    public ItemGameBoard() {
        super();
        this.itemBoard = new HashMap<>();
    }

    private int toKey(int row, int col) {
        return row * WIDTH + col;
    }

    @Override
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

    /**
     * - 꽉 찬 줄 삭제 (기본)
     * - LINE_CLEAR 아이템이 있는 줄 삭제 (아이템 모드 특성)
     */
    public int clearLinesWithItems() {
        int linesCleared = 0;

        // 아래에서 위로 검사하며 삭제
        for (int row = HEIGHT + BUFFER_ZONE - 1; row >= 0; row--) {
            boolean shouldClear = isLineFull(row);

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

            if (shouldClear) {
                clearLineWithItems(row);
                linesCleared++;
                row++; // 같은 줄을 다시 검사 (위에서 내려온 줄)
            }
        }

        return linesCleared;
    }

    /**
     * COLUMN_CLEAR 아이템이 있는 열 전체 삭제
     */
    public int clearColumnsWithItems() {
        int columnsCleared = 0;

        for (int col = 0; col < WIDTH; col++) {
            boolean shouldClear = false;
            for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
                ItemBlock item = itemBoard.get(toKey(row, col));
                if (item == ItemBlock.COLUMN_CLEAR) {
                    shouldClear = true;
                    break;
                }
            }
            if (shouldClear) {
                clearColumnWithItems(col);
                columnsCleared++;
            }
        }

        return columnsCleared;
    }

    /**
     * CROSS_CLEAR 아이템이 있는 위치의 가로줄과 세로줄 모두 삭제
     */
    public int clearCrossesWithItems() {
        int crossesCleared = 0;

        for (int row = HEIGHT + BUFFER_ZONE - 1; row >= 0; row--) {
            for (int col = 0; col < WIDTH; col++) {
                ItemBlock item = itemBoard.get(toKey(row, col));
                if (item == ItemBlock.CROSS_CLEAR) {
                    clearLineWithItems(row);
                    clearColumnWithItems(col);
                    crossesCleared++;
                    row++; // 같은 행 다시 검사
                    break;
                }
            }
        }

        return crossesCleared;
    }

    /**
     * WEIGHT, BOMB와 같은 특수 아이템의 효과를 즉시 적용합니다.
     * - WEIGHT: 현재 위치에서 보드의 바닥까지, 너비 4칸 영역을 모두 비웁니다.
     * - BOMB: 현재 위치를 중심으로 6x6 영역을 모두 비웁니다.
     * 아이템 자체는 일회성으로 간주하여 적용 후 제거합니다.
     */
    public void applyWeightAndBombEffects() {
        // 현재 아이템 스냅샷을 만들어 순회 중 변경으로부터 보호
        java.util.List<Map.Entry<Integer, ItemBlock>> entries = new java.util.ArrayList<>(itemBoard.entrySet());

        for (Map.Entry<Integer, ItemBlock> entry : entries) {
            int key = entry.getKey();
            int row = key / WIDTH;
            int col = key % WIDTH;
            ItemBlock item = entry.getValue();

            if (item == ItemBlock.WEIGHT) {
                applyWeightEffect(row, col);
                // 일회성 처리 후 제거
                itemBoard.remove(key);
            } else if (item == ItemBlock.BOMB) {
                applyBombEffect(row, col);
                itemBoard.remove(key);
            }
        }
    }

    // WEIGHT 아이템 효과: (row, col)에서 시작해 바닥까지, 가로 4칸 영역을 모두 비움
    private void applyWeightEffect(int startRow, int startCol) {
        int endRow = HEIGHT + BUFFER_ZONE - 1;
        int left = Math.max(0, startCol);
        int right = Math.min(WIDTH - 1, startCol + 3); // 너비 4칸

        for (int r = Math.max(0, startRow); r <= endRow; r++) {
            for (int c = left; c <= right; c++) {
                board[r][c] = 0;
                itemBoard.remove(toKey(r, c));
            }
        }
    }

    // BOMB 아이템 효과: (row, col) 중심의 6x6 영역을 모두 비움
    private void applyBombEffect(int centerRow, int centerCol) {
        // 중심의 2x2가 가운데 놓이도록 위로 2칸, 아래로 3칸 (총 6)
        int rStart = Math.max(0, centerRow - 2);
        int rEnd = Math.min(HEIGHT + BUFFER_ZONE - 1, centerRow + 3);
        int cStart = Math.max(0, centerCol - 2);
        int cEnd = Math.min(WIDTH - 1, centerCol + 3);

        for (int r = rStart; r <= rEnd; r++) {
            for (int c = cStart; c <= cEnd; c++) {
                board[r][c] = 0;
                itemBoard.remove(toKey(r, c));
            }
        }
    }

    // 외부에서 폭탄 효과를 트리거하기 위한 공개 메서드
    public void triggerBombAt(int topLeftRowOf2x2, int topLeftColOf2x2) {
        applyBombEffect(topLeftRowOf2x2, topLeftColOf2x2);
    }

    // WEIGHT 단계적 제거: startRow부터 stepRows만큼, 가로 4칸 영역을 비움
    public void clearWeightStep(int startRow, int startCol, int stepRows) {
        int endRow = Math.min(HEIGHT + BUFFER_ZONE - 1, startRow + stepRows - 1);
        int left = Math.max(0, startCol);
        int right = Math.min(WIDTH - 1, startCol + 3);
        if (endRow < 0 || left > right) return;

        for (int r = Math.max(0, startRow); r <= endRow; r++) {
            for (int c = left; c <= right; c++) {
                board[r][c] = 0;
                itemBoard.remove(toKey(r, c));
            }
        }
    }

    private boolean isLineFull(int row) {
        for (int col = 0; col < WIDTH; col++) {
            if (board[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    private void clearLineWithItems(int lineIndex) {
        // 아이템 보드 처리: 해당 줄의 아이템 제거 및 위 줄들 이동
        Map<Integer, ItemBlock> newItemBoard = new HashMap<>();
        for (Map.Entry<Integer, ItemBlock> entry : itemBoard.entrySet()) {
            int key = entry.getKey();
            int row = key / WIDTH;
            int col = key % WIDTH;

            if (row < lineIndex) {
                newItemBoard.put(toKey(row + 1, col), entry.getValue());
            } else if (row > lineIndex) {
                newItemBoard.put(key, entry.getValue());
            }
            // row == lineIndex 인 경우 제거
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

    private void clearColumnWithItems(int colIndex) {
        // 아이템 보드 처리: 해당 열의 아이템만 제거
        Map<Integer, ItemBlock> newItemBoard = new HashMap<>();
        for (Map.Entry<Integer, ItemBlock> entry : itemBoard.entrySet()) {
            int key = entry.getKey();
            int col = key % WIDTH;
            if (col != colIndex) {
                newItemBoard.put(key, entry.getValue());
            }
        }
        itemBoard.clear();
        itemBoard.putAll(newItemBoard);

        // 일반 보드 처리: 해당 열을 모두 비움 (다른 열 이동 없음)
        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            board[row][colIndex] = 0;
        }
    }

    @Override
    public void clear() {
        super.clear();
        itemBoard.clear();
    }

    public void setItemBlock(int row, int col, ItemBlock item) {
        int key = toKey(row, col);
        if (item == ItemBlock.NONE) {
            itemBoard.remove(key);
        } else {
            itemBoard.put(key, item);
        }
    }

    public ItemBlock getItemBlock(int row, int col) {
        int key = toKey(row, col);
        return itemBoard.getOrDefault(key, ItemBlock.NONE);
    }

    public void clearItemBlock(int row, int col) {
        int key = toKey(row, col);
        itemBoard.remove(key);
    }

    @Override
    public ItemBlock getItemAt(int row, int col) {
        return itemBoard.getOrDefault(toKey(row, col), ItemBlock.NONE);
    }
}
