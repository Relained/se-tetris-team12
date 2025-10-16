package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.service.ScoreManager;

/**
 * Score Input 화면의 UI를 담당하는 View 클래스
 */
public class ScoreInputView extends BaseView {
    
    private TextField nameInput;
    private Text rankText;
    
    public ScoreInputView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    /**
     * Score Input 화면의 UI를 구성하고 반환합니다.
     * @param score 최종 점수
     * @param lines 클리어한 총 라인 수
     * @param level 도달한 레벨
     * @param onSubmit Submit 버튼 클릭 시 실행될 콜백
     * @param onSkip Skip 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(int score, int lines, int level, Runnable onSubmit, Runnable onSkip) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.setPadding(new Insets(40));
        root.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
        root.setMaxWidth(500);
        root.setMaxHeight(450);
        
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
        Text scoreText = new Text(String.format("Score: %,d  |  Lines: %d  |  Level: %d", 
                                          score, lines, level));
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Arial", 16));
        
        // Instruction
        Text instructionText = new Text("Enter your name (max 3 characters):");
        instructionText.setFill(Color.LIGHTGRAY);
        instructionText.setFont(Font.font("Arial", 14));
        
        // Name input
        nameInput = new TextField();
        nameInput.setPromptText("ABC");
        nameInput.setMaxWidth(300);
        nameInput.setFont(Font.font("Arial", 14));
        nameInput.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        
        // 3글자 제한
        nameInput.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 3) {
                nameInput.setText(newText.substring(0, 3));
            }
        });
        
        // Enter key on text field
        nameInput.setOnAction(e -> {
            if (!nameInput.getText().trim().isEmpty()) {
                onSubmit.run();
            }
        });
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        var submitButton = buttonSystem.createNavigableButton("Submit Score", onSubmit);
        var skipButton = buttonSystem.createNavigableButton("Skip", onSkip);
        
        // 버튼 스타일 설정
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20;");
        skipButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 20;");
        
        // 빈 입력 시 버튼 비활성화
        submitButton.setDisable(true);
        nameInput.textProperty().addListener((obs, oldText, newText) -> {
            submitButton.setDisable(newText.trim().isEmpty());
        });
        
        buttonBox.getChildren().addAll(submitButton, skipButton);
        
        root.getChildren().addAll(title, rankText, scoreText, instructionText, nameInput, buttonBox);
        
        return root;
    }
    
    /**
     * 입력된 플레이어 이름을 반환합니다.
     * @return 플레이어 이름
     */
    public String getPlayerName() {
        return nameInput != null ? nameInput.getText().trim() : "";
    }
    
    /**
     * 이름 입력 필드에 포커스를 맞춥니다.
     */
    public void focusNameInput() {
        if (nameInput != null) {
            nameInput.requestFocus();
        }
    }
    
    /**
     * TextField를 반환합니다 (테스트용).
     * @return TextField 인스턴스
     */
    public TextField getNameInput() {
        return nameInput;
    }
}
