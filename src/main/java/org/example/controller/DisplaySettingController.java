package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.model.SettingData.ScreenSize;
import org.example.view.DisplaySettingView;

/**
 * DisplaySetting 화면의 화면 크기 설정 처리를 담당하는 Controller
 */
public class DisplaySettingController extends BaseController {
    
    private DisplaySettingView displaySettingView;
    private ScreenSize selectedSize;
    
    public DisplaySettingController() {
        this.displaySettingView = new DisplaySettingView();
        this.selectedSize = settingManager.getCurrentSettings().screenSize;
    }

    @Override
    protected Scene createScene() {
        var root = displaySettingView.createView(
            selectedSize,
            this::handleSmall,
            this::handleMedium,
            this::handleLarge,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }

    @Override
    public void exit() {
        // 설정 화면 종료 시 선택된 화면 크기를 저장
        settingManager.setScreenSize(selectedSize);
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
        popState();
    }
    
    /**
     * 화면 크기를 설정하고 뷰를 업데이트합니다.
     * @param size 설정할 화면 크기
     */
    private void setScreenSize(ScreenSize size) {
        selectedSize = size;
        displaySettingView.updateCurrentSize(size);
        
        // 설정을 SettingManager에 즉시 저장하고 DisplayManager를 통해 적용
        settingManager.setScreenSize(size);
        settingManager.applyScreenSize();
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
