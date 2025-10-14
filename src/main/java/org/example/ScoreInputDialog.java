package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.ScoreManager;

public class ScoreInputDialog extends VBox {
    private TextField nameInput;
    private Button submitButton;
    private Button skipButton;
    private Text scoreText;
    private Text instructionText;
    private Text rankText;
    
    private Runnable onSubmit;
    private Runnable onSkip;
    
    public ScoreInputDialog(int score, int lines, int level) {
        initializeUI(score, lines, level);
        setupEventHandlers();
    }
    
    private void initializeUI(int score, int lines, int level) {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));
        setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
        setMaxWidth(500);
        setMaxHeight(450);
        
        // Title
        Text title = new Text("NEW HIGH SCORE!");
        title.setFill(Color.GOLD);
        title.setFont(Font.font("Arial", 28));
        
        // Rank information
        int rank = ScoreManager.getInstance().getScoreRank(score);
        rankText = new Text(String.format("Rank: #%d", rank));
        rankText.setFill(Color.YELLOW);
        rankText.setFont(Font.font("Arial", 20));
        
        // Score information
        scoreText = new Text(String.format("Score: %,d  |  Lines: %d  |  Level: %d", 
                                          score, lines, level));
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Arial", 16));
        
        // Instruction
        instructionText = new Text("Enter your name (max 3 characters):");
        instructionText.setFill(Color.LIGHTGRAY);
        instructionText.setFont(Font.font("Arial", 14));
        
        // Name input
        nameInput = new TextField();
        nameInput.setPromptText("ABC");
        nameInput.setMaxWidth(300);
        nameInput.setFont(Font.font("Arial", 14));
        nameInput.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        submitButton = new Button("Submit Score");
        submitButton.setFont(Font.font("Arial", 14));
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20;");
        submitButton.setDefaultButton(true);
        
        skipButton = new Button("Skip");
        skipButton.setFont(Font.font("Arial", 14));
        skipButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 20;");
        
        buttonBox.getChildren().addAll(submitButton, skipButton);
        
        getChildren().addAll(title, rankText, scoreText, instructionText, nameInput, buttonBox);
    }
    
    private void setupEventHandlers() {
        submitButton.setOnAction(e -> handleSubmit());
        skipButton.setOnAction(e -> handleSkip());
        
        nameInput.setOnAction(e -> handleSubmit()); // Enter key on text field
        
        // 3글자 제한 및 버튼 상태 관리
        nameInput.textProperty().addListener((obs, oldText, newText) -> {
            // 3글자를 초과하면 잘라내기
            if (newText.length() > 3) {
                nameInput.setText(newText.substring(0, 3));
                return;
            }
            
            // 버튼 활성화/비활성화
            submitButton.setDisable(newText.trim().isEmpty());
        });
        
        // Initial state
        submitButton.setDisable(true);
    }
    
    private void handleSubmit() {
        if (!nameInput.getText().trim().isEmpty() && onSubmit != null) {
            onSubmit.run();
        }
    }
    
    private void handleSkip() {
        if (onSkip != null) {
            onSkip.run();
        }
    }
    
    public String getPlayerName() {
        return nameInput.getText().trim();
    }
    
    public void setOnSubmit(Runnable onSubmit) {
        this.onSubmit = onSubmit;
    }
    
    public void setOnSkip(Runnable onSkip) {
        this.onSkip = onSkip;
    }
    
    public void focusNameInput() {
        nameInput.requestFocus();
    }
    
    public TextField getNameInput() {
        return nameInput;
    }
}