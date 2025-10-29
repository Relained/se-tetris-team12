package org.example.service;

import org.example.model.GameBoard;
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

    private int score;
    private int lines;
    private int level;
    private int difficulty;
    private int levelFactor;
    private boolean canHold;
    private boolean gameOver;

    // Scoring system
    private static final int[] LINE_SCORES = {0, 100, 300, 500, 800}; // 0, 1, 2, 3, 4 lines
    private static final int SOFT_DROP_SCORE = 1;
    private static final int HARD_DROP_SCORE = 2;
    private static final int QUEUEING_SIZE = 7;

    public TetrisSystem() {
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
        canHold = true;

        if (!board.isValidPosition(currentPiece)) {
            gameOver = true;
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

    private void lockPiece() {
        board.placeTetromino(currentPiece);

        int clearedLines = board.clearLines();
        if (clearedLines > 0) {
            lines += clearedLines;
            score += LINE_SCORES[clearedLines] * calcScoreFactor();
            level = Math.min(20, (lines / levelFactor) + 1);
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