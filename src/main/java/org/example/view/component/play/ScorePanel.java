package org.example.view.component.play;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScorePanel extends VBox {
    private final Text title;
    private final Text scoreText;
    private final Text linesText;
    private final Text levelText;
    private final HBox linesLevelRow;
    private final Text modeText;
    private final Text difficultyText;
    private final HBox modeDifficultyRow;
    private final Text timerText;
    
    private double baseFontSize = 14;
    private boolean showTimer = false;

    public ScorePanel(String mode, String difficulty) {
        super(10);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10));

        title = new Text("Statistics");
        title.setFill(Color.WHITE);
        title.setFont(Font.font(18));

        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(14));

        // Lines와 Level을 같은 줄에 표시
        linesText = new Text("L: 0");
        linesText.setFill(Color.WHITE);
        linesText.setFont(Font.font(14));

        levelText = new Text("Lv: 1");
        levelText.setFill(Color.WHITE);
        levelText.setFont(Font.font(14));
        
        linesLevelRow = new HBox(10);
        linesLevelRow.setAlignment(Pos.CENTER);
        linesLevelRow.getChildren().addAll(linesText, levelText);

        modeText = new Text(mode);
        modeText.setFill(Color.LIGHTBLUE);
        modeText.setFont(Font.font(14));

        difficultyText = new Text(difficulty);
        // 난이도별 색상 설정
        Color difficultyColor = switch (difficulty) {
            case "Easy" -> Color.LIGHTGREEN;
            case "Normal" -> Color.YELLOW;
            case "Hard" -> Color.ORANGERED;
            default -> Color.WHITE;
        };
        difficultyText.setFill(difficultyColor);
        difficultyText.setFont(Font.font(14));
        
        modeDifficultyRow = new HBox(10);
        modeDifficultyRow.setAlignment(Pos.CENTER);
        modeDifficultyRow.getChildren().addAll(modeText, difficultyText);

        timerText = new Text("Time: 2:00");
        timerText.setFill(Color.YELLOW);
        timerText.setFont(Font.font(14));
        timerText.setVisible(false);

        getChildren().addAll(title, scoreText, linesLevelRow, modeDifficultyRow, timerText);
        setStyle("-fx-background-color: #333;");
        
        // 가로 크기 변경 감지 - 가로 크기 기준으로 폰트 조정
        widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            if (newWidth > 0) {
                adjustFontSizeByWidth(newWidth);
            }
        });
    }
    
    /**
     * 가로 크기를 기준으로 폰트 크기를 조정합니다.
     */
    private void adjustFontSizeByWidth(double containerWidth) {
        double padding = 15; // 좌우 패딩
        double availableWidth = containerWidth - padding;
        
        if (availableWidth <= 0) return;
        
        // 가로 크기에 비례하여 폰트 크기 조정
        baseFontSize = Math.max(11, Math.min(18, availableWidth / 7));
        
        title.setFont(Font.font(baseFontSize * 1.3));
        scoreText.setFont(Font.font(baseFontSize));
        linesText.setFont(Font.font(baseFontSize * 0.9));
        levelText.setFont(Font.font(baseFontSize * 0.9));
        modeText.setFont(Font.font(baseFontSize * 0.85));
        difficultyText.setFont(Font.font(baseFontSize * 0.85));
        timerText.setFont(Font.font(baseFontSize));
    }

    public void updateStats(int score, int lines, int level) {
        scoreText.setText("Score: " + score);
        linesText.setText("L: " + lines);
        levelText.setText("Lv: " + level);
    }

    /**
     * 타이머 표시 활성화/비활성화
     */
    public void setShowTimer(boolean show) {
        this.showTimer = show;
        timerText.setVisible(show);
    }

    /**
     * 남은 시간 업데이트 (밀리초 단위)
     */
    public void updateTimer(long remainingMillis) {
        if (!showTimer) return;
        
        long totalSeconds = remainingMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        
        timerText.setText(String.format("Time: %d:%02d", minutes, seconds));
        
        // 30초 이하일 때 빨간색으로 변경
        if (totalSeconds <= 30) {
            timerText.setFill(Color.RED);
        } else if (totalSeconds <= 60) {
            timerText.setFill(Color.ORANGE);
        } else {
            timerText.setFill(Color.YELLOW);
        }
    }
}