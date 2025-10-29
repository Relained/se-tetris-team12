package org.example.state;

import org.example.controller.StartController;
import org.example.service.StateManager;
import org.example.view.StartView;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

/**
 * 시작 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class StartState extends BaseState {
    
    private StartView startView;
    private StartController controller;
    
    public StartState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // State 진입 시 View와 Controller 초기화
        startView = new StartView();
        controller = new StartController(stateManager, startView);
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {

    }

    @Override
    public Scene createScene() {
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = startView.createView(
            () -> controller.handleStartGame(),       // Start Game 버튼 콜백
            () -> controller.handleViewScoreboard(),  // View Scoreboard 버튼 콜백
            () -> controller.handleSetting(),         // Setting 버튼 콜백
            () -> controller.handleExit()             // Exit 버튼 콜백
        );

        scene = new Scene(root, 1000, 700);
        // Scene 레벨에서 배경색 설정하여 플리커링 방지
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());

        // 키보드 입력은 Controller를 통해 처리
        scene.setOnKeyPressed(event -> controller.handleKeyInput(event));

        return scene;
    }
}
