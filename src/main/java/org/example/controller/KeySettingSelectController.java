package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import org.example.view.KeySettingSelectView;

/**
 * Key Setting Select 화면의 선택을 담당하는 Controller
 * 싱글플레이 또는 멀티플레이 키 설정으로 이동합니다.
 */
public class KeySettingSelectController extends BaseController {
    
    private KeySettingSelectView keySettingSelectView;
    
    public KeySettingSelectController() {
        this.keySettingSelectView = new KeySettingSelectView();
    }

    @Override
    protected Scene createScene() {
        VBox root = keySettingSelectView.createView(
            this::handleSinglePlayer,
            this::handleMultiPlayer,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }
    
    /**
     * 싱글플레이 키 설정 선택 시 처리
     */
    private void handleSinglePlayer() {
        stackState(new KeySettingController());
    }
    
    /**
     * 멀티플레이 키 설정 선택 시 처리
     */
    private void handleMultiPlayer() {
        stackState(new KeySettingController2());
    }
    
    /**
     * Go Back 버튼 클릭 시 처리 - 이전 화면으로 복귀
     */
    private void handleGoBack() {
        popState();
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    @Override
    protected void handleKeyInput(KeyEvent event) {
        keySettingSelectView.getNavigableButtonSystem().handleInput(event);
    }
}
