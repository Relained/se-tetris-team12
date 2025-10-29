package org.example.service;


import org.example.model.ItemBlock;
import org.example.model.ItemGameBoard;
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
        super(new ItemGameBoard());
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
            
            // 아이템 타입 랜덤 선택 (LINE, COLUMN, CROSS, WEIGHT, BOMB)
            int itemChoice = random.nextInt(5);
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
                case 3:
                    itemType = ItemBlock.WEIGHT;
                    break;
                case 4:
                    itemType = ItemBlock.BOMB;
                    break;
                default:
                    itemType = ItemBlock.LINE_CLEAR;
            }
            
            if (itemType == ItemBlock.WEIGHT || itemType == ItemBlock.BOMB) {
                // 특수 아이템은 ITEM 테트로미노로 대체 스폰
                int[][] custom = (itemType == ItemBlock.WEIGHT)
                        ? new int[][]{{0,1,1,0},{1,1,1,1}}
                        : new int[][]{{1,1},{1,1}};
                int spawnX = (org.example.model.GameBoard.WIDTH - custom[0].length) / 2;
                int spawnY = org.example.model.GameBoard.BUFFER_ZONE - custom.length;
                if (itemType == ItemBlock.WEIGHT) {
                    this.currentPiece = org.example.model.TetrominoPosition.createWeightPiece(spawnX, spawnY);
                } else {
                    this.currentPiece = org.example.model.TetrominoPosition.createBombPiece(spawnX, spawnY);
                }
            } else {
                piece.setItemAtBlockIndex(randomBlockIndex, itemType);
            }
        }
    }

    // ===== 특수 아이템 동작 상태 =====
    private boolean weightActive = false;
    private int weightCurrentRow;
    private int weightStartCol;
    
    @Override
    protected void lockPiece() {
        board.placeTetromino(currentPiece);

        // 일반 아이템 효과 처리 (WEIGHT/BOMB는 moveDown에서 처리)
        ItemGameBoard itemBoard = (ItemGameBoard) board;

        int clearedCrosses = itemBoard.clearCrossesWithItems();
        int clearedLines = itemBoard.clearLinesWithItems();
        int clearedColumns = itemBoard.clearColumnsWithItems();
        
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
            score += lineScore * calcScoreFactor();
            
            level = Math.min(20, (lines / levelFactor) + 1);
            
            // 10줄마다 새로운 아이템 생성
            linesSinceLastItem += totalCleared;
            if (linesSinceLastItem >= ItemBlock.LINES_FOR_ITEM_GENERATION) {
                // 아이템 생성 주기 충족 시 다음 조각에 아이템 부착
                nextPieceShouldHaveItem = true;
                linesSinceLastItem = 0;
            }
        }

        if (board.isGameOver()) {
            gameOver = true;
        } else {
            spawnNewPiece();
        }
    }

    @Override
    public boolean rotateClockwise() {
        if (currentPiece != null && currentPiece.isRotationLocked()) return false;
        return super.rotateClockwise();
    }

    @Override
    public boolean rotateCounterClockwise() {
        if (currentPiece != null && currentPiece.isRotationLocked()) return false;
        return super.rotateCounterClockwise();
    }

    @Override
    public void hardDrop() {
        if (gameOver || currentPiece == null) return;
        var special = currentPiece.getSpecialKind();
        if (special == org.example.model.TetrominoPosition.SpecialKind.BOMB) {
            // 즉시 바닥 위치로 가정하고 폭탄 효과 적용
            int topLeftRow = Math.max(0, currentPiece.getY());
            int topLeftCol = Math.max(0, currentPiece.getX());
            ((ItemGameBoard)board).triggerBombAt(topLeftRow, topLeftCol);
            currentPiece = null;
            spawnNewPiece();
            return;
        } else if (special == org.example.model.TetrominoPosition.SpecialKind.WEIGHT) {
            // 락 후 단계적 제거 시작
            weightActive = true;
            weightCurrentRow = Math.max(0, currentPiece.getY() + (currentPiece.getCurrentShape().length - 1));
            weightStartCol = Math.max(0, currentPiece.getX());
            currentPiece = null;
            return;
        }
        super.hardDrop();
    }

    @Override
    public boolean moveDown() {
        if (weightActive) return false;
        if (gameOver || currentPiece == null) return false;

        var special = currentPiece.getSpecialKind();
        org.example.model.TetrominoPosition newPos = org.example.service.SuperRotationSystem.moveDown(currentPiece, board);
        if (newPos != null) {
            currentPiece = newPos;
            score += SOFT_DROP_SCORE * calcScoreFactor();
            return true;
        } else {
            if (special == org.example.model.TetrominoPosition.SpecialKind.BOMB) {
                int topLeftRow = Math.max(0, currentPiece.getY());
                int topLeftCol = Math.max(0, currentPiece.getX());
                ((ItemGameBoard)board).triggerBombAt(topLeftRow, topLeftCol);
                currentPiece = null;
                spawnNewPiece();
                return false;
            } else if (special == org.example.model.TetrominoPosition.SpecialKind.WEIGHT) {
                weightActive = true;
                weightCurrentRow = Math.max(0, currentPiece.getY() + (currentPiece.getCurrentShape().length - 1));
                weightStartCol = Math.max(0, currentPiece.getX());
                currentPiece = null;
                return false;
            }
            // 일반 조각
            lockPiece();
            return false;
        }
    }

    @Override
    public void update() {
        if (weightActive) {
            ItemGameBoard igb = (ItemGameBoard) board;
            igb.clearWeightStep(weightCurrentRow, weightStartCol, 4);
            weightCurrentRow += 4;
            if (weightCurrentRow > org.example.model.GameBoard.HEIGHT + org.example.model.GameBoard.BUFFER_ZONE - 1) {
                weightActive = false;
                spawnNewPiece();
            }
            return;
        }
        super.update();
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
