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
import org.example.service.ScoreManager;
import org.example.service.StateManager;
import org.example.view.ScoreInputView;
import org.example.view.ScoreboardView;
import org.example.view.component.NavigableButtonSystem;

/**
 * Scoreboard 관련 화면을 담당하는 State
 * 세 가지 모드를 지원합니다:
 * 1. INPUT - 점수 입력 모드
 * 2. NOT_ELIGIBLE - 상위 10개에 들지 못한 경우
 * 3. SCOREBOARD - 스코어보드 조회 모드
 */
public class ScoreboardState extends BaseState {
    
    // State modes
    public enum Mode {
        INPUT,           // Score input mode
        NOT_ELIGIBLE,    // Score not eligible for top 10
        SCOREBOARD       // Scoreboard viewing mode
    }
    
    private Mode currentMode;
    
    // Score input related fields
    private int finalScore;
    private int finalLines;
    private int finalLevel;
    private ScoreInputView scoreInputView;
    private ScoreInputController scoreInputController;
    
    // Scoreboard related fields
    private ScoreboardView scoreboardView;
    private ScoreboardController scoreboardController;
    private boolean showNewlyAddedHighlight;
    
    // Not eligible mode fields
    private NavigableButtonSystem notEligibleButtonSystem;

    // Constructor for score input mode (from game over)
    public ScoreboardState(StateManager stateManager, int score, int lines, int level) {
        super(stateManager);
        this.finalScore = score;
        this.finalLines = lines;
        this.finalLevel = level;
        this.showNewlyAddedHighlight = true;
        
        // Determine initial mode based on score eligibility
        if (ScoreManager.getInstance().isScoreEligibleForSaving(finalScore)) {
            this.currentMode = Mode.INPUT;
        } else {
            this.currentMode = Mode.NOT_ELIGIBLE;
        }
    }

    // Constructor for scoreboard viewing mode
    public ScoreboardState(StateManager stateManager, boolean showNewlyAddedHighlight) {
        super(stateManager);
        this.currentMode = Mode.SCOREBOARD;
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
    }

    // Constructor for scoreboard viewing mode (default highlight)
    public ScoreboardState(StateManager stateManager) {
        this(stateManager, true);
    }

    @Override
    public void enter() {
        // Mode별 초기화는 createScene에서 수행
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        if (currentMode == Mode.SCOREBOARD && scoreboardView != null) {
            scoreboardView.refresh();
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
        scoreInputController = new ScoreInputController(stateManager, scoreInputView, 
                                                       finalScore, finalLines, finalLevel);
        
        VBox inputBox = scoreInputView.createView(
            finalScore, 
            finalLines, 
            finalLevel,
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
        
        Text scoreText = new Text("Final Score: " + finalScore);
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setTextAlignment(TextAlignment.CENTER);
        
        notEligibleButtonSystem = new NavigableButtonSystem();
        var scoreboardButton = notEligibleButtonSystem.createNavigableButton("View Scoreboard", () -> {
            // Switch to scoreboard mode
            currentMode = Mode.SCOREBOARD;
            showNewlyAddedHighlight = false;
            Scene newScene = createScoreboardScene();
            stateManager.getPrimaryStage().setScene(newScene);
        });
        var gameOverButton = notEligibleButtonSystem.createNavigableButton("Continue", () -> {
            // Go to Game Over screen with score info
            GameOverState gameOverState = 
                new GameOverState(stateManager, finalScore, finalLines, finalLevel);
            stateManager.addState("gameOver", gameOverState);
            stateManager.setState("gameOver");
        });
        
        messageBox.getChildren().addAll(messageText, scoreText, scoreboardButton, gameOverButton);
        root.getChildren().add(messageBox);
        
        scene = new Scene(root, 800, 600);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                // Go to Game Over screen on ESC
                GameOverState gameOverState = 
                    new GameOverState(stateManager, finalScore, finalLines, finalLevel);
                stateManager.addState("gameOver", gameOverState);
                stateManager.setState("gameOver");
            } else {
                notEligibleButtonSystem.handleInput(event);
            }
        });
        
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
        
        return scene;
    }

    private Scene createScoreboardScene() {
        scoreboardView = new ScoreboardView(showNewlyAddedHighlight);
        scoreboardController = new ScoreboardController(stateManager, scoreboardView);
        
        BorderPane root = scoreboardView.createView(
            () -> scoreboardController.handleBackToMenu(),
            () -> scoreboardController.handleClearScores()
        );

        scene = new Scene(root, 800, 700);
        scene.setOnKeyPressed(event -> scoreboardController.handleKeyInput(event));

        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }

    // Getters for score information
    public int getFinalScore() {
        return finalScore;
    }

    public int getFinalLines() {
        return finalLines;
    }

    public int getFinalLevel() {
        return finalLevel;
    }

    public Mode getCurrentMode() {
        return currentMode;
    }
}
