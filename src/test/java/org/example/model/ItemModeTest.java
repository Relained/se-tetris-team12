package org.example.model;

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
        assertEquals(GameMode.NORMAL, game.getGameMode());
        assertFalse(game.nextPieceHasItem());
    }

    @Test
    void testItemModeCreation() {
        TetrisSystem game = new TetrisSystem(GameMode.ITEM);
        assertEquals(GameMode.ITEM, game.getGameMode());
        assertFalse(game.nextPieceHasItem());
    }

    @Test
    void testItemBlockTypes() {
        ItemBlock lineClear = ItemBlock.LINE_CLEAR;
        assertEquals('L', lineClear.getSymbol());
        assertTrue(lineClear.isItem());

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
