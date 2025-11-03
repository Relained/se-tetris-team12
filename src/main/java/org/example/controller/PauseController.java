package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.service.StateManager;
import org.example.state.SettingState;
import org.example.state.StartState;
import org.example.view.PauseView;


/**
 * PauseState의 입력을 처리하는 Controller
 */
public class PauseController {
    
    private StateManager stateManager;
    private PauseView pauseView;
    private Runnable gamePlayResetCallback;
    
    public PauseController(StateManager stateManager, PauseView pauseView, Runnable gamePlayResetCallback) {
        this.stateManager = stateManager;
        this.pauseView = pauseView;
        this.gamePlayResetCallback = gamePlayResetCallback;
    }
    
    /**
     * Resume 버튼 클릭 시 처리 - 이전 상태(게임)로 복귀
     */
    public void handleResume() {
        stateManager.popState();
    }
    
    /**
     * Restart 버튼 클릭 시 처리 - 현재 게임 모드로 재시작
     */
    public void handleRestart() {
        gamePlayResetCallback.run();
        stateManager.popState();
    }
    
    /**
     * Settings 버튼 클릭 시 처리 - 설정 화면으로 이동
     */
    public void handleSettings() {
        stateManager.stackState(new SettingState(stateManager));
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 메인 메뉴로 이동
     */
    public void handleMainMenu() {
        stateManager.setState(new StartState(stateManager));
    }

    public void handleExit() {
        System.exit(0);
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        pauseView.getButtonSystem().handleInput(event);
    }
}
