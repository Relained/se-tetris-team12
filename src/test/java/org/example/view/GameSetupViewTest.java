package org.example.view;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.GameMode;
import org.example.service.ColorManager;
import org.example.view.component.RadioButtonGroupSystem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameSetupView에 대한 Unit Test
 * 게임 모드와 난이도 선택 UI를 테스트
 */
@ExtendWith(ApplicationExtension.class)
class GameSetupViewTest {
    
    private GameSetupView view;
    
    @BeforeAll
    static void initToolkit() {
        // JavaFX 환경 초기화는 @Start에서 처리됨
    }
    
    @Start
    void start(Stage stage) {
        // BaseView 초기화
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        view = new GameSetupView();
    }
    
    @Test
    void testViewCreation() {
        assertNotNull(view, "GameSetupView should be created");
        assertNotNull(view.getRadioButtonSystem(), "RadioButtonSystem should be initialized");
        assertNotNull(view.getButtonSystem(), "ButtonSystem should be initialized");
    }
    
    @Test
    void testCreateViewWithDefaultTitle() {
        // Given: Runnable 콜백들
        AtomicBoolean startCalled = new AtomicBoolean(false);
        AtomicBoolean goBackCalled = new AtomicBoolean(false);
        
        Runnable onStart = () -> startCalled.set(true);
        Runnable onGoBack = () -> goBackCalled.set(true);
        
        // When: View 생성
        VBox root = view.createView(onStart, onGoBack);
        
        // Then: Root가 생성되어야 함
        assertNotNull(root, "Root should be created");
        assertTrue(root.getStyleClass().contains("root-dark"), "Background CSS class should be set");
        assertTrue(root.getChildren().size() > 0, "Root should have children");
    }
    
    @Test
    void testCreateViewWithCustomTitle() {
        // Given: 커스텀 타이틀과 콜백들
        String customTitle = "Custom Game Setup";
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        VBox root = view.createView(customTitle, onStart, onGoBack);
        
        // Then: Root가 생성되어야 함
        assertNotNull(root, "Root should be created");
        assertTrue(root.getChildren().size() > 0, "Root should have children");
    }
    
    @Test
    void testViewStructure() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        VBox root = view.createView(onStart, onGoBack);
        
