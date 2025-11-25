package org.example.view.component.play;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScorePanel extends VBox {
    private final Text title;
    private final Text scoreText;
    private final Text linesText;
    private final Text levelText;
    private final Text modeText;
    private final Text difficultyText;
    
    private double baseFontSize = 14;

    public ScorePanel(String mode, String difficulty) {
        super(15);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10));

        title = new Text("Statistics");
        title.setFill(Color.WHITE);
        title.setFont(Font.font(18));

        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(14));

        linesText = new Text("Lines: 0");
        linesText.setFill(Color.WHITE);
        linesText.setFont(Font.font(14));

        levelText = new Text("Level: 1");
        levelText.setFill(Color.WHITE);
        levelText.setFont(Font.font(14));

        modeText = new Text("Mode: " + mode);
        modeText.setFill(Color.WHITE);
        modeText.setFont(Font.font(14));

        difficultyText = new Text("Difficulty: " + difficulty);
        difficultyText.setFill(Color.WHITE);
        difficultyText.setFont(Font.font(14));

        getChildren().addAll(title, scoreText, linesText, levelText, modeText, difficultyText);
        setStyle("-fx-background-color: #333;");
        
        // 크기 변경 감지
        widthProperty().addListener((obs, oldVal, newVal) -> adjustFontSize());
        heightProperty().addListener((obs, oldVal, newVal) -> adjustFontSize());
    }
    
    private void adjustFontSize() {
        double availableHeight = getPrefHeight() > 0 ? getPrefHeight() : getHeight();
        
        if (availableHeight <= 0) return;
        
        // 높이에 따라 폰트 크기 조정
        baseFontSize = Math.max(10, Math.min(16, availableHeight / 15));
        
        title.setFont(Font.font(baseFontSize * 1.3));
        scoreText.setFont(Font.font(baseFontSize));
        linesText.setFont(Font.font(baseFontSize));
        levelText.setFont(Font.font(baseFontSize));
        modeText.setFont(Font.font(baseFontSize));
        difficultyText.setFont(Font.font(baseFontSize));
    }

    public void updateStats(int score, int lines, int level) {
        scoreText.setText("Score: " + score);
        linesText.setText("Lines: " + lines);
        levelText.setText("Level: " + level);
    }
}