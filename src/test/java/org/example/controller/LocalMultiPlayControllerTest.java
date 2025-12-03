package org.example.controller;

import org.example.model.GameMode;
import org.example.service.TetrisSystem;
import org.example.service.ItemTetrisSystem;
import org.example.service.TimeTetrisSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LocalMultiPlayController unit tests
 * Tests controller logic without JavaFX dependency
 */
class LocalMultiPlayControllerTest {

    @Test
    @DisplayName("LocalMultiPlayController initializes with NORMAL mode")
    void testInitializationNormalMode() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, 1);

        assertNotNull(controller.getPlayer1System(), "Player 1 system should be initialized");
        assertNotNull(controller.getPlayer2System(), "Player 2 system should be initialized");
        assertNotNull(controller.getPlayer1AdderBoard(), "Player 1 AdderBoard should be initialized");
        assertNotNull(controller.getPlayer2AdderBoard(), "Player 2 AdderBoard should be initialized");
        assertTrue(controller.getPlayer1System() instanceof TetrisSystem, "Should be TetrisSystem for NORMAL mode");
        assertTrue(controller.getPlayer2System() instanceof TetrisSystem, "Should be TetrisSystem for NORMAL mode");
    }

    @Test
    @DisplayName("LocalMultiPlayController initializes with ITEM mode")
    void testInitializationItemMode() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.ITEM, 1);

        assertNotNull(controller.getPlayer1System(), "Player 1 system should be initialized");
        assertNotNull(controller.getPlayer2System(), "Player 2 system should be initialized");
        assertTrue(controller.getPlayer1System() instanceof ItemTetrisSystem, "Should be ItemTetrisSystem for ITEM mode");
        assertTrue(controller.getPlayer2System() instanceof ItemTetrisSystem, "Should be ItemTetrisSystem for ITEM mode");
    }

    @Test
    @DisplayName("LocalMultiPlayController initializes with TIME_ATTACK mode")
    void testInitializationTimeAttackMode() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.TIME_ATTACK, 1);

        assertNotNull(controller.getPlayer1System(), "Player 1 system should be initialized");
        assertNotNull(controller.getPlayer2System(), "Player 2 system should be initialized");
        assertTrue(controller.getPlayer1System() instanceof TimeTetrisSystem, "Should be TimeTetrisSystem for TIME_ATTACK mode");
        assertTrue(controller.getPlayer2System() instanceof TimeTetrisSystem, "Should be TimeTetrisSystem for TIME_ATTACK mode");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("LocalMultiPlayController initializes with different difficulties")
    void testInitializationDifferentDifficulties(int difficulty) {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, difficulty);

        assertNotNull(controller.getPlayer1System(), "Player 1 system should exist");
        assertNotNull(controller.getPlayer2System(), "Player 2 system should exist");
    }

    @Test
    @DisplayName("LocalMultiPlayController creates independent player systems")
    void testIndependentPlayerSystems() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, 1);

        TetrisSystem player1 = controller.getPlayer1System();
        TetrisSystem player2 = controller.getPlayer2System();

        assertNotSame(player1, player2, "Player systems should be different instances");
        assertNotSame(player1.getBoard(), player2.getBoard(), "Boards should be independent");
    }

    @Test
    @DisplayName("LocalMultiPlayController initializes AdderBoards")
    void testAdderBoardInitialization() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, 1);

        assertEquals(0, controller.getPlayer1AdderBoard().getLineCount(), 
            "Player 1 AdderBoard should start empty");
        assertEquals(0, controller.getPlayer2AdderBoard().getLineCount(), 
            "Player 2 AdderBoard should start empty");
    }

    @Test
    @DisplayName("LocalMultiPlayController resets drop times")
    void testResetLastDropTime() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, 1);

        assertDoesNotThrow(() -> controller.resetLastDropTime(), 
            "Reset drop time should not throw exception");
    }

    @Test
    @DisplayName("LocalMultiPlayController systems are not game over initially")
    void testInitialGameState() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, 1);

        assertFalse(controller.getPlayer1System().isGameOver(), 
            "Player 1 should not be game over initially");
        assertFalse(controller.getPlayer2System().isGameOver(), 
            "Player 2 should not be game over initially");
    }

    @Test
    @DisplayName("LocalMultiPlayController player systems have current pieces")
    void testPlayerSystemsHaveCurrentPieces() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, 1);

        assertNotNull(controller.getPlayer1System().getCurrentPiece(), 
            "Player 1 should have current piece");
        assertNotNull(controller.getPlayer2System().getCurrentPiece(), 
            "Player 2 should have current piece");
    }

    @Test
    @DisplayName("LocalMultiPlayController player systems have independent boards")
    void testPlayerSystemsHaveIndependentBoards() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, 1);

        assertNotNull(controller.getPlayer1System().getBoard(), "Player 1 should have board");
        assertNotNull(controller.getPlayer2System().getBoard(), "Player 2 should have board");
        assertNotSame(controller.getPlayer1System().getBoard(), 
                     controller.getPlayer2System().getBoard(), 
                     "Boards should be independent");
    }

    @Test
    @DisplayName("LocalMultiPlayController AdderBoards are independent")
    void testAdderBoardsAreIndependent() {
        LocalMultiPlayController controller = new LocalMultiPlayController(GameMode.NORMAL, 1);

        assertNotSame(controller.getPlayer1AdderBoard(), 
                     controller.getPlayer2AdderBoard(), 
                     "AdderBoards should be independent");
    }
}
