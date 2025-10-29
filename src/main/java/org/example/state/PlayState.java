package org.example.state;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

import org.example.controller.PlayController;
import org.example.service.StateManager;
import org.example.service.TetrisSystem;
import org.example.view.PlayView;

/**
 * 게임 플레이 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class PlayState extends BaseState {
    
    private PlayView playView;
    private PlayController controller;
    private TetrisSystem gameLogic;
    private AnimationTimer gameTimer;

    public PlayState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // State 진입 시 게임 로직, View, Controller 초기화
        gameLogic = new TetrisSystem();
        playView = new PlayView();
        controller = new PlayController(stateManager, playView, gameLogic);

        BaseState previousState = stateManager.getCurrentState();
        if (previousState instanceof DifficultyState playState) {
            gameLogic.setDifficulty(playState.getDifficulty());
        }

        // 게임 루프 시작
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                controller.update(now / 1_000_000_000.0);
            }
        };
        gameTimer.start();
    }

    @Override
    public void exit() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    @Override
    public void resume() {
        if (gameTimer != null) {
            gameTimer.start();
        }
    }

    @Override
    public Scene createScene() {
        // View로부터 UI 구성 요소를 받아옴
        HBox root = playView.createView();

        scene = new Scene(root, 1000, 700);
        // Scene 레벨에서 배경색 설정하여 플리커링 방지
        scene.setFill(org.example.service.ColorManager.getInstance().getGameBackgroundColor());

        // 높이와 너비 변경 시 캔버스 크기 비율에 맞게 자동 조정
        scene.heightProperty().addListener((_, _, _) -> playView.updateCanvasSize(scene));
        scene.widthProperty().addListener((_, _, _) -> playView.updateCanvasSize(scene));
        
        // 초기 캔버스 크기 설정
        playView.updateCanvasSize(scene);

        // 키보드 입력은 Controller를 통해 처리
        scene.setOnKeyPressed(event -> controller.handleKeyPressed(event.getCode()));
        scene.setOnKeyReleased(event -> controller.handleKeyReleased(event.getCode()));

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }

    /**
     * GameOverState에서 최종 점수를 가져오기 위한 메서드
     */
    public TetrisSystem getGameLogic() {
        return gameLogic;
    }
}
