package org.example.controller;

import javafx.scene.Scene;

import org.example.view.LocalMultiGameOverView;

/**
 * Local Multiplayer 모드의 게임 오버 화면 처리를 담당하는 Controller
 * GameOverController를 상속받아 LocalMultiGameOverView를 사용합니다.
 */
public class LocalMultiGameOverController extends GameOverController {
    
    private LocalMultiGameOverView localMultiGameOverView;
    private String winner;
    private boolean isItemMode;
    private int difficulty;
    
    public LocalMultiGameOverController(String winner, boolean isItemMode, int difficulty) {
        super(null); // 부모 생성자 호출 (record는 사용하지 않음)
        this.localMultiGameOverView = new LocalMultiGameOverView();
        this.gameOverView = this.localMultiGameOverView; // 부모의 protected 필드도 설정
        this.winner = winner;
        this.isItemMode = isItemMode;
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
        setState(new LocalMultiPlayController(isItemMode, difficulty));
    }
    // handleMainMenu, handleExit, handleKeyInput은 부모 클래스에서 상속받아 사용
}
