package org.example.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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
        container.setMaxWidth(Double.MAX_VALUE); // 최대한 늘어나도록
        container.setPrefWidth(550); // 선호 크기는 유지
        container.setMinWidth(400); // 최소 크기 설정
        container.setMinHeight(550);

        titleLabel = new Text("HIGH SCORES");
        titleLabel.setFill(Color.GOLD);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane headerGrid = createHeaderRow();

        scoresContainer = new VBox(8);
        scoresContainer.setAlignment(Pos.TOP_CENTER);
        scoresContainer.setMinHeight(300);

        Text noScoresLabel = new Text("Loading...");
        noScoresLabel.setFill(Color.LIGHTGRAY);
        noScoresLabel.setFont(Font.font("Arial", 16));
        scoresContainer.getChildren().add(noScoresLabel);

        container.getChildren().addAll(titleLabel, headerGrid, scoresContainer);
        return container;
    }

    private GridPane createHeaderRow() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(0, 0, 10, 0));
        grid.setMaxWidth(Double.MAX_VALUE);
        
        // 컬럼 제약 조건 설정 (퍼센트 기반)
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10); // RANK
        col1.setHalignment(HPos.CENTER);
        
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(15); // NAME
        col2.setHalignment(HPos.CENTER);
        
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25); // SCORE
        col3.setHalignment(HPos.RIGHT);
        
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(15); // LEVEL
        col4.setHalignment(HPos.CENTER);
        
        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(15); // DIFF
        col5.setHalignment(HPos.CENTER);
        
        ColumnConstraints col6 = new ColumnConstraints();
        col6.setPercentWidth(20); // DATE
        col6.setHalignment(HPos.CENTER);
        
        grid.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6);
        
        // 헤더 텍스트 생성
        Text rankHeader = createHeaderText("RANK");
        Text nameHeader = createHeaderText("NAME");
        Text scoreHeader = createHeaderText("SCORE");
        Text levelHeader = createHeaderText("LEVEL");
        Text diffHeader = createHeaderText("DIFF");
        Text dateHeader = createHeaderText("DATE");
        
        // GridPane에 추가
        grid.add(rankHeader, 0, 0);
        grid.add(nameHeader, 1, 0);
        grid.add(scoreHeader, 2, 0);
        grid.add(levelHeader, 3, 0);
        grid.add(diffHeader, 4, 0);
        grid.add(dateHeader, 5, 0);
        
        return grid;
    }
    
    private Text createHeaderText(String content) {
        Text text = new Text(content);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        return text;
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
            GridPane scoreRow = createScoreRow(i + 1, record);
            scoresContainer.getChildren().add(scoreRow);
        }
    }

    private GridPane createScoreRow(int rank, ScoreRecord record) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxWidth(Double.MAX_VALUE);
        
        // 헤더와 동일한 컬럼 제약 조건 적용
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10); // RANK
        col1.setHalignment(HPos.CENTER);
        
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(15); // NAME
        col2.setHalignment(HPos.CENTER);
        
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25); // SCORE
        col3.setHalignment(HPos.RIGHT);
        
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(15); // LEVEL
        col4.setHalignment(HPos.CENTER);
        
        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(15); // DIFF
        col5.setHalignment(HPos.CENTER);
        
        ColumnConstraints col6 = new ColumnConstraints();
        col6.setPercentWidth(20); // DATE
        col6.setHalignment(HPos.CENTER);
        
        grid.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6);

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

        // GridPane에 텍스트 추가
        grid.add(rankText, 0, 0);
        grid.add(nameText, 1, 0);
        grid.add(scoreText, 2, 0);
        grid.add(levelText, 3, 0);
        grid.add(diffText, 4, 0);
        grid.add(dateText, 5, 0);

        return grid;
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
