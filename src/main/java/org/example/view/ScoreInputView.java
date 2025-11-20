package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.example.service.FontManager;

/**
 * Score Input 화면의 UI를 담당하는 View 클래스
 */
public class ScoreInputView extends BaseView {
    
    // 기준 크기 (MEDIUM 기준)
    private static final double BASE_MAX_WIDTH = 500.0;
    private static final double BASE_MAX_HEIGHT = 450.0;
    private static final double BASE_INPUT_WIDTH = 300.0;
    
    private TextField nameInput;
    private Text title;
    private Text rankText;
    private Text scoreText;
    private Text instructionText;
    private Text keyHintText;
    private VBox root;

    public ScoreInputView() {
        super(false);
    }
    
    @Override
    protected void onScaleChanged(double scale) {
        // UI 요소가 아직 생성되지 않았으면 스킵
        if (title == null) {
            return;
        }
        
        // 스케일에 맞춰 폰트 크기 조정
        title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_MEDIUM * scale));
        rankText.setFont(fontManager.getFont(FontManager.SIZE_BODY_LARGE * scale));
        scoreText.setFont(fontManager.getFont(FontManager.SIZE_BODY_MEDIUM * scale));
        instructionText.setFont(fontManager.getFont(FontManager.SIZE_BODY_SMALL * scale));
        nameInput.setFont(fontManager.getFont(FontManager.SIZE_BODY_SMALL * scale));
        keyHintText.setFont(fontManager.getFont(FontManager.SIZE_CAPTION * scale));
        
        // 컨테이너 크기 조정
        root.setMaxWidth(BASE_MAX_WIDTH * scale);
        root.setMaxHeight(BASE_MAX_HEIGHT * scale);
        nameInput.setMaxWidth(BASE_INPUT_WIDTH * scale);
    }
    
    /**
     * Score Input 화면의 UI를 구성하고 반환합니다.
     * @param rank 달성한 순위
     * @param score 최종 점수
     * @param lines 클리어한 총 라인 수
     * @param level 도달한 레벨
     * @param onSubmit Submit 버튼 클릭 시 실행될 콜백
     * @param onSkip Skip 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(int rank, int score, int lines, int level, Runnable onSubmit, Runnable onSkip) {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.setPadding(new Insets(40));
        root.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
        root.setMaxWidth(BASE_MAX_WIDTH * currentScale);
        root.setMaxHeight(BASE_MAX_HEIGHT * currentScale);
        
        title = new Text("NEW HIGH SCORE!");
        title.setFill(Color.GOLD);
        title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_MEDIUM * currentScale));
        
        rankText = new Text(String.format("Rank: #%d", rank));
        rankText.setFill(Color.YELLOW);
        rankText.setFont(fontManager.getFont(FontManager.SIZE_BODY_LARGE * currentScale));
        
        scoreText = new Text(String.format("Score: %,d  |  Lines: %d  |  Level: %d", 
                                          score, lines, level));
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(fontManager.getFont(FontManager.SIZE_BODY_MEDIUM * currentScale));
        
        instructionText = new Text("Enter your name (max 3 characters):");
        instructionText.setFill(Color.LIGHTGRAY);
        instructionText.setFont(fontManager.getFont(FontManager.SIZE_BODY_SMALL * currentScale));
        
        nameInput = new TextField();
        nameInput.setPromptText("ABC");
        nameInput.setMaxWidth(BASE_INPUT_WIDTH * currentScale);
        nameInput.setFont(fontManager.getFont(FontManager.SIZE_BODY_SMALL * currentScale));
        nameInput.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        
        nameInput.textProperty().addListener((_, _, newText) -> {
            // 공백 입력 시 필터링
            String filteredText = newText.replace(" ", "");
            
            if (filteredText.length() > 3) {
                filteredText = filteredText.substring(0, 3);
            }
            
            // 필터링된 텍스트가 원본과 다르면 업데이트
            if (!filteredText.equals(newText)) {
                nameInput.setText(filteredText);
            }
        });
        
        nameInput.setOnAction(_ -> {
            if (!nameInput.getText().trim().isEmpty()) {
                onSubmit.run();
            }
        });
        
        // Key instructions
        keyHintText = new Text("Press ENTER to submit  |  Press ESC to cancel");
        keyHintText.setFill(Color.LIGHTGREEN);
        keyHintText.setFont(fontManager.getFont(FontManager.SIZE_CAPTION * currentScale));
        
        root.getChildren().addAll(title, rankText, scoreText, instructionText, nameInput, keyHintText);
        
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
