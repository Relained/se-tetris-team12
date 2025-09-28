package org.example.game.logic;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameLogic {
    private final GameBoard board;
    private TetrominoPosition currentPiece;
    private TetrominoPosition holdPiece;
    private final List<Tetromino> nextQueue;
    private final Random random;

    private int score;
    private int lines;
    private int level;
    private boolean canHold;
    private boolean gameOver;

    // Scoring system
    private static final int[] LINE_SCORES = {0, 100, 300, 500, 800}; // 0, 1, 2, 3, 4 lines
    private static final int SOFT_DROP_SCORE = 1;
    private static final int HARD_DROP_SCORE = 2;

    public GameLogic() {
        this.board = new GameBoard();
        this.nextQueue = new ArrayList<>();
        this.random = new Random();
        this.score = 0;
        this.lines = 0;
        this.level = 1;
        this.canHold = true;
        this.gameOver = false;

        fillNextQueue();
        spawnNewPiece();
    }

    private void fillNextQueue() {
        while (nextQueue.size() < 7) {
            List<Tetromino> bag = new ArrayList<>();
            Collections.addAll(bag, Tetromino.values());
            Collections.shuffle(bag, random);
            nextQueue.addAll(bag);
        }
    }

    private void spawnNewPiece() {
        if (nextQueue.isEmpty()) {
            fillNextQueue();
        }

        Tetromino nextType = nextQueue.remove(0);
        fillNextQueue();

        // Spawn position (top-center of board, accounting for buffer zone)
        int spawnX = (GameBoard.WIDTH - nextType.getShape()[0].length) / 2;
        int spawnY = GameBoard.BUFFER_ZONE - nextType.getShape().length;

        currentPiece = new TetrominoPosition(nextType, spawnX, spawnY, 0);
        canHold = true;

        if (!board.isValidPosition(currentPiece)) {
            gameOver = true;
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
            score += SOFT_DROP_SCORE;
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
        score += dropDistance * HARD_DROP_SCORE;

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
            int spawnX = (GameBoard.WIDTH - temp.getType().getShape()[0].length) / 2;
            int spawnY = GameBoard.BUFFER_ZONE - temp.getType().getShape().length;
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
            score += LINE_SCORES[clearedLines] * level;
            level = Math.min(20, (lines / 10) + 1);
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

    // Getters
    public GameBoard getBoard() { return board; }
    public TetrominoPosition getCurrentPiece() { return currentPiece; }
    public TetrominoPosition getHoldPiece() { return holdPiece; }
    public List<Tetromino> getNextQueue() { return new ArrayList<>(nextQueue.subList(0, Math.min(5, nextQueue.size()))); }
    public int getScore() { return score; }
    public int getLines() { return lines; }
    public int getLevel() { return level; }
    public boolean isGameOver() { return gameOver; }

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
        // Drop interval in milliseconds, decreases with level
        return Math.max(50, 1000 - (level - 1) * 50);
    }
}