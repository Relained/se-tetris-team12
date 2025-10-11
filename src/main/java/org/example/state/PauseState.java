package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import org.example.controller.PauseController;
import org.example.service.StateManager;
import org.example.view.PauseView;

/**
 * 일시정지 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class PauseState extends State {
    
    private PauseView pauseView;
    private PauseController controller;
    
    public PauseState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // State 진입 시 View와 Controller 초기화
        pauseView = new PauseView();
        controller = new PauseController(stateManager, pauseView);
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        // 설정창에서 돌아올 때 색상이 변경되었을 수 있으므로 색상 갱신
        if (pauseView != null) {
            pauseView.refreshColors();
        }
    }

    @Override
    public Scene createScene() {
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = pauseView.createView(
            () -> controller.handleResume(),      // Resume 버튼
            () -> controller.handleRestart(),     // Restart 버튼
            () -> controller.handleSettings(),    // Settings 버튼
            () -> controller.handleMainMenu()     // Main Menu 버튼
        );

        scene = new Scene(root, 1000, 700);
        // Scene 레벨에서 배경색 설정하여 플리커링 방지
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());

        // 키보드 입력은 Controller를 통해 처리
        scene.setOnKeyPressed(event -> controller.handleKeyInput(event));

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }
}
