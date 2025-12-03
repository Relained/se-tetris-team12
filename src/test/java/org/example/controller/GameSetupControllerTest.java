package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.model.GameMode;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameSetupController에 대한 Unit Test
 * 게임 모드와 난이도 선택 기능을 테스트
 */
@ExtendWith(ApplicationExtension.class)
class GameSetupControllerTest {
    
    private GameSetupController controller;
    private Stage testStage;
    
    @BeforeAll
    static void initToolkit() {
        // JavaFX 환경 초기화는 @Start에서 처리됨
    }
    
    @Start
    void start(Stage stage) {
        this.testStage = stage;
        
        // BaseController 초기화
        SettingManager settingManager = new SettingManager();
        BaseController.Initialize(stage, settingManager);
        
        // BaseView 초기화
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        controller = new GameSetupController();
    }
    
    @Test
    void testControllerCreation() {
        assertNotNull(controller, "GameSetupController should be created");
        assertNotNull(controller.view, "View should be initialized");
        assertTrue(controller.isRadioGroupFocused, "Radio group should be focused initially");
    }
    
    @Test
    void testCreateScene() {
        // When: Scene 생성
        Scene scene = controller.createScene();
        
        // Then: Scene이 생성되어야 함
        assertNotNull(scene, "Scene should be created");
        assertNotNull(scene.getRoot(), "Scene root should exist");
        assertTrue(scene.getWidth() > 0, "Scene width should be positive");
        assertTrue(scene.getHeight() > 0, "Scene height should be positive");
    }
    
    @Test
    void testGetTitle() {
        // When: 기본 타이틀 확인
        String title = controller.getTitle();
        
        // Then: "Game Setup" 이어야 함
        assertEquals("Game Setup", title, "Title should be 'Game Setup'");
    }
    
    @Test
    void testHandleGoBack() {
        // Given: Scene 생성
        controller.createScene();
        
        // When: Go Back 처리 (popState 호출)
        // Then: 예외 없이 처리되어야 함
        assertDoesNotThrow(() -> controller.handleGoBack(),
            "handleGoBack should not throw exception");
    }
    
    @Test
    void testHandleKeyInput_Left_RadioGroupFocused() {
        // Given: Scene 생성 및 라디오 그룹에 포커스
        controller.createScene();
        assertTrue(controller.isRadioGroupFocused, "Radio group should be focused");
        
        // When: LEFT 키 입력
        KeyEvent leftEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.LEFT,
            false, false, false, false
        );
        
