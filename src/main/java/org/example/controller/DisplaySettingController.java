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
        
        // 설정을 SettingManager에 즉시 저장하고 DisplayManager를 통해 적용
        stateManager.settingManager.setScreenSize(size);
        stateManager.settingManager.applyScreenSize(stateManager.getPrimaryStage());
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
