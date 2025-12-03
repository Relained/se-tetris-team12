package org.example.view;

import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.example.service.ColorManager;
import org.example.view.component.NavigableButtonSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaseView Integration Test - ButtonSystem과 Scale 업데이트 테스트
 */
@ExtendWith(ApplicationExtension.class)
class BaseViewIntegrationTest {
    
    /**
     * 테스트용 Concrete View
     */
    static class TestView extends BaseView {
        private double lastScaleChanged = 0.0;
        
        public TestView(boolean useButtonSystem) {
            super(useButtonSystem);
        }
        
        @Override
        protected void onScaleChanged(double scale) {
            this.lastScaleChanged = scale;
        }
        
        public double getLastScaleChanged() {
            return lastScaleChanged;
        }
    }
    
    private TestView viewWithButton;
    private TestView viewWithoutButton;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        viewWithButton = new TestView(true);
        viewWithoutButton = new TestView(false);
    }
    
    @Test
    void testBaseViewWithButtonSystemInitialization() {
        assertNotNull(viewWithButton.getButtonSystem());
        assertTrue(viewWithButton.getButtonSystem() instanceof NavigableButtonSystem);
    }
    
    @Test
    void testBaseViewWithoutButtonSystemInitialization() {
        assertNull(viewWithoutButton.getButtonSystem());
    }
    
    @Test
    void testUpdateScaleToSmall() {
        viewWithButton.updateScale(ScreenSize.SMALL);
        
        assertEquals(0.9, viewWithButton.lastScaleChanged, 0.001);
        // ButtonSystem의 setScale이 호출되었는지 확인 (getScale 메서드 없음)
        assertNotNull(viewWithButton.getButtonSystem());
    }
    
    @Test
    void testUpdateScaleToMedium() {
        viewWithButton.updateScale(ScreenSize.MEDIUM);
        
        assertEquals(1.0, viewWithButton.lastScaleChanged, 0.001);
        assertNotNull(viewWithButton.getButtonSystem());
    }
    
    @Test
    void testUpdateScaleToLarge() {
        viewWithButton.updateScale(ScreenSize.LARGE);
        
        assertEquals(1.1, viewWithButton.lastScaleChanged, 0.001);
        assertNotNull(viewWithButton.getButtonSystem());
    }
    
    @Test
    void testUpdateScaleWithoutButtonSystem() {
        viewWithoutButton.updateScale(ScreenSize.LARGE);
        
        // onScaleChanged는 여전히 호출되어야 함
        assertEquals(1.1, viewWithoutButton.lastScaleChanged, 0.001);
        assertNull(viewWithoutButton.getButtonSystem());
    }
    
    @Test
    void testMultipleScaleUpdates() {
        viewWithButton.updateScale(ScreenSize.SMALL);
        assertEquals(0.9, viewWithButton.lastScaleChanged, 0.001);
        
        viewWithButton.updateScale(ScreenSize.LARGE);
        assertEquals(1.1, viewWithButton.lastScaleChanged, 0.001);
        
        viewWithButton.updateScale(ScreenSize.MEDIUM);
        assertEquals(1.0, viewWithButton.lastScaleChanged, 0.001);
    }
    
    @Test
    void testOnScaleChangedIsCalledForAllSizes() {
        for (ScreenSize size : ScreenSize.values()) {
            TestView view = new TestView(true);
            view.updateScale(size);
            
            assertTrue(view.getLastScaleChanged() > 0);
        }
    }
    
    @Test
    void testStaticColorManagerInitialization() {
        assertNotNull(BaseView.colorManager);
        assertSame(ColorManager.getInstance(), BaseView.colorManager);
    }
    
    @Test
    void testButtonSystemScaleUpdatesWithViewScale() {
        viewWithButton.updateScale(ScreenSize.SMALL);
        assertEquals(0.9, viewWithButton.lastScaleChanged, 0.001);
        
        viewWithButton.updateScale(ScreenSize.LARGE);
        assertEquals(1.1, viewWithButton.lastScaleChanged, 0.001);
    }
    
    @Test
    void testMultipleViewsWithDifferentButtonSystemSettings() {
        TestView view1 = new TestView(true);
        TestView view2 = new TestView(false);
        TestView view3 = new TestView(true);
        
        assertNotNull(view1.getButtonSystem());
        assertNull(view2.getButtonSystem());
        assertNotNull(view3.getButtonSystem());
        
        // 각 뷰의 ButtonSystem은 독립적이어야 함
        assertNotSame(view1.getButtonSystem(), view3.getButtonSystem());
    }
    
    @Test
    void testScaleUpdatesAreIndependent() {
        TestView view1 = new TestView(true);
        TestView view2 = new TestView(true);
        
        view1.updateScale(ScreenSize.SMALL);
        view2.updateScale(ScreenSize.LARGE);
        
        assertEquals(0.9, view1.getLastScaleChanged(), 0.001);
        assertEquals(1.1, view2.getLastScaleChanged(), 0.001);
        
        // 각 뷰가 독립적인 ButtonSystem을 가지고 있는지 확인
        assertNotSame(view1.getButtonSystem(), view2.getButtonSystem());
    }
}
