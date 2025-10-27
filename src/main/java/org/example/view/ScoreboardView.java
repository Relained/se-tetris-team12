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

    public ScoreboardView(boolean showNewlyAddedHighlight) {
        super(true); // NavigableButtonSystem 사용
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
    }

    /**
     * Scoreboard 화면의 UI를 구성하고 반환합니다.
     * @param onBackToMenu Back to Menu 버튼 클릭 시 실행될 콜백
     * @param onClearScores Clear Scores 버튼 클릭 시 실행될 콜백
     * @return 구성된 BorderPane root
     */
    public BorderPane createView(Runnable onBackToMenu, Runnable onClearScores) {
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        // Scoreboard content
        VBox scoreboardContent = createScoreboardContent();
        root.setCenter(scoreboardContent);

        // Button panel
        HBox buttonPanel = createButtonPanel(onBackToMenu, onClearScores);
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

        // Title
        titleLabel = new Text("HIGH SCORES");
        titleLabel.setFill(Color.GOLD);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Header row
        HBox headerBox = createHeaderRow();

        // Scores container
        scoresContainer = new VBox(8);
        scoresContainer.setAlignment(Pos.CENTER);

        // Initial empty state - will be populated by controller
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

        Text dateHeader = new Text("DATE");
        dateHeader.setFill(Color.WHITE);
        dateHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        VBox rankBox = createAlignedTextBox(rankHeader, 60, Pos.CENTER);
        VBox nameBox = createAlignedTextBox(nameHeader, 80, Pos.CENTER);
        VBox scoreBox = createAlignedTextBox(scoreHeader, 120, Pos.CENTER_RIGHT);
        VBox levelBox = createAlignedTextBox(levelHeader, 80, Pos.CENTER);
        VBox dateBox = createAlignedTextBox(dateHeader, 120, Pos.CENTER);

        headerBox.getChildren().addAll(rankBox, nameBox, scoreBox, levelBox, dateBox);
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

    private HBox createButtonPanel(Runnable onBackToMenu, Runnable onClearScores) {
        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setStyle("-fx-padding: 20;");

        var backButton = buttonSystem.createNavigableButton("Back to Menu", onBackToMenu);
        var clearButton = buttonSystem.createNavigableButton("Clear Scores", onClearScores);

        buttonPanel.getChildren().addAll(backButton, clearButton);
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
        Text dateText = new Text(record.getPlayDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")));

        // Set colors based on rank
        Color textColor = getColorForRank(rank);
        rankText.setFill(textColor);
        nameText.setFill(textColor);
        scoreText.setFill(textColor);
        levelText.setFill(textColor);
        dateText.setFill(textColor);

        Font font = Font.font("Courier New", 13);
        
        // Apply underline for newly added scores if highlighting is enabled
        if (showNewlyAddedHighlight && record.isNewlyAdded()) {
            font = Font.font("Courier New", FontWeight.BOLD, 13);
            rankText.setUnderline(true);
            nameText.setUnderline(true);
            scoreText.setUnderline(true);
            levelText.setUnderline(true);
            dateText.setUnderline(true);
        }
        
        rankText.setFont(font);
        nameText.setFont(font);
        scoreText.setFont(font);
        levelText.setFont(font);
        dateText.setFont(font);

        VBox rankBox = createAlignedTextBox(rankText, 60, Pos.CENTER);
        VBox nameBox = createAlignedTextBox(nameText, 80, Pos.CENTER);
        VBox scoreBox = createAlignedTextBox(scoreText, 120, Pos.CENTER_RIGHT);
        VBox levelBox = createAlignedTextBox(levelText, 80, Pos.CENTER);
        VBox dateBox = createAlignedTextBox(dateText, 120, Pos.CENTER);

        row.getChildren().addAll(rankBox, nameBox, scoreBox, levelBox, dateBox);
        return row;
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
