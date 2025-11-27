package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.view.LocalGameOverView;

/**
 * Local Multiplayer 모드의 게임 오버 화면 처리를 담당하는 Controller
 */
public class LocalGameOverController extends BaseController {
    
    private LocalGameOverView localGameOverView;
    private String winner;
    private boolean isItemMode;
    private int difficulty;
    
    public LocalGameOverController(String winner, boolean isItemMode, int difficulty) {
        this.localGameOverView = new LocalGameOverView();
        this.winner = winner;
        this.isItemMode = isItemMode;
        this.difficulty = difficulty;
    }

    @Override
    protected Scene createScene() {
        var root = localGameOverView.createView(
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
    public void handlePlayAgain() {
        setState(new LocalMultiPlayController(isItemMode, difficulty));
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
        localGameOverView.getButtonSystem().handleInput(event);
    }
}