        // Then: 4개의 주요 섹션이 있어야 함 (타이틀, 게임 모드, 난이도, 버튼)
        assertEquals(4, root.getChildren().size(), "Should have 4 main sections");
    }
    
    @Test
    void testGameModeRadioInitialization() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        
        // Then: 게임 모드 라디오가 초기화되어야 함
        assertNotNull(view.getGameModeRadio(), "Game mode radio should be initialized");
        assertEquals(3, view.getGameModeRadio().getButtons().size(),
            "Should have 3 game mode options");
    }
    
    @Test
    void testDifficultyRadioInitialization() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        
        // Then: 난이도 라디오가 초기화되어야 함
        assertNotNull(view.getDifficultyRadio(), "Difficulty radio should be initialized");
        assertEquals(3, view.getDifficultyRadio().getButtons().size(),
            "Should have 3 difficulty options");
    }
    
    @Test
    void testRadioButtonSystemInitialization() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        
        // Then: RadioButtonSystem에 2개의 그룹이 있어야 함
        assertEquals(2, view.getRadioButtonSystem().getGroupCount(),
            "RadioButtonSystem should have 2 groups");
    }
    
    @Test
    void testDefaultGameModeSelection() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: 기본 게임 모드는 NORMAL이어야 함
        assertEquals(GameMode.NORMAL, view.getSelectedGameMode(),
            "Default game mode should be NORMAL");
    }
    
    @Test
    void testDefaultDifficultySelection() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: 기본 난이도는 1 (Easy)이어야 함
        assertEquals(1, view.getSelectedDifficulty(),
            "Default difficulty should be 1 (Easy)");
    }
    
    @Test
    void testGameModeSelection_Normal() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: NORMAL 선택
        view.getGameModeRadio().setSelectedIndex(0);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: NORMAL이 선택되어야 함
        assertEquals(GameMode.NORMAL, view.getSelectedGameMode(),
            "Selected game mode should be NORMAL");
        assertEquals(0, view.getGameModeRadio().getSelectedIndex(),
            "Selected index should be 0");
    }
    
    @Test
    void testGameModeSelection_Item() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: ITEM 선택
        view.getGameModeRadio().setSelectedIndex(1);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: ITEM이 선택되어야 함
        assertEquals(GameMode.ITEM, view.getSelectedGameMode(),
            "Selected game mode should be ITEM");
        assertEquals(1, view.getGameModeRadio().getSelectedIndex(),
            "Selected index should be 1");
    }
    
    @Test
    void testGameModeSelection_TimeAttack() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: TIME_ATTACK 선택
        view.getGameModeRadio().setSelectedIndex(2);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: TIME_ATTACK이 선택되어야 함
        assertEquals(GameMode.TIME_ATTACK, view.getSelectedGameMode(),
            "Selected game mode should be TIME_ATTACK");
        assertEquals(2, view.getGameModeRadio().getSelectedIndex(),
            "Selected index should be 2");
    }
    
    @Test
    void testDifficultySelection_Easy() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: Easy 선택
        view.getDifficultyRadio().setSelectedIndex(0);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: Easy (1)이 선택되어야 함
        assertEquals(1, view.getSelectedDifficulty(),
            "Selected difficulty should be 1 (Easy)");
        assertEquals(0, view.getDifficultyRadio().getSelectedIndex(),
            "Selected index should be 0");
    }
    
    @Test
    void testDifficultySelection_Normal() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: Normal 선택
        view.getDifficultyRadio().setSelectedIndex(1);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: Normal (2)이 선택되어야 함
        assertEquals(2, view.getSelectedDifficulty(),
            "Selected difficulty should be 2 (Normal)");
        assertEquals(1, view.getDifficultyRadio().getSelectedIndex(),
            "Selected index should be 1");
    }
    
    @Test
    void testDifficultySelection_Hard() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: Hard 선택
        view.getDifficultyRadio().setSelectedIndex(2);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: Hard (3)이 선택되어야 함
        assertEquals(3, view.getSelectedDifficulty(),
            "Selected difficulty should be 3 (Hard)");
        assertEquals(2, view.getDifficultyRadio().getSelectedIndex(),
            "Selected index should be 2");
    }
    
    @Test
    void testStartButtonCallback() {
        // Given: 콜백
        AtomicInteger startCallCount = new AtomicInteger(0);
        Runnable onStart = () -> startCallCount.incrementAndGet();
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: 버튼이 생성되어야 함
        var buttons = view.getButtonSystem().getButtons();
        assertNotNull(buttons, "Buttons should be created");
        assertEquals(2, buttons.size(), "Should have 2 buttons");
        assertEquals("Start", buttons.get(0).getText(), "First button should be Start");
    }
    
    @Test
    void testGoBackButtonCallback() {
        // Given: 콜백
        AtomicInteger goBackCallCount = new AtomicInteger(0);
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> goBackCallCount.incrementAndGet();
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: 버튼이 생성되어야 함
        var buttons = view.getButtonSystem().getButtons();
        assertNotNull(buttons, "Buttons should be created");
        assertEquals(2, buttons.size(), "Should have 2 buttons");
        assertEquals("Go Back", buttons.get(1).getText(), "Second button should be Go Back");
    }
    
    @Test
    void testButtonCount() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: 2개의 버튼이 있어야 함 (Start, Go Back)
        assertEquals(2, view.getButtonSystem().getButtonCount(),
            "Should have 2 buttons");
    }
    
    @Test
    void testInitialFocus() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: 첫 번째 라디오 그룹에 포커스
        assertEquals(0, view.getRadioButtonSystem().getFocusedGroupIndex(),
            "First radio group should be focused");
    }
    
    @Test
    void testGameModeRadioNext() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: 다음 게임 모드 선택
        view.getGameModeRadio().selectNext();
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: ITEM이 선택되어야 함
        assertEquals(GameMode.ITEM, view.getSelectedGameMode(),
            "Should select ITEM after next");
    }
    
    @Test
    void testGameModeRadioPrevious() {
        // Given: View 생성 및 ITEM 선택
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        view.getGameModeRadio().setSelectedIndex(1);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: 이전 게임 모드 선택
        view.getGameModeRadio().selectPrevious();
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: NORMAL이 선택되어야 함
        assertEquals(GameMode.NORMAL, view.getSelectedGameMode(),
            "Should select NORMAL after previous");
    }
    
    @Test
    void testDifficultyRadioNext() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: 다음 난이도 선택
        view.getDifficultyRadio().selectNext();
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: Normal (2)이 선택되어야 함
        assertEquals(2, view.getSelectedDifficulty(),
            "Should select Normal after next");
    }
    
    @Test
    void testDifficultyRadioPrevious() {
        // Given: View 생성 및 Normal 선택
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        view.createView(onStart, onGoBack);
        view.getDifficultyRadio().setSelectedIndex(1);
        WaitForAsyncUtils.waitForFxEvents();
        
        // When: 이전 난이도 선택
        view.getDifficultyRadio().selectPrevious();
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: Easy (1)이 선택되어야 함
        assertEquals(1, view.getSelectedDifficulty(),
            "Should select Easy after previous");
    }
    
    @Test
    void testRadioButtonSystemAccess() {
        // Given: View 생성
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: View 생성
        view.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: RadioButtonSystem 접근 가능
        RadioButtonGroupSystem system = view.getRadioButtonSystem();
        assertNotNull(system, "RadioButtonSystem should be accessible");
        assertEquals(2, system.getGroupCount(), "Should have 2 radio groups");
    }
    
    @Test
    void testMultipleViewInstances() {
        // Given: 여러 View 인스턴스
        GameSetupView view1 = new GameSetupView();
        GameSetupView view2 = new GameSetupView();
        
        Runnable onStart = () -> {};
        Runnable onGoBack = () -> {};
        
        // When: 각각 View 생성
        view1.createView(onStart, onGoBack);
        view2.createView(onStart, onGoBack);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Then: 독립적으로 작동해야 함
        assertNotSame(view1.getRadioButtonSystem(), view2.getRadioButtonSystem(),
            "Each view should have its own RadioButtonSystem");
        assertNotSame(view1.getButtonSystem(), view2.getButtonSystem(),
            "Each view should have its own ButtonSystem");
    }
}
