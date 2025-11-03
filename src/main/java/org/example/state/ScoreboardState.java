package org.example.state;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.example.controller.ScoreInputController;
import org.example.controller.ScoreboardController;
import org.example.service.StateManager;
import org.example.view.ScoreInputView;
import org.example.view.ScoreboardView;
import org.example.view.component.NavigableButtonSystem;
import org.example.model.ScoreRecord;


/**
 * Scoreboard 관련 화면을 담당하는 State
 * 세 가지 모드를 지원:
 * 1. INPUT - 점수 입력 모드
 * 2. NOT_ELIGIBLE - 상위 10개에 들지 못한 경우
 * 3. SCOREBOARD - 스코어보드 조회 모드
 */
public class ScoreboardState extends BaseState {
    
    // State modes
    public enum Mode {
        INPUT,           // 점수 입력 모드
        NOT_ELIGIBLE,    // 상위 10개에 들지 못한 경우
        SCOREBOARD       // 스코어보드 보기 모드
    }
    
    private Mode currentMode;
    
    // Score 입력 관련 필드
    private ScoreRecord record;
    private ScoreInputView scoreInputView;
    private ScoreInputController scoreInputController;

    // Scoreboard 관련 필드
    private ScoreboardView scoreboardView;
    private ScoreboardController scoreboardController;
    private boolean isAfterGamePlay = false; // 게임 플레이 후인지 여부
    private boolean scoreWasSubmitted = false;
    
    // Not eligible mode fields
    private NavigableButtonSystem notEligibleButtonSystem;

    // 게임 끝나고 점수 입력 확인 절차 거치는 생성자
    public ScoreboardState(StateManager stateManager, ScoreRecord record, boolean isEligible) {
        super(stateManager);
        this.record = record;
        if (isEligible) {
            this.currentMode = Mode.INPUT;
        } else {
            this.currentMode = Mode.NOT_ELIGIBLE;
        }
    }

    // 단순히 스코어보드 보여주는 생성자
    public ScoreboardState(StateManager stateManager) {
        super(stateManager);
        this.currentMode = Mode.SCOREBOARD;
        this.isAfterGamePlay = false;
        this.scoreWasSubmitted = false;
    }

    // 점수 입력 절차 끝나고 스코어보드 화면으로 전환하는 메서드
    public void setScoreBoardScene(boolean scoreWasSubmitted) {
        this.currentMode = Mode.SCOREBOARD;
        this.isAfterGamePlay = true;
        this.scoreWasSubmitted = scoreWasSubmitted;
        var scoreboardScene = createScoreboardScene();
        stateManager.getPrimaryStage().setScene(scoreboardScene);
    }

    @Override
    public void exit() {
        isAfterGamePlay = false;
    }

    @Override
    public void resume() {
        if (currentMode == Mode.SCOREBOARD && scoreboardController != null) {
            scoreboardController.refreshScoreboard();
        }
    }

    @Override
    public Scene createScene() {
        switch (currentMode) {
            case INPUT:
                return createScoreInputScene();
            case NOT_ELIGIBLE:
                return createNotEligibleScene();
            case SCOREBOARD:
                return createScoreboardScene();
            default:
                throw new IllegalStateException("Unknown mode: " + currentMode);
        }
    }

    private Scene createScoreInputScene() {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));

        scoreInputView = new ScoreInputView();
        scoreInputController = new ScoreInputController(stateManager, scoreInputView, record);
        
        VBox inputBox = scoreInputView.createView(
            scoreInputController.getRank(),  // Controller에서 rank를 계산해서 전달
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

    private Scene createNotEligibleScene() {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));

        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);
        
        Text messageText = new Text("Your score didn't make it to the top 10.\nCheck out the current scoreboard!");
        messageText.setFill(Color.LIGHTGRAY);
        messageText.setFont(Font.font("Arial", 18));
        messageText.setTextAlignment(TextAlignment.CENTER);
        
        Text scoreText = new Text("Final Score: " + record.getScore());
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setTextAlignment(TextAlignment.CENTER);
        
        notEligibleButtonSystem = new NavigableButtonSystem();
        var gameOverButton = notEligibleButtonSystem.createNavigableButton("Continue", () -> {
            currentMode = Mode.SCOREBOARD;
            stateManager.setState("gameover");
        });
        
        messageBox.getChildren().addAll(messageText, scoreText, gameOverButton);
        root.getChildren().add(messageBox);
        
        scene = new Scene(root, 800, 600);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                stateManager.setState("gameover");
            } else {
                notEligibleButtonSystem.handleInput(event);
            }
        });
        
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
        
        return scene;
    }

    private Scene createScoreboardScene() {
        scoreboardView = new ScoreboardView(scoreWasSubmitted, isAfterGamePlay);
        scoreboardController = new ScoreboardController(stateManager, scoreboardView, isAfterGamePlay);
        
        BorderPane root = scoreboardView.createView(
            () -> scoreboardController.handleGoBack(),
            isAfterGamePlay ? null : () -> scoreboardController.handleClearScores()
        );
        
        // View 생성 후 데이터 로드
        scoreboardController.refreshScoreboard();

        scene = new Scene(root, 800, 700);
        scene.setOnKeyPressed(event -> scoreboardController.handleKeyInput(event));

        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }

    // Getters for score information
    public int getFinalScore() { return record.getScore(); }
    public int getFinalLines() { return record.getLines(); }
    public int getFinalLevel() { return record.getLevel(); }

    public Mode getCurrentMode() {
        return currentMode;
    }
}
