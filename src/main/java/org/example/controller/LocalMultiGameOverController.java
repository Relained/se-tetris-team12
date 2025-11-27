package org.example.controller;

import javafx.scene.Scene;

import org.example.view.GameOverView;

/**
 * Local Multiplayer 모드의 게임 오버 화면 처리를 담당하는 Controller
 * GameOverController를 상속받아 GameOverView를 재활용합니다.
 */
public class LocalMultiGameOverController extends GameOverController {
    
    private String winner;
    private boolean isItemMode;
    private int difficulty;
    
    public LocalMultiGameOverController(String winner, boolean isItemMode, int difficulty) {
        super(null); // 부모 생성자 호출 (record는 사용하지 않음)
        this.gameOverView = new GameOverView(); // 부모의 protected 필드 사용
        this.winner = winner;
        this.isItemMode = isItemMode;
        this.difficulty = difficulty;
    }

    @Override
    protected Scene createScene() {
        var root = gameOverView.createView(
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
        setState(new LocalMultiPlayController(isItemMode, difficulty));
    }
    // handleMainMenu, handleExit, handleKeyInput은 부모 클래스에서 상속받아 사용
}
