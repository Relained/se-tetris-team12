package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.example.controller.ColorSettingController;
import org.example.model.SettingData.ColorBlindMode;
import org.example.service.StateManager;
import org.example.view.ColorSettingView;

/**
 * 색상 설정 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class ColorSettingState extends State {
    
    private ColorSettingView colorSettingView;
    private ColorSettingController controller;
    
    public ColorSettingState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // State 진입 시 View와 Controller 초기화
        colorSettingView = new ColorSettingView();
        controller = new ColorSettingController(stateManager, colorSettingView);
    }

    @Override
    public void exit() {
        // 설정 화면 종료 시 선택된 색맹 모드를 저장
        stateManager.settingManager.setColorSetting(controller.getSelectedMode());
    }

    @Override
    public void resume() {
        // 설정창에서 돌아올 때 필요한 작업
    }

    @Override
    public Scene createScene() {
        // 현재 설정된 색맹 모드 가져오기
        ColorBlindMode currentMode = stateManager.settingManager.getCurrentSettings().colorBlindMode;
        
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = colorSettingView.createView(
            currentMode,
            () -> controller.handleDefault(),        // Default 버튼
            () -> controller.handleProtanopia(),     // PROTANOPIA 버튼
            () -> controller.handleDeuteranopia(),   // DEUTERANOPIA 버튼
            () -> controller.handleTritanopia(),     // TRITANOPIA 버튼
            () -> controller.handleGoBack()          // Go Back 버튼
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
