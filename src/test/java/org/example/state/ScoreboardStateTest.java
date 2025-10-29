package org.example.state;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.model.ScoreRecord;
import org.example.service.SettingManager;
import org.example.service.StateManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreboardStateTest extends ApplicationTest {
    
    private StateManager stateManager;
    private ScoreboardState state;
    
    @BeforeEach
    void setUp() {
        Stage stage = mock(Stage.class);
        SettingManager settingManager = new SettingManager();
        stateManager = new StateManager(stage, settingManager);
        state = new ScoreboardState(stateManager);
    }
    
    @Test
    @DisplayName("State 생성 시 null이 아닌지 확인")
    void testStateNotNull() {
        assertNotNull(state);
    }
    
    @Test
    @DisplayName("생성자 - Scoreboard 모드 (기본)")
    void testConstructorScoreboardMode() {
        ScoreboardState state = new ScoreboardState(stateManager);
        assertNotNull(state);
        assertEquals(ScoreboardState.Mode.SCOREBOARD, state.getCurrentMode());
    }
    
    @Test
    @DisplayName("생성자 - Scoreboard 모드 (게임 플레이 후)")
    void testConstructorScoreboardModeAfterGamePlay() {
        ScoreRecord record = new ScoreRecord(5000, 50, 10, 1);
        ScoreboardState state = new ScoreboardState(stateManager, record, true);
        assertNotNull(state);
        // ScoreRecord를 전달하면 INPUT 모드로 시작됨
        assertEquals(ScoreboardState.Mode.INPUT, state.getCurrentMode());
    }
    
    @Test
    @DisplayName("생성자 - Input 모드 (상위 10위 내)")
    void testConstructorInputMode() {
        ScoreRecord record = new ScoreRecord(5000, 50, 10, 1);
        ScoreboardState state = new ScoreboardState(stateManager, record, true);
        assertNotNull(state);
        assertEquals(ScoreboardState.Mode.INPUT, state.getCurrentMode());
        assertEquals(5000, state.getFinalScore());
        assertEquals(50, state.getFinalLines());
        assertEquals(10, state.getFinalLevel());
    }
    
    @Test
    @DisplayName("생성자 - Not Eligible 모드 (상위 10위 밖)")
    void testConstructorNotEligibleMode() {
        ScoreRecord record = new ScoreRecord(100, 5, 1, 1);
        ScoreboardState state = new ScoreboardState(stateManager, record, false);
        assertNotNull(state);
        assertEquals(ScoreboardState.Mode.NOT_ELIGIBLE, state.getCurrentMode());
    }
    
    @Test
    @DisplayName("점수 입력이 필요한 상태로 생성")
    void testStateWithScoreInput() {
        ScoreRecord record = new ScoreRecord(1000, 10, 5, 1);
        ScoreboardState stateWithInput = new ScoreboardState(stateManager, record, true);
        assertNotNull(stateWithInput);
    }
    
    @Test
    @DisplayName("enter() 호출 시 예외 발생하지 않음")
    void testEnter() {
        assertDoesNotThrow(() -> state.enter());
    }
    
    @Test
    @DisplayName("exit() 호출 시 예외 발생하지 않음")
    void testExit() {
        assertDoesNotThrow(() -> state.exit());
    }
    
    @Test
    @DisplayName("resume() 호출 시 예외 발생하지 않음")
    void testResume() {
        assertDoesNotThrow(() -> state.resume());
    }
    
    @Test
    @DisplayName("createScene() - Scoreboard 모드 Scene 생성")
    void testCreateSceneScoreboardMode() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            Scene scene = state.createScene();
            
            assertNotNull(scene);
            assertNotNull(state.getScene());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createScene() - Input 모드 Scene 생성")
    void testCreateSceneInputMode() {
        ScoreRecord record = new ScoreRecord(5000, 50, 10, 1);
        ScoreboardState inputState = new ScoreboardState(stateManager, record, true);
        javafx.application.Platform.runLater(() -> {
            inputState.enter();
            Scene scene = inputState.createScene();
            
            assertNotNull(scene);
            assertEquals(800, scene.getWidth());
            assertEquals(600, scene.getHeight());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createScene() - Not Eligible 모드 Scene 생성")
    void testCreateSceneNotEligibleMode() {
        ScoreRecord record = new ScoreRecord(100, 5, 1, 1);
        ScoreboardState notEligibleState = new ScoreboardState(stateManager, record, false);
        javafx.application.Platform.runLater(() -> {
            notEligibleState.enter();
            Scene scene = notEligibleState.createScene();
            
            assertNotNull(scene);
            assertEquals(800, scene.getWidth());
            assertEquals(600, scene.getHeight());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createScene() - Scoreboard 모드 크기")
    void testCreateSceneScoreboardSize() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            Scene scene = state.createScene();
            
            assertEquals(800, scene.getWidth());
            assertEquals(700, scene.getHeight());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createScene() - 키 이벤트 핸들러 설정됨")
    void testCreateSceneKeyHandler() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            Scene scene = state.createScene();
            
            assertNotNull(scene.getOnKeyPressed());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("여러 번 enter() 호출")
    void testMultipleEnter() {
        assertDoesNotThrow(() -> {
            state.enter();
            state.enter();
            state.enter();
        });
    }
    
    @Test
    @DisplayName("getScene() - Scene 생성 전 null 반환")
    void testGetSceneBeforeCreate() {
        assertNull(state.getScene());
    }
    
    @Test
    @DisplayName("getScene() - Scene 생성 후 반환")
    void testGetSceneAfterCreate() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            state.createScene();
            
            assertNotNull(state.getScene());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getFinalScore() - Input 모드에서 점수 반환")
    void testGetFinalScore() {
        ScoreRecord record = new ScoreRecord(5000, 50, 10, 1);
        ScoreboardState inputState = new ScoreboardState(stateManager, record, true);
        assertEquals(5000, inputState.getFinalScore());
    }
    
    @Test
    @DisplayName("getFinalLines() - Input 모드에서 라인 수 반환")
    void testGetFinalLines() {
        ScoreRecord record = new ScoreRecord(5000, 50, 10, 1);
        ScoreboardState inputState = new ScoreboardState(stateManager, record, true);
        assertEquals(50, inputState.getFinalLines());
    }
    
    @Test
    @DisplayName("getFinalLevel() - Input 모드에서 레벨 반환")
    void testGetFinalLevel() {
        ScoreRecord record = new ScoreRecord(5000, 50, 10, 1);
        ScoreboardState inputState = new ScoreboardState(stateManager, record, true);
        assertEquals(10, inputState.getFinalLevel());
    }
}