package org.example.service;

import org.example.model.BoardSnapshot;
import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Deque;
import java.util.ArrayDeque;

public class TetrisSystem {
    protected final GameBoard board;
    protected TetrominoPosition currentPiece;
    protected TetrominoPosition holdPiece;
    protected final Deque<TetrominoPosition> nextQueue;
    protected final Random random;
    private final List<Double> cumulativeWeights;
    protected BoardSnapshot previousSnapshot;
    protected Runnable onPieceLocked;

    // 게임 상태
    protected int score;
    protected int lines;
    protected int level;
    protected int difficulty;
    protected int levelFactor;
    protected boolean canHold;
    protected boolean gameOver;

    // 점수 시스템
    protected static final int[] LINE_SCORES = {0, 100, 300, 500, 800}; // 0, 1, 2, 3, 4 lines
    protected static final int SOFT_DROP_SCORE = 1;
    protected static final int HARD_DROP_SCORE = 2;
    protected static final int QUEUEING_SIZE = 7;

    public TetrisSystem() {
        this(new GameBoard());
    }

    protected TetrisSystem(GameBoard board) {
        this.board = board;
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

    protected void fillNextQueue() {
        while (nextQueue.size() < QUEUEING_SIZE) {
            Tetromino type = selectWeightedRandom();
            nextQueue.addLast(new TetrominoPosition(type, 0, 0, 0));
        }
    }

    protected void spawnNewPiece() {
        fillNextQueue();
        TetrominoPosition nextPiece = nextQueue.pollFirst();

        // Spawn position (top-center of board, accounting for buffer zone)
        int[][] shape = nextPiece.getCurrentShape();
        int spawnX = (GameBoard.WIDTH - shape[0].length) / 2;
        int spawnY = GameBoard.BUFFER_ZONE - shape.length;

        currentPiece = nextPiece.copy();
        currentPiece.setX(spawnX);
        currentPiece.setY(spawnY);
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

    protected void lockPiece() {
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

        // 피스가 배치된 후 콜백 실행 (previousSnapshot 사용)
        if (onPieceLocked != null) {
            onPieceLocked.run();
        }

        // 현재 보드 상태를 스냅샷으로 캡처 (다음 턴에서 사용)
        captureSnapshotBeforeLock();
    }
    
    /**
     * 현재 보드에서 완성된 라인의 인덱스 리스트를 반환합니다.
     * 버퍼존을 제외한 보이는 영역 기준 인덱스(0-based)입니다.
     * CLEAR_MARK(-1)로 표시된 전체 라인을 찾습니다.
     * 
     * @return 완성된 라인의 인덱스 리스트
     */
    public java.util.List<Integer> getCompletedLineIndices() {
        java.util.List<Integer> completed = new java.util.ArrayList<>();
        
        for (int row = GameBoard.BUFFER_ZONE; row < GameBoard.HEIGHT + GameBoard.BUFFER_ZONE; row++) {
            boolean isFullyMarked = true;
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                if (board.getCellColor(row, col) != GameBoard.CLEAR_MARK) {
                    isFullyMarked = false;
                    break;
                }
            }
            if (isFullyMarked) {
                // 보이는 영역 기준으로 인덱스 변환
                completed.add(row - GameBoard.BUFFER_ZONE);
            }
        }
        
        return completed;
    }

    public void update() {
        if (!gameOver) {
            moveDown();
        }
    }

    protected double calcScoreFactor() {
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
    public BoardSnapshot getPreviousSnapshot() { return previousSnapshot; }
    public List<TetrominoPosition> getNextQueue() {
        List<TetrominoPosition> preview = new ArrayList<>(5);
        int i = 0;
        for (TetrominoPosition t : nextQueue) {
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
        previousSnapshot = null;
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
    
    /**
     * 현재 보드 상태의 스냅샷을 캡처합니다. (다음 턴에서 사용)
     */
    protected void captureSnapshotBeforeLock() {
        previousSnapshot = new BoardSnapshot(board);
    }
    
    /**
     * lockPiece 후 실행할 콜백을 설정합니다.
     */
    public void setOnPieceLocked(Runnable callback) {
        this.onPieceLocked = callback;
    }

    /**
     * 현재 게임 상태를 압축하여 int[][]로 반환
     * @param ghostPiece 고스트 조각 (없으면 null)
     * @return 압축된 int[20][10] 보드
     */
    public int[][] getCompressedBoardData(TetrominoPosition ghostPiece) {
        // Magic Number
        final int WEIGHT_MARK = 200;
        final int BOMB_MARK = 201;
        final int GHOST_MARK = -2;

        // 비트마스킹: 상위 16비트(symbol), 하위 8비트(color)
        // 아이템 블록: (symbol << 16) | colorIndex

        // 나중에 이 부분을 보드에서 바로 압축해서 주도록 바꿔야함
        int[][] visible = board.getVisibleBoard();
        int[][] compressed = new int[GameBoard.HEIGHT][GameBoard.WIDTH];

        // 1. 보드 복사 및 아이템 정보 반영
        for (int row = 0; row < GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                int v = visible[row][col];
                if (v == 0) {
                    compressed[row][col] = 0;
                } else if (v == GameBoard.CLEAR_MARK) {
                    compressed[row][col] = GameBoard.CLEAR_MARK;
                } else {
                    var item = board.getItemAt(row + GameBoard.BUFFER_ZONE, col);
                    if (item != null && item.isItem()) {
                        int symbol = item.getSymbol();
                        compressed[row][col] = (symbol << 16) | (v & 0xFF);
                    } else {
                        compressed[row][col] = v;
                    }
                }
            }
        }

        // 2. 고스트 조각 덮어쓰기 (테두리만 표시하고 싶으면 GHOST_MARK 사용)
        if (ghostPiece != null) {
            int[][] shape = ghostPiece.getCurrentShape();
            int startX = ghostPiece.getX();
            int startY = ghostPiece.getY() - GameBoard.BUFFER_ZONE;
            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[r].length; c++) {
                    if (shape[r][c] == 1) {
                        int x = startX + c;
                        int y = startY + r;
                        if (x >= 0 && x < GameBoard.WIDTH && y >= 0 && y < GameBoard.HEIGHT) {
                            compressed[y][x] = GHOST_MARK;
                        }
                    }
                }
            }
        }

        // 3. 현재 조각 덮어쓰기 (특수/아이템/일반)
        if (currentPiece != null) {
            var special = currentPiece.getSpecialKind();
            int[][] shape = currentPiece.getCurrentShape();
            int startX = currentPiece.getX();
            int startY = currentPiece.getY() - GameBoard.BUFFER_ZONE;
            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[r].length; c++) {
                    if (shape[r][c] == 1) {
                        int x = startX + c;
                        int y = startY + r;
                        if (x >= 0 && x < GameBoard.WIDTH && y >= 0 && y < GameBoard.HEIGHT) {
                            if (special == TetrominoPosition.SpecialKind.WEIGHT) {
                                compressed[y][x] = WEIGHT_MARK;
                            } else if (special == TetrominoPosition.SpecialKind.BOMB) {
                                compressed[y][x] = BOMB_MARK;
                            } else {
                                var item = currentPiece.getItemAt(r, c);
                                if (item != null && item.isItem()) {
                                    int symbol = item.getSymbol();
                                    int color = currentPiece.getType().getColorIndex();
                                    compressed[y][x] = (symbol << 16) | (color & 0xFF);
                                } else {
                                    compressed[y][x] = currentPiece.getType().getColorIndex();
                                }
                            }
                        }
                    }
                }
            }
        }

        return compressed;
    }
}