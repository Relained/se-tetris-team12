package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.model.SettingData.ScreenSize;
import org.example.service.StateManager;
import org.example.view.DisplaySettingView;

/**
 * DisplaySettingState의 화면 크기 설정 처리를 담당하는 Controller
 */
public class DisplaySettingController {
    
    private StateManager stateManager;
    private DisplaySettingView displaySettingView;
    private ScreenSize selectedSize;
    
    public DisplaySettingController(StateManager stateManager, DisplaySettingView displaySettingView) {
        this.stateManager = stateManager;
        this.displaySettingView = displaySettingView;
        // 현재 설정된 화면 크기를 가져옴
        this.selectedSize = stateManager.settingManager.getCurrentSettings().screenSize;
    }
    
    /**
     * Small 크기 선택 시 처리
     */
    public void handleSmall() {
        setScreenSize(ScreenSize.SMALL);
    }
    
    /**
     * Medium 크기 선택 시 처리
     */
    public void handleMedium() {
        setScreenSize(ScreenSize.MEDIUM);
    }
    
    /**
     * Large 크기 선택 시 처리
     */
    public void handleLarge() {
        setScreenSize(ScreenSize.LARGE);
    }
    
    /**
     * Go Back 버튼 클릭 시 처리 - 이전 화면으로 복귀
     */
    public void handleGoBack() {
        stateManager.popState();
    }
    
    /**
     * 화면 크기를 설정하고 뷰를 업데이트합니다.
     * @param size 설정할 화면 크기
     */
    private void setScreenSize(ScreenSize size) {
        selectedSize = size;
        displaySettingView.updateCurrentSize(size);
        
        // 설정을 SettingManager에 즉시 저장하고 적용
        stateManager.settingManager.setScreenSize(size);
        applyScreenSize(size);
    }
    
    /**
     * 화면 크기를 실시간으로 적용합니다.
     * @param size 적용할 화면 크기
     */
    private void applyScreenSize(ScreenSize size) {
        var stage = stateManager.getPrimaryStage();
        switch (size) {
            case SMALL:
                stage.setWidth(800);
                stage.setHeight(600);
                break;
            case MEDIUM:
                stage.setWidth(1000);
                stage.setHeight(700);
                break;
            case LARGE:
                stage.setWidth(1200);
                stage.setHeight(800);
                break;
        }
    }
    
    /**
     * 선택된 화면 크기를 반환합니다.
     * @return 현재 선택된 화면 크기
     */
    public ScreenSize getSelectedSize() {
        return selectedSize;
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        displaySettingView.getButtonSystem().handleInput(event);
    }
}
