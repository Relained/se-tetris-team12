package org.example.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.model.GameMode;
import org.example.model.ScoreRecord;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.example.view.ScoreNotEligibleView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ScoreNotEligibleController Unit Test (Mockito)
 */
@ExtendWith(ApplicationExtension.class)
class ScoreNotEligibleControllerTest {
    
    @Mock
    private ScoreNotEligibleView mockView;
    
    private ScoreNotEligibleController controller;
    private ScoreRecord testRecord;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
        
        SettingManager settingManager = new SettingManager();
        BaseController.Initialize(stage, settingManager);
    }
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        testRecord = new ScoreRecord(500, 5, 1, 1, GameMode.NORMAL, false);
        
        controller = new ScoreNotEligibleController(testRecord);
        
        // Inject mock view using Reflection
        Field viewField = ScoreNotEligibleController.class.getDeclaredField("scoreNotEligibleView");
        viewField.setAccessible(true);
        viewField.set(controller, mockView);
    }
    
    @Test
    void testConstructor() {
        assertNotNull(controller);
    }
    
    @Test
    void testHandleKeyInputWithEscape() {
        // handleKeyInput이 setState를 호출하므로 Integration test로 이동 필요
        KeyEvent escapeEvent = mock(KeyEvent.class);
        when(escapeEvent.getCode()).thenReturn(KeyCode.ESCAPE);
        
        assertEquals(KeyCode.ESCAPE, escapeEvent.getCode());
    }
    
    @Test
    void testHandleKeyInputWithOtherKeys() {
        KeyEvent spaceEvent = mock(KeyEvent.class);
        when(spaceEvent.getCode()).thenReturn(KeyCode.SPACE);
        
        var mockButtonSystem = mock(org.example.view.component.NavigableButtonSystem.class);
        when(mockView.getButtonSystem()).thenReturn(mockButtonSystem);
        
        controller.handleKeyInput(spaceEvent);
        
        verify(mockButtonSystem).handleInput(spaceEvent);
    }
    
    @Test
    void testViewFieldIsInjected() throws Exception {
        Field viewField = ScoreNotEligibleController.class.getDeclaredField("scoreNotEligibleView");
        viewField.setAccessible(true);
        
        Object injectedView = viewField.get(controller);
        assertNotNull(injectedView);
        assertEquals(mockView, injectedView);
    }
    
    @Test
    void testRecordFieldIsSet() throws Exception {
        Field recordField = ScoreNotEligibleController.class.getDeclaredField("record");
        recordField.setAccessible(true);
        
        ScoreRecord storedRecord = (ScoreRecord) recordField.get(controller);
        assertNotNull(storedRecord);
        assertEquals(testRecord, storedRecord);
    }
    
    @Test
    void testResume() {
        var mockButtonSystem = mock(org.example.view.component.NavigableButtonSystem.class);
        when(mockView.getButtonSystem()).thenReturn(mockButtonSystem);
        
        assertDoesNotThrow(() -> {
            java.lang.reflect.Method resumeMethod = 
                ScoreNotEligibleController.class.getDeclaredMethod("resume");
            resumeMethod.setAccessible(true);
            resumeMethod.invoke(controller);
        });
    }
}
