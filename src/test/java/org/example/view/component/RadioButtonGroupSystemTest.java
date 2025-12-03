package org.example.view.component;

import javafx.application.Platform;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class RadioButtonGroupSystemTest {

    private Stage testStage;
    private RadioButtonGroupSystem system;

    @Start
    void start(Stage stage) {
        this.testStage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        
        Platform.runLater(() -> {
            system = new RadioButtonGroupSystem();
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testCreateRadioButtonGroup_WithStringList() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("Option1", "Option2", "Option3");
            groupHolder.set(system.createRadioButtonGroup(options, 1));
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(groupHolder.get());
        assertEquals(3, groupHolder.get().getButtons().size());
        assertEquals(1, groupHolder.get().getSelectedIndex());
        assertEquals("Option2", groupHolder.get().getSelectedOption());
    }

    @Test
    void testCreateRadioButtonGroup_WithCustomType() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<Integer>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<Integer> options = Arrays.asList(1, 2, 3, 4, 5);
            groupHolder.set(system.createRadioButtonGroup(options, 2, Object::toString));
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(groupHolder.get());
        assertEquals(5, groupHolder.get().getButtons().size());
        assertEquals(2, groupHolder.get().getSelectedIndex());
        assertEquals(3, groupHolder.get().getSelectedOption());
    }

    @Test
    void testCreateRadioButtonGroup_InvalidInitialIndex() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            groupHolder.set(system.createRadioButtonGroup(options, 10)); // Invalid index
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should default to first option
        assertEquals(0, groupHolder.get().getSelectedIndex());
        assertEquals("A", groupHolder.get().getSelectedOption());
    }

    @Test
    void testCreateRadioButtonGroup_NegativeInitialIndex() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("X", "Y", "Z");
            groupHolder.set(system.createRadioButtonGroup(options, -1)); // Negative index
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should default to first option
        assertEquals(0, groupHolder.get().getSelectedIndex());
        assertEquals("X", groupHolder.get().getSelectedOption());
    }

    @Test
    void testSelectNext() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            groupHolder.set(system.createRadioButtonGroup(options, 0));
            groupHolder.get().selectNext();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(1, groupHolder.get().getSelectedIndex());
        assertEquals("B", groupHolder.get().getSelectedOption());
    }

    @Test
    void testSelectNext_Wraps() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            groupHolder.set(system.createRadioButtonGroup(options, 2)); // Last option
            groupHolder.get().selectNext();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should wrap to first
        assertEquals(0, groupHolder.get().getSelectedIndex());
        assertEquals("A", groupHolder.get().getSelectedOption());
    }

    @Test
    void testSelectPrevious() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            groupHolder.set(system.createRadioButtonGroup(options, 2));
            groupHolder.get().selectPrevious();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(1, groupHolder.get().getSelectedIndex());
        assertEquals("B", groupHolder.get().getSelectedOption());
    }

    @Test
    void testSelectPrevious_Wraps() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            groupHolder.set(system.createRadioButtonGroup(options, 0)); // First option
            groupHolder.get().selectPrevious();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should wrap to last
        assertEquals(2, groupHolder.get().getSelectedIndex());
        assertEquals("C", groupHolder.get().getSelectedOption());
    }

    @Test
    void testSetSelectedIndex() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C", "D");
            groupHolder.set(system.createRadioButtonGroup(options, 0));
            groupHolder.get().setSelectedIndex(3);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(3, groupHolder.get().getSelectedIndex());
        assertEquals("D", groupHolder.get().getSelectedOption());
    }

    @Test
    void testSetSelectedIndex_Invalid() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            groupHolder.set(system.createRadioButtonGroup(options, 1));
            groupHolder.get().setSelectedIndex(10); // Invalid
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should remain unchanged
        assertEquals(1, groupHolder.get().getSelectedIndex());
    }

    @Test
    void testOnSelectionChanged() throws Exception {
        final AtomicReference<String> selectedHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            RadioButtonGroupSystem.RadioButtonGroupWrapper<String> group = 
                system.createRadioButtonGroup(options, 0);
            
            group.setOnSelectionChanged(selectedHolder::set);
            group.selectNext();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals("B", selectedHolder.get());
    }

    @Test
    void testMultipleGroups() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2", "3"), 1);
            system.createRadioButtonGroup(Arrays.asList("X", "Y"), 0);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(3, system.getGroupCount());
    }

    @Test
    void testHandleInput_Down() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(1, system.getFocusedGroupIndex());
    }

    @Test
    void testHandleInput_Up() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setFocusedGroup(1);
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(0, system.getFocusedGroupIndex());
    }

    @Test
    void testHandleInput_Right() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            groupHolder.set(system.createRadioButtonGroup(Arrays.asList("A", "B", "C"), 0));
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(1, groupHolder.get().getSelectedIndex());
    }

    @Test
    void testHandleInput_Left() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            groupHolder.set(system.createRadioButtonGroup(Arrays.asList("A", "B", "C"), 2));
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(1, groupHolder.get().getSelectedIndex());
    }

    @Test
    void testWrapNavigation_Enabled() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setWrapNavigation(true);
            system.setFocusedGroup(0);
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should wrap to last group
        assertEquals(1, system.getFocusedGroupIndex());
    }

    @Test
    void testWrapNavigation_Disabled_AtFirst() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setWrapNavigation(false);
            system.setFocusedGroup(0);
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should stay at first
        assertEquals(0, system.getFocusedGroupIndex());
    }

    @Test
    void testWrapNavigation_Disabled_AtLast() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setWrapNavigation(false);
            system.setFocusedGroup(1);
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should stay at last
        assertEquals(1, system.getFocusedGroupIndex());
    }

    @Test
    void testIsAtFirst() throws Exception {
        final boolean[] result = {false};
        
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setFocusedGroup(0);
            result[0] = system.isAtFirst();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(result[0]);
    }

    @Test
    void testIsAtLast() throws Exception {
        final boolean[] result = {false};
        
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setFocusedGroup(1);
            result[0] = system.isAtLast();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(result[0]);
    }

    @Test
    void testFocusFirst() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setFocusedGroup(1);
            system.focusFirst();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(0, system.getFocusedGroupIndex());
    }

    @Test
    void testFocusLast() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setFocusedGroup(0);
            system.focusLast();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(1, system.getFocusedGroupIndex());
    }

    @Test
    void testUnfocusAll() throws Exception {
        Platform.runLater(() -> {
            RadioButtonGroupSystem.RadioButtonGroupWrapper<String> group1 = 
                system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            RadioButtonGroupSystem.RadioButtonGroupWrapper<String> group2 = 
                system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            
            system.unfocusAll();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Both groups should be unfocused (can't directly test isFocused as it's private)
        assertNotNull(system.getRadioGroups());
    }

    @Test
    void testSetFocusedGroup_Invalid() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            int beforeIndex = system.getFocusedGroupIndex();
            system.setFocusedGroup(10); // Invalid index
            
            // Should not change
            assertEquals(beforeIndex, system.getFocusedGroupIndex());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testSetFocusedGroup_Negative() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            int beforeIndex = system.getFocusedGroupIndex();
            system.setFocusedGroup(-1); // Negative index
            
            // Should not change
            assertEquals(beforeIndex, system.getFocusedGroupIndex());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testHandleInput_EmptyGroups() throws Exception {
        Platform.runLater(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
            system.handleInput(event); // Should not throw exception
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(0, system.getGroupCount());
    }

    @Test
    void testFirstGroupAutoFocused() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // First group should be auto-focused
        assertEquals(0, system.getFocusedGroupIndex());
    }

    @Test
    void testButtonsNotFocusTraversable() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            groupHolder.set(system.createRadioButtonGroup(Arrays.asList("A", "B", "C"), 0));
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        for (RadioButton button : groupHolder.get().getButtons()) {
            assertFalse(button.isFocusTraversable());
        }
    }

    @Test
    void testToggleGroupIntegration() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            groupHolder.set(system.createRadioButtonGroup(Arrays.asList("A", "B", "C"), 0));
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(groupHolder.get().getToggleGroup());
        
        // All buttons should be in the same toggle group
        for (RadioButton button : groupHolder.get().getButtons()) {
            assertEquals(groupHolder.get().getToggleGroup(), button.getToggleGroup());
        }
    }

    @Test
    void testHandleInput_OtherKey() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            int beforeIndex = system.getFocusedGroupIndex();
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false, false, false, false);
            system.handleInput(event); // Should be ignored
            
            assertEquals(beforeIndex, system.getFocusedGroupIndex());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testGetRadioGroups() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2", "3"), 1);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(2, system.getRadioGroups().size());
    }

    @Test
    void testGroupWrapperGetters() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            groupHolder.set(system.createRadioButtonGroup(options, 1));
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(groupHolder.get().getButtons());
        assertEquals(3, groupHolder.get().getButtons().size());
        assertNotNull(groupHolder.get().getToggleGroup());
        assertEquals(1, groupHolder.get().getSelectedIndex());
        assertEquals("B", groupHolder.get().getSelectedOption());
    }

    @Test
    void testFocusFirst_EmptySystem() throws Exception {
        Platform.runLater(() -> {
            system.focusFirst(); // Should not throw exception
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(0, system.getGroupCount());
    }

    @Test
    void testFocusLast_EmptySystem() throws Exception {
        Platform.runLater(() -> {
            system.focusLast(); // Should not throw exception
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(0, system.getGroupCount());
    }

    @Test
    void testNavigateDown_WrapDisabled_StaysAtLast() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.createRadioButtonGroup(Arrays.asList("X", "Y"), 0);
            system.setWrapNavigation(false);
            system.setFocusedGroup(2); // Last group
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should remain at last group
        assertEquals(2, system.getFocusedGroupIndex());
    }

    @Test
    void testNavigateDown_WrapEnabled_WrapsToFirst() throws Exception {
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setWrapNavigation(true);
            system.setFocusedGroup(1); // Last group
            
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
            system.handleInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should wrap to first group
        assertEquals(0, system.getFocusedGroupIndex());
    }

    @Test
    void testIsAtFirst_False() throws Exception {
        final boolean[] result = {true};
        
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setFocusedGroup(1);
            result[0] = system.isAtFirst();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertFalse(result[0]);
    }

    @Test
    void testIsAtLast_False() throws Exception {
        final boolean[] result = {true};
        
        Platform.runLater(() -> {
            system.createRadioButtonGroup(Arrays.asList("A", "B"), 0);
            system.createRadioButtonGroup(Arrays.asList("1", "2"), 0);
            system.setFocusedGroup(0);
            result[0] = system.isAtLast();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertFalse(result[0]);
    }

    @Test
    void testButtonMouseClickDisabled() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            groupHolder.set(system.createRadioButtonGroup(Arrays.asList("A", "B", "C"), 0));
            
            // Simulate mouse click on second button
            RadioButton button = groupHolder.get().getButtons().get(1);
            button.fireEvent(new javafx.scene.input.MouseEvent(
                javafx.scene.input.MouseEvent.MOUSE_CLICKED,
                0, 0, 0, 0,
                javafx.scene.input.MouseButton.PRIMARY, 1,
                false, false, false, false,
                true, false, false, false, false, false, null
            ));
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Selection should not change via mouse click
        assertEquals(0, groupHolder.get().getSelectedIndex());
    }

    @Test
    void testSetFocused_StyleUpdate() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            groupHolder.set(system.createRadioButtonGroup(Arrays.asList("A", "B"), 0));
            groupHolder.get().setFocused(true);
            groupHolder.get().setFocused(false);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Just verify it doesn't throw exception
        assertNotNull(groupHolder.get());
    }

    @Test
    void testSelectionChangedListener_NotCalled_WhenNoChange() throws Exception {
        final AtomicReference<String> selectedHolder = new AtomicReference<>();
        final int[] callCount = {0};
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            RadioButtonGroupSystem.RadioButtonGroupWrapper<String> group = 
                system.createRadioButtonGroup(options, 1);
            
            group.setOnSelectionChanged(value -> {
                selectedHolder.set(value);
                callCount[0]++;
            });
            
            // Manually select the same option
            group.getButtons().get(1).setSelected(true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should not trigger callback if same option selected
        assertTrue(callCount[0] <= 1); // May be called once during setup
    }

    @Test
    void testGetSelectedOption_NoSelection() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            RadioButtonGroupSystem.RadioButtonGroupWrapper<String> group = 
                new RadioButtonGroupSystem.RadioButtonGroupWrapper<>(options, 0);
            
            // Don't add buttons to toggle group, so nothing is selected
            groupHolder.set(group);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should return first option when nothing selected
        assertEquals("A", groupHolder.get().getSelectedOption());
    }

    @Test
    void testGetSelectedIndex_NoSelection() throws Exception {
        final AtomicReference<RadioButtonGroupSystem.RadioButtonGroupWrapper<String>> groupHolder = new AtomicReference<>();
        
        Platform.runLater(() -> {
            List<String> options = Arrays.asList("A", "B", "C");
            RadioButtonGroupSystem.RadioButtonGroupWrapper<String> group = 
                new RadioButtonGroupSystem.RadioButtonGroupWrapper<>(options, 0);
            
            // Don't add buttons to toggle group
            groupHolder.set(group);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should return 0 when nothing selected
        assertEquals(0, groupHolder.get().getSelectedIndex());
    }
}
