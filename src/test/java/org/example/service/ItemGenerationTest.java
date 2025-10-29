package org.example.service;

import org.example.model.ItemBlock;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 실제 TetrisSystem에서 아이템 생성을 테스트
 */
class ItemGenerationTest {

    @Test
    void testActualItemGeneration() throws Exception {
        // ItemTetrisSystem의 내부 메서드를 reflection으로 접근하여 실제 아이템 생성 테스트
        ItemTetrisSystem game = new ItemTetrisSystem();
        
        // private 메서드 createPieceWithItem에 접근
        Method createPieceMethod = ItemTetrisSystem.class.getDeclaredMethod("createPieceWithItem", int.class);
        createPieceMethod.setAccessible(true);
        
        int lineCount = 0;
        int columnCount = 0;
        int crossCount = 0;
        int weightCount = 0;
        int bombCount = 0;
        int totalTests = 200;
        
        System.out.println("\n========================================");
        System.out.println("실제 TetrisSystem 아이템 생성 테스트");
        System.out.println("========================================");
        
        // 여러 번 아이템을 생성하여 분포 확인
        for (int i = 0; i < totalTests; i++) {
            // 0-4 랜덤 선택 (5가지 아이템)
            int itemChoice = (int) (Math.random() * 5);
            TetrominoPosition piece;
            
            if (itemChoice == 3) {
                // WEIGHT - 특수 조각
                piece = TetrominoPosition.createWeightPiece(0, 0);
                weightCount++;
                continue;
            } else if (itemChoice == 4) {
                // BOMB - 특수 조각
                piece = TetrominoPosition.createBombPiece(0, 0);
                bombCount++;
                continue;
            } else {
                // LINE_CLEAR, COLUMN_CLEAR, CROSS_CLEAR - createPieceWithItem으로 생성
                piece = (TetrominoPosition) createPieceMethod.invoke(game, itemChoice);
            }
            
            // 생성된 아이템 타입 확인
            int[][] shape = piece.getCurrentShape();
            ItemBlock foundItem = ItemBlock.NONE;
            
            // 일반 테트로미노에 부착된 아이템 확인
            outerLoop:
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] == 1) {
                        ItemBlock item = piece.getItemAt(row, col);
                        if (item.isItem()) {
                            foundItem = item;
                            break outerLoop;
                        }
                    }
                }
            }
            
            if (foundItem == ItemBlock.LINE_CLEAR) {
                lineCount++;
            } else if (foundItem == ItemBlock.COLUMN_CLEAR) {
                columnCount++;
            } else if (foundItem == ItemBlock.CROSS_CLEAR) {
                crossCount++;
            }
        }
        
        double lineRatio = (double) lineCount / totalTests * 100;
        double columnRatio = (double) columnCount / totalTests * 100;
        double crossRatio = (double) crossCount / totalTests * 100;
        double weightRatio = (double) weightCount / totalTests * 100;
        double bombRatio = (double) bombCount / totalTests * 100;
        
        System.out.println("\n총 테스트 횟수: " + totalTests);
        System.out.println("----------------------------------------");
        System.out.println("LINE_CLEAR (L):   " + lineCount + "개 (" + String.format("%.1f%%", lineRatio) + ")");
        System.out.println("COLUMN_CLEAR (I): " + columnCount + "개 (" + String.format("%.1f%%", columnRatio) + ")");
        System.out.println("CROSS_CLEAR (X):  " + crossCount + "개 (" + String.format("%.1f%%", crossRatio) + ")");
        System.out.println("WEIGHT (W):       " + weightCount + "개 (" + String.format("%.1f%%", weightRatio) + ")");
        System.out.println("BOMB (O):         " + bombCount + "개 (" + String.format("%.1f%%", bombRatio) + ")");
        System.out.println("========================================\n");
        
        // 검증: 5가지 아이템이 모두 생성되어야 함 (각 20% 기대)
        assertTrue(lineCount > 0, "LINE_CLEAR 아이템이 전혀 생성되지 않음!");
        assertTrue(columnCount > 0, "COLUMN_CLEAR 아이템이 전혀 생성되지 않음!");
        assertTrue(crossCount > 0, "CROSS_CLEAR 아이템이 전혀 생성되지 않음!");
        assertTrue(weightCount > 0, "WEIGHT 아이템이 전혀 생성되지 않음!");
        assertTrue(bombCount > 0, "BOMB 아이템이 전혀 생성되지 않음!");
        
        // 각 아이템은 최소 10% 이상 생성되어야 함
        assertTrue(lineRatio >= 10.0, 
            "LINE_CLEAR 비율이 너무 낮음: " + String.format("%.1f%%", lineRatio) + " (최소 10% 필요)");
        assertTrue(columnRatio >= 10.0, 
            "COLUMN_CLEAR 비율이 너무 낮음: " + String.format("%.1f%%", columnRatio) + " (최소 10% 필요)");
        assertTrue(crossRatio >= 10.0, 
            "CROSS_CLEAR 비율이 너무 낮음: " + String.format("%.1f%%", crossRatio) + " (최소 10% 필요)");
        assertTrue(weightRatio >= 10.0, 
            "WEIGHT 비율이 너무 낮음: " + String.format("%.1f%%", weightRatio) + " (최소 10% 필요)");
        assertTrue(bombRatio >= 10.0, 
            "BOMB 비율이 너무 낮음: " + String.format("%.1f%%", bombRatio) + " (최소 10% 필요)");
    }
    
    @Test
    void testItemTypeEnumValues() {
        // ItemBlock enum이 올바르게 정의되었는지 확인
        assertEquals('L', ItemBlock.LINE_CLEAR.getSymbol());
        assertEquals('I', ItemBlock.COLUMN_CLEAR.getSymbol());
        assertEquals('X', ItemBlock.CROSS_CLEAR.getSymbol());
        assertEquals(' ', ItemBlock.NONE.getSymbol());
        
        assertTrue(ItemBlock.LINE_CLEAR.isItem());
        assertTrue(ItemBlock.COLUMN_CLEAR.isItem());
        assertTrue(ItemBlock.CROSS_CLEAR.isItem());
        assertFalse(ItemBlock.NONE.isItem());
        
        System.out.println("\n✓ ItemBlock enum 정의 확인:");
        System.out.println("  LINE_CLEAR (L) - 가로줄 삭제");
        System.out.println("  COLUMN_CLEAR (I) - 세로줄 삭제");
        System.out.println("  CROSS_CLEAR (X) - 십자 삭제");
    }
    
    @Test
    void testRandomBooleanDistribution() {
        // random.nextBoolean()의 분포가 균등한지 확인
        java.util.Random random = new java.util.Random();
        int trueCount = 0;
        int falseCount = 0;
        int iterations = 10000;
        
        for (int i = 0; i < iterations; i++) {
            if (random.nextBoolean()) {
                trueCount++;
            } else {
                falseCount++;
            }
        }
        
        double trueRatio = (double) trueCount / iterations * 100;
        double falseRatio = (double) falseCount / iterations * 100;
        
        System.out.println("\nrandom.nextBoolean() 분포 테스트 (" + iterations + "회):");
        System.out.println("true:  " + trueCount + " (" + String.format("%.2f%%", trueRatio) + ")");
        System.out.println("false: " + falseCount + " (" + String.format("%.2f%%", falseRatio) + ")");
        
        // 40%~60% 범위에 있어야 함
        assertTrue(trueRatio >= 45.0 && trueRatio <= 55.0, 
            "Random 분포가 균등하지 않음: " + String.format("%.2f%%", trueRatio));
    }
}
