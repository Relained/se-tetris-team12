package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Game Over 화면의 UI를 담당하는 View 클래스
 */
public class GameOverView extends BaseView {
    
    public GameOverView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    /**
     * Game Over 화면의 UI를 구성하고 반환합니다.
     * @param finalScore 최종 점수
     * @param finalLines 클리어한 총 라인 수
     * @param finalLevel 도달한 레벨
     * @param onPlayAgain Play Again 버튼 클릭 시 실행될 콜백
     * @param onViewScoreboard View Scoreboard 버튼 클릭 시 실행될 콜백
     * @param onMainMenu Main Menu 버튼 클릭 시 실행될 콜백
     * @param onExit Exit Game 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(int finalScore, 
                          int finalLines, 
                          int finalLevel,
                          Runnable onPlayAgain,
                          Runnable onViewScoreboard,
                          Runnable onMainMenu,
                          Runnable onExit) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));

        Text title = new Text("GAME OVER");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 42));

        Text scoreText = new Text("Final Score: " + finalScore);
        scoreText.setFill(Color.LIGHTGRAY);
        scoreText.setFont(Font.font("Arial", 20));

        Text linesText = new Text("Lines Cleared: " + finalLines);
        linesText.setFill(Color.LIGHTGRAY);
        linesText.setFont(Font.font("Arial", 20));

        Text levelText = new Text("Level Reached: " + finalLevel);
        levelText.setFill(Color.LIGHTGRAY);
        levelText.setFont(Font.font("Arial", 20));

        var playAgainButton = buttonSystem.createNavigableButton("Play Again", onPlayAgain);
        var viewScoreboardButton = buttonSystem.createNavigableButton("View Scoreboard", onViewScoreboard);
        var mainMenuButton = buttonSystem.createNavigableButton("Main Menu", onMainMenu);
        var exitButton = buttonSystem.createNavigableButton("Exit Game", onExit);

        root.getChildren().addAll(
                title,
                scoreText,
                linesText,
                levelText,
                playAgainButton,
                viewScoreboardButton,
                mainMenuButton,
                exitButton
        );

        return root;
    }
}
