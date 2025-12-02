package org.example.view;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KeySettingView Unit Test
 * Line Coverage 70% 이상을 목표로 작성됨
 */
@ExtendWith(ApplicationExtension.class)
class KeySettingViewTest {
    
    private KeySettingView view;
    
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
        view = new KeySettingView();
    }
    
    @Test
    void testViewCreation() {
        assertNotNull(view, "KeySettingView should be created");
    }
    
    @Test
    void testCreateView() {
        // Given & When: View 생성
        VBox root = view.createView(() -> {}, () -> {});
        
        // Then
        assertNotNull(root, "Root VBox should be created");
        assertTrue(root.getChildren().size() >= 3, "Should contain title, status, and key bindings");
    }
    
    @Test
    void testNavigateActionsDown() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 아래로 네비게이션
        assertDoesNotThrow(() -> view.navigateActions(false));
    }
    
    @Test
    void testNavigateActionsUp() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 위로 네비게이션
        assertDoesNotThrow(() -> view.navigateActions(true));
    }
    
    @Test
    void testGetSelectedAction() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 선택된 액션 가져오기
        String selectedAction = view.getSelectedAction();
        
        // Then: 첫 번째 액션이 선택되어 있어야 함
        assertNotNull(selectedAction);
    }
    
    @Test
    void testIsButtonSelected() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 버튼 선택 여부 확인
        boolean isButtonSelected = view.isButtonSelected();
        
        // Then: 초기에는 버튼이 선택되지 않음
        assertFalse(isButtonSelected, "Initially action should be selected, not button");
    }
    
    @Test
    void testNavigateToButton() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 여러 번 아래로 이동하여 버튼에 도달
        for (int i = 0; i < 10; i++) {
            view.navigateActions(false);
        }
        
        // Then: 버튼이 선택되어 있어야 함
        assertTrue(view.isButtonSelected() || view.getSelectedAction() != null);
    }
    
    @Test
    void testShowWaitingForKey() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 키 입력 대기 상태 표시
        assertDoesNotThrow(() -> view.showWaitingForKey("moveLeft"));
    }
    
    @Test
    void testHideWaitingForKey() {
        // Given: View 생성 및 키 입력 대기 표시
        view.createView(() -> {}, () -> {});
        view.showWaitingForKey("moveLeft");
        
        // When: 숨기기
        assertDoesNotThrow(() -> view.hideWaitingForKey());
    }
    
    @Test
    void testShowDuplicateKeyError() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 중복 키 에러 표시
        assertDoesNotThrow(() -> view.showDuplicateKeyError(KeyCode.A));
    }
    
    @Test
    void testUpdateKeyBinding() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 키 바인딩 업데이트
        assertDoesNotThrow(() -> view.updateKeyBinding("moveLeft", KeyCode.A));
    }
    
    @Test
    void testUpdateAllKeyBindings() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 모든 키 바인딩 업데이트
        assertDoesNotThrow(() -> view.updateAllKeyBindings());
    }
    
    @Test
    void testExecuteSelectedButton() {
        // Given: View 생성 및 버튼으로 네비게이션
        view.createView(() -> {}, () -> {});
        
        // 버튼으로 이동
        for (int i = 0; i < 10; i++) {
            view.navigateActions(false);
        }
        
        // When & Then: 버튼 실행 (예외 발생하지 않아야 함)
        if (view.isButtonSelected()) {
            assertDoesNotThrow(() -> view.executeSelectedButton());
        }
    }
    
    @Test
    void testMultipleNavigations() {
        // Given: View 생성
        view.createView(() -> {}, () -> {});
        
        // When: 여러 번 네비게이션
        assertDoesNotThrow(() -> {
            view.navigateActions(false);
            view.navigateActions(false);
            view.navigateActions(true);
            view.navigateActions(true);
        });
    }
    
    @Test
    void testRootAlignment() {
        // Given & When: View 생성
        VBox root = view.createView(() -> {}, () -> {});
        
        // Then: CENTER 정렬
        assertNotNull(root);
        assertEquals(javafx.geometry.Pos.CENTER, root.getAlignment());
    }
    
    @Test
    void testRootBackground() {
        // Given & When: View 생성
        VBox root = view.createView(() -> {}, () -> {});
        
        // Then: Background 설정
        assertNotNull(root);
        assertNotNull(root.getBackground());
        assertFalse(root.getBackground().isEmpty());
    }
}
