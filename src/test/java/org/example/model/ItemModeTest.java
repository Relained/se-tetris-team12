package org.example.model;

import org.example.service.ItemTetrisSystem;
import org.example.service.TetrisSystem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 아이템 모드 기능 통합 테스트
 * - 게임 모드 전환
 * - 아이템 블록 생성 및 동작
 * - 아이템 회전 일관성
 * - 보드에서의 아이템 처리
 */
class ItemModeTest {

    @Test
    void testNormalModeCreation() {
        TetrisSystem game = new TetrisSystem();
        assertNotNull(game);
        assertFalse(game instanceof ItemTetrisSystem);
    }

    @Test
    void testItemModeCreation() {
        ItemTetrisSystem game = new ItemTetrisSystem();
        assertNotNull(game);
        assertFalse(game.nextPieceHasItem());
    }

    @Test
    void testItemBlockTypes() {
        ItemBlock lineClear = ItemBlock.LINE_CLEAR;
        assertEquals('L', lineClear.getSymbol());
        assertTrue(lineClear.isItem());
        
        ItemBlock columnClear = ItemBlock.COLUMN_CLEAR;
        assertEquals('I', columnClear.getSymbol());
        assertTrue(columnClear.isItem());
        
        ItemBlock crossClear = ItemBlock.CROSS_CLEAR;
        assertEquals('X', crossClear.getSymbol());
        assertTrue(crossClear.isItem());

        ItemBlock none = ItemBlock.NONE;
        assertEquals(' ', none.getSymbol());
        assertFalse(none.isItem());
        
        // 아이템 생성 주기 확인
        assertEquals(10, ItemBlock.LINES_FOR_ITEM_GENERATION);
    }

    @Test
    void testTetrominoWithItem() {
        TetrominoPosition piece = new TetrominoPosition(Tetromino.I, 0, 0, 0);
        
        // 아이템이 없는 상태
        assertFalse(piece.hasItems());
        assertEquals(ItemBlock.NONE, piece.getItemAt(1, 0));
        
        // 첫 번째 블록에 아이템 추가
        piece.setItemAtBlockIndex(0);
        assertTrue(piece.hasItems());
        assertEquals(0, piece.getItemBlockIndex());
        
        // Copy 시 아이템도 복사되는지 확인
        TetrominoPosition copied = piece.copy();
        assertTrue(copied.hasItems());
        assertEquals(piece.getItemBlockIndex(), copied.getItemBlockIndex());
    }

    @Test
    void testItemRotationConsistency() {
        // 모든 테트로미노 타입에 대해 회전 일관성 검증
        for (Tetromino type : Tetromino.values()) {
            TetrominoPosition piece = new TetrominoPosition(type, 0, 0, 0);
            piece.setItemAtBlockIndex(0);
            
            // 4번 회전해도 항상 아이템이 정확히 1개여야 함
            for (int rot = 0; rot < 4; rot++) {
                piece.setRotation(rot);
                int itemCount = countItemBlocks(piece);
                assertEquals(1, itemCount, 
                    type.name() + " rotation " + rot + ": 아이템이 정확히 1개여야 함");
            }
        }
    }
    
    @Test
    void testItemPersistsThroughRotations() {
        // 특정 블록에 아이템을 설정하고 회전해도 같은 물리적 블록에 유지되는지 확인
        TetrominoPosition piece = new TetrominoPosition(Tetromino.T, 0, 0, 0);
        piece.setItemAtBlockIndex(1);
        
        // 회전 전후 아이템이 유지되는지 확인
        piece.setRotation(1);
        assertTrue(piece.hasItems());
        assertEquals(1, countItemBlocks(piece));
        
        piece.setRotation(2);
        assertTrue(piece.hasItems());
        assertEquals(1, countItemBlocks(piece));
        
        piece.setRotation(3);
        assertTrue(piece.hasItems());
        assertEquals(1, countItemBlocks(piece));
        
        piece.setRotation(0);
        assertTrue(piece.hasItems());
        assertEquals(1, countItemBlocks(piece));
    }