        // Then: 예외 없이 처리되어야 함
        assertDoesNotThrow(() -> controller.handleKeyInput(leftEvent));
    }
    
    @Test
    void testHandleKeyInput_Right_RadioGroupFocused() {
        // Given: Scene 생성 및 라디오 그룹에 포커스
        controller.createScene();
        assertTrue(controller.isRadioGroupFocused, "Radio group should be focused");
        
        // When: RIGHT 키 입력
        KeyEvent rightEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.RIGHT,
            false, false, false, false
        );
        
        // Then: 예외 없이 처리되어야 함
        assertDoesNotThrow(() -> controller.handleKeyInput(rightEvent));
    }
    
    @Test
    void testHandleKeyInput_Down_TransitionToButtons() {
        // Given: Scene 생성 및 난이도 그룹에 포커스
        controller.createScene();
        controller.view.getRadioButtonSystem().setFocusedGroup(1); // 난이도 그룹
        assertTrue(controller.isRadioGroupFocused, "Radio group should be focused");
        
        // When: DOWN 키로 버튼으로 전환
        KeyEvent downEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.DOWN,
            false, false, false, false
        );
        controller.handleKeyInput(downEvent);
        
        // Then: 버튼으로 포커스 전환
        assertFalse(controller.isRadioGroupFocused, "Should transition to button focus");
    }
    
    @Test
    void testHandleKeyInput_Up_FromFirstRadioGroup() {
        // Given: Scene 생성 및 첫 번째 라디오 그룹에 포커스
        controller.createScene();
        controller.view.getRadioButtonSystem().setFocusedGroup(0);
        assertTrue(controller.isRadioGroupFocused, "Radio group should be focused");
        
        // When: UP 키 입력 (첫 번째 그룹에서)
        KeyEvent upEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.UP,
            false, false, false, false
        );
        controller.handleKeyInput(upEvent);
        
        // Then: 라디오 그룹에 여전히 포커스
        assertTrue(controller.isRadioGroupFocused, "Should remain in radio group focus");
        assertEquals(0, controller.view.getRadioButtonSystem().getFocusedGroupIndex(),
            "Should stay at first group");
    }
    
    @Test
    void testHandleKeyInput_Down_BetweenRadioGroups() {
        // Given: Scene 생성 및 첫 번째 라디오 그룹에 포커스
        controller.createScene();
        controller.view.getRadioButtonSystem().setFocusedGroup(0);
        
        // When: DOWN 키 입력 (라디오 그룹 사이에서)
        KeyEvent downEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.DOWN,
            false, false, false, false
        );
        controller.handleKeyInput(downEvent);
        
        // Then: 두 번째 라디오 그룹으로 이동
        assertTrue(controller.isRadioGroupFocused, "Should remain in radio group focus");
        assertEquals(1, controller.view.getRadioButtonSystem().getFocusedGroupIndex(),
            "Should move to second group");
    }
    
    @Test
    void testHandleKeyInput_Up_TransitionFromButtons() {
        // Given: Scene 생성 및 버튼에 포커스
        controller.createScene();
        controller.isRadioGroupFocused = false;
        controller.view.getRadioButtonSystem().unfocusAll();
        controller.view.getButtonSystem().focusButton(0);
        
        // When: UP 키로 라디오 그룹으로 전환
        KeyEvent upEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.UP,
            false, false, false, false
        );
        controller.handleKeyInput(upEvent);
        
        // Then: 라디오 그룹으로 포커스 전환
        assertTrue(controller.isRadioGroupFocused, "Should transition to radio group focus");
        assertEquals(1, controller.view.getRadioButtonSystem().getFocusedGroupIndex(),
            "Should focus last radio group");
    }
    
    @Test
    void testHandleKeyInput_Down_FromLastButton() {
        // Given: Scene 생성 및 마지막 버튼에 포커스
        controller.createScene();
        controller.isRadioGroupFocused = false;
        int lastButtonIndex = controller.view.getButtonSystem().getButtonCount() - 1;
        controller.view.getButtonSystem().focusButton(lastButtonIndex);
        
        // When: DOWN 키 입력 (마지막 버튼에서)
        KeyEvent downEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.DOWN,
            false, false, false, false
        );
        controller.handleKeyInput(downEvent);
        
        // Then: 버튼에 여전히 포커스
        assertFalse(controller.isRadioGroupFocused, "Should remain in button focus");
        assertEquals(lastButtonIndex, controller.view.getButtonSystem().getSelectedIndex(),
            "Should stay at last button");
    }
    
    @Test
    void testHandleKeyInput_Left_ButtonFocused() {
        // Given: Scene 생성 및 버튼에 포커스
        controller.createScene();
        controller.isRadioGroupFocused = false;
        controller.view.getButtonSystem().focusButton(1);
        
        // When: LEFT 키 입력
        KeyEvent leftEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.LEFT,
            false, false, false, false
        );
        
        // Then: 예외 없이 처리되어야 함
        assertDoesNotThrow(() -> controller.handleKeyInput(leftEvent));
    }
    
    @Test
    void testHandleKeyInput_Right_ButtonFocused() {
        // Given: Scene 생성 및 버튼에 포커스
        controller.createScene();
        controller.isRadioGroupFocused = false;
        controller.view.getButtonSystem().focusButton(0);
        
        // When: RIGHT 키 입력
        KeyEvent rightEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.RIGHT,
            false, false, false, false
        );
        
        // Then: 예외 없이 처리되어야 함
        assertDoesNotThrow(() -> controller.handleKeyInput(rightEvent));
    }
    
    @Test
    void testHandleKeyInput_Enter_RadioGroupFocused() {
        // Given: Scene 생성 및 라디오 그룹에 포커스
        controller.createScene();
        assertTrue(controller.isRadioGroupFocused, "Radio group should be focused");
        
        // When: ENTER 키 입력
        KeyEvent enterEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        
        // Then: 라디오 그룹에서는 ENTER가 무시됨
        assertDoesNotThrow(() -> controller.handleKeyInput(enterEvent));
    }
    
    @Test
    void testHandleKeyInput_Space_RadioGroupFocused() {
        // Given: Scene 생성 및 라디오 그룹에 포커스
        controller.createScene();
        assertTrue(controller.isRadioGroupFocused, "Radio group should be focused");
        
        // When: SPACE 키 입력
        KeyEvent spaceEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.SPACE,
            false, false, false, false
        );
        
        // Then: 라디오 그룹에서는 SPACE가 무시됨
        assertDoesNotThrow(() -> controller.handleKeyInput(spaceEvent));
    }
    
    @Test
    void testHandleKeyInput_OtherKey() {
        // Given: Scene 생성
        controller.createScene();
        
        // When: 다른 키 입력 (예: A)
        KeyEvent aEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.A,
            false, false, false, false
        );
        
        // Then: 예외 없이 처리되어야 함 (무시됨)
        assertDoesNotThrow(() -> controller.handleKeyInput(aEvent));
    }
    
    @Test
    void testVerticalNavigation_DownBetweenButtons() {
        // Given: Scene 생성 및 첫 번째 버튼에 포커스
        controller.createScene();
        controller.isRadioGroupFocused = false;
        controller.view.getButtonSystem().focusButton(0);
        
        // When: DOWN 키로 다음 버튼으로 이동
        KeyEvent downEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.DOWN,
            false, false, false, false
        );
        controller.handleKeyInput(downEvent);
        
        // Then: 다음 버튼으로 이동
        assertFalse(controller.isRadioGroupFocused, "Should remain in button focus");
    }
    
    @Test
    void testVerticalNavigation_UpBetweenButtons() {
        // Given: Scene 생성 및 두 번째 버튼에 포커스
        controller.createScene();
        controller.isRadioGroupFocused = false;
        controller.view.getButtonSystem().focusButton(1);
        
        // When: UP 키로 이전 버튼으로 이동
        KeyEvent upEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.UP,
            false, false, false, false
        );
        controller.handleKeyInput(upEvent);
        
        // Then: 이전 버튼으로 이동
        assertFalse(controller.isRadioGroupFocused, "Should remain in button focus");
    }
    
    @Test
    void testVerticalNavigation_UpBetweenRadioGroups() {
        // Given: Scene 생성 및 두 번째 라디오 그룹에 포커스
        controller.createScene();
        controller.view.getRadioButtonSystem().setFocusedGroup(1);
        
        // When: UP 키로 첫 번째 그룹으로 이동
        KeyEvent upEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.UP,
            false, false, false, false
        );
        controller.handleKeyInput(upEvent);
        
        // Then: 첫 번째 그룹으로 이동
        assertTrue(controller.isRadioGroupFocused, "Should remain in radio group focus");
        assertEquals(0, controller.view.getRadioButtonSystem().getFocusedGroupIndex(),
            "Should move to first group");
    }
    
    @Test
    void testDefaultGameModeSelection() {
        // Given & When: Scene 생성
        controller.createScene();
        
        // Then: 기본값은 NORMAL이어야 함
        assertEquals(GameMode.NORMAL, controller.view.getSelectedGameMode(),
            "Default game mode should be NORMAL");
    }
    
    @Test
    void testDefaultDifficultySelection() {
        // Given & When: Scene 생성
        controller.createScene();
        
        // Then: 기본값은 1 (Easy)이어야 함
        assertEquals(1, controller.view.getSelectedDifficulty(),
            "Default difficulty should be 1 (Easy)");
    }
    
    @Test
    void testSceneKeyEventHandler() {
        // Given: Scene 생성
        Scene scene = controller.createScene();
        
        // When: 키 이벤트 핸들러 확인
        var keyHandler = scene.getOnKeyPressed();
        
        // Then: 핸들러가 설정되어야 함
        assertNotNull(keyHandler, "Key event handler should be set");
    }
}
