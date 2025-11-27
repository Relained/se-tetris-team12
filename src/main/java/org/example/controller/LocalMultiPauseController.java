package org.example.controller;

import javafx.scene.Scene;

/**
 * Local Multiplayer 모드의 Pause 화면 Controller
 * PauseController를 상속하여 Restart 및 Main Menu 동작만 재정의합니다.
 */
public class LocalMultiPauseController extends PauseController {
    
    private boolean isItemMode;
    private int difficulty;
    
    public LocalMultiPauseController(boolean isItemMode, int difficulty) {
        super();
        this.isItemMode = isItemMode;
        this.difficulty = difficulty;
    }

    @Override
    protected Scene createScene() {
        // Pause 화면도 멀티플레이 크기(2배)로 유지
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(true);
        return super.createScene();
    }
    
    @Override
    protected void resume() {
        // Resume 시 멀티플레이 모드 복원
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(true);
    }
    
    /**
     * Restart 버튼 클릭 시 처리 - LocalMultiPlay 모드로 재시작
     */
    @Override
    public void handleRestart() {
        setState(new LocalMultiPlayController(isItemMode, difficulty));
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 메인 메뉴로 이동
     */
    @Override
    public void handleMainMenu() {
        // 멀티플레이 모드 종료
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(false);
        setState(new StartController());
    }
}
