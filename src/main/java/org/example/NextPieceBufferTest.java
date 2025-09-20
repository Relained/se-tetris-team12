package org.example;

/**
 * Test class to verify the next piece buffer system
 */
public class NextPieceBufferTest {
    public static void main(String[] args) {
        System.out.println("=== Next Piece Buffer System Test ===");
        
        testNextPieceBuffer();
        testInstantSpawn();
        
        System.out.println("All next piece buffer tests completed successfully!");
    }
    
    private static void testNextPieceBuffer() {
        System.out.println("\n1. Testing Next Piece Buffer:");
        
        TetrisGame game = new TetrisGame(10, 20);
        game.startGame();
        
        // Check that both current and next pieces are initialized
        assert game.getCurrentPiece() != null : "Current piece should be initialized";
        assert game.getNextPiece() != null : "Next piece should be initialized";
        
        System.out.println("   ✓ Both current and next pieces initialized");
        
        // Store references to original pieces
        Tetromino originalCurrent = game.getCurrentPiece();
        Tetromino originalNext = game.getNextPiece();
        
        // Force piece placement (simulate hard drop effect)
        game.hardDrop();
        
        // Check that next piece became current piece
        Tetromino newCurrent = game.getCurrentPiece();
        Tetromino newNext = game.getNextPiece();
        
        assert newCurrent != null : "New current piece should exist";
        assert newNext != null : "New next piece should exist";
        assert newCurrent.getType() == originalNext.getType() : "Next piece should become current piece";
        assert newNext != originalNext : "New next piece should be different from original next";
        
        System.out.println("   ✓ Next piece properly became current piece");
        System.out.println("   ✓ New next piece generated immediately");
    }
    
    private static void testInstantSpawn() {
        System.out.println("\n2. Testing Instant Spawn (No Delay):");
        
        TetrisGame game = new TetrisGame(10, 20);
        game.startGame();
        
        Tetromino originalNext = game.getNextPiece();
        
        // Simulate piece placement
        long startTime = System.nanoTime();
        game.hardDrop();
        long endTime = System.nanoTime();
        
        // Check that new pieces are available immediately
        assert game.getCurrentPiece() != null : "Current piece should be available immediately";
        assert game.getNextPiece() != null : "Next piece should be available immediately";
        assert game.getCurrentPiece().getType() == originalNext.getType() : "Transition should be instant";
        
        // The entire operation should be very fast (much less than typical game frame)
        long elapsedNanos = endTime - startTime;
        assert elapsedNanos < 1_000_000 : "Spawn should be instant (< 1ms)"; // Less than 1 millisecond
        
        System.out.println("   ✓ Piece spawn is instant (no delay)");
        System.out.println("   ✓ Hard drop immediately provides next piece");
        System.out.printf("   ✓ Spawn time: %.3f μs%n", elapsedNanos / 1000.0);
    }
}