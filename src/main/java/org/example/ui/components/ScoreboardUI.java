package org.example.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
        setMaxWidth(600);

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
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

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

        // Set fixed widths for alignment
        rankHeader.setWrappingWidth(50);
        nameHeader.setWrappingWidth(120);
        scoreHeader.setWrappingWidth(100);
        levelHeader.setWrappingWidth(60);
        dateHeader.setWrappingWidth(120);

        headerBox.getChildren().addAll(rankHeader, nameHeader, scoreHeader, levelHeader, dateHeader);
        return headerBox;
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
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

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

        // Set fixed widths for alignment
        rankText.setWrappingWidth(50);
        nameText.setWrappingWidth(120);
        scoreText.setWrappingWidth(100);
        levelText.setWrappingWidth(60);
        dateText.setWrappingWidth(120);

        row.getChildren().addAll(rankText, nameText, scoreText, levelText, dateText);
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