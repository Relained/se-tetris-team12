package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.model.ScoreRecord;
import org.example.service.StateManager;
import org.example.state.PlayState;
import org.example.state.ScoreboardState;
import org.example.state.StartState;
import org.example.view.GameOverView;

/**
 * GameOverState의 게임 오버 화면 처리를 담당하는 Controller
 */
public class GameOverController {
    
    private StateManager stateManager;
    private GameOverView gameOverView;
    private ScoreRecord record;
    
    public GameOverController(StateManager stateManager, GameOverView gameOverView, ScoreRecord record) {
        this.stateManager = stateManager;
        this.gameOverView = gameOverView;
        this.record = record;
    }

    /**
     * Play Again 버튼 클릭 시 처리 - 게임을 다시 시작
     */
    public void handlePlayAgain() {
        stateManager.setState(new PlayState(stateManager, record.getGameMode(), record.getDifficulty()));
    }
    
    /**
     * View Scoreboard 버튼 클릭 시 처리 - 스코어보드로 이동
     * 점수가 제출되었다면 하이라이트 표시 (record.isNewAndEligible() 참조)
     */
    public void handleViewScoreboard() {
        stateManager.stackState(new ScoreboardState(stateManager, record));
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 시작 화면으로 이동
     */
    public void handleMainMenu() {
        stateManager.setState(new StartState(stateManager));
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
