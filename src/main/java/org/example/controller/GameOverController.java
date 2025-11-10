package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.model.ScoreRecord;
import org.example.view.GameOverView;

/**
 * GameOver 화면의 게임 오버 화면 처리를 담당하는 Controller
 */
public class GameOverController extends BaseController {
    
    private GameOverView gameOverView;
    private ScoreRecord record;
    
    public GameOverController(ScoreRecord record) {
        this.gameOverView = new GameOverView();
        this.record = record;
    }

    @Override
    protected Scene createScene() {
        var root = gameOverView.createView(
            record.getScore(),
            record.getLines(),
            record.getLevel(),
            this::handlePlayAgain,
            this::handleViewScoreboard,
            this::handleMainMenu,
            this::handleExit
        );
        createDefaultScene(root);
        return scene;
    }

    /**
     * Play Again 버튼 클릭 시 처리 - 게임을 다시 시작
     */
    public void handlePlayAgain() {
        setState(new PlayController(record.getGameMode(), record.getDifficulty()));
    }
    
    /**
     * View Scoreboard 버튼 클릭 시 처리 - 스코어보드로 이동
     * 점수가 제출되었다면 하이라이트 표시 (record.isNewAndEligible() 참조)
     */
    public void handleViewScoreboard() {
        stackState(new ScoreboardController(record));
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 시작 화면으로 이동
     */
    public void handleMainMenu() {
        setState(new StartController());
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
    @Override
    public void handleKeyInput(KeyEvent event) {
        gameOverView.getButtonSystem().handleInput(event);
    }
}
