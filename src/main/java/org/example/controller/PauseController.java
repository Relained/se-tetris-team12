package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.view.PauseView;

/**
 * Pause 화면의 입력을 처리하는 Controller
 */
public class PauseController extends BaseController {
    
    private PauseView pauseView;
    private Runnable gamePlayResetCallback;
    
    public PauseController(Runnable gamePlayResetCallback) {
        this.pauseView = new PauseView();
        this.gamePlayResetCallback = gamePlayResetCallback;
    }

    @Override
    protected Scene createScene() {
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

    /**
     * Resume 버튼 클릭 시 처리 - 이전 상태(게임)로 복귀
     */
    public void handleResume() {
        popState();
    }
    
    /**
     * Restart 버튼 클릭 시 처리 - 현재 게임 모드로 재시작
     */
    public void handleRestart() {
        gamePlayResetCallback.run();
        popState();
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
        setState(new StartController());
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
