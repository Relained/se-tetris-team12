package org.example.controller;

import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.example.view.DisplaySettingView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DisplaySettingController 클래스의 Unit Test (Mockito 사용)
 */
@ExtendWith(ApplicationExtension.class)
class DisplaySettingControllerTest {
    
    @Mock
    private DisplaySettingView mockView;
    
    private DisplaySettingController controller;
    
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
        controller = new DisplaySettingController();
        
        // Reflection을 사용하여 mock view 주입
        Field viewField = DisplaySettingController.class.getDeclaredField("displaySettingView");
        viewField.setAccessible(true);
        viewField.set(controller, mockView);
    }
    
    @Test
    void testHandleSmall() {
        controller.handleSmall();
        
        verify(mockView).updateCurrentSize(ScreenSize.SMALL);
        assertEquals(ScreenSize.SMALL, controller.getSelectedSize());
    }
    
    @Test
    void testHandleMedium() {
        controller.handleMedium();
        
        verify(mockView).updateCurrentSize(ScreenSize.MEDIUM);
        assertEquals(ScreenSize.MEDIUM, controller.getSelectedSize());
    }
    
    @Test
    void testHandleLarge() {
        controller.handleLarge();
        
        verify(mockView).updateCurrentSize(ScreenSize.LARGE);
        assertEquals(ScreenSize.LARGE, controller.getSelectedSize());
    }
    
    @Test
    void testSetScreenSizeWithSmall() throws Exception {
        Method setScreenSize = DisplaySettingController.class.getDeclaredMethod("setScreenSize", ScreenSize.class);
        setScreenSize.setAccessible(true);
        
        setScreenSize.invoke(controller, ScreenSize.SMALL);
        
        verify(mockView).updateCurrentSize(ScreenSize.SMALL);
        assertEquals(ScreenSize.SMALL, controller.getSelectedSize());
    }
    
    @Test
    void testSetScreenSizeWithMedium() throws Exception {
        Method setScreenSize = DisplaySettingController.class.getDeclaredMethod("setScreenSize", ScreenSize.class);
        setScreenSize.setAccessible(true);
        
        setScreenSize.invoke(controller, ScreenSize.MEDIUM);
        
        verify(mockView).updateCurrentSize(ScreenSize.MEDIUM);
        assertEquals(ScreenSize.MEDIUM, controller.getSelectedSize());
    }
    
    @Test
    void testSetScreenSizeWithLarge() throws Exception {
        Method setScreenSize = DisplaySettingController.class.getDeclaredMethod("setScreenSize", ScreenSize.class);
        setScreenSize.setAccessible(true);
        
        setScreenSize.invoke(controller, ScreenSize.LARGE);
        
        verify(mockView).updateCurrentSize(ScreenSize.LARGE);
        assertEquals(ScreenSize.LARGE, controller.getSelectedSize());
    }
    
    @Test
    void testHandleSmallMethodExists() {
        assertDoesNotThrow(() -> controller.handleSmall());
    }
    
    @Test
    void testHandleMediumMethodExists() {
        assertDoesNotThrow(() -> controller.handleMedium());
    }
    
    @Test
    void testHandleLargeMethodExists() {
        assertDoesNotThrow(() -> controller.handleLarge());
    }
    
    @Test
    void testMultipleSizeSwitches() {
        controller.handleSmall();
        verify(mockView).updateCurrentSize(ScreenSize.SMALL);
        assertEquals(ScreenSize.SMALL, controller.getSelectedSize());
        
        controller.handleMedium();
        verify(mockView).updateCurrentSize(ScreenSize.MEDIUM);
        assertEquals(ScreenSize.MEDIUM, controller.getSelectedSize());
        
        controller.handleLarge();
        verify(mockView).updateCurrentSize(ScreenSize.LARGE);
        assertEquals(ScreenSize.LARGE, controller.getSelectedSize());
        
        controller.handleSmall();
        verify(mockView, times(2)).updateCurrentSize(ScreenSize.SMALL);
        assertEquals(ScreenSize.SMALL, controller.getSelectedSize());
    }
    
    @Test
    void testSelectedSizeField() throws Exception {
        Field selectedSizeField = DisplaySettingController.class.getDeclaredField("selectedSize");
        selectedSizeField.setAccessible(true);
        
        controller.handleLarge();
        ScreenSize selectedSize = (ScreenSize) selectedSizeField.get(controller);
        
        assertEquals(ScreenSize.LARGE, selectedSize);
    }
    
    @Test
    void testViewFieldIsInjected() throws Exception {
        Field viewField = DisplaySettingController.class.getDeclaredField("displaySettingView");
        viewField.setAccessible(true);
        
        Object injectedView = viewField.get(controller);
        assertNotNull(injectedView);
        assertEquals(mockView, injectedView);
    }
    
    @Test
    void testGetSelectedSize() {
        ScreenSize initialSize = controller.getSelectedSize();
        assertNotNull(initialSize);
        
        controller.handleLarge();
        assertEquals(ScreenSize.LARGE, controller.getSelectedSize());
        
        controller.handleSmall();
        assertEquals(ScreenSize.SMALL, controller.getSelectedSize());
    }
    
    @Test
    void testHandleKeyInput() {
        javafx.scene.input.KeyEvent mockKeyEvent = mock(javafx.scene.input.KeyEvent.class);
        org.example.view.component.NavigableButtonSystem mockButtonSystem = mock(org.example.view.component.NavigableButtonSystem.class);
        
        when(mockView.getButtonSystem()).thenReturn(mockButtonSystem);
        
        controller.handleKeyInput(mockKeyEvent);
        
        verify(mockView).getButtonSystem();
        verify(mockButtonSystem).handleInput(mockKeyEvent);
    }
    
    @Test
    void testExitSavesSelectedSize() throws Exception {
        controller.handleLarge();
        assertEquals(ScreenSize.LARGE, controller.getSelectedSize());
        
        Method exitMethod = DisplaySettingController.class.getDeclaredMethod("exit");
        exitMethod.setAccessible(true);
        exitMethod.invoke(controller);
        
        Field settingManagerField = BaseController.class.getDeclaredField("settingManager");
        settingManagerField.setAccessible(true);
        SettingManager settingManager = (SettingManager) settingManagerField.get(null);
        
        assertEquals(ScreenSize.LARGE, settingManager.getCurrentSettings().screenSize);
    }
    
    @Test
    void testInitialSelectedSizeFromSettings() throws Exception {
        Field settingManagerField = BaseController.class.getDeclaredField("settingManager");
        settingManagerField.setAccessible(true);
        SettingManager settingManager = (SettingManager) settingManagerField.get(null);
        settingManager.setScreenSize(ScreenSize.SMALL);
        
        DisplaySettingController newController = new DisplaySettingController();
        
        assertEquals(ScreenSize.SMALL, newController.getSelectedSize());
    }
}
