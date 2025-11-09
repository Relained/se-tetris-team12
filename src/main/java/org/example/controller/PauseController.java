package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

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
        VBox root = pauseView.createView(
            () -> handleResume(),      // Resume 버튼
            () -> handleRestart(),     // Restart 버튼
            () -> handleSettings(),    // Settings 버튼
            () -> handleMainMenu(),    // Main Menu 버튼
            () -> handleExit()         // Exit 버튼
        );

        scene = new Scene(root, 1000, 700);
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());
        scene.setOnKeyPressed(event -> handleKeyInput(event));
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
        return scene;
    }

    @Override
    protected void resume() {
        // 설정창에서 돌아올 때 색상이 변경되었을 수 있으므로 색상 갱신
        if (pauseView != null) {
            pauseView.refreshColors();
        }
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
