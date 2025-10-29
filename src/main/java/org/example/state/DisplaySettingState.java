package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.example.controller.DisplaySettingController;
import org.example.model.SettingData.ScreenSize;
import org.example.service.StateManager;
import org.example.view.DisplaySettingView;

/**
 * 화면 크기 설정 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class DisplaySettingState extends BaseState {

    private DisplaySettingView displaySettingView;
    private DisplaySettingController controller;

    public DisplaySettingState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // State 진입 시 View와 Controller 초기화
        displaySettingView = new DisplaySettingView();
        controller = new DisplaySettingController(stateManager, displaySettingView);
    }
    
    @Override
    public void exit() {
        // 설정 화면 종료 시 선택된 화면 크기를 저장
        stateManager.settingManager.setScreenSize(controller.getSelectedSize());
    }

    @Override
    public void resume() {
        // 설정창에서 돌아올 때 필요한 작업
    }

    @Override
    public Scene createScene() {
        // 현재 설정된 화면 크기 가져오기
        ScreenSize currentSize = stateManager.settingManager.getCurrentSettings().screenSize;
        
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = displaySettingView.createView(
            currentSize,
            () -> controller.handleSmall(),      // Small 버튼
            () -> controller.handleMedium(),     // Medium 버튼
            () -> controller.handleLarge(),      // Large 버튼
            () -> controller.handleGoBack()      // Go Back 버튼
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
