package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.example.model.ScoreRecord;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Scoreboard 화면의 UI를 담당하는 View 클래스
 */
public class ScoreboardView extends BaseView {
    private static final int MAX_DISPLAY_SCORES = 10;
    private VBox scoresContainer;
    private Text titleLabel;
    private boolean showNewlyAddedHighlight;
    private boolean isAfterGamePlay = false; // 게임 플레이 후인지 여부

    public ScoreboardView(boolean showNewlyAddedHighlight) {
        super(true);
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
    }
    
    public ScoreboardView(boolean showNewlyAddedHighlight, boolean isAfterGamePlay) {
        super(true);
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
        this.isAfterGamePlay = isAfterGamePlay;
    }

    /**
     * Scoreboard 화면의 UI를 구성하고 반환합니다.
     * @param onBackToMenu Back to Menu 버튼 클릭 시 실행될 콜백
     * @param onClearScores Clear Scores 버튼 클릭 시 실행될 콜백 (게임 플레이 후에는 null 가능)
     * @return 구성된 BorderPane root
     */
    public BorderPane createView(Runnable onBackToMenu, Runnable onClearScores) {
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        // Scoreboard content를 포함하는 컨테이너
        VBox topContainer = new VBox();
        topContainer.setAlignment(Pos.TOP_CENTER);
        topContainer.setPadding(new Insets(40, 0, 0, 0));
        
        VBox scoreboardContent = createScoreboardContent();
        topContainer.getChildren().add(scoreboardContent);
        
        root.setTop(topContainer);

        VBox buttonPanel = createButtonPanel(onBackToMenu, onClearScores, isAfterGamePlay);
        root.setBottom(buttonPanel);

        return root;
    }

