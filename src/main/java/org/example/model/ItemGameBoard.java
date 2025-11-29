package org.example.model;

public class ItemGameBoard extends GameBoard {

    public ItemGameBoard() {
        super();
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
                        // 아이템이 있으면 아이템 char 값을 저장, 없으면 색상 인덱스 저장
                        ItemBlock item = position.getItemAt(row, col);
                        if (item != null && item.isItem()) {
                            board[boardY][boardX] = item.getSymbol();
                        } else {
                            board[boardY][boardX] = color;
                        }
                    }
                }
            }
        }
    }

    /**
     * LINE_CLEAR 아이템이 있는 모든 줄을 찾아서 한 번에 삭제합니다.
     *
     * @return int[] [0]=전체 삭제 줄 수, [1]=아이템으로 인한 삭제 줄 수
     */
    public int[] clearLinesWithItems() {
        java.util.Set<Integer> linesToClear = new java.util.HashSet<>();
        java.util.Set<Integer> itemClearedLines = new java.util.HashSet<>();

        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            boolean hasLineClear = false;
            boolean isFull = isLineFull(row);

            // 이 줄에 LINE_CLEAR가 있는지 확인
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] == 'L') {
                    hasLineClear = true;
                    break;
                }
            }

            if (hasLineClear) {
                linesToClear.add(row);
                itemClearedLines.add(row);
            } else if (isFull) {
                linesToClear.add(row);
            }
        }

        if (linesToClear.isEmpty()) {
            return new int[]{0, 0};
        }

        for (int row : linesToClear) {
            playClearLineEffect(0, row, WIDTH - 1, row);
        }

        return new int[]{linesToClear.size(), itemClearedLines.size()};
    }

    /**
     * COLUMN_CLEAR 아이템이 있는 모든 열을 찾아서 한 번에 삭제합니다.
     */
    public int clearColumnsWithItems() {
        java.util.Set<Integer> columnsToClear = new java.util.HashSet<>();

        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] == 'I') {
                    columnsToClear.add(col);
                }
            }
        }

        for (int col : columnsToClear) {
            clearColumnWithItems(col);
        }

        return columnsToClear.size();
    }

    /**
     * CROSS_CLEAR 아이템이 있는 위치의 가로줄과 세로줄을 모두 삭제합니다.
     */
    public int clearCrossesWithItems() {
        java.util.Set<Integer> rowsToClear = new java.util.HashSet<>();
        java.util.Set<Integer> columnsToClear = new java.util.HashSet<>();

        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] == 'X') {
                    rowsToClear.add(row);
                    columnsToClear.add(col);
                }
            }
        }

        int crossesCleared = rowsToClear.size() + columnsToClear.size();

        // 열 삭제 먼저
        for (int col : columnsToClear) {
            clearColumnWithItems(col);
        }

        // 줄 삭제 (아래에서 위로)
        java.util.List<Integer> sortedRows = new java.util.ArrayList<>(rowsToClear);
        sortedRows.sort(java.util.Collections.reverseOrder());
        for (int row : sortedRows) {
            clearLineWithItems(row);
        }

        return crossesCleared;
    }

    /**
     * WEIGHT, BOMB와 같은 특수 아이템의 효과를 즉시 적용합니다.
     */
    public void applyWeightAndBombEffects() {
        // 현재 보드 스냅샷을 만들어 순회 중 변경으로부터 보호
        java.util.List<int[]> itemPositions = new java.util.ArrayList<>();

        for (int row = 0; row < HEIGHT + BUFFER_ZONE; row++) {
            for (int col = 0; col < WIDTH; col++) {
                int value = board[row][col];
                if (value == 'W' || value == 'B') {
                    itemPositions.add(new int[]{row, col, value});
                }
            }
        }

        for (int[] pos : itemPositions) {
            int row = pos[0];
            int col = pos[1];
            int item = pos[2];

            if (item == 'W') {
                applyWeightEffect(row, col);
                board[row][col] = 0; // 일회성 처리 후 제거
            } else if (item == 'B') {
                applyBombEffect(row, col);
                board[row][col] = 0;
            }
        }
    }

    private void applyWeightEffect(int startRow, int startCol) {
        int endRow = HEIGHT + BUFFER_ZONE - 1;
        int left = Math.max(0, startCol);
        int right = Math.min(WIDTH - 1, startCol + 3);

        for (int r = Math.max(0, startRow); r <= endRow; r++) {
            for (int c = left; c <= right; c++) {
                board[r][c] = 0;
            }
        }
    }

    private void applyBombEffect(int topLeftRow, int topLeftCol) {
        int rStart = Math.max(0, topLeftRow - 2);
        int rEnd = Math.min(HEIGHT + BUFFER_ZONE - 1, topLeftRow + 3);
        int cStart = Math.max(0, topLeftCol - 2);
        int cEnd = Math.min(WIDTH - 1, topLeftCol + 3);

        playClearLineEffect(cStart, rStart, cEnd, rEnd);
    }

    public void triggerBombAt(int topLeftRowOf2x2, int topLeftColOf2x2) {
        applyBombEffect(topLeftRowOf2x2, topLeftColOf2x2);
    }

    public void triggerWeightEffect(int weightTopRow, int weightStartCol, int[][] weightShape) {
        int weightHeight = weightShape.length;
        int weightWidth = weightShape[0].length;

        int left = Math.max(0, weightStartCol);
        int right = Math.min(WIDTH - 1, weightStartCol + weightWidth - 1);

        // 무게추 아래의 모든 블록을 제거
        for (int row = weightTopRow; row < HEIGHT + BUFFER_ZONE; row++) {
            for (int col = left; col <= right; col++) {
                board[row][col] = 0;
            }
        }

        // 무게추를 바닥에 배치 + WEIGHT 아이템 정보 저장
        int bottomRow = HEIGHT + BUFFER_ZONE - 1;

        for (int r = 0; r < weightHeight; r++) {
            int targetRow = bottomRow - (weightHeight - 1 - r);
            if (targetRow >= 0 && targetRow < HEIGHT + BUFFER_ZONE) {
                for (int c = 0; c < weightWidth; c++) {
                    int targetCol = weightStartCol + c;
                    if (targetCol >= 0 && targetCol < WIDTH && weightShape[r][c] != 0) {
                        board[targetRow][targetCol] = 'W'; // WEIGHT char 값 저장
                    }
                }
            }
        }
    }

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
                    }
                }
            }
        }
    }

    public void clearWeightStep(int currentRow, int startCol, int width) {
        if (currentRow < 0 || currentRow >= HEIGHT + BUFFER_ZONE) return;

        int left = Math.max(0, startCol);
        int right = Math.min(WIDTH - 1, startCol + width - 1);
        if (left > right) return;

        for (int c = left; c <= right; c++) {
            board[currentRow][c] = 0;
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
        playClearLineEffect(0, lineIndex, WIDTH - 1, lineIndex);
    }

    private void clearColumnWithItems(int colIndex) {
        playClearLineEffect(colIndex, BUFFER_ZONE, colIndex, HEIGHT + BUFFER_ZONE - 1);
    }

    @Override
    public void processPendingClearsIfDue() {
        if (pendingClearDueMs == 0L) return;
        long now = System.currentTimeMillis();
        if (now < pendingClearDueMs) return;

        // Clear fully marked rows (bottom-up)
        for (int row = HEIGHT + BUFFER_ZONE - 1; row >= 0; row--) {
            boolean fullMarked = true;
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != CLEAR_MARK) { fullMarked = false; break; }
            }
            if (fullMarked) {
                // 줄 이동: 위 줄들을 아래로 내림 (아이템 정보도 함께 이동됨)
                for (int r = row; r > 0; r--) {
                    System.arraycopy(board[r - 1], 0, board[r], 0, WIDTH);
                }
                for (int c = 0; c < WIDTH; c++) board[0][c] = 0;

                row++; // re-check same index after shift
            }
        }

        // Clear remaining marked cells
        for (int y = 0; y < HEIGHT + BUFFER_ZONE; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (board[y][x] == CLEAR_MARK) {
                    board[y][x] = 0;
                }
            }
        }

        pendingClearDueMs = 0L;
    }

    public void setItemBlock(int row, int col, ItemBlock item) {
        if (row >= 0 && row < HEIGHT + BUFFER_ZONE && col >= 0 && col < WIDTH) {
            if (item == ItemBlock.NONE) {
                board[row][col] = 0;
            } else {
                board[row][col] = item.getSymbol();
            }
        }
    }

    public ItemBlock getItemBlock(int row, int col) {
        if (row >= 0 && row < HEIGHT + BUFFER_ZONE && col >= 0 && col < WIDTH) {
            return ItemBlock.fromSymbol(board[row][col]);
        }
        return ItemBlock.NONE;
    }

    public void clearItemBlock(int row, int col) {
        if (row >= 0 && row < HEIGHT + BUFFER_ZONE && col >= 0 && col < WIDTH) {
            board[row][col] = 0;
        }
    }

    @Override
    public ItemBlock getItemAt(int row, int col) {
        if (row >= 0 && row < HEIGHT + BUFFER_ZONE && col >= 0 && col < WIDTH) {
            return ItemBlock.fromSymbol(board[row][col]);
        }
        return ItemBlock.NONE;
    }
}
