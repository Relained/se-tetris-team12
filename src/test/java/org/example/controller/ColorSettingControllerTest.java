package org.example.controller;

import javafx.stage.Stage;
import org.example.model.SettingData.ColorBlindMode;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.example.view.ColorSettingView;
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
 * ColorSettingController 클래스의 Unit Test (Mockito 사용)
 */
@ExtendWith(ApplicationExtension.class)
class ColorSettingControllerTest {
    
    @Mock
    private ColorSettingView mockView;
    
    private ColorSettingController controller;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManagerInstance = ColorManager.getInstance();
        BaseView.Initialize(colorManagerInstance);
        
        SettingManager settingManager = new SettingManager();
        BaseController.Initialize(stage, settingManager);
    }
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new ColorSettingController();
        
        // Reflection을 사용하여 mock view 주입
        Field viewField = ColorSettingController.class.getDeclaredField("colorSettingView");
        viewField.setAccessible(true);
        viewField.set(controller, mockView);
    }
    
    @Test
    void testHandleDefaultMode() {
        controller.handleDefault();
        
        // updateCurrentMode가 Default로 호출되었는지 확인
        verify(mockView).updateCurrentMode(ColorBlindMode.Default);
        
        // selectedMode 필드가 변경되었는지 확인
        assertEquals(ColorBlindMode.Default, controller.getSelectedMode());
    }
    
    @Test
    void testHandleProtanopiaMode() {
        controller.handleProtanopia();
        
        verify(mockView).updateCurrentMode(ColorBlindMode.PROTANOPIA);
        assertEquals(ColorBlindMode.PROTANOPIA, controller.getSelectedMode());
    }
    
    @Test
    void testHandleDeuteranopiaMode() {
        controller.handleDeuteranopia();
        
        verify(mockView).updateCurrentMode(ColorBlindMode.DEUTERANOPIA);
        assertEquals(ColorBlindMode.DEUTERANOPIA, controller.getSelectedMode());
    }
    
    @Test
    void testHandleTritanopiaMode() {
        controller.handleTritanopia();
        
        verify(mockView).updateCurrentMode(ColorBlindMode.TRITANOPIA);
        assertEquals(ColorBlindMode.TRITANOPIA, controller.getSelectedMode());
    }
    
    @Test
    void testSetColorModeWithDefault() throws Exception {
        Method setColorMode = ColorSettingController.class.getDeclaredMethod("setColorMode", ColorBlindMode.class);
        setColorMode.setAccessible(true);
        
        setColorMode.invoke(controller, ColorBlindMode.Default);
        
        verify(mockView).updateCurrentMode(ColorBlindMode.Default);
        assertEquals(ColorBlindMode.Default, controller.getSelectedMode());
    }
    
    @Test
    void testSetColorModeWithProtanopia() throws Exception {
        Method setColorMode = ColorSettingController.class.getDeclaredMethod("setColorMode", ColorBlindMode.class);
        setColorMode.setAccessible(true);
        
        setColorMode.invoke(controller, ColorBlindMode.PROTANOPIA);
        
        verify(mockView).updateCurrentMode(ColorBlindMode.PROTANOPIA);
        assertEquals(ColorBlindMode.PROTANOPIA, controller.getSelectedMode());
    }
    
    @Test
    void testSetColorModeWithDeuteranopia() throws Exception {
        Method setColorMode = ColorSettingController.class.getDeclaredMethod("setColorMode", ColorBlindMode.class);
        setColorMode.setAccessible(true);
        
        setColorMode.invoke(controller, ColorBlindMode.DEUTERANOPIA);
        
        verify(mockView).updateCurrentMode(ColorBlindMode.DEUTERANOPIA);
        assertEquals(ColorBlindMode.DEUTERANOPIA, controller.getSelectedMode());
    }
    
    @Test
    void testSetColorModeWithTritanopia() throws Exception {
        Method setColorMode = ColorSettingController.class.getDeclaredMethod("setColorMode", ColorBlindMode.class);
        setColorMode.setAccessible(true);
        
        setColorMode.invoke(controller, ColorBlindMode.TRITANOPIA);
        
        verify(mockView).updateCurrentMode(ColorBlindMode.TRITANOPIA);
        assertEquals(ColorBlindMode.TRITANOPIA, controller.getSelectedMode());
    }
    
    @Test
    void testHandleDefaultMethodExists() {
        assertDoesNotThrow(() -> controller.handleDefault());
    }
    
    @Test
    void testHandleProtanopiaMethodExists() {
        assertDoesNotThrow(() -> controller.handleProtanopia());
    }
    
    @Test
    void testHandleDeuteranopiaMethodExists() {
        assertDoesNotThrow(() -> controller.handleDeuteranopia());
    }
    
    @Test
    void testHandleTritanopiaMethodExists() {
        assertDoesNotThrow(() -> controller.handleTritanopia());
    }
    
    @Test
    void testMultipleModeSwitches() {
        controller.handleDefault();
        verify(mockView).updateCurrentMode(ColorBlindMode.Default);
        assertEquals(ColorBlindMode.Default, controller.getSelectedMode());
        
        controller.handleProtanopia();
        verify(mockView).updateCurrentMode(ColorBlindMode.PROTANOPIA);
        assertEquals(ColorBlindMode.PROTANOPIA, controller.getSelectedMode());
        
        controller.handleDeuteranopia();
        verify(mockView).updateCurrentMode(ColorBlindMode.DEUTERANOPIA);
        assertEquals(ColorBlindMode.DEUTERANOPIA, controller.getSelectedMode());
        
        controller.handleTritanopia();
        verify(mockView).updateCurrentMode(ColorBlindMode.TRITANOPIA);
        assertEquals(ColorBlindMode.TRITANOPIA, controller.getSelectedMode());
        
        controller.handleDefault();
        verify(mockView, times(2)).updateCurrentMode(ColorBlindMode.Default);
        assertEquals(ColorBlindMode.Default, controller.getSelectedMode());
    }
    
    @Test
    void testSelectedModeField() throws Exception {
        Field selectedModeField = ColorSettingController.class.getDeclaredField("selectedMode");
        selectedModeField.setAccessible(true);
        
        controller.handleProtanopia();
        ColorBlindMode selectedMode = (ColorBlindMode) selectedModeField.get(controller);
        
        assertEquals(ColorBlindMode.PROTANOPIA, selectedMode);
    }
    
    @Test
    void testViewFieldIsInjected() throws Exception {
        Field viewField = ColorSettingController.class.getDeclaredField("colorSettingView");
        viewField.setAccessible(true);
        
        Object injectedView = viewField.get(controller);
        assertNotNull(injectedView);
        assertEquals(mockView, injectedView);
    }
    
    @Test
    void testGetSelectedMode() {
        // 초기값은 생성자에서 settingManager.getCurrentSettings().colorBlindMode로 설정됨
        ColorBlindMode initialMode = controller.getSelectedMode();
        assertNotNull(initialMode);
        
        // handleProtanopia 호출 후 변경 확인
        controller.handleProtanopia();
        assertEquals(ColorBlindMode.PROTANOPIA, controller.getSelectedMode());
        
        // handleDefault 호출 후 변경 확인
        controller.handleDefault();
        assertEquals(ColorBlindMode.Default, controller.getSelectedMode());
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
    void testExitSavesSelectedMode() throws Exception {
        // selectedMode 설정
        controller.handleProtanopia();
        assertEquals(ColorBlindMode.PROTANOPIA, controller.getSelectedMode());
        
        // exit() 메서드 호출
        Method exitMethod = ColorSettingController.class.getDeclaredMethod("exit");
        exitMethod.setAccessible(true);
        exitMethod.invoke(controller);
        
        // SettingManager를 통해 Reflection으로 확인
        Field settingManagerField = BaseController.class.getDeclaredField("settingManager");
        settingManagerField.setAccessible(true);
        SettingManager settingManager = (SettingManager) settingManagerField.get(null);
        
        // exit()가 호출되면 selectedMode가 settingManager에 저장됨
        assertEquals(ColorBlindMode.PROTANOPIA, settingManager.getCurrentSettings().colorBlindMode);
    }
    
    @Test
    void testInitialSelectedModeFromSettings() throws Exception {
        // SettingManager의 현재 colorBlindMode를 DEUTERANOPIA로 설정
        Field settingManagerField = BaseController.class.getDeclaredField("settingManager");
        settingManagerField.setAccessible(true);
        SettingManager settingManager = (SettingManager) settingManagerField.get(null);
        settingManager.setColorSetting(ColorBlindMode.DEUTERANOPIA);
        
        // 새 controller 생성 시 settingManager의 모드를 가져와야 함
        ColorSettingController newController = new ColorSettingController();
        
        assertEquals(ColorBlindMode.DEUTERANOPIA, newController.getSelectedMode());
    }
}

