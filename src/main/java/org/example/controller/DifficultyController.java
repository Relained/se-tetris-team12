package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.service.StateManager;
import org.example.view.DifficultyView;

/**
 * Difficulty 화면의 입력을 처리하는 Controller
 */
public class DifficultyController {

    private int difficulty;
    private StateManager stateManager;
    private DifficultyView difficultyView;

    public DifficultyController(StateManager stateManager, DifficultyView difficultyView) {
        this.stateManager = stateManager;
        this.difficultyView = difficultyView;
        this.difficulty = 2;
    }

    /**
     * Easy 난이도 선택 처리
     * 실제 난이도 로직은 아직 구현되지 않았으므로 TODO로 표시하고 이전 화면으로 복귀합니다.
     */
    public void handleEasy() {
        difficulty = 1;
        stateManager.setState("play");
    }

    /**
     * Medium 난이도 선택 처리
     */
    public void handleMedium() {
        difficulty = 2;
        stateManager.setState("play");
    }

    /**
     * Hard 난이도 선택 처리
     */
    public void handleHard() {
        difficulty = 3;
        stateManager.setState("play");
    }

    /**
     * Go Back 버튼 클릭 시 처리 - 설정을 저장하거나 반영할 필요가 있으면 추가
     */
    public void handleGoBack() {
        // 필요시 설정 적용 또는 저장
        stateManager.popState();
    }

    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        difficultyView.getButtonSystem().handleInput(event);
    }

    public int getDifficulty() {
        return difficulty;
    }
}

