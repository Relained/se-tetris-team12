package org.example.view.component.play;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class HoldPanelTest {
    
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @Test
    @DisplayName("테스트 플레이스홀더")
    void testPlaceholder() {
        // Component 테스트를 위한 플레이스홀더
        assertTrue(true);
    }
}
