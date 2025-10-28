package org.example.controller;

import org.example.service.StateManager;
import org.example.state.ScoreboardState;
import org.example.view.StartView;

import javafx.scene.input.KeyEvent;

/**
 * StartState의 입력을 처리하는 Controller
 */
public class StartController {
    
    private StateManager stateManager;
    private StartView startView;
    
    public StartController(StateManager stateManager, StartView startView) {
        this.stateManager = stateManager;
        this.startView = startView;
    }
    
    /**
     * Start Game 버튼 클릭 시 처리
     */
    public void handleStartGame() {
        // 게임 시작 전에 난이도 선택 화면으로 이동
        stateManager.stackState("difficulty");
    }
    
    /**
     * View Scoreboard 버튼 클릭 시 처리
     */
    public void handleViewScoreboard() {
        ScoreboardState scoreboardState = new ScoreboardState(stateManager, false);
        stateManager.addState("scoreboard", scoreboardState);
        stateManager.setState("scoreboard");
    }

    /**
     * Setting 버튼 클릭 시 처리
     */
    public void handleSetting() {
        stateManager.stackState("setting");
    }
    
    /**
     * Exit 버튼 클릭 시 처리
     */
    public void handleExit() {
        System.exit(0);
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        startView.getButtonSystem().handleInput(event);
    }
}

