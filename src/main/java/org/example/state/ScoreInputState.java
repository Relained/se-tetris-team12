package org.example.state;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.controller.ScoreInputController;
import org.example.model.ScoreRecord;
import org.example.service.StateManager;
import org.example.view.ScoreInputView;

/**
 * 점수 입력 화면을 담당하는 State
 * 게임 종료 후 상위 10개에 든 경우 플레이어 이름을 입력받습니다.
 */
public class ScoreInputState extends BaseState {
    
    final private ScoreRecord record;
    private ScoreInputView scoreInputView;
    private ScoreInputController scoreInputController;

    public ScoreInputState(StateManager stateManager, ScoreRecord record) {
        super(stateManager);
        this.record = record;
        scoreInputView = new ScoreInputView();
        scoreInputController = new ScoreInputController(stateManager, scoreInputView, record);
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        // Not applicable for score input state
    }

    @Override
    public Scene createScene() {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));
        
        VBox inputBox = scoreInputView.createView(
            scoreInputController.getRank(),
            record.getScore(), 
            record.getLines(), 
            record.getLevel(),
            () -> scoreInputController.handleSubmit(),
            () -> scoreInputController.handleSkip()
        );

        root.getChildren().add(inputBox);

        scene = new Scene(root, 800, 600);
        scene.setOnKeyPressed(event -> scoreInputController.handleKeyInput(event));

        scoreInputView.focusNameInput();
        return scene;
    }
}
