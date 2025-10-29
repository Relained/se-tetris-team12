package org.example.service;

import org.example.model.GameBoard;
import org.example.model.GameMode;
import org.example.model.ItemBlock;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Deque;
import java.util.ArrayDeque;

public class TetrisSystem {
    private final GameBoard board;
    private TetrominoPosition currentPiece;
    private TetrominoPosition holdPiece;
    private final Deque<Tetromino> nextQueue;
    private final Random random;
    private final ArrayList<Double> cumulativeWeights;

    // 게임 상태
    private int score;
    private int lines;
    private int level;
    private int difficulty;
    private int levelFactor;
    private boolean canHold;
    private boolean gameOver;
    
    // 아이템 모드 관련
    private final GameMode gameMode;
    private int linesSinceLastItem;  // 마지막 아이템 생성 이후 삭제된 줄 수
    private boolean nextPieceShouldHaveItem;  // 다음 생성될 블록에 아이템 포함 여부

    // 점수 시스템
    private static final int[] LINE_SCORES = {0, 100, 300, 500, 800}; // 0, 1, 2, 3, 4 lines
    private static final int SOFT_DROP_SCORE = 1;
    private static final int HARD_DROP_SCORE = 2;
    private static final int QUEUEING_SIZE = 7;

    public TetrisSystem() {
        this(GameMode.NORMAL);
    }
    
    public TetrisSystem(GameMode mode) {
        this.gameMode = mode;
        this.board = new GameBoard();
        this.nextQueue = new ArrayDeque<>();
        this.random = new Random();
        this.cumulativeWeights = new ArrayList<>();
        this.score = 0;
        this.lines = 0;
        this.level = 1;
        this.difficulty = 2;
        this.levelFactor = 10;
        this.canHold = true;
        this.gameOver = false;
        this.linesSinceLastItem = 0;
        this.nextPieceShouldHaveItem = false;

        double cum = 0.0;
        for (int i = 0; i < Tetromino.values().length; i++) {
            cum += 1.0;
            this.cumulativeWeights.add(cum);
        }

        fillNextQueue();
        spawnNewPiece();
    }

    private void fillNextQueue() {
        while (nextQueue.size() < QUEUEING_SIZE) {
            nextQueue.addLast(selectWeightedRandom());
        }
    }

    private void spawnNewPiece() {
        fillNextQueue();
        Tetromino nextType = nextQueue.pollFirst();

        // Spawn position (top-center of board, accounting for buffer zone)
        int spawnX = (GameBoard.WIDTH - nextType.getShape(0)[0].length) / 2;
        int spawnY = GameBoard.BUFFER_ZONE - nextType.getShape(0).length;

        currentPiece = new TetrominoPosition(nextType, spawnX, spawnY, 0);
        
        // 아이템 모드에서 아이템 추가
        if (gameMode == GameMode.ITEM && nextPieceShouldHaveItem) {
            addRandomItemToPiece(currentPiece);
            nextPieceShouldHaveItem = false;
        }
        
        canHold = true;

        if (!board.isValidPosition(currentPiece)) {
            gameOver = true;
        }
    }
    
