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
    private int finalScore;
    private int finalLines;
    private int finalLevel;
    private ScoreInputView scoreInputView;
    private ScoreInputController scoreInputController;

    // Scoreboard 관련 필드
    private ScoreboardView scoreboardView;
    private ScoreboardController scoreboardController;
    private boolean showNewlyAddedHighlight;
    private boolean isAfterGamePlay = false; // 게임 플레이 후인지 여부
    
    private int gameScore;
    private int gameLines;
    private int gameLevel;
    private boolean scoreWasSubmitted;
    
    // Not eligible mode fields
    private NavigableButtonSystem notEligibleButtonSystem;

    // Controller가 확인한 eligible 여부로 모드 결정
    public ScoreboardState(StateManager stateManager, int score, int lines, int level, boolean isEligible) {
        super(stateManager);
        this.finalScore = score;
        this.finalLines = lines;
        this.finalLevel = level;
        this.showNewlyAddedHighlight = true;
        
        if (isEligible) {
            this.currentMode = Mode.INPUT;
        } else {
            this.currentMode = Mode.NOT_ELIGIBLE;
        }
    }

    // scoreboard viewing mode constructor
    public ScoreboardState(StateManager stateManager, boolean showNewlyAddedHighlight) {
        super(stateManager);
        this.currentMode = Mode.SCOREBOARD;
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
        this.isAfterGamePlay = false;
    }
    
    // Constructor for scoreboard viewing mode after game play
    public ScoreboardState(StateManager stateManager, boolean showNewlyAddedHighlight, 
                          int score, int lines, int level, boolean scoreSubmitted) {
        super(stateManager);
        this.currentMode = Mode.SCOREBOARD;
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
        this.isAfterGamePlay = true;
        this.gameScore = score;
        this.gameLines = lines;
        this.gameLevel = level;
        this.scoreWasSubmitted = scoreSubmitted;
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
        scoreInputController = new ScoreInputController(stateManager, scoreInputView, 
                                                       finalScore, finalLines, finalLevel);
        
        VBox inputBox = scoreInputView.createView(
            scoreInputController.getRank(),  // Controller에서 rank를 계산해서 전달
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
        scoreboardView = new ScoreboardView(showNewlyAddedHighlight, isAfterGamePlay);
        
        // 게임 플레이 후인지에 따라 다른 생성자 사용
        if (isAfterGamePlay) {
            scoreboardController = new ScoreboardController(stateManager, scoreboardView,
                                                           gameScore, gameLines, gameLevel, scoreWasSubmitted);
        } else {
            scoreboardController = new ScoreboardController(stateManager, scoreboardView);
        }
        
        BorderPane root = scoreboardView.createView(
            () -> scoreboardController.handleBackToMenu(),
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
