package org.example.service;

import org.example.model.GameMode;
import org.example.model.ItemBlock;
import org.example.model.Tetromino;
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
        
        // private 메서드 addRandomItemToPiece에 접근
        Method addItemMethod = ItemTetrisSystem.class.getDeclaredMethod("addRandomItemToPiece", TetrominoPosition.class);
        addItemMethod.setAccessible(true);
        
        int lineCount = 0;
        int columnCount = 0;
        int crossCount = 0;
        int noneCount = 0;
        int totalTests = 100;
        
        System.out.println("\n========================================");
        System.out.println("실제 TetrisSystem 아이템 생성 테스트");
        System.out.println("========================================");
        
        // 여러 번 아이템을 생성하여 분포 확인
        for (int i = 0; i < totalTests; i++) {
            TetrominoPosition piece = new TetrominoPosition(Tetromino.I, 0, 0, 0);
            
            // TetrisSystem의 실제 메서드 호출
            addItemMethod.invoke(game, piece);
            
            // 생성된 아이템 타입 확인
            int[][] shape = piece.getCurrentShape();
            ItemBlock foundItem = ItemBlock.NONE;
            
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
            } else {
                noneCount++;
            }
        }
        
        double lineRatio = (double) lineCount / totalTests * 100;
        double columnRatio = (double) columnCount / totalTests * 100;
        double crossRatio = (double) crossCount / totalTests * 100;
        
        System.out.println("\n총 테스트 횟수: " + totalTests);
        System.out.println("----------------------------------------");
        System.out.println("LINE_CLEAR (L):   " + lineCount + "개 (" + String.format("%.1f%%", lineRatio) + ")");
        System.out.println("COLUMN_CLEAR (I): " + columnCount + "개 (" + String.format("%.1f%%", columnRatio) + ")");
        System.out.println("CROSS_CLEAR (X):  " + crossCount + "개 (" + String.format("%.1f%%", crossRatio) + ")");
        System.out.println("NONE:             " + noneCount + "개");
        System.out.println("========================================\n");
        
        // 검증: 각각 최소 20% 이상 생성되어야 함 (통계적 편차 고려, 33%±13%)
        assertTrue(lineCount > 0, "LINE_CLEAR 아이템이 전혀 생성되지 않음!");
        assertTrue(columnCount > 0, "COLUMN_CLEAR 아이템이 전혀 생성되지 않음!");
        assertTrue(crossCount > 0, "CROSS_CLEAR 아이템이 전혀 생성되지 않음!");
        assertTrue(lineRatio >= 20.0, 
            "LINE_CLEAR 비율이 너무 낮음: " + String.format("%.1f%%", lineRatio) + " (최소 20% 필요)");
        assertTrue(columnRatio >= 20.0, 
            "COLUMN_CLEAR 비율이 너무 낮음: " + String.format("%.1f%%", columnRatio) + " (최소 20% 필요)");
        assertTrue(crossRatio >= 20.0, 
            "CROSS_CLEAR 비율이 너무 낮음: " + String.format("%.1f%%", crossRatio) + " (최소 20% 필요)");
        
        // 아이템이 없는 경우가 없어야 함 (모든 조각에 아이템이 부착되어야 함)
        assertEquals(0, noneCount, "아이템이 부착되지 않은 조각이 있음: " + noneCount + "개");
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
