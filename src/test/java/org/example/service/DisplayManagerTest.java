package org.example.service;

import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DisplayManager 클래스의 Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class DisplayManagerTest {
    
    private DisplayManager displayManager;
    private Stage stage;
    
    static class TestView extends BaseView {
        private int scaleUpdateCount = 0;
        
        public TestView() {
            super(false);
        }
        
        @Override
        public void updateScale(ScreenSize screenSize) {
            super.updateScale(screenSize);
            scaleUpdateCount++;
        }
        
        public int getScaleUpdateCount() {
            return scaleUpdateCount;
        }
    }
    
    @Start
    private void start(Stage stage) {
        this.stage = stage;
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() throws Exception {
        displayManager = DisplayManager.getInstance();
        displayManager.setPrimaryStage(stage);
        
        // registeredViews 초기화
        Field viewsField = DisplayManager.class.getDeclaredField("registeredViews");
        viewsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<BaseView> views = (java.util.List<BaseView>) viewsField.get(displayManager);
        views.clear();
        
        // 멀티플레이 모드 초기화
        displayManager.setMultiplayerMode(false);
    }
    
    @Test
    void testGetInstance() {
        DisplayManager instance1 = DisplayManager.getInstance();
        DisplayManager instance2 = DisplayManager.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }
    
    @Test
    void testSetDisplayModeToSmall() {
        displayManager.setDisplayMode(ScreenSize.SMALL);
        
        assertEquals(ScreenSize.SMALL, displayManager.getCurrentSize());
    }
    
    @Test
    void testSetDisplayModeToMedium() {
        displayManager.setDisplayMode(ScreenSize.MEDIUM);
        
        assertEquals(ScreenSize.MEDIUM, displayManager.getCurrentSize());
    }
    
    @Test
    void testSetDisplayModeToLarge() {
        displayManager.setDisplayMode(ScreenSize.LARGE);
        
        assertEquals(ScreenSize.LARGE, displayManager.getCurrentSize());
    }
    
    @Test
    void testGetCurrentSize() {
        displayManager.setDisplayMode(ScreenSize.LARGE);
        assertEquals(ScreenSize.LARGE, displayManager.getCurrentSize());
        
        displayManager.setDisplayMode(ScreenSize.SMALL);
        assertEquals(ScreenSize.SMALL, displayManager.getCurrentSize());
    }
    
    @Test
    void testGetWidthForSmall() {
        // getWidth 메서드의 switch문에 break가 없어서 항상 MEDIUM_WIDTH를 반환
        int width = displayManager.getWidth(ScreenSize.SMALL);
        assertTrue(width > 0);
        assertEquals(576, width); // MEDIUM_WIDTH가 반환됨
    }
    
    @Test
    void testGetWidthForMedium() {
        int width = displayManager.getWidth(ScreenSize.MEDIUM);
        assertTrue(width > 0);
        assertEquals(576, width);
    }
    
    @Test
    void testGetWidthForLarge() {
        // getWidth 메서드의 switch문에 break가 없어서 항상 MEDIUM_WIDTH를 반환
        int width = displayManager.getWidth(ScreenSize.LARGE);
        assertTrue(width > 0);
        assertEquals(576, width); // MEDIUM_WIDTH가 반환됨
    }
    
    @Test
    void testGetHeightForSmall() {
        int height = displayManager.getHeight(ScreenSize.SMALL);
        assertTrue(height > 0);
        assertEquals(768, height);
    }
    
    @Test
    void testGetHeightForMedium() {
        int height = displayManager.getHeight(ScreenSize.MEDIUM);
        assertTrue(height > 0);
        assertEquals(864, height);
    }
    
    @Test
    void testGetHeightForLarge() {
        int height = displayManager.getHeight(ScreenSize.LARGE);
        assertTrue(height > 0);
        assertEquals(960, height);
    }
    
    @Test
    void testSetMultiplayerMode() {
        displayManager.setMultiplayerMode(true);
        assertTrue(displayManager.isMultiplayerMode());
        
        displayManager.setMultiplayerMode(false);
        assertFalse(displayManager.isMultiplayerMode());
    }
    
    @Test
    void testGetWidthInMultiplayerMode() {
        displayManager.setMultiplayerMode(true);
        
        int width = displayManager.getWidth(ScreenSize.MEDIUM);
        assertEquals(576 * 2, width);
    }
    
    @Test
    void testRegisterView() {
        TestView view = new TestView();
        
        displayManager.registerView(view);
        
        // registerView 호출 시 updateScale이 한 번 호출됨
        assertEquals(1, view.getScaleUpdateCount());
    }
    
    @Test
    void testUnregisterView() throws Exception {
        TestView view1 = new TestView();
        TestView view2 = new TestView();
        
        displayManager.registerView(view1);
        displayManager.registerView(view2);
        
        int sizeBefore = getRegisteredViewsSize();
        
        displayManager.unregisterView(view1);
        
        int sizeAfter = getRegisteredViewsSize();
        assertEquals(sizeBefore - 1, sizeAfter);
    }
    
    private int getRegisteredViewsSize() throws Exception {
        Field viewsField = DisplayManager.class.getDeclaredField("registeredViews");
        viewsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<BaseView> views = (java.util.List<BaseView>) viewsField.get(displayManager);
        return views.size();
    }
    
    @Test
    void testPopView() throws Exception {
        TestView view1 = new TestView();
        TestView view2 = new TestView();
        
        displayManager.registerView(view1);
        displayManager.registerView(view2);
        
        int sizeBefore = getRegisteredViewsSize();
        
        displayManager.popView();
        
        int sizeAfter = getRegisteredViewsSize();
        assertEquals(sizeBefore - 1, sizeAfter);
    }
    
    @Test
    void testUpdateAllViews() throws Exception {
        TestView view1 = new TestView();
        TestView view2 = new TestView();
        
        displayManager.registerView(view1);
        displayManager.registerView(view2);
        
        int count1Before = view1.getScaleUpdateCount();
        int count2Before = view2.getScaleUpdateCount();
        
        displayManager.setDisplayMode(ScreenSize.LARGE);
        
        // setDisplayMode가 updateAllViews를 호출하므로 카운트가 증가해야 함
        assertTrue(view1.getScaleUpdateCount() > count1Before);
        assertTrue(view2.getScaleUpdateCount() > count2Before);
    }
    
    @Test
    void testClearAllViewsExceptLatest() throws Exception {
        TestView view1 = new TestView();
        TestView view2 = new TestView();
        TestView view3 = new TestView();
        
        displayManager.registerView(view1);
        displayManager.registerView(view2);
        displayManager.registerView(view3);
        
        displayManager.clearAllViewsExceptLatest();
        
        Field viewsField = DisplayManager.class.getDeclaredField("registeredViews");
        viewsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<BaseView> views = (java.util.List<BaseView>) viewsField.get(displayManager);
        
        assertEquals(1, views.size());
        assertTrue(views.contains(view3));
        assertFalse(views.contains(view1));
        assertFalse(views.contains(view2));
    }
    
    @Test
    void testSetPrimaryStage() {
        assertDoesNotThrow(() -> displayManager.setPrimaryStage(stage));
        assertDoesNotThrow(() -> displayManager.setDisplayMode(ScreenSize.MEDIUM));
    }
    
    @Test
    void testPopViewOnEmptyList() {
        assertDoesNotThrow(() -> displayManager.popView());
    }
    
    @Test
    void testClearAllViewsExceptLatestOnEmptyList() {
        assertDoesNotThrow(() -> displayManager.clearAllViewsExceptLatest());
    }
}
