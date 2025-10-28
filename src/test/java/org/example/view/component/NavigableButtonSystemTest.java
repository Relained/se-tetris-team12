package org.example.view.component;

import javafx.scene.control.Button;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class NavigableButtonSystemTest {
    private NavigableButtonSystem buttonSystem;

    @BeforeAll
    static void initJavaFX() {
        // JavaFX 플랫폼 초기화
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }

    @BeforeEach
    void setUp() {
        buttonSystem = new NavigableButtonSystem();
    }

    @Test
    @DisplayName("NavigableButtonSystem 생성 테스트")
    void testConstructor() {
        assertNotNull(buttonSystem);
        assertNotNull(buttonSystem.getButtons());
        assertEquals(0, buttonSystem.getButtons().size());
    }

    @Test
    @DisplayName("버튼 생성 테스트")
    void testCreateNavigableButton() {
        javafx.application.Platform.runLater(() -> {
            boolean[] actionExecuted = {false};
            Runnable action = () -> actionExecuted[0] = true;
            
            Button button = buttonSystem.createNavigableButton("Test Button", action);
            
            assertNotNull(button);
            assertEquals("Test Button", button.getText());
            assertEquals(1, buttonSystem.getButtons().size());
            assertFalse(button.isFocusTraversable());
        });
    }

    @Test
    @DisplayName("여러 버튼 생성 테스트")
    void testCreateMultipleButtons() {
        javafx.application.Platform.runLater(() -> {
            buttonSystem.createNavigableButton("Button 1", () -> {});
            buttonSystem.createNavigableButton("Button 2", () -> {});
            buttonSystem.createNavigableButton("Button 3", () -> {});
            
            assertEquals(3, buttonSystem.getButtons().size());
        });
    }

    @Test
    @DisplayName("버튼 시스템 리셋 테스트")
    void testResetSystem() {
        javafx.application.Platform.runLater(() -> {
            buttonSystem.createNavigableButton("Button 1", () -> {});
            buttonSystem.createNavigableButton("Button 2", () -> {});
            
            buttonSystem.resetSystem();
            
            assertEquals(0, buttonSystem.getButtons().size());
        });
    }

    @Test
    @DisplayName("getButtons 반환 테스트")
    void testGetButtons() {
        assertNotNull(buttonSystem.getButtons());
        assertTrue(buttonSystem.getButtons() instanceof java.util.ArrayList);
    }
}
