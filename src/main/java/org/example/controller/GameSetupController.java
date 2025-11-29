package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.model.GameMode;
import org.example.view.GameSetupView;

/**
 * 게임 모드와 난이도를 함께 선택하는 통합 Controller
 * 
 * 레이아웃:
 * 1. 게임 모드 라디오 그룹 (Normal / Item) - 좌우 키로 선택
 * 2. 난이도 라디오 그룹 (Easy / Medium / Hard) - 좌우 키로 선택
 * 3. Start 버튼 - Enter로 게임 시작
 * 4. Go Back 버튼 - Enter로 뒤로가기
 * 
 * 상하 키로 라디오 그룹 및 버튼 간 이동, 좌우 키로 라디오 옵션 또는 버튼 선택
 */
public class GameSetupController extends BaseController {

    protected final GameSetupView view;
    protected boolean isRadioGroupFocused = true; // 초기: 라디오 그룹에 포커스

    public GameSetupController() {
        this.view = new GameSetupView();
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            getTitle(),
            this::handleStart,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }
    
    /**
     * 화면 제목을 반환합니다. 서브클래스에서 오버라이드하여 변경 가능.
     */
    protected String getTitle() {
        return "Game Setup";
    }

    /**
     * Start 버튼 클릭 시 처리
     * 선택된 게임 모드와 난이도로 게임 시작
     */
    public void handleStart() {
        GameMode selectedMode = view.getSelectedGameMode();
        int selectedDifficulty = view.getSelectedDifficulty();
        setState(new PlayController(selectedMode, selectedDifficulty));
    }

    /**
     * Go Back 버튼 클릭 시 처리
     */
    public void handleGoBack() {
        popState();
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
            case DOWN:
                // 상하 키: 라디오 그룹과 버튼 간 전환
                handleVerticalNavigation(event);
                break;
            case LEFT:
            case RIGHT:
                // 좌우 키: 포커스 위치에 따라 처리
                if (isRadioGroupFocused) {
                    view.getRadioButtonSystem().handleInput(event);
                } else {
                    view.getButtonSystem().handleInput(event);
                }
                break;
            case ENTER:
            case SPACE:
                // Enter/Space: 버튼에서만 실행
                if (!isRadioGroupFocused) {
                    view.getButtonSystem().handleInput(event);
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 상하 네비게이션 처리 (라디오 그룹 ↔ 버튼)
     * 
     * 상하 관계:
     * - 라디오 그룹 시스템 (상단): 첫 번째 그룹에서 UP 시 순환하지 않음
     * - 버튼 시스템 (하단): 마지막 버튼에서 DOWN 시 순환하지 않음
     */
    private void handleVerticalNavigation(KeyEvent event) {
        if (isRadioGroupFocused) {
            // 라디오 그룹에서의 네비게이션
            int groupIndex = view.getRadioButtonSystem().getFocusedGroupIndex();
            int totalGroups = view.getRadioButtonSystem().getGroupCount();
            
            if (event.getCode() == javafx.scene.input.KeyCode.DOWN && 
                groupIndex == totalGroups - 1) {
                // 마지막 라디오 그룹 → 버튼으로 이동
                isRadioGroupFocused = false;
                view.getRadioButtonSystem().unfocusAll();
                view.getButtonSystem().focusButton(0);
            } else if (event.getCode() == javafx.scene.input.KeyCode.UP && 
                       groupIndex == 0) {
                // 첫 번째 라디오 그룹에서 위로 → 아무것도 하지 않음 (순환 방지)
                // 상단 시스템이므로 위로 이동 불가
            } else {
                // 라디오 그룹 내에서 이동
                view.getRadioButtonSystem().handleInput(event);
            }
        } else {
            // 버튼에서의 네비게이션
            int buttonIndex = view.getButtonSystem().getSelectedIndex();
            int buttonCount = view.getButtonSystem().getButtonCount();
            
            if (event.getCode() == javafx.scene.input.KeyCode.UP && buttonIndex == 0) {
                // 첫 번째 버튼에서 위로 → 라디오 그룹의 마지막으로 이동
                isRadioGroupFocused = true;
                view.getButtonSystem().unfocusAll();
                view.getRadioButtonSystem().focusLast();
            } else if (event.getCode() == javafx.scene.input.KeyCode.DOWN && 
                       buttonIndex == buttonCount - 1) {
                // 마지막 버튼에서 아래로 → 아무것도 하지 않음 (순환 방지)
                // 하단 시스템이므로 아래로 이동 불가
            } else {
                // 버튼 내에서 이동
                view.getButtonSystem().handleInput(event);
            }
        }
    }
}

