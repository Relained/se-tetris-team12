package org.example.view;

import java.util.List;

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
        container.setMaxWidth(650);
        container.setPrefWidth(650);
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
        grid.setMaxWidth(600);
        grid.setPadding(new Insets(0, 0, 10, 0));

        // 컬럼 제약 설정
        ColumnConstraints col1 = new ColumnConstraints(60); // RANK
        ColumnConstraints col2 = new ColumnConstraints(80); // NAME
        ColumnConstraints col3 = new ColumnConstraints(120); // SCORE
        ColumnConstraints col4 = new ColumnConstraints(60); // LEVEL
        ColumnConstraints col5 = new ColumnConstraints(70); // DIFF
        ColumnConstraints col6 = new ColumnConstraints(70); // MODE
        ColumnConstraints col7 = new ColumnConstraints(120); // DATE
        
        col1.setHalignment(HPos.CENTER);
        col2.setHalignment(HPos.CENTER);
        col3.setHalignment(HPos.RIGHT);
        col4.setHalignment(HPos.CENTER);
        col5.setHalignment(HPos.CENTER);
        col6.setHalignment(HPos.CENTER);
        col7.setHalignment(HPos.CENTER);
        
        grid.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);

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

        Text modeHeader = new Text("MODE");
        modeHeader.setFill(Color.WHITE);
        modeHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Text dateHeader = new Text("DATE");
        dateHeader.setFill(Color.WHITE);
        dateHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        grid.add(rankHeader, 0, 0);
        grid.add(nameHeader, 1, 0);
        grid.add(scoreHeader, 2, 0);
        grid.add(levelHeader, 3, 0);
        grid.add(diffHeader, 4, 0);
        grid.add(modeHeader, 5, 0);
        grid.add(dateHeader, 6, 0);

        return grid;
    }

    private VBox createButtonPanel(Runnable onBackToMenu, Runnable onClearScores, boolean afterGamePlay) {
        VBox buttonPanel = new VBox(15);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(30, 0, 40, 0));
        
        // 게임 플레이 후가 아닐 때만 Clear Scores 버튼 추가
        if (!afterGamePlay) {
            var created = buttonSystem.createNavigableButtonFromList(
                List.of("Go Back", "Clear Scores"),
                List.of(onBackToMenu, onClearScores)
            );
            created.forEach(button -> button.setPrefWidth(200));
            buttonPanel.getChildren().addAll(created);
        } else {
            var backButton = buttonSystem.createNavigableButton("Go Back", onBackToMenu);
            backButton.setPrefWidth(200);
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
        grid.setMaxWidth(600);

        Text rankText = new Text(String.valueOf(rank));
        Text nameText = new Text(record.getPlayerName());
        Text scoreText = new Text(String.format("%,d", record.getScore()));
        Text levelText = new Text(String.valueOf(record.getLevel()));
        Text diffText = new Text(mapDifficulty(record.getDifficulty()));
        Text modeText = new Text(record.getGameMode().toString());
        Text dateText = new Text(record.getPlayDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")));

        // Rank에 따른 색상 지정
        Color textColor = getColorForRank(rank);
        rankText.setFill(textColor);
        nameText.setFill(textColor);
        scoreText.setFill(textColor);
        levelText.setFill(textColor);
        diffText.setFill(textColor);
        modeText.setFill(textColor);
        dateText.setFill(textColor);

        Font font = Font.font("Courier New", 13);
        
        // Highlight가 활성화된 경우 새로 추가된 점수에 밑줄 적용
        if (showNewlyAddedHighlight && record.isNewAndEligible()) {
            font = Font.font("Courier New", FontWeight.BOLD, 13);
            rankText.setUnderline(true);
            nameText.setUnderline(true);
            scoreText.setUnderline(true);
            levelText.setUnderline(true);
            diffText.setUnderline(true);
            modeText.setUnderline(true);
            dateText.setUnderline(true);
        }
        
        rankText.setFont(font);
        nameText.setFont(font);
        scoreText.setFont(font);
        levelText.setFont(font);
        diffText.setFont(font);
        modeText.setFont(font);
        dateText.setFont(font);

        // 컬럼 제약 설정 (헤더와 동일)
        ColumnConstraints col1 = new ColumnConstraints(60);
        ColumnConstraints col2 = new ColumnConstraints(80);
        ColumnConstraints col3 = new ColumnConstraints(120);
        ColumnConstraints col4 = new ColumnConstraints(60);
        ColumnConstraints col5 = new ColumnConstraints(70);
        ColumnConstraints col6 = new ColumnConstraints(70);
        ColumnConstraints col7 = new ColumnConstraints(120);
        
        col1.setHalignment(HPos.CENTER);
        col2.setHalignment(HPos.CENTER);
        col3.setHalignment(HPos.RIGHT);
        col4.setHalignment(HPos.CENTER);
        col5.setHalignment(HPos.CENTER);
        col6.setHalignment(HPos.CENTER);
        col7.setHalignment(HPos.CENTER);
        
        grid.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);

        grid.add(rankText, 0, 0);
        grid.add(nameText, 1, 0);
        grid.add(scoreText, 2, 0);
        grid.add(levelText, 3, 0);
        grid.add(diffText, 4, 0);
        grid.add(modeText, 5, 0);
        grid.add(dateText, 6, 0);

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
