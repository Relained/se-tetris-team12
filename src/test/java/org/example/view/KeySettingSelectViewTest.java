package org.example.view;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KeySettingSelectView Unit Test
 * Line Coverage 70% 이상을 목표로 작성됨
 */
@ExtendWith(ApplicationExtension.class)
class KeySettingSelectViewTest {
    
    private KeySettingSelectView view;
    
    @BeforeAll
    static void initToolkit() {
        // JavaFX 환경 초기화
    }
    
    @Start
    void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        view = new KeySettingSelectView();
    }
    
    @Test
    void testViewCreation() {
        assertNotNull(view, "KeySettingSelectView should be created");
        assertNotNull(view.getButtonSystem(), "ButtonSystem should be initialized");
    }
    
    @Test
    void testCreateViewWithCallbacks() {
        // Given: 콜백 카운터
        AtomicInteger player1Counter = new AtomicInteger(0);
        AtomicInteger player2Counter = new AtomicInteger(0);
        AtomicInteger goBackCounter = new AtomicInteger(0);
        
        // When: View 생성
        VBox root = view.createView(
            player1Counter::incrementAndGet,
            player2Counter::incrementAndGet,
            goBackCounter::incrementAndGet
        );
        
        // Then: UI 구성 확인
        assertNotNull(root, "Root VBox should be created");
        assertTrue(root.getChildren().size() >= 3, "Root should contain title, instruction, and buttons");
    }
    
    @Test
    void testTitleText() {
        // Given: View 생성
        VBox root = view.createView(() -> {}, () -> {}, () -> {});
        
        // When: 첫 번째 텍스트 요소 확인
        var firstChild = root.getChildren().get(0);
        
        // Then: 타이틀이어야 함
        assertTrue(firstChild instanceof javafx.scene.text.Text);
        javafx.scene.text.Text title = (javafx.scene.text.Text) firstChild;
        assertEquals("Key Settings", title.getText());
    }
    
    @Test
    void testButtonSystemInitialization() {
        // Given: View 생성
        VBox root = view.createView(() -> {}, () -> {}, () -> {});
        
        // When: ButtonSystem 확인
        var buttonSystem = view.getButtonSystem();
        
        // Then: 3개 버튼 (Single Settings, 2P Settings, Go Back)
        assertNotNull(buttonSystem);
        assertEquals(3, buttonSystem.getButtonCount(), "Should have 3 buttons");
    }
    
    @Test
    void testButtonTexts() {
        // Given: View 생성
        VBox root = view.createView(() -> {}, () -> {}, () -> {});
        
        // When: 버튼 텍스트 확인
        var buttons = view.getButtonSystem().getButtons();
        
        // Then
        assertEquals("Single Settings", buttons.get(0).getText());
        assertEquals("2P Settings", buttons.get(1).getText());
        assertEquals("Go Back", buttons.get(2).getText());
    }
    
    @Test
    void testGetNavigableButtonSystem() {
        // Given: View 생성
        view.createView(() -> {}, () -> {}, () -> {});
        
        // When: NavigableButtonSystem 가져오기
        var navSystem = view.getNavigableButtonSystem();
        
        // Then: ButtonSystem과 동일해야 함
        assertNotNull(navSystem);
        assertEquals(view.getButtonSystem(), navSystem);
    }
    
    @Test
    void testRootAlignment() {
        // Given & When: View 생성
        VBox root = view.createView(() -> {}, () -> {}, () -> {});
        
        // Then: CENTER 정렬
        assertNotNull(root);
        assertEquals(javafx.geometry.Pos.CENTER, root.getAlignment());
    }
    
    @Test
    void testRootPadding() {
        // Given & When: View 생성
        VBox root = view.createView(() -> {}, () -> {}, () -> {});
        
        // Then: Padding이 설정되어 있어야 함
        assertNotNull(root);
        assertNotNull(root.getPadding());
    }
    
    @Test
    void testBackgroundColor() {
        // Given & When: View 생성
        VBox root = view.createView(() -> {}, () -> {}, () -> {});
        
        // Then: Background가 설정되어야 함
        assertNotNull(root);
        assertNotNull(root.getBackground());
        assertFalse(root.getBackground().isEmpty());
    }
    
    @Test
    void testInstructionText() {
        // Given: View 생성
        VBox root = view.createView(() -> {}, () -> {}, () -> {});
        
        // When: 두 번째 텍스트 요소 찾기
        javafx.scene.text.Text instruction = null;
        for (var child : root.getChildren()) {
            if (child instanceof javafx.scene.text.Text) {
                javafx.scene.text.Text text = (javafx.scene.text.Text) child;
                if (text.getText().contains("Select player")) {
                    instruction = text;
                    break;
                }
            }
        }
        
        // Then: 안내 텍스트가 존재해야 함
        assertNotNull(instruction);
        assertTrue(instruction.getText().contains("configure"));
    }
    
    @Test
    void testMultipleViewCreation() {
        // Given & When: 여러 번 View 생성
        VBox root1 = view.createView(() -> {}, () -> {}, () -> {});
        VBox root2 = view.createView(() -> {}, () -> {}, () -> {});
        
        // Then: 모두 정상적으로 생성되어야 함
        assertNotNull(root1);
        assertNotNull(root2);
        assertNotEquals(root1, root2);
    }
}
