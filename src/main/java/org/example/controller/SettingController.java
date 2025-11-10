package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import org.example.view.SettingView;

/**
 * Setting 화면의 설정 처리를 담당하는 Controller
 */
public class SettingController extends BaseController {
    
    private SettingView settingView;
    
    public SettingController() {
        this.settingView = new SettingView();
    }

    @Override
    protected Scene createScene() {
        VBox root = settingView.createView(
            this::handleScreenSize,
            this::handleControls,
            this::handleColorBlindSetting,
            this::handleResetScoreBoard,
            this::handleResetAllSetting,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }

    /**
     * Screen Size 버튼 클릭 시 처리 - 화면 크기 설정 화면으로 이동
     */
    public void handleScreenSize() {
        stackState(new DisplaySettingController());
    }
    
    /**
     * Controls 버튼 클릭 시 처리
     * 키 설정 화면으로 이동
     */
    public void handleControls() {
        stackState(new KeySettingController());
    }
    
    /**
     * Color Blind Setting 버튼 클릭 시 처리 - 색상 블라인드 설정 화면으로 이동
     */
    public void handleColorBlindSetting() {
        stackState(new ColorSettingController());
    }
    
    /**
     * Reset Score Board 버튼 클릭 시 처리 - 스코어보드 초기화
     */
    public void handleResetScoreBoard() {
        settingManager.resetScoreboard();
    }
    
    /**
     * Reset All Setting 버튼 클릭 시 처리 - 모든 설정을 기본값으로 초기화
     */
    public void handleResetAllSetting() {
        settingManager.resetToDefault();
        settingManager.applyColorSetting();
    }
    
    /**
     * Go Back 버튼 클릭 시 처리 - 설정을 저장하고 이전 화면으로 복귀
     */
    public void handleGoBack() {
        // 색상 설정 적용
        settingManager.applyColorSetting();
        // 설정 데이터 저장
        settingManager.saveSettingData();
        // 이전 상태로 복귀
        popState();
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    @Override
    public void handleKeyInput(KeyEvent event) {
        settingView.getButtonSystem().handleInput(event);
    }
}
