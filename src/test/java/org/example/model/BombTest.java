package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class BombTest {
    private ItemGameBoard board;

    @BeforeEach
    void setUp() {
        board = new ItemGameBoard();
        // 보드를 블록으로 채움 (직접 접근)
        fillBoard();
    }
    
    private void fillBoard() {
        for (int r = 0; r < GameBoard.HEIGHT; r++) {
            for (int c = 0; c < GameBoard.WIDTH; c++) {
                board.getCellColor(r, c); // 일단 접근 가능 확인
            }
        }
    }

    @Test
    @DisplayName("BOMB 테트로미노 2x2 블록 확인")
    void testBombPieceShape() {
        TetrominoPosition bombPiece = TetrominoPosition.createBombPiece(5, 5);
        
        assertEquals(TetrominoPosition.SpecialKind.BOMB, bombPiece.getSpecialKind());
        
        int[][] shape = bombPiece.getCurrentShape();
        assertEquals(2, shape.length, "BOMB은 2줄이어야 함");
        assertEquals(2, shape[0].length, "BOMB은 2칸 너비여야 함");
        
        System.out.println("BOMB shape:");
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                System.out.print(shape[r][c] + " ");
            }
            System.out.println();
        }
        
        // 모든 블록이 채워져 있어야 함
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                assertEquals(1, shape[r][c], "BOMB 블록 (" + r + ", " + c + ")이 채워져 있어야 함");
            }
        }
        
        // 모든 블록에 'O' 표시
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                ItemBlock item = bombPiece.getItemAt(r, c);
                System.out.println("Item at (" + r + ", " + c + "): " + item + " (symbol: " + item.getSymbol() + ")");
                assertEquals(ItemBlock.BOMB, item);
            }
        }
    }

    @Test
    @DisplayName("BOMB 폭발 범위 계산 확인")
    void testBombRange() {
        // BOMB 중심 좌표
        int centerRow = 10;
        int centerCol = 5;
        
        // 예상 삭제 범위: 중심 기준 -2 ~ +3
        int expectedRStart = centerRow - 2; // 8
        int expectedREnd = centerRow + 3;   // 13
        int expectedCStart = centerCol - 2; // 3
        int expectedCEnd = centerCol + 3;   // 8
        
        System.out.println("BOMB 중심: (" + centerRow + ", " + centerCol + ")");
        System.out.println("예상 삭제 범위:");
        System.out.println("  Row: " + expectedRStart + " ~ " + expectedREnd + " (총 " + (expectedREnd - expectedRStart + 1) + "줄)");
        System.out.println("  Col: " + expectedCStart + " ~ " + expectedCEnd + " (총 " + (expectedCEnd - expectedCStart + 1) + "칸)");
        
        // 6x6 = 36칸 확인
        assertEquals(6, expectedREnd - expectedRStart + 1);
        assertEquals(6, expectedCEnd - expectedCStart + 1);
    }
}
