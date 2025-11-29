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
    
    public ItemTetrisSystem() {
        super(new ItemGameBoard());
        this.linesSinceLastItem = 0;
    }
    
    /**
     * 아이템 테트로미노를 생성하여 큐의 인덱스 1번에 추가합니다 (바로 다음에 나올 위치).
     */
    private void generateItemPiece() {
        // 아이템 타입 랜덤 선택 (LINE, COLUMN, CROSS, WEIGHT, BOMB)
        int itemChoice = random.nextInt(5);
        TetrominoPosition itemPiece;
        
        switch (itemChoice) {
            case 0: // LINE_CLEAR
            case 1: // COLUMN_CLEAR
            case 2: // CROSS_CLEAR
                // 일반 아이템: 랜덤 테트로미노에 아이템 부착
                itemPiece = createPieceWithItem(itemChoice);
                break;
            case 3: // WEIGHT
                itemPiece = TetrominoPosition.createWeightPiece(0, 0);
                break;
            case 4: // BOMB
                itemPiece = TetrominoPosition.createBombPiece(0, 0);
                break;
            default:
                itemPiece = createPieceWithItem(0);
        }
        
        // 큐의 인덱스 1번 위치에 추가 (다음에 나올 조각)
        // 방법: 첫 번째 요소를 임시 저장 → 아이템 추가 → 첫 번째 요소 복원
        if (!nextQueue.isEmpty()) {
            TetrominoPosition firstPiece = nextQueue.removeFirst();
            nextQueue.addFirst(itemPiece);
            nextQueue.addFirst(firstPiece);
        } else {
            // 큐가 비어있으면 그냥 추가
            nextQueue.addFirst(itemPiece);
        }
        
        // 큐 크기가 QUEUEING_SIZE를 초과하면 맨 뒤 제거
        while (nextQueue.size() > QUEUEING_SIZE) {
            nextQueue.removeLast();
        }
    }
    
    /**
     * 랜덤 테트로미노에 아이템을 부착한 TetrominoPosition 생성
     */
    private TetrominoPosition createPieceWithItem(int itemChoice) {
        // 랜덤 테트로미노 선택 (부모 클래스의 private 메서드를 사용할 수 없으므로 직접 구현)
        var values = org.example.model.Tetromino.values();
        double total = 0;
        for (int i = 0; i < values.length; i++) {
            total += 1.0;
        }
        double r = random.nextDouble() * total;
        double cum = 0.0;
        org.example.model.Tetromino type = values[0];
        for (int i = 0; i < values.length; i++) {
            cum += 1.0;
            if (r < cum) {
                type = values[i];
                break;
            }
        }
        
        TetrominoPosition piece = new TetrominoPosition(type, 0, 0, 0);
        
        // 아이템 타입 결정
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
        
        // rotation 0 기준으로 블록 인덱스 계산 (회전에 독립적)
        int[][] shape = type.getShape(0);
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
        
        // 랜덤한 블록 인덱스에 아이템 부착
        if (!blockIndices.isEmpty()) {
            int randomBlockIndex = blockIndices.get(random.nextInt(blockIndices.size()));
            piece.setItemAtBlockIndex(randomBlockIndex, itemType);
        }
        
        return piece;
    }

    // ===== 특수 아이템 동작 상태 =====
    private boolean weightActive = false;
    private boolean weightShowingAtBottom = false;  // 무게추가 바닥에 표시 중인지
    private int weightStartCol;
    private int[][] weightShape;  // 무게추 모양 저장
    
    @Override
    protected void lockPiece() {
        // lockPiece 시작 전에 스냅샷 캡처 (라인 클리어 전 상태)
        captureSnapshotBeforeLock();
        
        TetrominoPosition.SpecialKind special = currentPiece != null ? currentPiece.getSpecialKind() : TetrominoPosition.SpecialKind.NONE;

        // 특수 조각 처리
        if (special == TetrominoPosition.SpecialKind.BOMB) {
            int topLeftRow = Math.max(0, currentPiece.getY());
            int topLeftCol = Math.max(0, currentPiece.getX());
            ((ItemGameBoard) board).triggerBombAt(topLeftRow, topLeftCol);

            // 폭발 후 다음 조각 생성
            currentPiece = null;
            if (board.isGameOver()) {
                gameOver = true;
            } else {
                spawnNewPiece();
            }
            return;
        }

        if (special == TetrominoPosition.SpecialKind.WEIGHT) {
            weightActive = true;
            weightStartCol = Math.max(0, Math.min(currentPiece.getX(), org.example.model.GameBoard.WIDTH - 4));
            return;
        }

        // 일반 조각: 보드에 고정 후 아이템 효과 처리
        board.placeTetromino(currentPiece);

        ItemGameBoard itemBoard = (ItemGameBoard) board;

        // 순서 중요: 열 삭제 → 십자 삭제 → 줄 삭제
        // (줄 삭제가 좌표를 변경하므로 가장 마지막에 실행)
        int clearedColumns = itemBoard.clearColumnsWithItems();
        int clearedCrosses = itemBoard.clearCrossesWithItems();
        int[] lineResults = itemBoard.clearLinesWithItems();  // [전체, 아이템으로 인한]
        
        int totalCleared = lineResults[0] + clearedColumns + clearedCrosses;
        int normalCleared = lineResults[0] - lineResults[1];  // 일반 삭제 = 전체 - 아이템
        
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
            
            // 10줄마다 새로운 아이템 생성 (아이템으로 인한 삭제는 제외)
            linesSinceLastItem += normalCleared;  // 일반 삭제만 카운트
            if (linesSinceLastItem >= ItemBlock.LINES_FOR_ITEM_GENERATION) {
                generateItemPiece();  // 큐의 맨 앞에 아이템 조각 추가
                linesSinceLastItem = 0;
            }
        }

        if (board.isGameOver()) {
            gameOver = true;
        } else {
            spawnNewPiece();
        }

        // 피스가 배치된 후 콜백 실행 (멀티플레이 공격용)
        if (onPieceLocked != null) {
            onPieceLocked.run();
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
            // BOMB: 먼저 바닥까지 내려간 다음 착지 위치에서 폭발
            super.hardDrop(); // 바닥까지 내려가서 착지
            // lockPiece()에서 이미 폭발 효과가 적용되므로 여기서는 추가 처리 불필요
            return;
        } else if (special == org.example.model.TetrominoPosition.SpecialKind.WEIGHT) {
            // 하드드롭: 무게추 아래를 모두 지우고 바닥에 배치
            ItemGameBoard igb = (ItemGameBoard) board;
            weightShape = currentPiece.getCurrentShape();
            weightStartCol = Math.max(0, currentPiece.getX());
            
            igb.triggerWeightEffect(currentPiece.getY(), weightStartCol, weightShape);
            weightActive = true;
            weightShowingAtBottom = true;
            currentPiece = null;
            return;
        }
        super.hardDrop();
    }

    @Override
    public boolean hold() {
        // 특수 아이템 조각(WEIGHT, BOMB)은 hold 불가
        if (currentPiece != null) {
            var special = currentPiece.getSpecialKind();
            if (special == org.example.model.TetrominoPosition.SpecialKind.WEIGHT ||
                special == org.example.model.TetrominoPosition.SpecialKind.BOMB) {
                return false; // hold 불가
            }
        }
        
        // hold 기능 구현 (아이템 정보 보존)
        if (gameOver || !canHold || currentPiece == null) return false;

        if (holdPiece == null) {
            holdPiece = currentPiece;
            spawnNewPiece();
        } else {
            TetrominoPosition temp = holdPiece;
            holdPiece = currentPiece;

            // Reset held piece to spawn position (아이템 정보 보존)
            int spawnX = (org.example.model.GameBoard.WIDTH - temp.getType().getShape(0)[0].length) / 2;
            int spawnY = org.example.model.GameBoard.BUFFER_ZONE - temp.getType().getShape(0).length;
            
            // 기존 조각을 복사한 후 위치만 변경 (아이템 정보 보존)
            currentPiece = temp.copy();
            currentPiece.setX(spawnX);
            currentPiece.setY(spawnY);
            currentPiece.setRotation(0);

            if (!board.isValidPosition(currentPiece)) {
                gameOver = true;
                return false;
            }
        }

        canHold = false;
        return true;
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
                // moveDown 실패: 무게추 아래를 모두 지우고 바닥에 배치
                ItemGameBoard igb = (ItemGameBoard) board;
                weightShape = currentPiece.getCurrentShape();
                weightStartCol = Math.max(0, currentPiece.getX());
                
                igb.triggerWeightEffect(currentPiece.getY(), weightStartCol, weightShape);
                weightActive = true;
                weightShowingAtBottom = true;
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
            if (weightShowingAtBottom) {
                // 바닥에 무게추가 표시 중 -> 다음 틱에 제거하고 새 조각 생성
                ItemGameBoard igb = (ItemGameBoard) board;
                igb.clearWeight(weightStartCol, weightShape);
                weightActive = false;
                weightShowingAtBottom = false;
                spawnNewPiece();
            }
            // 조각을 한 칸 아래로 이동시켜 내려가는 모습을 표시
            currentPiece.setY(currentPiece.getY() + 1);

            return;
        }
        super.update();
    }
    
    @Override
    public void reset() {
        super.reset();
        linesSinceLastItem = 0;
        weightActive = false;
        weightShowingAtBottom = false;
    }
}
