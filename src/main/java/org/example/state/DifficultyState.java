package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import org.example.controller.DifficultyController;
import org.example.model.GameMode;
import org.example.service.StateManager;
import org.example.view.DifficultyView;

/**
 * 난이도 선택 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class DifficultyState extends BaseState {

    private DifficultyView difficultyView;
    private DifficultyController controller;
    private GameMode gameMode;

    public DifficultyState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // State 진입 시 View와 Controller 초기화
        difficultyView = new DifficultyView();
        controller = new DifficultyController(stateManager, difficultyView);

        BaseState previousState = stateManager.getCurrentState();
        if (previousState instanceof GameModeState gameModeState) {
            gameMode = gameModeState.getGameMode();
        }
    }

    @Override
    public void exit() {
        // 필요 시 정리 작업
    }

    @Override
    public void resume() {
        // 다른 설정 화면에서 돌아올 때 필요한 작업이 있다면 여기에 구현
    }

    @Override
    public Scene createScene() {
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = difficultyView.createView(
            () -> controller.handleEasy(),    // Easy 버튼
            () -> controller.handleMedium(),  // Medium 버튼
            () -> controller.handleHard(),    // Hard 버튼
            () -> controller.handleGoBack()   // Go Back 버튼
        );

        scene = new Scene(root, 1000, 700);
        // Scene 레벨에서 배경색 설정하여 플리커링 방지
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());

        // 키보드 입력은 Controller를 통해 처리
        scene.setOnKeyPressed(event -> controller.handleKeyInput(event));

        // 포커스 설정 (키 입력을 위해)
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }

    public int getDifficulty() {
        return controller.getDifficulty();
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}
