package org.example.view;

import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.model.ScoreRecord;

import java.time.format.DateTimeFormatter;

/**
 * Scoreboard 화면의 UI를 담당하는 View 클래스
 */
public class ScoreboardView extends BaseView {
    private static final int MAX_DISPLAY_SCORES = 10;
    
    // 화면 너비 대비 비율
    private static final double CONTAINER_WIDTH_RATIO = 0.92; // 컨테이너는 화면 너비의 92%
    private static final double GRID_WIDTH_RATIO = 0.88; // GridPane은 화면 너비의 88%
    
    // 컬럼 너비 비율 (전체 대비)
    private static final double COL_RANK_RATIO = 0.08;
    private static final double COL_NAME_RATIO = 0.12;
    private static final double COL_SCORE_RATIO = 0.15;
    private static final double COL_LEVEL_RATIO = 0.09;
    private static final double COL_DIFF_RATIO = 0.10;
    private static final double COL_MODE_RATIO = 0.10;
    private static final double COL_DATE_RATIO = 0.16;
    
    private VBox scoresContainer;
    private VBox container;
    private GridPane headerGrid;
    private Text titleLabel;
    private boolean showNewlyAddedHighlight = false;
    private boolean isAfterGamePlay = false; // 게임 플레이 후인지 여부

    private double currentWidth;

    public ScoreboardView() {
        super(true);
        var displayManager = org.example.service.DisplayManager.getInstance();
        this.currentWidth = displayManager.getWidth(displayManager.getCurrentSize());
    }
    
    public ScoreboardView(boolean isAfterGamePlay, boolean showNewlyAddedHighlight) {
        this();
        this.isAfterGamePlay = isAfterGamePlay;
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
    }
    
    @Override
    protected void onScaleChanged(double scale) {
        // container가 아직 생성되지 않았으면 스킵
        if (container == null) {
            return;
        }
        
        // DisplayManager에서 현재 화면 크기에 맞는 너비 가져오기
        var displayManager = org.example.service.DisplayManager.getInstance();
        double width = displayManager.getWidth(displayManager.getCurrentSize());
        updateLayout(width);
    }

    /**
     * Scoreboard 화면의 UI를 구성하고 반환합니다.
     * @param onBackToMenu Back to Menu 버튼 클릭 시 실행될 콜백
     * @param onClearScores Clear Scores 버튼 클릭 시 실행될 콜백 (게임 플레이 후에는 null 가능)
     * @return 구성된 BorderPane root
     */
    public BorderPane createView(Runnable onBackToMenu, Runnable onClearScores) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-dark");

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
        container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(15);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("root-dark");
        container.setMinHeight(550);

        titleLabel = new Text("HIGH SCORES");
        titleLabel.getStyleClass().add("scoreboard-title");

        headerGrid = createHeaderRow();

        scoresContainer = new VBox(8);
        scoresContainer.setAlignment(Pos.TOP_CENTER);
        scoresContainer.setMinHeight(300);

        Text noScoresLabel = new Text("Loading...");
        noScoresLabel.getStyleClass().addAll("text-body-medium", "text-lightgray");
        scoresContainer.getChildren().add(noScoresLabel);

