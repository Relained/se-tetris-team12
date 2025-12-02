package org.example.view.component;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NavigableButtonSystem Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class NavigableButtonSystemTest {
    
    private NavigableButtonSystem buttonSystem;
    
    @Start
    private void start(Stage stage) {
        // JavaFX 초기화
    }
    
    @BeforeEach
    void setUp() {
        buttonSystem = new NavigableButtonSystem();
    }
    
    @Test
    void testConstructor() {
        assertNotNull(buttonSystem);
        assertEquals(0, buttonSystem.getButtonCount());
    }
    
    @Test
    void testCreateNavigableButton() {
        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        Runnable action = () -> actionExecuted.set(true);
        
        Button button = buttonSystem.createNavigableButton("Test", action);
        
        assertNotNull(button);
        assertEquals("Test", button.getText());
        assertEquals(1, buttonSystem.getButtonCount());
        assertEquals(button, buttonSystem.getSelectedButton());
    }
    
    @Test
    void testCreateNavigableButtonFromList() {
        List<String> texts = Arrays.asList("Button1", "Button2", "Button3");
        List<Runnable> actions = Arrays.asList(
            () -> {},
            () -> {},
            () -> {}
        );
        
        ArrayList<Button> buttons = buttonSystem.createNavigableButtonFromList(texts, actions);
        
        assertNotNull(buttons);
        assertEquals(3, buttons.size());
        assertEquals(3, buttonSystem.getButtonCount());
        assertEquals("Button1", buttons.get(0).getText());
        assertEquals("Button2", buttons.get(1).getText());
        assertEquals("Button3", buttons.get(2).getText());
    }
    
    @Test
    void testCreateNavigableButtonFromListMismatchSize() {
        List<String> texts = Arrays.asList("Button1", "Button2");
        List<Runnable> actions = Arrays.asList(() -> {});
        
        assertThrows(IllegalArgumentException.class, () -> {
            buttonSystem.createNavigableButtonFromList(texts, actions);
        });
    }
    
    @Test
    void testNavigateDown() {
        buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        buttonSystem.createNavigableButton("Button3", () -> {});
        
        assertEquals(0, buttonSystem.getSelectedIndex());
        
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(1, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testNavigateUp() {
        buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        buttonSystem.createNavigableButton("Button3", () -> {});
        
        assertEquals(0, buttonSystem.getSelectedIndex());
        
        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
        buttonSystem.handleInput(upEvent);
        
        assertEquals(2, buttonSystem.getSelectedIndex(), "Should wrap to last button");
    }
    
    @Test
    void testNavigateDownWraparound() {
        buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        assertEquals(1, buttonSystem.getSelectedIndex());
        
        buttonSystem.handleInput(downEvent);
        assertEquals(0, buttonSystem.getSelectedIndex(), "Should wrap to first button");
    }
    
    @Test
    void testExecuteSelectedButton() {
        AtomicBoolean action1Executed = new AtomicBoolean(false);
        AtomicBoolean action2Executed = new AtomicBoolean(false);
        
        buttonSystem.createNavigableButton("Button1", () -> action1Executed.set(true));
        buttonSystem.createNavigableButton("Button2", () -> action2Executed.set(true));
        
        KeyEvent enterEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false, false, false, false);
        buttonSystem.handleInput(enterEvent);
        
        assertTrue(action1Executed.get());
        assertFalse(action2Executed.get());
    }
    
    @Test
    void testExecuteSelectedButtonWithSpace() {
        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        buttonSystem.createNavigableButton("Button1", () -> actionExecuted.set(true));
        
        KeyEvent spaceEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false);
        buttonSystem.handleInput(spaceEvent);
        
        assertTrue(actionExecuted.get());
    }
    
    @Test
    void testFocusButton() {
        buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        buttonSystem.createNavigableButton("Button3", () -> {});
        
        boolean result = buttonSystem.focusButton(2);
        
        assertTrue(result);
        assertEquals(2, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testFocusButtonInvalidIndex() {
        buttonSystem.createNavigableButton("Button1", () -> {});
        
        boolean resultNegative = buttonSystem.focusButton(-1);
        boolean resultTooLarge = buttonSystem.focusButton(10);
        
        assertFalse(resultNegative);
        assertFalse(resultTooLarge);
        assertEquals(0, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testFocusButtonByText() {
        buttonSystem.createNavigableButton("Start", () -> {});
        buttonSystem.createNavigableButton("Settings", () -> {});
        buttonSystem.createNavigableButton("Quit", () -> {});
        
        boolean result = buttonSystem.focusButtonByText("Settings");
        
        assertTrue(result);
        assertEquals(1, buttonSystem.getSelectedIndex());
        assertEquals("Settings", buttonSystem.getSelectedButton().getText());
    }
    
    @Test
    void testFocusButtonByTextNotFound() {
        buttonSystem.createNavigableButton("Start", () -> {});
        
        boolean result = buttonSystem.focusButtonByText("NonExistent");
        
        assertFalse(result);
        assertEquals(0, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testGetSelectedButton() {
        Button button1 = buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        
        assertEquals(button1, buttonSystem.getSelectedButton());
    }
    
    @Test
    void testGetSelectedButtonWhenEmpty() {
        NavigableButtonSystem emptySystem = new NavigableButtonSystem();
        assertNull(emptySystem.getSelectedButton());
    }
    
    @Test
    void testGetButtons() {
        buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        
        ArrayList<Button> buttons = buttonSystem.getButtons();
        
        assertNotNull(buttons);
        assertEquals(2, buttons.size());
    }
    
    @Test
    void testSetScale() {
        buttonSystem.createNavigableButton("Button1", () -> {});
        
        buttonSystem.setScale(1.5);
        
        Button button = buttonSystem.getButtons().get(0);
        assertEquals(300.0, button.getPrefWidth(), 0.01); // 200 * 1.5
        assertEquals(75.0, button.getPrefHeight(), 0.01); // 50 * 1.5
    }
    
    @Test
    void testSetGridColumns() {
        buttonSystem.setGridColumns(3);
        
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        
        assertEquals(3, buttonSystem.getButtonCount());
    }
    
    @Test
    void testSetGridColumnsNegative() {
        buttonSystem.setGridColumns(-1);
        buttonSystem.createNavigableButton("Button1", () -> {});
        
        // Should default to at least 1
        assertEquals(1, buttonSystem.getButtonCount());
    }
    
    @Test
    void testSetHorizontalNavigation() {
        buttonSystem.setHorizontalNavigation(true);
        buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        
        KeyEvent rightEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
        buttonSystem.handleInput(rightEvent);
        
        assertEquals(1, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testHorizontalNavigationDisabled() {
        buttonSystem.setHorizontalNavigation(false);
        buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        
        KeyEvent rightEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
        buttonSystem.handleInput(rightEvent);
        
        assertEquals(0, buttonSystem.getSelectedIndex(), "Should not move with horizontal navigation disabled");
    }
    
    @Test
    void testNavigateLeftWithHorizontalNavigation() {
        buttonSystem.setHorizontalNavigation(true);
        buttonSystem.createNavigableButton("Button1", () -> {});
        buttonSystem.createNavigableButton("Button2", () -> {});
        
        KeyEvent leftEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false);
        buttonSystem.handleInput(leftEvent);
        
        assertEquals(1, buttonSystem.getSelectedIndex(), "Should wrap to last button");
    }
    
    @Test
    void testCreateStandaloneButton() {
        Button standaloneButton = NavigableButtonSystem.createStandaloneButton("Standalone");
        
        assertNotNull(standaloneButton);
        assertEquals("Standalone", standaloneButton.getText());
        assertEquals(200.0, standaloneButton.getPrefWidth(), 0.01);
        assertEquals(50.0, standaloneButton.getPrefHeight(), 0.01);
    }
    
    @Test
    void testMultipleButtonCreation() {
        for (int i = 0; i < 10; i++) {
            buttonSystem.createNavigableButton("Button" + i, () -> {});
        }
        
        assertEquals(10, buttonSystem.getButtonCount());
        assertEquals(0, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testNavigationWithSingleButton() {
        buttonSystem.createNavigableButton("Only", () -> {});
        
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(0, buttonSystem.getSelectedIndex(), "Should stay at same button");
    }
    
    @Test
    void testIgnoreOtherKeys() {
        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        buttonSystem.createNavigableButton("Button1", () -> actionExecuted.set(true));
        
        KeyEvent aKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.A, false, false, false, false);
        buttonSystem.handleInput(aKeyEvent);
        
        assertFalse(actionExecuted.get());
        assertEquals(0, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testGridNavigationDown() {
        buttonSystem.setGridColumns(2);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(2, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testGridNavigationRight() {
        buttonSystem.setGridColumns(2);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        
        KeyEvent rightEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
        buttonSystem.handleInput(rightEvent);
        
        assertEquals(1, buttonSystem.getSelectedIndex());
    }
    
    @Test
    void testGridNavigationUp() {
        buttonSystem.setGridColumns(2);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        
        // B3로 이동
        buttonSystem.focusButton(2);
        
        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
        buttonSystem.handleInput(upEvent);
        
        assertEquals(0, buttonSystem.getSelectedIndex(), "Should move from B3 to B1");
    }
    
    @Test
    void testGridNavigationUpFromTopRowWithWrap() {
        buttonSystem.setGridColumns(2);
        buttonSystem.setWrapNavigation(true);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        
        // B1에서 위로 (wrap)
        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
        buttonSystem.handleInput(upEvent);
        
        assertEquals(2, buttonSystem.getSelectedIndex(), "Should wrap to B3 (same column)");
    }
    
    @Test
    void testGridNavigationUpFromTopRowWithoutWrap() {
        buttonSystem.setGridColumns(2);
        buttonSystem.setWrapNavigation(false);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        
        // B1에서 위로 (no wrap)
        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
        buttonSystem.handleInput(upEvent);
        
        assertEquals(0, buttonSystem.getSelectedIndex(), "Should stay at B1");
    }
    
    @Test
    void testGridNavigationDownFromBottomRowWithWrap() {
        buttonSystem.setGridColumns(2);
        buttonSystem.setWrapNavigation(true);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        
        // B3로 이동 후 아래로 (wrap)
        buttonSystem.focusButton(2);
        
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(0, buttonSystem.getSelectedIndex(), "Should wrap to B1 (same column)");
    }
    
    @Test
    void testGridNavigationDownFromBottomRowWithoutWrap() {
        buttonSystem.setGridColumns(2);
        buttonSystem.setWrapNavigation(false);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        
        // B3로 이동 후 아래로 (no wrap)
        buttonSystem.focusButton(2);
        
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(2, buttonSystem.getSelectedIndex(), "Should stay at B3");
    }
    
    @Test
    void testGridNavigationWithIncompleteLastRow() {
        buttonSystem.setGridColumns(3);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        buttonSystem.createNavigableButton("B5", () -> {});
        
        // B2에서 아래로 (마지막 행이 불완전)
        buttonSystem.focusButton(1);
        
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(4, buttonSystem.getSelectedIndex(), "Should move to B5 (same column)");
    }
    
    @Test
    void testGridNavigationUpWithIncompleteLastRow() {
        buttonSystem.setGridColumns(3);
        buttonSystem.setWrapNavigation(true);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        buttonSystem.createNavigableButton("B5", () -> {});
        
        // B3에서 위로 (wrap, 마지막 행이 불완전)
        buttonSystem.focusButton(2);
        
        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
        buttonSystem.handleInput(upEvent);
        
        assertEquals(4, buttonSystem.getSelectedIndex(), "Should go to last button when wrapping with incomplete row");
    }
    
    @Test
    void testGridNavigationLeftWrap() {
        buttonSystem.setGridColumns(3);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        
        // B1에서 왼쪽 (wrap)
        KeyEvent leftEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false);
        buttonSystem.handleInput(leftEvent);
        
        assertEquals(2, buttonSystem.getSelectedIndex(), "Should wrap to B3");
    }
    
    @Test
    void testGridNavigationRightWrap() {
        buttonSystem.setGridColumns(3);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        
        // B3로 이동 후 오른쪽 (wrap)
        buttonSystem.focusButton(2);
        
        KeyEvent rightEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
        buttonSystem.handleInput(rightEvent);
        
        assertEquals(0, buttonSystem.getSelectedIndex(), "Should wrap to B1");
    }
    
    @Test
    void testGridNavigationRightWithIncompleteRow() {
        buttonSystem.setGridColumns(3);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        buttonSystem.createNavigableButton("B5", () -> {});
        
        // B5에서 오른쪽 (마지막 행이 불완전)
        buttonSystem.focusButton(4);
        
        KeyEvent rightEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
        buttonSystem.handleInput(rightEvent);
        
        assertEquals(3, buttonSystem.getSelectedIndex(), "Should wrap to B4 (first of same row)");
    }
    
    @Test
    void testGridNavigationLeftWithIncompleteRow() {
        buttonSystem.setGridColumns(3);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        buttonSystem.createNavigableButton("B5", () -> {});
        
        // B4에서 왼쪽
        buttonSystem.focusButton(3);
        
        KeyEvent leftEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false);
        buttonSystem.handleInput(leftEvent);
        
        assertEquals(4, buttonSystem.getSelectedIndex(), "Should move to B5 (last valid in row)");
    }
    
    @Test
    void testVerticalListNavigateUpWithoutWrap() {
        buttonSystem.setWrapNavigation(false);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        
        // B1에서 위로 (no wrap)
        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
        buttonSystem.handleInput(upEvent);
        
        assertEquals(0, buttonSystem.getSelectedIndex(), "Should stay at B1 without wrap");
    }
    
    @Test
    void testVerticalListNavigateDownWithoutWrap() {
        buttonSystem.setWrapNavigation(false);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        
        // B2로 이동 후 아래로 (no wrap)
        buttonSystem.focusButton(1);
        
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(1, buttonSystem.getSelectedIndex(), "Should stay at B2 without wrap");
    }
    
    @Test
    void testGridNavigationDownToIncompleteRowFirstColumn() {
        buttonSystem.setGridColumns(3);
        buttonSystem.setWrapNavigation(true);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        buttonSystem.createNavigableButton("B3", () -> {});
        buttonSystem.createNavigableButton("B4", () -> {});
        
        // B1에서 아래로
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(3, buttonSystem.getSelectedIndex(), "Should move to B4 (same column in next row)");
    }
    
    @Test
    void testIsAtFirstButton() {
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        
        assertTrue(buttonSystem.isAtFirstButton());
        
        buttonSystem.focusButton(1);
        assertFalse(buttonSystem.isAtFirstButton());
    }
    
    @Test
    void testIsAtLastButton() {
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        
        assertFalse(buttonSystem.isAtLastButton());
        
        buttonSystem.focusButton(1);
        assertTrue(buttonSystem.isAtLastButton());
    }
    
    @Test
    void testUnfocusAll() {
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        
        buttonSystem.unfocusAll();
        
        // 모든 버튼이 기본 스타일을 가져야 함
        for (Button button : buttonSystem.getButtons()) {
            assertTrue(button.getStyle().contains("#4a4a4a"));
        }
    }
    
    @Test
    void testSetWrapNavigation() {
        buttonSystem.setWrapNavigation(false);
        buttonSystem.createNavigableButton("B1", () -> {});
        buttonSystem.createNavigableButton("B2", () -> {});
        
        // B2로 이동
        buttonSystem.focusButton(1);
        
        // 아래로 (wrap 비활성화)
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        buttonSystem.handleInput(downEvent);
        
        assertEquals(1, buttonSystem.getSelectedIndex(), "Should stay at last with wrap disabled");
    }
}