    private VBox createScoreboardContent() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(15);
        container.setPadding(new Insets(20));
        container.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, null, null)));
        container.setMaxWidth(550);
        container.setPrefWidth(550);
        container.setMinHeight(550);

        titleLabel = new Text("HIGH SCORES");
        titleLabel.setFill(Color.GOLD);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        HBox headerBox = createHeaderRow();

        scoresContainer = new VBox(8);
        scoresContainer.setAlignment(Pos.TOP_CENTER);
        scoresContainer.setMinHeight(300);

        Text noScoresLabel = new Text("Loading...");
        noScoresLabel.setFill(Color.LIGHTGRAY);
        noScoresLabel.setFont(Font.font("Arial", 16));
        scoresContainer.getChildren().add(noScoresLabel);

        container.getChildren().addAll(titleLabel, headerBox, scoresContainer);
        return container;
    }

    private HBox createHeaderRow() {
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));
        headerBox.setMaxWidth(500);

        Text rankHeader = new Text("RANK");
        rankHeader.setFill(Color.WHITE);
        rankHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Text nameHeader = new Text("NAME");
        nameHeader.setFill(Color.WHITE);
        nameHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Text scoreHeader = new Text("SCORE");
        scoreHeader.setFill(Color.WHITE);
        scoreHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Text levelHeader = new Text("LEVEL");
        levelHeader.setFill(Color.WHITE);
        levelHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Text diffHeader = new Text("DIFF");
        diffHeader.setFill(Color.WHITE);
        diffHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Text dateHeader = new Text("DATE");
        dateHeader.setFill(Color.WHITE);
        dateHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        VBox rankBox = createAlignedTextBox(rankHeader, 60, Pos.CENTER);
        VBox nameBox = createAlignedTextBox(nameHeader, 80, Pos.CENTER);
        VBox scoreBox = createAlignedTextBox(scoreHeader, 120, Pos.CENTER_RIGHT);
        VBox levelBox = createAlignedTextBox(levelHeader, 80, Pos.CENTER);
        VBox diffBox = createAlignedTextBox(diffHeader, 80, Pos.CENTER);
        VBox dateBox = createAlignedTextBox(dateHeader, 120, Pos.CENTER);

        headerBox.getChildren().addAll(rankBox, nameBox, scoreBox, levelBox, diffBox, dateBox);
        return headerBox;
    }

    private VBox createAlignedTextBox(Text text, double width, Pos alignment) {
        VBox box = new VBox();
        box.setAlignment(alignment);
        box.setPrefWidth(width);
        box.setMaxWidth(width);
        box.setMinWidth(width);
        box.getChildren().add(text);
        return box;
    }

    private VBox createButtonPanel(Runnable onBackToMenu, Runnable onClearScores, boolean afterGamePlay) {
        VBox buttonPanel = new VBox(15);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(30, 0, 40, 0));

        var backButton = buttonSystem.createNavigableButton(
            afterGamePlay ? "Continue" : "Go Back", 
            onBackToMenu
        );
        backButton.setPrefWidth(200);
        
        // 게임 플레이 후가 아닐 때만 Clear Scores 버튼 추가
        if (!afterGamePlay && onClearScores != null) {
            var clearButton = buttonSystem.createNavigableButton("Clear Scores", onClearScores);
            clearButton.setPrefWidth(200);
            buttonPanel.getChildren().addAll(backButton, clearButton);
        } else {
            buttonPanel.getChildren().add(backButton);
        }

        return buttonPanel;
    }

    /**
     * 스코어보드를 갱신합니다.
     * @param topScores 표시할 점수 목록
     */
    public void updateScoreboard(List<ScoreRecord> topScores) {
        scoresContainer.getChildren().clear();

        if (topScores.isEmpty()) {
            Text noScoresLabel = new Text("No scores recorded yet!");
            noScoresLabel.setFill(Color.LIGHTGRAY);
            noScoresLabel.setFont(Font.font("Arial", 16));
            scoresContainer.getChildren().add(noScoresLabel);
            return;
        }

        for (int i = 0; i < Math.min(topScores.size(), MAX_DISPLAY_SCORES); i++) {
            ScoreRecord record = topScores.get(i);
            HBox scoreRow = createScoreRow(i + 1, record);
            scoresContainer.getChildren().add(scoreRow);
        }
    }

    private HBox createScoreRow(int rank, ScoreRecord record) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(500);

        Text rankText = new Text(String.valueOf(rank));
        Text nameText = new Text(record.getPlayerName());
        Text scoreText = new Text(String.format("%,d", record.getScore()));
        Text levelText = new Text(String.valueOf(record.getLevel()));
        Text diffText = new Text(mapDifficulty(record.getDifficulty()));
        Text dateText = new Text(record.getPlayDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")));

        // Rank에 따른 색상 지정
        Color textColor = getColorForRank(rank);
        rankText.setFill(textColor);
        nameText.setFill(textColor);
        scoreText.setFill(textColor);
        levelText.setFill(textColor);
        diffText.setFill(textColor);
        dateText.setFill(textColor);

        Font font = Font.font("Courier New", 13);
        
        // Highlight가 활성화된 경우 새로 추가된 점수에 밑줄 적용
        if (showNewlyAddedHighlight && record.isNewlyAdded()) {
            font = Font.font("Courier New", FontWeight.BOLD, 13);
            rankText.setUnderline(true);
            nameText.setUnderline(true);
            scoreText.setUnderline(true);
            levelText.setUnderline(true);
            diffText.setUnderline(true);
            dateText.setUnderline(true);
        }
        
        rankText.setFont(font);
        nameText.setFont(font);
        scoreText.setFont(font);
        levelText.setFont(font);
        diffText.setFont(font);
        dateText.setFont(font);

        VBox rankBox = createAlignedTextBox(rankText, 60, Pos.CENTER);
        VBox nameBox = createAlignedTextBox(nameText, 80, Pos.CENTER);
        VBox scoreBox = createAlignedTextBox(scoreText, 120, Pos.CENTER_RIGHT);
        VBox levelBox = createAlignedTextBox(levelText, 80, Pos.CENTER);
        VBox diffBox = createAlignedTextBox(diffText, 80, Pos.CENTER);
        VBox dateBox = createAlignedTextBox(dateText, 120, Pos.CENTER);

        row.getChildren().addAll(rankBox, nameBox, scoreBox, levelBox, diffBox, dateBox);
        return row;
    }

    private String mapDifficulty(int difficulty) {
        return switch (difficulty) {
            case 1 -> "Easy";
            case 2 -> "Normal";
            case 3 -> "Hard";
            default -> "-"; // legacy/unknown
        };
    }

    private Color getColorForRank(int rank) {
        return switch (rank) {
            case 1 -> Color.GOLD;
            case 2 -> Color.SILVER;
            case 3 -> Color.web("#CD7F32"); // Bronze
            default -> Color.WHITE;
        };
    }

    /**
     * 스코어보드를 새로고침합니다.
     * Controller에서 데이터를 가져와서 호출해야 합니다.
     */
    public void refresh(List<ScoreRecord> topScores) {
        updateScoreboard(topScores);
    }
}
