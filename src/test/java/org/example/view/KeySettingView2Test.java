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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KeySettingView2 Unit Test
 * 2플레이어 키 설정 화면 테스트
 */
@ExtendWith(ApplicationExtension.class)
class KeySettingView2Test {
    
    private KeySettingView2 view;
    
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
        view = new KeySettingView2();
    }
    
    @Test
    void testViewCreation() {
        assertNotNull(view, "KeySettingView2 should be created");
        assertNotNull(view.getButtonSystem(), "ButtonSystem should be initialized");
    }
    
    @Test
    void testCreateView() {
        // Given: 플레이어 키 바인딩
        Map<String, KeyCode> player1Bindings = createDefaultPlayer1Bindings();
        Map<String, KeyCode> player2Bindings = createDefaultPlayer2Bindings();
        
        // When: View 생성
        VBox root = view.createView(player1Bindings, player2Bindings, () -> {}, () -> {});
        
        // Then
        assertNotNull(root, "Root VBox should be created");
        assertTrue(root.getChildren().size() >= 3, "Should contain title, status, and grid");
    }
    
    @Test
    void testGetActions() {
        // When: 액션 목록 가져오기
        String[] actions = KeySettingView2.getActions();
        
        // Then: 7개 액션
        assertNotNull(actions);
        assertEquals(7, actions.length);
        assertEquals("moveLeft", actions[0]);
        assertEquals("hold", actions[6]);
    }
    
    @Test
    void testGetActionDisplayName() {
        // When & Then: 액션 표시 이름 확인
        assertEquals("Move Left", KeySettingView2.getActionDisplayName("moveLeft"));
        assertEquals("Move Right", KeySettingView2.getActionDisplayName("moveRight"));
        assertEquals("Soft Drop", KeySettingView2.getActionDisplayName("softDrop"));
        assertEquals("Hard Drop", KeySettingView2.getActionDisplayName("hardDrop"));
        assertEquals("Rotate CCW", KeySettingView2.getActionDisplayName("rotateCounterClockwise"));
        assertEquals("Rotate CW", KeySettingView2.getActionDisplayName("rotateClockwise"));
        assertEquals("Hold", KeySettingView2.getActionDisplayName("hold"));
    }
    
    @Test
    void testIsWaitingForKeyInitially() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When & Then: 초기에는 대기 중이 아님
        assertFalse(view.isWaitingForKey());
    }
    
    @Test
    void testGetWaitingPlayerInitially() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When & Then: 초기에는 null
        assertNull(view.getWaitingPlayer());
    }
    
    @Test
    void testGetWaitingActionInitially() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When & Then: 초기에는 null
        assertNull(view.getWaitingAction());
    }
    
    @Test
    void testCancelKeyBinding() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When: 키 바인딩 취소
        assertDoesNotThrow(() -> view.cancelKeyBinding());
    }
    
    @Test
    void testHideWaitingForKey() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When: 대기 상태 숨기기
        assertDoesNotThrow(() -> view.hideWaitingForKey());
    }
    
    @Test
    void testShowDuplicateKeyError() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When: 중복 키 에러 표시
        assertDoesNotThrow(() -> view.showDuplicateKeyError(KeyCode.A));
    }
    
    @Test
    void testUpdateKeyBinding() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When: 키 바인딩 업데이트
        assertDoesNotThrow(() -> view.updateKeyBinding(1, "moveLeft", KeyCode.Q));
    }
    
    @Test
    void testUpdateAllKeyBindings() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When: 모든 키 바인딩 업데이트
        Map<String, KeyCode> newP1 = createDefaultPlayer1Bindings();
        Map<String, KeyCode> newP2 = createDefaultPlayer2Bindings();
        
        assertDoesNotThrow(() -> view.updateAllKeyBindings(newP1, newP2));
    }
    
    @Test
    void testRootAlignment() {
        // Given & When: View 생성
        VBox root = view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // Then: CENTER 정렬
        assertNotNull(root);
        assertEquals(javafx.geometry.Pos.CENTER, root.getAlignment());
    }
    
    @Test
    void testRootBackground() {
        // Given & When: View 생성
        VBox root = view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // Then: Background 설정
        assertNotNull(root);
        assertNotNull(root.getBackground());
    }
    
    @Test
    void testButtonSystemGridColumns() {
        // Given: View 생성
        view.createView(createDefaultPlayer1Bindings(), createDefaultPlayer2Bindings(), () -> {}, () -> {});
        
        // When: ButtonSystem 확인
        var buttonSystem = view.getButtonSystem();
        
        // Then: 2열 그리드 설정 확인 (간접적)
        assertNotNull(buttonSystem);
        assertTrue(buttonSystem.getButtonCount() > 0);
    }
    
    // Helper methods
    private Map<String, KeyCode> createDefaultPlayer1Bindings() {
        Map<String, KeyCode> bindings = new HashMap<>();
        bindings.put("moveLeft", KeyCode.LEFT);
        bindings.put("moveRight", KeyCode.RIGHT);
        bindings.put("softDrop", KeyCode.DOWN);
        bindings.put("hardDrop", KeyCode.ENTER);
        bindings.put("rotateCounterClockwise", KeyCode.QUOTE);
        bindings.put("rotateClockwise", KeyCode.UP);
        bindings.put("hold", KeyCode.SHIFT);
        return bindings;
    }
    
    private Map<String, KeyCode> createDefaultPlayer2Bindings() {
        Map<String, KeyCode> bindings = new HashMap<>();
        bindings.put("moveLeft", KeyCode.A);
        bindings.put("moveRight", KeyCode.D);
        bindings.put("softDrop", KeyCode.S);
        bindings.put("hardDrop", KeyCode.SPACE);
        bindings.put("rotateCounterClockwise", KeyCode.Z);
        bindings.put("rotateClockwise", KeyCode.W);
        bindings.put("hold", KeyCode.C);
        return bindings;
    }
}
