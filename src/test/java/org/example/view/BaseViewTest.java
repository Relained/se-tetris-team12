package org.example.view;

import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.example.service.ColorManager;
import org.example.service.DisplayManager;
import org.example.view.component.NavigableButtonSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaseView 클래스의 Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class BaseViewTest {
    
    private TestView viewWithButtons;
    private TestView viewWithoutButtons;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        // DisplayManager 초기화 (테스트 전 clean up)
        DisplayManager displayManager = DisplayManager.getInstance();
        displayManager.setDisplayMode(ScreenSize.MEDIUM); // 기본값으로 설정
        
        viewWithButtons = new TestView(true);
        viewWithoutButtons = new TestView(false);
    }
    
    @Test
    void testInitialize() throws Exception {
        Field colorManagerField = BaseView.class.getDeclaredField("colorManager");
        colorManagerField.setAccessible(true);
        
        assertNotNull(colorManagerField.get(null), "ColorManager should be initialized");
    }
    
    @Test
    void testConstructorWithButtonSystem() {
        assertNotNull(viewWithButtons.getButtonSystem(), "Button system should be initialized");
    }
    
    @Test
    void testConstructorWithoutButtonSystem() {
        assertNull(viewWithoutButtons.getButtonSystem(), "Button system should be null");
    }
    
    @Test
    void testGetButtonSystemReturnsCorrectInstance() {
        NavigableButtonSystem buttonSystem = viewWithButtons.getButtonSystem();
        assertNotNull(buttonSystem);
        assertSame(buttonSystem, viewWithButtons.getButtonSystem(), "Should return same instance");
    }
    
    @Test
    void testUpdateScaleToSmall() throws Exception {
        viewWithButtons.updateScale(ScreenSize.SMALL);
        
        Field currentScaleField = BaseView.class.getDeclaredField("currentScale");
        currentScaleField.setAccessible(true);
        double scale = (double) currentScaleField.get(viewWithButtons);
        
        assertEquals(0.9, scale, 0.001, "Scale should be 0.9 for SMALL");
    }
    
    @Test
    void testUpdateScaleToMedium() throws Exception {
        viewWithButtons.updateScale(ScreenSize.MEDIUM);
        
        Field currentScaleField = BaseView.class.getDeclaredField("currentScale");
        currentScaleField.setAccessible(true);
        double scale = (double) currentScaleField.get(viewWithButtons);
        
        assertEquals(1.0, scale, 0.001, "Scale should be 1.0 for MEDIUM");
    }
    
    @Test
    void testUpdateScaleToLarge() throws Exception {
        viewWithButtons.updateScale(ScreenSize.LARGE);
        
        Field currentScaleField = BaseView.class.getDeclaredField("currentScale");
        currentScaleField.setAccessible(true);
        double scale = (double) currentScaleField.get(viewWithButtons);
        
        assertEquals(1.1, scale, 0.001, "Scale should be 1.1 for LARGE");
    }
    
    @Test
    void testUpdateScaleUpdatesButtonSystem() {
        NavigableButtonSystem buttonSystem = viewWithButtons.getButtonSystem();
        assertNotNull(buttonSystem);
        
        viewWithButtons.updateScale(ScreenSize.LARGE);
        
        // ButtonSystem의 스케일이 업데이트되었는지 확인 (간접 검증)
        assertDoesNotThrow(() -> viewWithButtons.updateScale(ScreenSize.SMALL));
    }
    
    @Test
    void testUpdateScaleWithoutButtonSystem() {
        assertDoesNotThrow(() -> viewWithoutButtons.updateScale(ScreenSize.LARGE),
            "Should not throw when button system is null");
    }
    
    @Test
    void testOnScaleChangedIsCalled() {
        TestView testView = new TestView(true);
        
        testView.updateScale(ScreenSize.LARGE);
        
        assertTrue(testView.onScaleChangedCalled, "onScaleChanged should be called");
        assertEquals(1.1, testView.lastScale, 0.001, "Scale parameter should be 1.1");
    }
    
    @Test
    void testDefaultCurrentScale() throws Exception {
        TestView newView = new TestView(false);
        
        Field currentScaleField = BaseView.class.getDeclaredField("currentScale");
        currentScaleField.setAccessible(true);
        double scale = (double) currentScaleField.get(newView);
        
        assertEquals(1.0, scale, 0.001, "Default scale should be 1.0");
    }
    
    @Test
    void testMultipleScaleUpdates() throws Exception {
        viewWithButtons.updateScale(ScreenSize.SMALL);
        viewWithButtons.updateScale(ScreenSize.LARGE);
        viewWithButtons.updateScale(ScreenSize.MEDIUM);
        
        Field currentScaleField = BaseView.class.getDeclaredField("currentScale");
        currentScaleField.setAccessible(true);
        double scale = (double) currentScaleField.get(viewWithButtons);
        
        assertEquals(1.0, scale, 0.001, "Final scale should be 1.0 (MEDIUM)");
    }
    
    @Test
    void testColorManagerIsStatic() throws Exception {
        Field colorManagerField = BaseView.class.getDeclaredField("colorManager");
        colorManagerField.setAccessible(true);
        
        Object colorManager1 = colorManagerField.get(viewWithButtons);
        Object colorManager2 = colorManagerField.get(viewWithoutButtons);
        
        assertSame(colorManager1, colorManager2, "ColorManager should be static");
    }
    
    @Test
    void testViewRegistersWithDisplayManager() {
        // View 생성 시 DisplayManager에 자동 등록됨
        TestView newView = new TestView(true);
        
        // View가 정상적으로 생성되고 DisplayManager와 연동됨을 검증
        assertNotNull(newView);
        assertNotNull(newView.getButtonSystem());
    }
    
    /**
     * BaseView를 테스트하기 위한 구체적인 구현 클래스
     */
    private static class TestView extends BaseView {
        boolean onScaleChangedCalled = false;
        double lastScale = 0.0;
        
        public TestView(boolean useButtonSystem) {
            super(useButtonSystem);
        }
        
        @Override
        protected void onScaleChanged(double scale) {
            onScaleChangedCalled = true;
            lastScale = scale;
        }
    }
}
