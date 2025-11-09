package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import org.example.model.SettingData.ColorBlindMode;
import org.example.view.ColorSettingView;

/**
 * ColorSetting 화면의 색상 설정 처리를 담당하는 Controller
 */
public class ColorSettingController extends BaseController {
    
    private ColorSettingView colorSettingView;
    private ColorBlindMode selectedMode;
    
    public ColorSettingController() {
        this.colorSettingView = new ColorSettingView();
        // 현재 설정된 색맹 모드를 가져옴
        this.selectedMode = settingManager.getCurrentSettings().colorBlindMode;
    }

    @Override
    protected Scene createScene() {
        VBox root = colorSettingView.createView(
            selectedMode,
            () -> handleDefault(),
            () -> handleProtanopia(),
            () -> handleDeuteranopia(),
            () -> handleTritanopia(),
            () -> handleGoBack()
        );

        scene = new Scene(root, 1000, 700);
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());
        scene.setOnKeyPressed(event -> handleKeyInput(event));
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
        return scene;
    }
    
    /**
     * Default 모드 선택 시 처리
     */
    public void handleDefault() {
        setColorMode(ColorBlindMode.Default);
    }
    
    /**
     * Protanopia 모드 선택 시 처리
     */
    public void handleProtanopia() {
        setColorMode(ColorBlindMode.PROTANOPIA);
    }
    
    /**
     * Deuteranopia 모드 선택 시 처리
     */
    public void handleDeuteranopia() {
        setColorMode(ColorBlindMode.DEUTERANOPIA);
    }
    
    /**
     * Tritanopia 모드 선택 시 처리
     */
    public void handleTritanopia() {
        setColorMode(ColorBlindMode.TRITANOPIA);
    }
    
    /**
     * Go Back 버튼 클릭 시 처리 - 이전 화면으로 복귀
     */
    public void handleGoBack() {
        popState();
    }
    
    /**
     * 색맹 모드를 설정하고 뷰를 업데이트합니다.
     * @param mode 설정할 색맹 모드
     */
    private void setColorMode(ColorBlindMode mode) {
        selectedMode = mode;
        colorSettingView.updateCurrentMode(mode);
    }
    
    /**
     * 선택된 색맹 모드를 반환합니다.
     * @return 현재 선택된 색맹 모드
     */
    public ColorBlindMode getSelectedMode() {
        return selectedMode;
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        colorSettingView.getButtonSystem().handleInput(event);
    }
}
