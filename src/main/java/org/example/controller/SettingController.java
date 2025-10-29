package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.service.StateManager;
import org.example.view.SettingView;

/**
 * SettingState의 설정 처리를 담당하는 Controller
 */
public class SettingController {
    
    private StateManager stateManager;
    private SettingView settingView;
    
    public SettingController(StateManager stateManager, SettingView settingView) {
        this.stateManager = stateManager;
        this.settingView = settingView;
    }
    
    /**
     * Screen Size 버튼 클릭 시 처리 - 화면 크기 설정 화면으로 이동
     */
    public void handleScreenSize() {
        stateManager.stackState("display_setting");
    }
    
    /**
     * Controls 버튼 클릭 시 처리
     * 키 설정 화면으로 이동
     */
    public void handleControls() {
        stateManager.stackState("key_setting");
    }
    
    /**
     * Color Blind Setting 버튼 클릭 시 처리 - 색상 블라인드 설정 화면으로 이동
     */
    public void handleColorBlindSetting() {
        stateManager.stackState("color_setting");
    }
    
    /**
     * Reset Score Board 버튼 클릭 시 처리 - 스코어보드 초기화
     */
    public void handleResetScoreBoard() {
        stateManager.settingManager.resetScoreboard();
    }
    
    /**
     * Reset All Setting 버튼 클릭 시 처리 - 모든 설정을 기본값으로 초기화
     */
    public void handleResetAllSetting() {
        stateManager.settingManager.resetToDefault();
        stateManager.settingManager.applyColorSetting();
    }
    
    /**
     * Go Back 버튼 클릭 시 처리 - 설정을 저장하고 이전 화면으로 복귀
     */
    public void handleGoBack() {
        // 색상 설정 적용
        stateManager.settingManager.applyColorSetting();
        // 설정 데이터 저장
        stateManager.settingManager.saveSettingData();
        // 이전 상태로 복귀
        stateManager.popState();
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        settingView.getButtonSystem().handleInput(event);
    }
}
