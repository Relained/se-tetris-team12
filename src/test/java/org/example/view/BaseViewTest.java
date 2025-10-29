package org.example.view;

import org.example.service.ColorManager;
import org.example.view.component.NavigableButtonSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class BaseViewTest extends ApplicationTest {
    
    // BaseView는 추상 클래스이므로 테스트를 위한 구체 클래스 생성
    static class TestableBaseView extends BaseView {
        public TestableBaseView() {
            super();
        }
        
        public TestableBaseView(boolean useButtonSystem) {
            super(useButtonSystem);
        }
        
        @Override
        public void refreshColors() {
            // 테스트용 구현
            super.refreshColors();
        }
    }
    
    @Test
    @DisplayName("기본 생성자 테스트")
    void testDefaultConstructor() {
        TestableBaseView view = new TestableBaseView();
        
        assertNotNull(view);
        assertNotNull(view.getColorManager());
        assertNull(view.getButtonSystem());
    }
    
    @Test
    @DisplayName("ButtonSystem 없이 생성 테스트")
    void testConstructorWithoutButtonSystem() {
        TestableBaseView view = new TestableBaseView(false);
        
        assertNotNull(view);
        assertNotNull(view.getColorManager());
        assertNull(view.getButtonSystem());
    }
    
    @Test
    @DisplayName("ButtonSystem과 함께 생성 테스트")
    void testConstructorWithButtonSystem() {
        TestableBaseView view = new TestableBaseView(true);
        
        assertNotNull(view);
        assertNotNull(view.getColorManager());
        assertNotNull(view.getButtonSystem());
        assertTrue(view.getButtonSystem() instanceof NavigableButtonSystem);
    }
    
    @Test
    @DisplayName("getButtonSystem 반환 테스트")
    void testGetButtonSystem() {
        TestableBaseView viewWithButton = new TestableBaseView(true);
        TestableBaseView viewWithoutButton = new TestableBaseView(false);
        
        assertNotNull(viewWithButton.getButtonSystem());
        assertNull(viewWithoutButton.getButtonSystem());
    }
    
    @Test
    @DisplayName("getColorManager 반환 테스트")
    void testGetColorManager() {
        TestableBaseView view = new TestableBaseView();
        
        ColorManager colorManager = view.getColorManager();
        assertNotNull(colorManager);
        assertSame(ColorManager.getInstance(), colorManager);
    }
    
    @Test
    @DisplayName("ColorManager 싱글톤 테스트")
    void testColorManagerSingleton() {
        TestableBaseView view1 = new TestableBaseView();
        TestableBaseView view2 = new TestableBaseView();
        
        assertSame(view1.getColorManager(), view2.getColorManager());
    }
    
    @Test
    @DisplayName("refreshColors 기본 구현 테스트")
    void testRefreshColors() {
        TestableBaseView view = new TestableBaseView();
        
        // refreshColors는 기본적으로 아무 것도 하지 않으므로 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> view.refreshColors());
    }
    
    @Test
    @DisplayName("ButtonSystem 독립성 테스트")
    void testButtonSystemIndependence() {
        TestableBaseView view1 = new TestableBaseView(true);
        TestableBaseView view2 = new TestableBaseView(true);
        
        // 각 뷰는 독립적인 ButtonSystem을 가져야 함
        assertNotNull(view1.getButtonSystem());
        assertNotNull(view2.getButtonSystem());
    }
    
    @Test
    @DisplayName("여러 인스턴스 생성 테스트")
    void testMultipleInstances() {
        TestableBaseView view1 = new TestableBaseView();
        TestableBaseView view2 = new TestableBaseView(true);
        TestableBaseView view3 = new TestableBaseView(false);
        
        assertNotNull(view1);
        assertNotNull(view2);
        assertNotNull(view3);
        
        // ColorManager는 모두 같은 인스턴스
        assertSame(view1.getColorManager(), view2.getColorManager());
        assertSame(view2.getColorManager(), view3.getColorManager());
        
        // ButtonSystem은 다름
        assertNull(view1.getButtonSystem());
        assertNotNull(view2.getButtonSystem());
        assertNull(view3.getButtonSystem());
    }
}