        container.getChildren().addAll(titleLabel, headerGrid, scoresContainer);
        return container;
    }
    
    /**
     * 화면 크기에 따라 레이아웃을 업데이트합니다.
     * @param width 현재 Stage 너비
     */
    private void updateLayout(double width) {
        this.currentWidth = width;
        
        // 컨테이너 크기 업데이트
        double containerWidth = width * CONTAINER_WIDTH_RATIO;
        container.setMaxWidth(containerWidth);
        container.setPrefWidth(containerWidth);
        
        // 헤더 그리드 업데이트
        double gridWidth = width * GRID_WIDTH_RATIO;
        headerGrid.setMaxWidth(gridWidth);
        updateGridColumns(headerGrid, width);
        
        // 기존 점수 행들도 업데이트
        scoresContainer.getChildren().forEach(node -> {
            if (node instanceof GridPane) {
                GridPane grid = (GridPane) node;
                grid.setMaxWidth(gridWidth);
                updateGridColumns(grid, width);
            }
        });
    }
    
    /**
     * GridPane의 컬럼 크기를 화면 너비에 맞게 업데이트합니다.
     * @param grid 업데이트할 GridPane
     * @param screenWidth 현재 화면 너비
     */
    private void updateGridColumns(GridPane grid, double screenWidth) {
        grid.getColumnConstraints().clear();
        
        ColumnConstraints col1 = new ColumnConstraints(screenWidth * COL_RANK_RATIO);
        ColumnConstraints col2 = new ColumnConstraints(screenWidth * COL_NAME_RATIO);
        ColumnConstraints col3 = new ColumnConstraints(screenWidth * COL_SCORE_RATIO);
        ColumnConstraints col4 = new ColumnConstraints(screenWidth * COL_LEVEL_RATIO);
        ColumnConstraints col5 = new ColumnConstraints(screenWidth * COL_DIFF_RATIO);
        ColumnConstraints col6 = new ColumnConstraints(screenWidth * COL_MODE_RATIO);
        ColumnConstraints col7 = new ColumnConstraints(screenWidth * COL_DATE_RATIO);
        
        col1.setHalignment(HPos.CENTER);
        col2.setHalignment(HPos.CENTER);
        col3.setHalignment(HPos.RIGHT);
        col4.setHalignment(HPos.CENTER);
        col5.setHalignment(HPos.CENTER);
        col6.setHalignment(HPos.CENTER);
        col7.setHalignment(HPos.CENTER);
        
        grid.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);
    }

    private GridPane createHeaderRow() {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(0, 0, 10, 0));

        // 초기 컬럼 제약 설정 (updateGridColumns에서 동적으로 업데이트됨)
        updateGridColumns(grid, currentWidth);

        Text rankHeader = new Text("RANK");
        rankHeader.getStyleClass().add("scoreboard-header");

        Text nameHeader = new Text("NAME");
        nameHeader.getStyleClass().add("scoreboard-header");

        Text scoreHeader = new Text("SCORE");
        scoreHeader.getStyleClass().add("scoreboard-header");

        Text levelHeader = new Text("LEVEL");
        levelHeader.getStyleClass().add("scoreboard-header");

        Text diffHeader = new Text("DIFF");
        diffHeader.getStyleClass().add("scoreboard-header");

        Text modeHeader = new Text("MODE");
        modeHeader.getStyleClass().add("scoreboard-header");

        Text dateHeader = new Text("DATE");
        dateHeader.getStyleClass().add("scoreboard-header");

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
            buttonPanel.getChildren().addAll(created);
        } else {
            var backButton = buttonSystem.createNavigableButton("Continue", onBackToMenu);
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
            noScoresLabel.getStyleClass().addAll("text-body-medium", "text-lightgray");
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
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxWidth(currentWidth * GRID_WIDTH_RATIO);

        Text rankText = new Text(String.valueOf(rank));
        Text nameText = new Text(record.getPlayerName());
        Text scoreText = new Text(String.format("%,d", record.getScore()));
        Text levelText = new Text(String.valueOf(record.getLevel()));
        Text diffText = new Text(mapDifficulty(record.getDifficulty()));
        Text modeText = new Text(record.getGameMode().toString());
        Text dateText = new Text(record.getPlayDate().format(DateTimeFormatter.ofPattern("MM/dd/yy")));

        // Rank에 따른 색상 클래스 지정
        String rankColorClass = getRankColorClass(rank);
        
        // Highlight가 활성화된 경우 새로 추가된 점수에 밑줄 적용
        String cellClass = (showNewlyAddedHighlight && record.isNewAndEligible()) 
            ? "scoreboard-cell-highlight" 
            : "scoreboard-cell";
        
        // 모든 텍스트에 스타일 클래스 적용
        for (Text text : List.of(rankText, nameText, scoreText, levelText, diffText, modeText, dateText)) {
            text.getStyleClass().addAll(cellClass, rankColorClass);
            if (showNewlyAddedHighlight && record.isNewAndEligible()) {
                text.setUnderline(true);
            }
        }

        // 컬럼 제약 설정 (화면 크기에 비례)
        updateGridColumns(grid, currentWidth);

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

    private String getRankColorClass(int rank) {
        return switch (rank) {
            case 1 -> "scoreboard-rank-1";
            case 2 -> "scoreboard-rank-2";
            case 3 -> "scoreboard-rank-3";
            default -> "text-white";
        };
    }
}
