package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import org.example.controller.SettingController;
import org.example.service.StateManager;
import org.example.view.SettingView;

/**
 * 설정 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class SettingState extends BaseState {
    
    private SettingView settingView;
    private SettingController controller;
    
    public SettingState(StateManager stateManager) {
        super(stateManager);
        settingView = new SettingView();
        controller = new SettingController(stateManager, settingView);
    }

    @Override
    public void exit() {
        // 설정창 종료 시 필요한 정리 작업 수행
    }

    @Override
    public void resume() {
        // 색상 설정 화면에서 돌아올 때 필요한 작업
    }

    @Override
    public Scene createScene() {
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = settingView.createView(
            () -> controller.handleScreenSize(),           // Screen Size 버튼
            () -> controller.handleControls(),             // Controls 버튼
            () -> controller.handleColorBlindSetting(),    // Color Blind Setting 버튼
            () -> controller.handleResetScoreBoard(),      // Reset Score Board 버튼
            () -> controller.handleResetAllSetting(),      // Reset All Setting 버튼
            () -> controller.handleGoBack()                // Go Back 버튼
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
