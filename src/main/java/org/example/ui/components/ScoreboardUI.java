package org.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.example.game.state.ScoreManager;
import org.example.model.ScoreRecord;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScoreboardUI extends VBox {
    private static final int MAX_DISPLAY_SCORES = 10;
    private VBox scoresContainer;
    private Text titleLabel;
    private boolean showNewlyAddedHighlight = true; // 새로 추가된 점수 하이라이트 표시 여부

    public ScoreboardUI() {
        this(true); // 기본적으로 하이라이트 표시
    }
    
    public ScoreboardUI(boolean showNewlyAddedHighlight) {
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
        initializeUI();
        updateScoreboard();
    }

    private void initializeUI() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(20));
        setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, null, null)));
        setMaxWidth(550); // 이름이 3글자로 제한되어 전체 폭 축소
        setPrefWidth(550);

        // Title
        titleLabel = new Text("HIGH SCORES");
        titleLabel.setFill(Color.GOLD);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Header row
        HBox headerBox = createHeaderRow();

        // Scores container
        scoresContainer = new VBox(8);
        scoresContainer.setAlignment(Pos.CENTER);

        getChildren().addAll(titleLabel, headerBox, scoresContainer);
    }

    private HBox createHeaderRow() {
        HBox headerBox = new HBox(10); // 컬럼 간격 추가
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));
        headerBox.setMaxWidth(500); // 전체 폭 축소

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

        // 3글자 이름에 맞춘 컬럼 폭 조정
        VBox rankBox = createAlignedTextBox(rankHeader, 60, Pos.CENTER);
        VBox nameBox = createAlignedTextBox(nameHeader, 80, Pos.CENTER); // 140 → 80으로 축소
        VBox scoreBox = createAlignedTextBox(scoreHeader, 120, Pos.CENTER_RIGHT);
        VBox levelBox = createAlignedTextBox(levelHeader, 80, Pos.CENTER);
        VBox dateBox = createAlignedTextBox(dateHeader, 120, Pos.CENTER);

        headerBox.getChildren().addAll(rankBox, nameBox, scoreBox, levelBox, dateBox);
        return headerBox;
    }

    /**
     * 텍스트를 정렬된 박스에 넣는 헬퍼 메서드
     */
    private VBox createAlignedTextBox(Text text, double width, Pos alignment) {
        VBox box = new VBox();
        box.setAlignment(alignment);
        box.setPrefWidth(width);
        box.setMaxWidth(width);
        box.setMinWidth(width);
        box.getChildren().add(text);
        return box;
    }

    public void updateScoreboard() {
        scoresContainer.getChildren().clear();

        List<ScoreRecord> topScores = ScoreManager.getInstance().getTopScores();

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
        HBox row = new HBox(10); // 컬럼 간격 추가
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(500); // 전체 폭 축소

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

        // 3글자 이름에 맞춘 정렬된 박스로 각 컬럼 생성
        VBox rankBox = createAlignedTextBox(rankText, 60, Pos.CENTER);
        VBox nameBox = createAlignedTextBox(nameText, 80, Pos.CENTER); // 140 → 80으로 축소
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

    public void refresh() {
        updateScoreboard();
    }

    public void clearScores() {
        ScoreManager.getInstance().clearScores();
        updateScoreboard();
    }
}