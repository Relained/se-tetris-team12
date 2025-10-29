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
     * LINE_CLEAR 아이템이 있는 모든 줄을 찾아서 한 번에 삭제합니다.
     * 1. LINE_CLEAR가 있는 모든 줄을 찾음
     * 2. 해당 줄들을 모두 한 번에 삭제
     * 3. 위쪽 줄들을 아래로 내림
     * 
     * @return int[] [0]=전체 삭제 줄 수, [1]=아이템으로 인한 삭제 줄 수
     */
    public int[] clearLinesWithItems() {
        // 1단계: LINE_CLEAR가 있는 모든 줄과 꽉 찬 줄을 찾음
        java.util.Set<Integer> linesToClear = new java.util.HashSet<>();
        java.util.Set<Integer> itemClearedLines = new java.util.HashSet<>();
        
        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            boolean hasLineClear = false;
            boolean isFull = isLineFull(row);
            
            // 이 줄에 LINE_CLEAR가 있는지 확인
            for (int col = 0; col < WIDTH; col++) {
                ItemBlock item = itemBoard.get(toKey(row, col));
                if (item == ItemBlock.LINE_CLEAR) {
                    hasLineClear = true;
                    break;
                }
            }
            
            // 줄 삭제 대상 체크
            if (hasLineClear) {
                linesToClear.add(row);
                itemClearedLines.add(row);  // 아이템으로 인한 삭제
            } else if (isFull) {
                linesToClear.add(row);
                // itemClearedLines에는 추가 안 함 (일반 삭제)
            }
        }
        
        if (linesToClear.isEmpty()) {
            return new int[]{0, 0};
        }
        
        // 2단계: 여러 줄을 한 번에 삭제 (이펙트 먼저 표시)
        // 각 줄에 대해 이펙트 마킹
        for (int row : linesToClear) {
            playClearLineEffect(0, row, WIDTH - 1, row);
        }
        
        // 실제 삭제는 processPendingClearsIfDue에서 처리되므로
        // 여기서는 마킹만 하고 반환
        
        return new int[]{linesToClear.size(), itemClearedLines.size()};
    }

    /**
     * COLUMN_CLEAR 아이템이 있는 모든 열을 찾아서 한 번에 삭제합니다.
     * 1. COLUMN_CLEAR가 있는 모든 열을 찾음
     * 2. 해당 열들을 모두 비움
     */
    public int clearColumnsWithItems() {
        // 1단계: COLUMN_CLEAR가 있는 모든 열을 찾음
        java.util.Set<Integer> columnsToClear = new java.util.HashSet<>();
        
        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            for (int col = 0; col < WIDTH; col++) {
                ItemBlock item = itemBoard.get(toKey(row, col));
                if (item == ItemBlock.COLUMN_CLEAR) {
                    columnsToClear.add(col);
                }
            }
        }
        
        // 2단계: 해당 열들을 모두 삭제
        for (int col : columnsToClear) {
            clearColumnWithItems(col);
        }
        
        return columnsToClear.size();
    }

    /**
     * CROSS_CLEAR 아이템이 있는 위치의 가로줄과 세로줄을 모두 삭제합니다.
     * 1. CROSS_CLEAR가 있는 모든 위치를 찾음
     * 2. 해당 위치의 열들을 먼저 삭제 (좌표 변경 없음)
     * 3. 해당 위치의 줄(행)들을 삭제 (좌표 변경 발생)
     */
    public int clearCrossesWithItems() {
        // 1단계: CROSS_CLEAR가 있는 모든 위치를 찾음
        java.util.Set<Integer> rowsToClear = new java.util.HashSet<>();
        java.util.Set<Integer> columnsToClear = new java.util.HashSet<>();
        
        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            for (int col = 0; col < WIDTH; col++) {
                ItemBlock item = itemBoard.get(toKey(row, col));
                if (item == ItemBlock.CROSS_CLEAR) {
                    rowsToClear.add(row);
                    columnsToClear.add(col);
                }
            }
        }
        
        int crossesCleared = rowsToClear.size() + columnsToClear.size();
        
        // 2단계: 열 삭제 먼저 (좌표 변경 없음)
        for (int col : columnsToClear) {
            clearColumnWithItems(col);
        }
        
        // 3단계: 줄 삭제 (아래에서 위로 - 좌표 변경 발생)
        java.util.List<Integer> sortedRows = new java.util.ArrayList<>(rowsToClear);
        sortedRows.sort(java.util.Collections.reverseOrder());
        for (int row : sortedRows) {
            clearLineWithItems(row);
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

    // BOMB 아이템 효과: 2x2 블록 중심의 6x6 영역을 모두 비움
    // centerRow, centerCol: 2x2 블록의 왼쪽 위 좌표
    private void applyBombEffect(int topLeftRow, int topLeftCol) {
        // 2x2 블록의 실제 중심: (topLeftRow + 0.5, topLeftCol + 0.5)
        // 6x6 영역을 만들려면 각 방향으로 3칸씩 (중심 포함하여 총 6칸)
        // 정수 좌표로 근사: 왼쪽/위로 2칸, 오른쪽/아래로 3칸
        // 2x2 블록이 중앙에 오도록 조정: topLeft 기준 -2 ~ +3 = 6칸
        int rStart = Math.max(0, topLeftRow - 2);
        int rEnd = Math.min(HEIGHT + BUFFER_ZONE - 1, topLeftRow + 3);
        int cStart = Math.max(0, topLeftCol - 2);
        int cEnd = Math.min(WIDTH - 1, topLeftCol + 3);

        // mark square area effect for bomb (deferred clear)
        playClearLineEffect(cStart, rStart, cEnd, rEnd);
    }

    // 외부에서 폭탄 효과를 트리거하기 위한 공개 메서드
    // topLeftRowOf2x2, topLeftColOf2x2: 2x2 BOMB 블록의 왼쪽 위 좌표
    public void triggerBombAt(int topLeftRowOf2x2, int topLeftColOf2x2) {
        applyBombEffect(topLeftRowOf2x2, topLeftColOf2x2);
    }

    // WEIGHT 효과: 무게추 아래의 모든 블록을 제거하고, 무게추를 바닥에 배치
    public void triggerWeightEffect(int weightTopRow, int weightStartCol, int[][] weightShape) {
        int weightHeight = weightShape.length;
        int weightWidth = weightShape[0].length;
        
        int left = Math.max(0, weightStartCol);
        int right = Math.min(WIDTH - 1, weightStartCol + weightWidth - 1);
        
        // 무게추 아래의 모든 블록을 제거 (무게추부터 바닥까지)
        for (int row = weightTopRow; row < HEIGHT + BUFFER_ZONE; row++) {
            for (int col = left; col <= right; col++) {
                board[row][col] = 0;
                itemBoard.remove(toKey(row, col));
            }
        }
        
        // 무게추를 바닥에 배치 (실제 바닥)
        int bottomRow = HEIGHT + BUFFER_ZONE - 1;
        
        // 무게추 실제 모양대로 바닥에 배치 + WEIGHT 아이템 정보 추가
        for (int r = 0; r < weightHeight; r++) {
            int targetRow = bottomRow - (weightHeight - 1 - r);
            if (targetRow >= 0 && targetRow < HEIGHT + BUFFER_ZONE) {
                for (int c = 0; c < weightWidth; c++) {
                    int targetCol = weightStartCol + c;
                    if (targetCol >= 0 && targetCol < WIDTH && weightShape[r][c] != 0) {
                        board[targetRow][targetCol] = 2; // YELLOW (GOLD와 유사한 색상)
                        // WEIGHT 아이템 정보 추가 (W 글자 표시를 위해)
                        itemBoard.put(toKey(targetRow, targetCol), ItemBlock.WEIGHT);
                    }
                }
            }
        }
    }
    
    // WEIGHT 제거: 바닥에 있는 무게추를 제거
    public void clearWeight(int weightStartCol, int[][] weightShape) {
        int weightHeight = weightShape.length;
        int weightWidth = weightShape[0].length;
        int bottomRow = HEIGHT + BUFFER_ZONE - 1;
        
        for (int r = 0; r < weightHeight; r++) {
            int targetRow = bottomRow - (weightHeight - 1 - r);
            if (targetRow >= 0 && targetRow < HEIGHT + BUFFER_ZONE) {
                for (int c = 0; c < weightWidth; c++) {
                    int targetCol = weightStartCol + c;
                    if (targetCol >= 0 && targetCol < WIDTH && weightShape[r][c] != 0) {
                        board[targetRow][targetCol] = 0;
                        itemBoard.remove(toKey(targetRow, targetCol)); // 아이템 정보도 제거
                    }
                }
            }
        }
    }

    // WEIGHT 단계적 제거: 한 줄씩 덮어씌우면서 내려감
    public void clearWeightStep(int currentRow, int startCol, int width) {
        if (currentRow < 0 || currentRow >= HEIGHT + BUFFER_ZONE) return;
        
        int left = Math.max(0, startCol);
        int right = Math.min(WIDTH - 1, startCol + width - 1);
        if (left > right) return;

        // 현재 줄의 블록들을 덮어씌움 (삭제)
        for (int c = left; c <= right; c++) {
            board[currentRow][c] = 0;
            itemBoard.remove(toKey(currentRow, c));
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
        playClearLineEffect(0, lineIndex, WIDTH - 1, lineIndex);
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

        playClearLineEffect(colIndex, BUFFER_ZONE, colIndex, HEIGHT + BUFFER_ZONE - 1);
    }

    @Override
    public void clear() {
        super.clear();
        itemBoard.clear();
    }

    @Override
    public void processPendingClearsIfDue() {
        if (pendingClearDueMs == 0L) return;
        long now = System.currentTimeMillis();
        if (now < pendingClearDueMs) return;

        // 1) Clear fully marked rows with itemBoard shift (bottom-up)
        for (int row = HEIGHT + BUFFER_ZONE - 1; row >= 0; row--) {
            boolean fullMarked = true;
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != CLEAR_MARK) { fullMarked = false; break; }
            }
            if (fullMarked) {
                // 줄 이동: 위 줄들을 아래로 내림
                for (int r = row; r > 0; r--) {
                    System.arraycopy(board[r - 1], 0, board[r], 0, WIDTH);
                }
                for (int c = 0; c < WIDTH; c++) board[0][c] = 0;
                
                // 아이템 보드도 함께 이동
                Map<Integer, ItemBlock> newItemBoard = new HashMap<>();
                for (Map.Entry<Integer, ItemBlock> entry : itemBoard.entrySet()) {
                    int key = entry.getKey();
                    int itemRow = key / WIDTH;
                    int itemCol = key % WIDTH;
                    
                    if (itemRow < row) {
                        // 삭제된 줄 위의 아이템들은 한 줄 아래로 이동
                        newItemBoard.put(toKey(itemRow + 1, itemCol), entry.getValue());
                    } else if (itemRow > row) {
                        // 삭제된 줄 아래의 아이템들은 그대로
                        newItemBoard.put(key, entry.getValue());
                    }
                    // itemRow == row인 경우 제거 (삭제된 줄)
                }
                itemBoard.clear();
                itemBoard.putAll(newItemBoard);
                
                row++; // re-check same index after shift
            }
        }

        // 3) Clear remaining marked cells (rectangles/segments) and their items
        for (int y = 0; y < HEIGHT + BUFFER_ZONE; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (board[y][x] == CLEAR_MARK) {
                    board[y][x] = 0;
                    itemBoard.remove(toKey(y, x)); // 아이템도 함께 제거
                }
            }
        }

        pendingClearDueMs = 0L;
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
