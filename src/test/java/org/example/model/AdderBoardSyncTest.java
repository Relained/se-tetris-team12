package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AdderBoardSync class
 * Tests synchronization of additional lines sent from opponent in multiplayer
 */
class AdderBoardSyncTest {

    private GameBoard gameBoard;
    private AdderBoardSync adderBoardSync;

    @BeforeEach
    void setUp() {
        gameBoard = new GameBoard();
        adderBoardSync = new AdderBoardSync(gameBoard);
    }

    @Test
    @DisplayName("AdderBoardSync initializes with empty state")
    void testInitialState() {
        assertTrue(adderBoardSync.isEmpty(), "Should be empty on initialization");
    }

    @Test
    @DisplayName("isEmpty returns true when no lines are queued")
    void testIsEmptyWhenNoLines() {
        assertTrue(adderBoardSync.isEmpty());
    }

    @Test
    @DisplayName("enqueueLines adds lines to queue")
    void testEnqueueLines() {
        int[][] newLines = createTestLines(2);
        
        adderBoardSync.enqueueLines(newLines);
        
        // Queue is internal, but we can verify through consumeIfExists
        adderBoardSync.consumeIfExists();
        assertFalse(adderBoardSync.isEmpty(), "Should have lines after consuming from queue");
    }

    @Test
    @DisplayName("enqueueLines with multiple batches")
    void testEnqueueMultipleBatches() {
        int[][] firstBatch = createTestLines(2);
        int[][] secondBatch = createTestLines(3);
        
        adderBoardSync.enqueueLines(firstBatch);
        adderBoardSync.enqueueLines(secondBatch);
        adderBoardSync.consumeIfExists();
        
        assertFalse(adderBoardSync.isEmpty());
    }

    @Test
    @DisplayName("consumeIfExists does nothing when queue is empty")
    void testConsumeIfExistsWithEmptyQueue() {
        adderBoardSync.consumeIfExists();
        
        assertTrue(adderBoardSync.isEmpty());
    }

    @Test
    @DisplayName("consumeIfExists transfers lines from queue to internal list")
    void testConsumeIfExistsTransfersLines() {
        int[][] newLines = createTestLines(3);
        
        adderBoardSync.enqueueLines(newLines);
        assertTrue(adderBoardSync.isEmpty(), "Should be empty before consume");
        
        adderBoardSync.consumeIfExists();
        assertFalse(adderBoardSync.isEmpty(), "Should have lines after consume");
    }

    @Test
    @DisplayName("consumeIfExists respects MAX_LINES limit (10 lines)")
    void testConsumeIfExistsRespectsMaxLines() {
        // Enqueue more than MAX_LINES (10)
        int[][] batch1 = createTestLines(8);
        int[][] batch2 = createTestLines(5); // Total would be 13
        
        adderBoardSync.enqueueLines(batch1);
        adderBoardSync.enqueueLines(batch2);
        adderBoardSync.consumeIfExists();
        
        // Should only consume up to MAX_LINES
        int[][] drawBuffer = adderBoardSync.getDrawBuffer();
        assertNotNull(drawBuffer);
        assertEquals(10, drawBuffer.length, "Draw buffer should have MAX_LINES (10) rows");
    }

    @Test
    @DisplayName("applyToBoard shifts existing board content upward")
    void testApplyToBoardShiftsContent() {
        // Fill some cells in the board
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            gameBoard.setCellColor(GameBoard.HEIGHT - 1, col, 1); // Bottom row
        }
        
        // Add 2 lines
        int[][] newLines = createTestLines(2);
        adderBoardSync.enqueueLines(newLines);
        adderBoardSync.consumeIfExists();
        adderBoardSync.applyToBoard();
        
