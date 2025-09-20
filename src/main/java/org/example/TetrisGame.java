package org.example;

import java.util.Random;

public class TetrisGame {
    public enum GameState {
        MENU, PLAYING, GAME_OVER
    }
    
    private final int width;
    private final int height;
    private int[][] board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private Random random;
    private int score;
    private int level;
    private int linesCleared;
    private boolean gameOver;
    private GameState gameState;

    public TetrisGame(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new int[height][width];
        this.random = new Random();
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
        this.gameOver = false;
        this.gameState = GameState.MENU;
    }
    
    public void startGame() {
        this.gameState = GameState.PLAYING;
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
        this.gameOver = false;
        
        // Clear the board
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                board[y][x] = 0;
            }
        }
        
        // Initialize both current and next pieces
        nextPiece = Tetromino.getRandomPiece(random);
        spawnNewPiece();
    }
    
    public void resetGame() {
        startGame();
    }

    public void update() {
        if (gameState == GameState.PLAYING && !gameOver) {
            if (!moveDown()) {
                // Piece has landed - immediately place, clear lines, and spawn next
                placePiece();
                clearLines();
                spawnNewPiece(); // No delay - instant spawn
            }
        }
    }

    public boolean moveLeft() {
        if (currentPiece != null && isValidMove(currentPiece.getX() - 1, currentPiece.getY(), currentPiece.getShape())) {
            currentPiece.setX(currentPiece.getX() - 1);
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        if (currentPiece != null && isValidMove(currentPiece.getX() + 1, currentPiece.getY(), currentPiece.getShape())) {
            currentPiece.setX(currentPiece.getX() + 1);
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        if (currentPiece != null && isValidMove(currentPiece.getX(), currentPiece.getY() + 1, currentPiece.getShape())) {
            currentPiece.setY(currentPiece.getY() + 1);
            return true;
        }
        return false;
    }

    public void rotate() {
        rotate(true); // Default to clockwise rotation
    }

    public void rotate(boolean clockwise) {
        if (currentPiece == null) return;

        SRSSystem.RotationState fromState = currentPiece.getRotationState();
        SRSSystem.RotationState toState = clockwise ? fromState.getNext() : fromState.getPrevious();
        
        // Get the rotated shape
        int[][] rotatedShape = currentPiece.getRotatedShape(clockwise);
        
        // Get wall kick tests for this piece type and rotation
        int[][] wallKickTests = SRSSystem.getWallKickTests(
            currentPiece.getType(), fromState, toState
        );
        
        // Try each wall kick test
        for (int[] test : wallKickTests) {
            int testX = currentPiece.getX() + test[0];
            int testY = currentPiece.getY() + test[1];
            
            if (isValidMove(testX, testY, rotatedShape)) {
                // Success! Apply the rotation and position
                currentPiece.setX(testX);
                currentPiece.setY(testY);
                currentPiece.rotate(clockwise);
                return;
            }
        }
        
        // If we get here, rotation failed - piece stays in original state
    }

    public void hardDrop() {
        if (currentPiece == null) return;
        
        // Calculate the lowest valid position
        int dropDistance = 0;
        while (isValidMove(currentPiece.getX(), currentPiece.getY() + dropDistance + 1, currentPiece.getShape())) {
            dropDistance++;
        }
        
        // Move piece to the lowest position
        currentPiece.setY(currentPiece.getY() + dropDistance);
        
        // Immediately place the piece and spawn next - no delay whatsoever
        placePiece();
        clearLines();
        spawnNewPiece(); // Instant spawn from buffer
    }

    private boolean isValidMove(int x, int y, int[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int newX = x + col;
                    int newY = y + row;
                    
                    if (newX < 0 || newX >= width || newY >= height) {
                        return false;
                    }
                    
                    if (newY >= 0 && board[newY][newX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placePiece() {
        if (currentPiece == null) return;
        
        int[][] shape = currentPiece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int x = currentPiece.getX() + col;
                    int y = currentPiece.getY() + row;
                    if (y >= 0 && y < height && x >= 0 && x < width) {
                        board[y][x] = currentPiece.getType();
                    }
                }
            }
        }
    }

    private void clearLines() {
        int linesCleared = 0;
        for (int row = height - 1; row >= 0; row--) {
            boolean fullLine = true;
            for (int col = 0; col < width; col++) {
                if (board[row][col] == 0) {
                    fullLine = false;
                    break;
                }
            }
            
            if (fullLine) {
                // Move all rows above down by one
                for (int moveRow = row; moveRow > 0; moveRow--) {
                    System.arraycopy(board[moveRow - 1], 0, board[moveRow], 0, width);
                }
                // Clear the top row
                for (int col = 0; col < width; col++) {
                    board[0][col] = 0;
                }
                row++; // Check the same row again
                linesCleared++;
            }
        }
        
        if (linesCleared > 0) {
            this.linesCleared += linesCleared;
            
            // Calculate score based on lines cleared and level
            int baseScore = switch (linesCleared) {
                case 1 -> 100;
                case 2 -> 300;
                case 3 -> 500;
                case 4 -> 800; // Tetris!
                default -> 50;
            };
            score += baseScore * level;
            
            // Level up every 10 lines
            level = Math.min(10, (this.linesCleared / 10) + 1);
        }
    }

    private void spawnNewPiece() {
        // Use the next piece as current piece
        currentPiece = nextPiece;
        
        // Generate a new next piece immediately (no delay)
        nextPiece = Tetromino.getRandomPiece(random);
        
        // SRS standard spawn positions for current piece
        if (currentPiece.getType() == 1) { // I piece
            currentPiece.setX(width / 2 - 2); // I piece spawns 1 cell left
            currentPiece.setY(-1); // I piece spawns above the visible field
        } else if (currentPiece.getType() == 4) { // O piece
            currentPiece.setX(width / 2 - 1);
            currentPiece.setY(-1); // O piece spawns above the visible field
        } else { // J, L, S, T, Z pieces
            currentPiece.setX(width / 2 - 1);
            currentPiece.setY(-1); // All pieces spawn above the visible field
        }
        
        // Reset rotation state to spawn
        currentPiece.setRotationState(SRSSystem.RotationState.SPAWN);
        
        if (!isValidMove(currentPiece.getX(), currentPiece.getY(), currentPiece.getShape())) {
            gameOver = true;
            gameState = GameState.GAME_OVER;
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public Tetromino getCurrentPiece() {
        return currentPiece;
    }
    
    public Tetromino getNextPiece() {
        return nextPiece;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return gameOver;
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getLinesCleared() {
        return linesCleared;
    }
    
    public long getDropInterval() {
        // Speed increases with level (faster drop time)
        return Math.max(100_000_000L, 500_000_000L - (level - 1) * 50_000_000L);
    }
}