package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.example.controller.KeySettingController;
import org.example.service.StateManager;
import org.example.view.KeySettingView;

/**
 * 키 설정 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class KeySettingState extends BaseState {
    
    private KeySettingView keySettingView;
    private KeySettingController controller;
    
    public KeySettingState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // State 진입 시 View와 Controller 초기화
        keySettingView = new KeySettingView();
        controller = new KeySettingController(stateManager, keySettingView);
    }

    @Override
    public void exit() {
        // 키 설정 화면 종료 시 필요한 작업
        // Controller에서 이미 저장 처리를 하므로 여기서는 특별한 작업 불필요
    }

    @Override
    public void resume() {
        // 다른 설정창에서 돌아올 때 필요한 작업
        // 키 설정은 이미 메모리에 로드되어 있으므로 특별한 작업 불필요
    }

    @Override
    public Scene createScene() {
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = keySettingView.createView(
            () -> controller.handleResetToDefault(),  // Reset to Default 버튼
            () -> controller.handleApply(),           // Apply 버튼
            () -> controller.handleGoBack()           // Go Back 버튼
        );

        scene = new Scene(root, 1000, 700);
        // Scene 레벨에서 배경색 설정하여 플리커링 방지
        scene.setFill(Color.BLACK);

        // 키보드 입력은 Controller를 통해 처리
        scene.setOnKeyPressed(event -> controller.handleKeyInput(event));

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }
}
