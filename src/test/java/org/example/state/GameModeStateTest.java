package org.example.state;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class GameModeStateTest {

    @Test
    @DisplayName("GameModeState 인스턴스 생성 테스트")
    void testInstantiation() {
        GameModeState state = new GameModeState();
        assertNotNull(state);
    }
}