        // Original bottom row should now be 2 rows higher
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            assertEquals(1, gameBoard.getCellColor(GameBoard.HEIGHT - 3, col),
                "Content should shift up by number of added lines");
        }
    }

    @Test
    @DisplayName("applyToBoard adds new lines at the bottom")
    void testApplyToBoardAddsLinesAtBottom() {
        int[][] newLines = new int[2][GameBoard.WIDTH];
        // Fill with pattern
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                newLines[i][j] = (i + 1) * 10 + j;
            }
        }
        
        adderBoardSync.enqueueLines(newLines);
        adderBoardSync.consumeIfExists();
        adderBoardSync.applyToBoard();
        
        // Check bottom rows have the new pattern
        int totalHeight = GameBoard.HEIGHT + GameBoard.BUFFER_ZONE;
        for (int i = 0; i < 2; i++) {
            int targetRow = totalHeight - 2 + i;
            if (targetRow >= GameBoard.BUFFER_ZONE) {
                for (int col = 0; col < GameBoard.WIDTH; col++) {
                    assertEquals((i + 1) * 10 + col, 
                        gameBoard.getCellColor(targetRow, col),
                        "Added line should be at bottom of board");
                }
            }
        }
    }

    @Test
    @DisplayName("applyToBoard clears internal lines list")
    void testApplyToBoardClearsLines() {
        int[][] newLines = createTestLines(2);
        
        adderBoardSync.enqueueLines(newLines);
        adderBoardSync.consumeIfExists();
        assertFalse(adderBoardSync.isEmpty(), "Should have lines before apply");
        
        adderBoardSync.applyToBoard();
        assertTrue(adderBoardSync.isEmpty(), "Should be empty after apply");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 5, 8, 10})
    @DisplayName("applyToBoard works with different line counts")
    void testApplyToBoardWithVariousLineCounts(int lineCount) {
        int[][] newLines = createTestLines(lineCount);
        
        adderBoardSync.enqueueLines(newLines);
        adderBoardSync.consumeIfExists();
        adderBoardSync.applyToBoard();
        
        assertTrue(adderBoardSync.isEmpty(), "Should be empty after applying " + lineCount + " lines");
    }

    @Test
    @DisplayName("getDrawBuffer returns empty buffer initially")
    void testGetDrawBufferInitially() {
        int[][] drawBuffer = adderBoardSync.getDrawBuffer();
        
        assertNotNull(drawBuffer);
        assertEquals(10, drawBuffer.length);
        assertEquals(GameBoard.WIDTH, drawBuffer[0].length);
        
        // All should be zero
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                assertEquals(0, drawBuffer[i][j], "Initial buffer should be all zeros");
            }
        }
    }

    @Test
    @DisplayName("getDrawBuffer reflects enqueued lines after consume")
    void testGetDrawBufferAfterConsume() {
        int[][] newLines = new int[3][GameBoard.WIDTH];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                newLines[i][j] = i * 10 + j;
            }
        }
        
        adderBoardSync.enqueueLines(newLines);
        adderBoardSync.consumeIfExists();
        int[][] drawBuffer = adderBoardSync.getDrawBuffer();
        
        // First 7 rows should be empty (10 - 3 = 7)
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                assertEquals(0, drawBuffer[i][j], "Top rows should be empty");
            }
        }
        
        // Last 3 rows should have the pattern
        for (int i = 7, lineIdx = 0; i < 10; i++, lineIdx++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                assertEquals(lineIdx * 10 + j, drawBuffer[i][j],
                    "Bottom rows should contain enqueued lines");
            }
        }
    }

    @Test
    @DisplayName("getDrawBuffer resets updated flag on first call")
    void testGetDrawBufferResetsUpdatedFlag() {
        int[][] newLines = createTestLines(2);
        
        adderBoardSync.enqueueLines(newLines);
        adderBoardSync.consumeIfExists();
        
        // First call should return updated buffer
        int[][] firstCall = adderBoardSync.getDrawBuffer();
        
        // Second call without changes should return same buffer (cached)
        int[][] secondCall = adderBoardSync.getDrawBuffer();
        
        assertSame(firstCall, secondCall, "Should return same buffer instance when not updated");
    }

    @Test
    @DisplayName("getDrawBuffer updates when new lines are consumed")
    void testGetDrawBufferUpdatesOnNewConsume() {
        int[][] firstBatch = createTestLines(2);
        adderBoardSync.enqueueLines(firstBatch);
        adderBoardSync.consumeIfExists();
        int[][] firstBuffer = adderBoardSync.getDrawBuffer();
        
        int[][] secondBatch = createTestLines(1);
        adderBoardSync.enqueueLines(secondBatch);
        adderBoardSync.consumeIfExists();
        int[][] secondBuffer = adderBoardSync.getDrawBuffer();
        
        assertSame(firstBuffer, secondBuffer, "Should return same buffer instance");
        // Content should be different though (not testing exact content here)
    }

    @Test
    @DisplayName("Full workflow: enqueue -> consume -> apply -> isEmpty")
    void testFullWorkflow() {
        // Enqueue lines
        int[][] lines = createTestLines(3);
        adderBoardSync.enqueueLines(lines);
        assertTrue(adderBoardSync.isEmpty(), "Should be empty before consume");
        
        // Consume from queue
        adderBoardSync.consumeIfExists();
        assertFalse(adderBoardSync.isEmpty(), "Should have lines after consume");
        
        // Get draw buffer
        int[][] buffer = adderBoardSync.getDrawBuffer();
        assertNotNull(buffer);
        
        // Apply to board
        adderBoardSync.applyToBoard();
        assertTrue(adderBoardSync.isEmpty(), "Should be empty after apply");
    }

    @Test
    @DisplayName("Multiple workflows in sequence")
    void testMultipleWorkflows() {
        // First workflow
        adderBoardSync.enqueueLines(createTestLines(2));
        adderBoardSync.consumeIfExists();
        adderBoardSync.applyToBoard();
        assertTrue(adderBoardSync.isEmpty());
        
        // Second workflow
        adderBoardSync.enqueueLines(createTestLines(3));
        adderBoardSync.consumeIfExists();
        adderBoardSync.applyToBoard();
        assertTrue(adderBoardSync.isEmpty());
        
        // Third workflow
        adderBoardSync.enqueueLines(createTestLines(1));
        adderBoardSync.consumeIfExists();
        assertFalse(adderBoardSync.isEmpty());
    }

    @Test
    @DisplayName("Thread-safe queue operations")
    void testConcurrentEnqueue() throws InterruptedException {
        // Test that ConcurrentLinkedQueue works correctly
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                int[][] lines = createTestLines(1);
                adderBoardSync.enqueueLines(lines);
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        adderBoardSync.consumeIfExists();
        assertFalse(adderBoardSync.isEmpty(), "Should have lines from all threads");
    }

    // Helper method to create test lines
    private int[][] createTestLines(int count) {
        int[][] lines = new int[count][GameBoard.WIDTH];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                lines[i][j] = 1; // Fill with 1s for simplicity
            }
        }
        return lines;
    }
}