    /**
     * 테트로미노에 랜덤한 위치와 랜덤한 타입의 아이템을 부착합니다.
     * 아이템은 rotation 0 기준의 블록 인덱스에 저장되며, 회전과 무관하게 동일한 물리적 블록을 추적합니다.
     * 아이템 타입은 LINE_CLEAR(가로줄)와 COLUMN_CLEAR(세로줄), CROSS_CLEAR(십자)가 각각 33%씩 확률로 선택됩니다.
     * 
     * @param piece 아이템을 부착할 테트로미노
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
            
            // 아이템 타입 랜덤 선택 (LINE_CLEAR: 33%, COLUMN_CLEAR: 33%, CROSS_CLEAR: 33%)
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

    private Tetromino selectWeightedRandom() {
        var values = Tetromino.values();
        double total = cumulativeWeights.get(cumulativeWeights.size() - 1);

        double r = random.nextDouble() * total; // [0, total)
        for (int i = 0; i < cumulativeWeights.size(); i++) {
            if (r < cumulativeWeights.get(i)) {
                return values[i];
            }
        }
        return values[values.length - 1];
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;

        if (this.difficulty == 1) {
            setTetrominoWeight(Tetromino.I, 1.2);
            levelFactor = 12;
        }
        else if (this.difficulty == 2) {
            setTetrominoWeight(Tetromino.I, 1.0);
            levelFactor = 10;
        }
        else if (this.difficulty == 3) {
            setTetrominoWeight(Tetromino.I, 0.8);
            levelFactor = 8;
        }
    }

    public boolean moveLeft() {
        if (gameOver || currentPiece == null) return false;

        TetrominoPosition newPos = SuperRotationSystem.moveLeft(currentPiece, board);
        if (newPos != null) {
            currentPiece = newPos;
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        if (gameOver || currentPiece == null) return false;

        TetrominoPosition newPos = SuperRotationSystem.moveRight(currentPiece, board);
        if (newPos != null) {
            currentPiece = newPos;
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        if (gameOver || currentPiece == null) return false;

        TetrominoPosition newPos = SuperRotationSystem.moveDown(currentPiece, board);
        if (newPos != null) {
            currentPiece = newPos;
            score += SOFT_DROP_SCORE * calcScoreFactor();
            return true;
        } else {
            // Piece has landed
            lockPiece();
            return false;
        }
    }

    public boolean rotateClockwise() {
        if (gameOver || currentPiece == null) return false;

        TetrominoPosition newPos = SuperRotationSystem.attemptRotation(currentPiece, board, true);
        if (newPos != null) {
            currentPiece = newPos;
            return true;
        }
        return false;
    }

    public boolean rotateCounterClockwise() {
        if (gameOver || currentPiece == null) return false;

        TetrominoPosition newPos = SuperRotationSystem.attemptRotation(currentPiece, board, false);
        if (newPos != null) {
            currentPiece = newPos;
            return true;
        }
        return false;
    }

    public void hardDrop() {
        if (gameOver || currentPiece == null) return;

        TetrominoPosition dropPos = SuperRotationSystem.hardDrop(currentPiece, board);
        int dropDistance = dropPos.getY() - currentPiece.getY();
        score += dropDistance * HARD_DROP_SCORE * calcScoreFactor();

        currentPiece = dropPos;
        lockPiece();
    }

    public boolean hold() {
        if (gameOver || !canHold || currentPiece == null) return false;

        if (holdPiece == null) {
            holdPiece = currentPiece;
            spawnNewPiece();
        } else {
            TetrominoPosition temp = holdPiece;
            holdPiece = currentPiece;

            // Reset held piece to spawn position
            int spawnX = (GameBoard.WIDTH - temp.getType().getShape(0)[0].length) / 2;
            int spawnY = GameBoard.BUFFER_ZONE - temp.getType().getShape(0).length;
            currentPiece = new TetrominoPosition(temp.getType(), spawnX, spawnY, 0);

            if (!board.isValidPosition(currentPiece)) {
                gameOver = true;
                return false;
            }
        }

        canHold = false;
        return true;
    }

    /**
     * 블록을 보드에 고정하고 줄 삭제, 점수 계산, 레벨 업데이트를 처리합니다.
     * 아이템 모드에서는 LINE_CLEAR 아이템이 있는 줄도 삭제합니다.
     */
    private void lockPiece() {
        board.placeTetromino(currentPiece);

        // 줄 삭제 (아이템 모드와 일반 모드 분기)
        int clearedLines;
        int clearedColumns = 0;
        int clearedCrosses = 0;
        
        if (gameMode == GameMode.ITEM) {
            // 십자 아이템을 먼저 처리 (가로+세로 동시 삭제)
            clearedCrosses = board.clearCrossesWithItems();
            // 그 다음 가로줄 아이템 처리
            clearedLines = board.clearLinesWithItems();
            // 마지막으로 세로줄 아이템 처리
            clearedColumns = board.clearColumnsWithItems();
        } else {
            clearedLines = board.clearLines();
        }
        
        int totalCleared = clearedLines + clearedColumns + clearedCrosses;
        
        if (totalCleared > 0) {
            lines += totalCleared;
            
            int lineScore;
            if (totalCleared <= LINE_SCORES.length - 1) {
                lineScore = LINE_SCORES[totalCleared];
            } else {
                // 5줄 이상: 4줄 점수(800) + 추가 줄당 100점 (아이템 모드에서 가능)
                lineScore = LINE_SCORES[4] + (totalCleared - 4) * 100;
            }
            score += lineScore * calcScoreFactor();
            
            level = Math.min(20, (lines / levelFactor) + 1);
            
            // 아이템 모드: 10줄마다 새로운 아이템 블록 생성
            if (gameMode == GameMode.ITEM) {
                linesSinceLastItem += totalCleared;
                if (linesSinceLastItem >= ItemBlock.LINES_FOR_ITEM_GENERATION) {
                    nextPieceShouldHaveItem = true;
                    linesSinceLastItem -= ItemBlock.LINES_FOR_ITEM_GENERATION;
                }
            }
        }

        if (board.isGameOver()) {
            gameOver = true;
        } else {
            spawnNewPiece();
        }
    }

    public void update() {
        if (!gameOver) {
            moveDown();
        }
    }

    private double calcScoreFactor() {
        //difficulty: 1(Easy), 2(Normal), 3(Hard)
        //-1 ~ 1 * 0.2 + 1 = 0.8, 1, 1.2
        double dfactor = (difficulty - 2) * 0.2 + 1;
        //level: 1~20
        //(level - 1) / 5 + 1 = 1 ~ 4.8
        double lfactor = (1 + (level - 1) / 5);
        return lfactor * dfactor;
    }

    // Getters
    public GameBoard getBoard() { return board; }
    public TetrominoPosition getCurrentPiece() { return currentPiece; }
    public TetrominoPosition getHoldPiece() { return holdPiece; }
    public List<Tetromino> getNextQueue() {
        List<Tetromino> preview = new ArrayList<>(5);
        int i = 0;
        for (Tetromino t : nextQueue) {
            if (i++ >= 5) break;
            preview.add(t);
        }
        return preview;
    }
    public int getScore() { return score; }
    public int getLines() { return lines; }
    public int getLevel() { return level; }
    public boolean isGameOver() { return gameOver; }
    public int getDifficulty() { return difficulty; }
    public GameMode getGameMode() { return gameMode; }
    public boolean nextPieceHasItem() { return nextPieceShouldHaveItem; }

    public void reset() {
        board.clear();
        currentPiece = null;
        holdPiece = null;
        nextQueue.clear();
        score = 0;
        lines = 0;
        level = 1;
        canHold = true;
        gameOver = false;
        linesSinceLastItem = 0;
        nextPieceShouldHaveItem = false;

        fillNextQueue();
        spawnNewPiece();
    }

    public long getDropInterval() {
        return Math.max(50, 1000 - (level - 1) * 50);
    }

    public void setTetrominoWeight(Tetromino type, double weight) {
        int n = cumulativeWeights.size();
        int idx = type.ordinal();

        double val = weight - cumulativeWeights.get(idx);
        for (int i = idx; i < n; i++) {
            cumulativeWeights.set(i, cumulativeWeights.get(i) + val);
        }
    }
}