package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.example.view.MultiPlayModeView;

/**
 * MultiPlay Mode 선택 화면의 입력을 처리하는 Controller
 * 로컬 또는 온라인 멀티플레이 방식을 선택합니다.
 * 멀티플레이 모드 활성화 및 비활성화를 관리합니다.
 */
public class MultiPlayModeController extends BaseController {

    private final MultiPlayModeView view;

    public MultiPlayModeController() {
        this.view = new MultiPlayModeView();
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            this::handleLocal,
            this::handleOnline,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }

    /**
     * Local MultiPlay 선택 시 처리
     */
    public void handleLocal() {
        stackState(new LocalMultiSetupController());
    }

    /**
     * Online MultiPlay 선택 시 처리
     */
    public void handleOnline() {
        // TODO: 온라인 멀티플레이 구현 및 난이도 선택
        // 임시로 기본 설정(NORMAL, difficulty=1) 사용
        stackState(new MultiPlayController(org.example.model.GameMode.NORMAL, 1));
    }

    /**
     * Go Back 버튼 클릭 시 처리
     */
    public void handleGoBack() {
        popState();
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
