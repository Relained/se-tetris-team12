package org.example.controller;

import javafx.scene.Scene;

import org.example.model.GameMode;

/**
 * Local Multiplayer 모드의 Pause 화면 Controller
 * PauseController를 상속하여 Restart 및 Main Menu 동작만 재정의합니다.
 */
public class LocalMultiPauseController extends PauseController {
    
    private GameMode gameMode;
    private int difficulty;
    
    public LocalMultiPauseController(GameMode gameMode, int difficulty) {
        super();
        this.gameMode = gameMode;
        this.difficulty = difficulty;
    }

    @Override
    protected Scene createScene() {
        return super.createScene();
    }
    
    @Override
    protected void resume() {
        // Resume 시 특별한 처리 없음 (부모 클래스의 resume 사용)
    }
    
    /**
     * Restart 버튼 클릭 시 처리 - LocalMultiPlay 모드로 재시작
     */
    @Override
    public void handleRestart() {
        setState(new LocalMultiPlayController(gameMode, difficulty));
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 메인 메뉴로 이동
     * 멀티플레이 모드를 비활성화합니다.
     */
    @Override
    public void handleMainMenu() {
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(false);
        setState(new StartController());
    }
    
    /**
     * Exit Game 버튼 클릭 시 처리 - 게임 종료
     * 멀티플레이 모드를 비활성화합니다.
     */
    @Override
    public void handleExit() {
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(false);
        System.exit(0);
    }
}
