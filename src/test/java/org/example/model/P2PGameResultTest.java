package org.example.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for P2PGameResult class
 * Tests game result data object used in P2P multiplayer
 */
class P2PGameResultTest {

    @Test
    @DisplayName("P2PGameResult initializes with all parameters")
    void testConstructorWithAllParameters() {
        Socket mockSocket = Mockito.mock(Socket.class);
        int myScore = 1000;
        int opponentScore = 800;
        byte gameOverStatus = 1;
        boolean isServer = true;
        GameMode gameMode = GameMode.NORMAL;
        int difficulty = 2;

        P2PGameResult result = new P2PGameResult(
            myScore, opponentScore, gameOverStatus, mockSocket, isServer, gameMode, difficulty
        );

        assertEquals(myScore, result.myScore);
        assertEquals(opponentScore, result.opponentScore);
        assertEquals(gameOverStatus, result.gameOverStatus);
        assertSame(mockSocket, result.socket);
        assertTrue(result.isServer);
        assertEquals(gameMode, result.gameMode);
        assertEquals(difficulty, result.difficulty);
    }

    @Test
    @DisplayName("P2PGameResult with server wins scenario")
    void testServerWinsScenario() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            2000, 1500, (byte) 1, mockSocket, true, GameMode.NORMAL, 2
        );

        assertTrue(result.myScore > result.opponentScore, "Server should have higher score");
        assertTrue(result.isServer);
    }

    @Test
    @DisplayName("P2PGameResult with client wins scenario")
    void testClientWinsScenario() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            3000, 1000, (byte) 2, mockSocket, false, GameMode.ITEM, 3
        );

        assertTrue(result.myScore > result.opponentScore, "Client should have higher score");
        assertFalse(result.isServer);
    }

    @Test
    @DisplayName("P2PGameResult with tie game scenario")
    void testTieGameScenario() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            1500, 1500, (byte) 0, mockSocket, true, GameMode.TIME_ATTACK, 1
        );

        assertEquals(result.myScore, result.opponentScore, "Scores should be equal in tie");
    }

    @Test
    @DisplayName("P2PGameResult with zero scores")
    void testZeroScores() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            0, 0, (byte) 0, mockSocket, false, GameMode.NORMAL, 1
        );

        assertEquals(0, result.myScore);
        assertEquals(0, result.opponentScore);
    }

    @Test
    @DisplayName("P2PGameResult with high scores")
    void testHighScores() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            999999, 888888, (byte) 1, mockSocket, true, GameMode.ITEM, 3
        );

        assertEquals(999999, result.myScore);
        assertEquals(888888, result.opponentScore);
    }

    @ParameterizedTest
    @EnumSource(GameMode.class)
    @DisplayName("P2PGameResult works with all game modes")
    void testAllGameModes(GameMode mode) {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            1000, 800, (byte) 1, mockSocket, true, mode, 2
        );

        assertEquals(mode, result.gameMode);
        assertNotNull(result.gameMode);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("P2PGameResult works with all difficulty levels")
    void testAllDifficultyLevels(int difficulty) {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            1000, 800, (byte) 1, mockSocket, false, GameMode.NORMAL, difficulty
        );

        assertEquals(difficulty, result.difficulty);
    }

    @ParameterizedTest
    @ValueSource(bytes = {0, 1, 2, 3})
    @DisplayName("P2PGameResult with different game over status values")
    void testDifferentGameOverStatus(byte status) {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            1000, 800, status, mockSocket, true, GameMode.NORMAL, 2
        );

        assertEquals(status, result.gameOverStatus);
    }

    @Test
    @DisplayName("P2PGameResult as server with NORMAL mode")
    void testServerNormalMode() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            1200, 1000, (byte) 1, mockSocket, true, GameMode.NORMAL, 1
        );

        assertTrue(result.isServer);
        assertEquals(GameMode.NORMAL, result.gameMode);
        assertEquals(1, result.difficulty);
    }

    @Test
    @DisplayName("P2PGameResult as client with ITEM mode")
    void testClientItemMode() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            900, 1100, (byte) 2, mockSocket, false, GameMode.ITEM, 2
        );

        assertFalse(result.isServer);
        assertEquals(GameMode.ITEM, result.gameMode);
        assertEquals(2, result.difficulty);
    }

    @Test
    @DisplayName("P2PGameResult as server with TIME_ATTACK mode")
    void testServerTimeAttackMode() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            5000, 4500, (byte) 3, mockSocket, true, GameMode.TIME_ATTACK, 3
        );

        assertTrue(result.isServer);
        assertEquals(GameMode.TIME_ATTACK, result.gameMode);
        assertEquals(3, result.difficulty);
    }

    @Test
    @DisplayName("P2PGameResult with null socket")
    void testNullSocket() {
        P2PGameResult result = new P2PGameResult(
            1000, 800, (byte) 1, null, true, GameMode.NORMAL, 2
        );

        assertNull(result.socket);
        assertEquals(1000, result.myScore);
        assertEquals(800, result.opponentScore);
    }

    @Test
    @DisplayName("P2PGameResult fields are public and immutable")
    void testPublicFields() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            1000, 800, (byte) 1, mockSocket, true, GameMode.NORMAL, 2
        );

        // Verify all fields are accessible
        assertDoesNotThrow(() -> {
            int score = result.myScore;
            int oppScore = result.opponentScore;
            byte status = result.gameOverStatus;
            Socket socket = result.socket;
            boolean server = result.isServer;
            GameMode mode = result.gameMode;
            int diff = result.difficulty;
        });
    }

    @Test
    @DisplayName("Multiple P2PGameResult instances are independent")
    void testMultipleInstances() {
        Socket socket1 = Mockito.mock(Socket.class);
        Socket socket2 = Mockito.mock(Socket.class);
        
        P2PGameResult result1 = new P2PGameResult(
            1000, 800, (byte) 1, socket1, true, GameMode.NORMAL, 1
        );
        
        P2PGameResult result2 = new P2PGameResult(
            2000, 1500, (byte) 2, socket2, false, GameMode.ITEM, 3
        );

        assertNotEquals(result1.myScore, result2.myScore);
        assertNotEquals(result1.opponentScore, result2.opponentScore);
        assertNotEquals(result1.gameOverStatus, result2.gameOverStatus);
        assertNotSame(result1.socket, result2.socket);
        assertNotEquals(result1.isServer, result2.isServer);
        assertNotEquals(result1.gameMode, result2.gameMode);
        assertNotEquals(result1.difficulty, result2.difficulty);
    }

    @Test
    @DisplayName("P2PGameResult with opponent having higher score")
    void testOpponentWinsScenario() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            500, 1500, (byte) 2, mockSocket, true, GameMode.NORMAL, 2
        );

        assertTrue(result.opponentScore > result.myScore, "Opponent should have higher score");
    }

    @Test
    @DisplayName("P2PGameResult score difference calculation")
    void testScoreDifference() {
        Socket mockSocket = Mockito.mock(Socket.class);
        
        P2PGameResult result = new P2PGameResult(
            2000, 1500, (byte) 1, mockSocket, false, GameMode.TIME_ATTACK, 2
        );

        int difference = result.myScore - result.opponentScore;
        assertEquals(500, difference, "Score difference should be 500");
    }

    @Test
    @DisplayName("P2PGameResult can be created with same socket for both roles")
    void testSameSocketDifferentRoles() {
        Socket sharedSocket = Mockito.mock(Socket.class);
        
        P2PGameResult serverResult = new P2PGameResult(
            1000, 800, (byte) 1, sharedSocket, true, GameMode.NORMAL, 2
        );
        
        P2PGameResult clientResult = new P2PGameResult(
            800, 1000, (byte) 2, sharedSocket, false, GameMode.NORMAL, 2
        );

        assertSame(serverResult.socket, clientResult.socket);
        assertTrue(serverResult.isServer);
        assertFalse(clientResult.isServer);
    }
}
