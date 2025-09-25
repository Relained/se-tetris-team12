package org.example.ui.components;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScorePanel extends VBox {
    private final Text scoreText;
    private final Text linesText;
    private final Text levelText;

    public ScorePanel() {
        super(15);

        Text title = new Text("Statistics");
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

        getChildren().addAll(title, scoreText, linesText, levelText);
        setStyle("-fx-background-color: #333; -fx-padding: 10;");
    }

    public void updateStats(int score, int lines, int level) {
        scoreText.setText("Score: " + score);
        linesText.setText("Lines: " + lines);
        levelText.setText("Level: " + level);
    }
}