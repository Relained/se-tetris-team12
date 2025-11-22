package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.view.PauseView;

/**
 * Local Multiplayer 모드의 Pause 화면 Controller
 * Restart 시 LocalMultiPlayController로 돌아갑니다.
 */
public class LocalMultiPauseController extends BaseController {
    
    private PauseView pauseView;
    private boolean isItemMode;
    private int difficulty;
    
    public LocalMultiPauseController(boolean isItemMode, int difficulty) {
        this.pauseView = new PauseView();
        this.isItemMode = isItemMode;
        this.difficulty = difficulty;
    }

    @Override
    protected Scene createScene() {
        // Pause 화면도 멀티플레이 크기(2배)로 유지
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(true);
        
        var root = pauseView.createView(
            this::handleResume,      // Resume 버튼
            this::handleRestart,     // Restart 버튼
            this::handleSettings,    // Settings 버튼
            this::handleMainMenu,    // Main Menu 버튼
            this::handleExit         // Exit 버튼
        );
        createDefaultScene(root);
        return scene;
    }
    
    @Override
    protected void resume() {
        // Resume 시 멀티플레이 모드 복원
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(true);
    }

    /**
     * Resume 버튼 클릭 시 처리 - 이전 상태(게임)로 복귀
     */
    public void handleResume() {
        popState();
    }
    
    /**
     * Restart 버튼 클릭 시 처리 - LocalMultiPlay 모드로 재시작
     */
    public void handleRestart() {
        setState(new LocalMultiPlayController(isItemMode, difficulty));
    }
    
    /**
     * Settings 버튼 클릭 시 처리 - 설정 화면으로 이동
     */
    public void handleSettings() {
        stackState(new SettingController());
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 메인 메뉴로 이동
     */
    public void handleMainMenu() {
        // 멀티플레이 모드 종료
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(false);
        setState(new StartController());
    }

    public void handleExit() {
        System.exit(0);
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    @Override
    public void handleKeyInput(KeyEvent event) {
        pauseView.getButtonSystem().handleInput(event);
    }
}
