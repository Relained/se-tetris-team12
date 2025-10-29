package org.example.service;

import org.example.model.GameBoard;
import org.example.model.ItemBlock;
import org.example.model.TetrominoPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * 아이템 모드용 테트리스 시스템
 * TetrisSystem을 상속하여 아이템 관련 기능을 추가합니다.
 * - 10줄마다 아이템이 부착된 테트로미노 생성
 * - LINE_CLEAR, COLUMN_CLEAR, CROSS_CLEAR 아이템 지원
 */
public class ItemTetrisSystem extends TetrisSystem {
    
    private int linesSinceLastItem;  // 마지막 아이템 생성 이후 삭제된 줄 수
    private boolean nextPieceShouldHaveItem;  // 다음 생성될 블록에 아이템 포함 여부
    
    public ItemTetrisSystem() {
        super();
        this.linesSinceLastItem = 0;
        this.nextPieceShouldHaveItem = false;
    }
    
    @Override
    protected void spawnNewPiece() {
        // 부모 클래스의 spawnNewPiece 호출
        super.spawnNewPiece();
        
        // 아이템 추가가 필요한 경우
        if (nextPieceShouldHaveItem && currentPiece != null) {
            addRandomItemToPiece(currentPiece);
            nextPieceShouldHaveItem = false;
        }
    }
    
    /**
     * 테트로미노에 랜덤한 위치와 랜덤한 타입의 아이템을 부착합니다.
     */
    private void addRandomItemToPiece(TetrominoPosition piece) {
        // rotation 0 기준으로 블록 인덱스 계산 (회전에 독립적)
        int[][] shape = piece.getType().getShape(0);
        List<Integer> blockIndices = new ArrayList<>();
        
        // 블록이 있는 모든 위치의 인덱스 수집
        int blockIndex = 0;
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    blockIndices.add(blockIndex);
                    blockIndex++;
                }
            }
        }
        
        // 랜덤한 블록 인덱스와 아이템 타입 선택
        if (!blockIndices.isEmpty()) {
            int randomBlockIndex = blockIndices.get(random.nextInt(blockIndices.size()));
            
            // 아이템 타입 랜덤 선택 (LINE_CLEAR, COLUMN_CLEAR, CROSS_CLEAR 각 33%)
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
            
            piece.setItemAtBlockIndex(randomBlockIndex, itemType);
        }
    }
    
    @Override
    protected void lockPiece() {
        board.placeTetromino(currentPiece);

        // 아이템 모드: 십자, 가로줄, 세로줄 순으로 처리
        int clearedCrosses = board.clearCrossesWithItems();
        int clearedLines = board.clearLinesWithItems();
        int clearedColumns = board.clearColumnsWithItems();
        
        int totalCleared = clearedLines + clearedColumns + clearedCrosses;
        
        if (totalCleared > 0) {
            lines += totalCleared;
            
            int lineScore;
            if (totalCleared <= LINE_SCORES.length - 1) {
                lineScore = LINE_SCORES[totalCleared];
            } else {
                // 5줄 이상: 4줄 점수(800) + 추가 줄당 100점
                lineScore = LINE_SCORES[4] + (totalCleared - 4) * 100;
            }
            score += lineScore * level;
            
            level = Math.min(20, (lines / 10) + 1);
            
            // 10줄마다 새로운 아이템 생성
            linesSinceLastItem += totalCleared;
            if (linesSinceLastItem >= ItemBlock.LINES_FOR_ITEM_GENERATION) {
                nextPieceShouldHaveItem = true;
                linesSinceLastItem -= ItemBlock.LINES_FOR_ITEM_GENERATION;
            }
        }

        if (board.isGameOver()) {
            gameOver = true;
        } else {
            spawnNewPiece();
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        linesSinceLastItem = 0;
        nextPieceShouldHaveItem = false;
    }
    
    // 추가 getter
    public boolean nextPieceHasItem() {
        return nextPieceShouldHaveItem;
    }
}
