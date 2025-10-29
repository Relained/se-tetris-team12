package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.service.StateManager;
import org.example.state.ScoreboardState;
import org.example.view.GameOverView;

/**
 * GameOverState의 게임 오버 화면 처리를 담당하는 Controller
 */
public class GameOverController {
    
    private StateManager stateManager;
    private GameOverView gameOverView;
    private boolean scoreWasSubmitted; // 점수 제출 여부
    
    public GameOverController(StateManager stateManager, GameOverView gameOverView) {
        this(stateManager, gameOverView, false);
    }
    
    public GameOverController(StateManager stateManager, GameOverView gameOverView, boolean scoreWasSubmitted) {
        this.stateManager = stateManager;
        this.gameOverView = gameOverView;
        this.scoreWasSubmitted = scoreWasSubmitted;
    }
    
    /**
     * Play Again 버튼 클릭 시 처리 - 게임을 다시 시작
     */
    public void handlePlayAgain() {
        stateManager.setState("play");
    }
    
    /**
     * View Scoreboard 버튼 클릭 시 처리 - 스코어보드로 이동
     * 점수가 제출되었다면 하이라이트 표시
     */
    public void handleViewScoreboard() {
        // 점수가 제출되었으면 하이라이트 활성화, 아니면 비활성화
        ScoreboardState scoreboardState = new ScoreboardState(stateManager, scoreWasSubmitted);
        stateManager.addState("scoreboard", scoreboardState);
        stateManager.setState("scoreboard");
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 시작 화면으로 이동
     */
    public void handleMainMenu() {
        stateManager.setState("start");
    }
    
    /**
     * Exit Game 버튼 클릭 시 처리 - 게임 종료
     */
    public void handleExit() {
        System.exit(0);
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        gameOverView.getButtonSystem().handleInput(event);
    }
}