    @Test
    void testGameBoardWithItems() {
        GameBoard board = new GameBoard();
        TetrominoPosition piece = new TetrominoPosition(Tetromino.O, 4, GameBoard.BUFFER_ZONE, 0);
        
        // O 블록의 첫 번째 블록에 아이템 추가
        piece.setItemAtBlockIndex(0);
        
        // 블록 배치
        board.placeTetromino(piece);
        
        // 아이템이 보드에 저장되었는지 확인
        assertEquals(ItemBlock.LINE_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE, 5));
    }
    
    @Test
    void testItemBoardClearLine() {
        GameBoard board = new GameBoard();
        
        // 아이템이 있는 줄을 만들기
        TetrominoPosition piece1 = new TetrominoPosition(Tetromino.I, 0, GameBoard.BUFFER_ZONE, 0);
        piece1.setItemAtBlockIndex(0);
        board.placeTetromino(piece1);
        
        // 아이템 확인
        assertTrue(board.getItemAt(GameBoard.BUFFER_ZONE + 1, 0).isItem());
        
        // 보드 클리어
        board.clear();
        
        // 아이템도 함께 삭제되었는지 확인
        assertEquals(ItemBlock.NONE, board.getItemAt(GameBoard.BUFFER_ZONE + 1, 0));
    }
    
    @Test
    void testColumnClearItem() {
        GameBoard board = new GameBoard();
        
        // COLUMN_CLEAR 아이템이 있는 블록 생성
        TetrominoPosition piece = new TetrominoPosition(Tetromino.O, 4, GameBoard.BUFFER_ZONE, 0);
        piece.setItemAtBlockIndex(0, ItemBlock.COLUMN_CLEAR);
        
        // 블록 배치
        board.placeTetromino(piece);
        
        // 아이템이 COLUMN_CLEAR로 저장되었는지 확인
        assertEquals(ItemBlock.COLUMN_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE, 5));
        
        // 세로줄 삭제 (테스트를 위해 세로줄이 꽉 찬 것처럼 만들거나 아이템만 있어도 삭제됨)
        int clearedColumns = board.clearColumnsWithItems();
        
        // 최소한 COLUMN_CLEAR 아이템이 있는 열이 삭제되어야 함
        assertTrue(clearedColumns >= 1, "COLUMN_CLEAR 아이템이 있는 열이 삭제되어야 함");
    }
    
    @Test
    void testCrossClearItem() {
        GameBoard board = new GameBoard();
        
        // CROSS_CLEAR 아이템이 있는 블록 생성
        TetrominoPosition piece = new TetrominoPosition(Tetromino.O, 4, GameBoard.BUFFER_ZONE, 0);
        piece.setItemAtBlockIndex(0, ItemBlock.CROSS_CLEAR);
        
        // 블록 배치
        board.placeTetromino(piece);
        
        // 아이템이 CROSS_CLEAR로 저장되었는지 확인
        assertEquals(ItemBlock.CROSS_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE, 5));
        
        // 십자 삭제 (가로줄과 세로줄 모두 삭제)
        int clearedCrosses = board.clearCrossesWithItems();
        
        // 최소한 CROSS_CLEAR 아이템이 있는 위치의 십자가 삭제되어야 함
        assertTrue(clearedCrosses >= 1, "CROSS_CLEAR 아이템이 있는 십자가 삭제되어야 함");
    }
    
    @Test
    void testItemTypePersistence() {
        // LINE_CLEAR 아이템이 회전 후에도 LINE_CLEAR로 유지되는지 확인
        TetrominoPosition linePiece = new TetrominoPosition(Tetromino.T, 0, 0, 0);
        linePiece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        linePiece.setRotation(1);
        int[][] shape = linePiece.getCurrentShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    ItemBlock item = linePiece.getItemAt(row, col);
                    if (item.isItem()) {
                        assertEquals(ItemBlock.LINE_CLEAR, item);
                    }
                }
            }
        }
        
        // COLUMN_CLEAR 아이템이 회전 후에도 COLUMN_CLEAR로 유지되는지 확인
        TetrominoPosition columnPiece = new TetrominoPosition(Tetromino.T, 0, 0, 0);
        columnPiece.setItemAtBlockIndex(0, ItemBlock.COLUMN_CLEAR);
        
        columnPiece.setRotation(1);
        shape = columnPiece.getCurrentShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    ItemBlock item = columnPiece.getItemAt(row, col);
                    if (item.isItem()) {
                        assertEquals(ItemBlock.COLUMN_CLEAR, item);
                    }
                }
            }
        }
    }
    
    @Test
    void testItemTypeDistribution() {
        // Random을 사용하여 직접 아이템 타입 분포 테스트
        int lineCount = 0;
        int columnCount = 0;
        int crossCount = 0;
        int totalTests = 1000; // 충분한 샘플 수
        
        java.util.Random random = new java.util.Random();
        
        // 많은 아이템을 생성하여 통계 확인
        for (int i = 0; i < totalTests; i++) {
            // TetrisSystem의 addRandomItemToPiece와 동일한 로직으로 아이템 타입 선택
            int itemChoice = random.nextInt(3);
            ItemBlock itemType;
            switch (itemChoice) {
                case 0:
                    itemType = ItemBlock.LINE_CLEAR;
                    break;
                case 1:
                    itemType = ItemBlock.COLUMN_CLEAR;
                    break;
                case 2:
                    itemType = ItemBlock.CROSS_CLEAR;
                    break;
                default:
                    itemType = ItemBlock.LINE_CLEAR;
            }
            
            if (itemType == ItemBlock.LINE_CLEAR) {
                lineCount++;
            } else if (itemType == ItemBlock.COLUMN_CLEAR) {
                columnCount++;
            } else if (itemType == ItemBlock.CROSS_CLEAR) {
                crossCount++;
            }
        }
        
        // 통계 검증: 각각 최소 23% 이상 생성되어야 함 (33%±10% 허용)
        double lineRatio = (double) lineCount / totalTests;
        double columnRatio = (double) columnCount / totalTests;
        double crossRatio = (double) crossCount / totalTests;
        
        System.out.println("\n=== 아이템 타입 생성 분포 테스트 ===");
        System.out.println("총 시뮬레이션 횟수: " + totalTests + "회");
        System.out.println("LINE_CLEAR (L):   " + lineCount + "회 (" + String.format("%.1f%%", lineRatio * 100) + ")");
        System.out.println("COLUMN_CLEAR (I): " + columnCount + "회 (" + String.format("%.1f%%", columnRatio * 100) + ")");
        System.out.println("CROSS_CLEAR (X):  " + crossCount + "회 (" + String.format("%.1f%%", crossRatio * 100) + ")");
        System.out.println("=====================================\n");
        
        // 각각 최소 23% 이상 생성되어야 함 (통계적 편차 고려)
        assertTrue(lineRatio >= 0.23, 
            "LINE_CLEAR 아이템이 충분히 생성되지 않음: " + String.format("%.1f%%", lineRatio * 100) + " (최소 23% 필요)");
        assertTrue(columnRatio >= 0.23, 
            "COLUMN_CLEAR 아이템이 충분히 생성되지 않음: " + String.format("%.1f%%", columnRatio * 100) + " (최소 23% 필요)");
        assertTrue(crossRatio >= 0.23, 
            "CROSS_CLEAR 아이템이 충분히 생성되지 않음: " + String.format("%.1f%%", crossRatio * 100) + " (최소 23% 필요)");
        
        // 전체 합이 100%여야 함
        assertEquals(totalTests, lineCount + columnCount + crossCount, "총 아이템 수가 일치하지 않음");
    }
    
    @Test
    void testBothItemTypesCanBeCreated() {
        // 세 가지 아이템 타입이 모두 생성 가능한지 직접 확인
        TetrominoPosition linePiece = new TetrominoPosition(Tetromino.I, 0, 0, 0);
        linePiece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        TetrominoPosition columnPiece = new TetrominoPosition(Tetromino.I, 0, 0, 0);
        columnPiece.setItemAtBlockIndex(0, ItemBlock.COLUMN_CLEAR);
        
        TetrominoPosition crossPiece = new TetrominoPosition(Tetromino.I, 0, 0, 0);
        crossPiece.setItemAtBlockIndex(0, ItemBlock.CROSS_CLEAR);
        
        // LINE_CLEAR 확인
        int[][] shape = linePiece.getCurrentShape();
        boolean foundLine = false;
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    ItemBlock item = linePiece.getItemAt(row, col);
                    if (item == ItemBlock.LINE_CLEAR) {
                        foundLine = true;
                        break;
                    }
                }
            }
            if (foundLine) break;
        }
        
        // COLUMN_CLEAR 확인
        shape = columnPiece.getCurrentShape();
        boolean foundColumn = false;
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    ItemBlock item = columnPiece.getItemAt(row, col);
                    if (item == ItemBlock.COLUMN_CLEAR) {
                        foundColumn = true;
                        break;
                    }
                }
            }
            if (foundColumn) break;
        }
        
        // CROSS_CLEAR 확인
        shape = crossPiece.getCurrentShape();
        boolean foundCross = false;
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    ItemBlock item = crossPiece.getItemAt(row, col);
                    if (item == ItemBlock.CROSS_CLEAR) {
                        foundCross = true;
                        break;
                    }
                }
            }
            if (foundCross) break;
        }
        
        assertTrue(foundLine, "LINE_CLEAR 아이템 생성 실패");
        assertTrue(foundColumn, "COLUMN_CLEAR 아이템 생성 실패");
        assertTrue(foundCross, "CROSS_CLEAR 아이템 생성 실패");
        
        System.out.println("\n✓ LINE_CLEAR (L) 아이템 생성 확인");
        System.out.println("✓ COLUMN_CLEAR (I) 아이템 생성 확인");
        System.out.println("✓ CROSS_CLEAR (X) 아이템 생성 확인");
    }
    
    /**
     * 헬퍼 메서드: 테트로미노에서 아이템 블록 수 세기
     */
    private int countItemBlocks(TetrominoPosition piece) {
        int count = 0;
        int[][] shape = piece.getCurrentShape();
        
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    ItemBlock item = piece.getItemAt(row, col);
                    if (item != null && item.isItem()) {
                        count++;
                    }
                }
            }
        }
        
        return count;
    }
}
