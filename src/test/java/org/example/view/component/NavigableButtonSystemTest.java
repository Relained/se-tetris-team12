package org.example.view.component;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import static org.junit.jupiter.api.Assertions.*;

class NavigableButtonSystemTest extends ApplicationTest {
    private NavigableButtonSystem buttonSystem;

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
            assertEquals(200.0, button.getPrefWidth());
            assertEquals(50.0, button.getPrefHeight());
            assertNotNull(button.getUserData());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("여러 버튼 생성 테스트")
    void testCreateMultipleButtons() {
        javafx.application.Platform.runLater(() -> {
            buttonSystem.createNavigableButton("Button 1", () -> {});
            buttonSystem.createNavigableButton("Button 2", () -> {});
            buttonSystem.createNavigableButton("Button 3", () -> {});
            
            assertEquals(3, buttonSystem.getButtons().size());
            assertEquals("Button 1", buttonSystem.getButtons().get(0).getText());
            assertEquals("Button 2", buttonSystem.getButtons().get(1).getText());
            assertEquals("Button 3", buttonSystem.getButtons().get(2).getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
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
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("getButtons 반환 테스트")
    void testGetButtons() {
        assertNotNull(buttonSystem.getButtons());
        assertTrue(buttonSystem.getButtons() instanceof java.util.ArrayList);
    }

    @Test
    @DisplayName("DOWN 키로 버튼 네비게이션 테스트")
    void testHandleInputDown() {
        javafx.application.Platform.runLater(() -> {
            buttonSystem.createNavigableButton("Button 1", () -> {});
            buttonSystem.createNavigableButton("Button 2", () -> {});
            buttonSystem.createNavigableButton("Button 3", () -> {});
            
            // DOWN 키 이벤트 생성
            KeyEvent downEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.DOWN,
                false, false, false, false
            );
            
            // 첫 번째 버튼이 선택됨 -> DOWN -> 두 번째 버튼 선택
            buttonSystem.handleInput(downEvent);
            
            // 스타일 체크를 통해 두 번째 버튼이 선택되었는지 확인
            String selectedStyle = buttonSystem.getButtons().get(1).getStyle();
            assertTrue(selectedStyle.contains("yellow"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("UP 키로 버튼 네비게이션 테스트")
    void testHandleInputUp() {
        javafx.application.Platform.runLater(() -> {
            buttonSystem.createNavigableButton("Button 1", () -> {});
            buttonSystem.createNavigableButton("Button 2", () -> {});
            buttonSystem.createNavigableButton("Button 3", () -> {});
            
            KeyEvent upEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.UP,
                false, false, false, false
            );
            
            // 첫 번째 버튼이 선택됨 -> UP -> 마지막 버튼 선택 (순환)
            buttonSystem.handleInput(upEvent);
            
            String selectedStyle = buttonSystem.getButtons().get(2).getStyle();
            assertTrue(selectedStyle.contains("yellow"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("ENTER 키로 버튼 액션 실행 테스트")
    void testHandleInputEnter() {
        javafx.application.Platform.runLater(() -> {
            boolean[] actionExecuted = {false};
            buttonSystem.createNavigableButton("Button 1", () -> actionExecuted[0] = true);
            
            KeyEvent enterEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.ENTER,
                false, false, false, false
            );
            
            buttonSystem.handleInput(enterEvent);
            
            assertTrue(actionExecuted[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("SPACE 키로 버튼 액션 실행 테스트")
    void testHandleInputSpace() {
        javafx.application.Platform.runLater(() -> {
            boolean[] actionExecuted = {false};
            buttonSystem.createNavigableButton("Button 1", () -> actionExecuted[0] = true);
            
            KeyEvent spaceEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.SPACE,
                false, false, false, false
            );
            
            buttonSystem.handleInput(spaceEvent);
            
            assertTrue(actionExecuted[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("다른 키 입력 무시 테스트")
    void testHandleInputOtherKeys() {
        javafx.application.Platform.runLater(() -> {
            buttonSystem.createNavigableButton("Button 1", () -> {});
            buttonSystem.createNavigableButton("Button 2", () -> {});
            
            KeyEvent otherEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.A,
                false, false, false, false
            );
            
            // A 키는 무시되어야 함 - 첫 번째 버튼이 계속 선택됨
            buttonSystem.handleInput(otherEvent);
            
            String selectedStyle = buttonSystem.getButtons().get(0).getStyle();
            assertTrue(selectedStyle.contains("yellow"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("순환 네비게이션 테스트 - DOWN")
    void testCircularNavigationDown() {
        javafx.application.Platform.runLater(() -> {
            buttonSystem.createNavigableButton("Button 1", () -> {});
            buttonSystem.createNavigableButton("Button 2", () -> {});
            buttonSystem.createNavigableButton("Button 3", () -> {});
            
            KeyEvent downEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.DOWN,
                false, false, false, false
            );
            
            // 0 -> 1 -> 2 -> 0 (순환)
            buttonSystem.handleInput(downEvent); // 0 -> 1
            buttonSystem.handleInput(downEvent); // 1 -> 2
            buttonSystem.handleInput(downEvent); // 2 -> 0
            
            String selectedStyle = buttonSystem.getButtons().get(0).getStyle();
            assertTrue(selectedStyle.contains("yellow"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("첫 번째 버튼 선택 스타일 테스트")
    void testFirstButtonSelectedStyle() {
        javafx.application.Platform.runLater(() -> {
            Button button1 = buttonSystem.createNavigableButton("Button 1", () -> {});
            Button button2 = buttonSystem.createNavigableButton("Button 2", () -> {});
            
            // 첫 번째 버튼은 선택된 스타일
            String style1 = button1.getStyle();
            assertTrue(style1.contains("yellow"));
            assertTrue(style1.contains("#6a6a6a"));
            
            // 두 번째 버튼은 기본 스타일
            String style2 = button2.getStyle();
            assertTrue(style2.contains("white"));
            assertTrue(style2.contains("#4a4a4a"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("여러 액션 버튼 실행 테스트")
    void testMultipleButtonActions() {
        javafx.application.Platform.runLater(() -> {
            int[] counter1 = {0};
            int[] counter2 = {0};
            int[] counter3 = {0};
            
            buttonSystem.createNavigableButton("Button 1", () -> counter1[0]++);
            buttonSystem.createNavigableButton("Button 2", () -> counter2[0]++);
            buttonSystem.createNavigableButton("Button 3", () -> counter3[0]++);
            
            KeyEvent enterEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.ENTER,
                false, false, false, false
            );
            
            KeyEvent downEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.DOWN,
                false, false, false, false
            );
            
            // 첫 번째 버튼 실행
            buttonSystem.handleInput(enterEvent);
            assertEquals(1, counter1[0]);
            
            // 두 번째 버튼으로 이동 후 실행
            buttonSystem.handleInput(downEvent);
            buttonSystem.handleInput(enterEvent);
            assertEquals(1, counter2[0]);
            
            // 세 번째 버튼으로 이동 후 실행
            buttonSystem.handleInput(downEvent);
            buttonSystem.handleInput(enterEvent);
            assertEquals(1, counter3[0]);
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
