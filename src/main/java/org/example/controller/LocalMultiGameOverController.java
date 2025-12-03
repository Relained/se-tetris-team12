package org.example.controller;

import javafx.scene.Scene;

import org.example.model.GameMode;
import org.example.view.LocalMultiGameOverView;

/**
 * Local Multiplayer 모드의 게임 오버 화면 처리를 담당하는 Controller
 * GameOverController를 상속받아 LocalMultiGameOverView를 사용합니다.
 */
public class LocalMultiGameOverController extends GameOverController {
    
    private LocalMultiGameOverView localMultiGameOverView;
    private String winner;
    private GameMode gameMode;
    private int difficulty;
    
    public LocalMultiGameOverController(String winner, GameMode gameMode, int difficulty) {
        super(null); // 부모 생성자 호출 (record는 사용하지 않음)
        this.localMultiGameOverView = new LocalMultiGameOverView();
        this.gameOverView = this.localMultiGameOverView; // 부모의 protected 필드도 설정
        this.winner = winner;
        this.gameMode = gameMode;
        this.difficulty = difficulty;
    }

    @Override
    protected Scene createScene() {
        var root = localMultiGameOverView.createView(
            winner,
            this::handlePlayAgain,
            this::handleMainMenu,
            this::handleExit
        );
        createDefaultScene(root);
        return scene;
    }

    /**
     * Play Again 버튼 클릭 시 처리 - 같은 모드와 난이도로 게임을 다시 시작
     */
    @Override
    public void handlePlayAgain() {
        setState(new LocalMultiPlayController(gameMode, difficulty));
    }
    
    /**
     * Main Menu 버튼 클릭 시 처리 - 시작 화면으로 이동
     * 멀티플레이 모드를 비활성화합니다.
     */
    @Override
    public void handleMainMenu() {
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(false);
        setState(new StartController());
    }
    
    /**
     * Exit Game 버튼 클릭 시 처리 - 게임 종료
     * 멀티플레이 모드를 비활성화합니다.
     */
    @Override
    public void handleExit() {
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(false);
        System.exit(0);
    }
    // handleKeyInput은 부모 클래스에서 상속받아 사용
}
