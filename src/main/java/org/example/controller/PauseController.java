package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.service.StateManager;
import org.example.view.PauseView;

/**
 * PauseState의 입력을 처리하는 Controller
 */
public class PauseController {
    
    private StateManager stateManager;
    private PauseView pauseView;
    
    public PauseController(StateManager stateManager, PauseView pauseView) {
        this.stateManager = stateManager;
        this.pauseView = pauseView;
    }
    
    /**
     * Resume 버튼 클릭 시 처리 - 이전 상태(게임)로 복귀
     */
    public void handleResume() {
        stateManager.popState();
    }
    
    /**
     * Restart 버튼 클릭 시 처리 - 게임 재시작
     */
    public void handleRestart() {
        stateManager.setState("play");
    }
    
    /**
     * Settings 버튼 클릭 시 처리 - 설정 화면으로 이동
     */
    public void handleSettings() {
        stateManager.stackState("setting");
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 메인 메뉴로 이동
     */
    public void handleMainMenu() {
        stateManager.setState("start");
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        pauseView.getButtonSystem().handleInput(event);
    }
}
