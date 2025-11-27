package org.example.view;

import java.util.List;

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

        Text scoreText = createText("Final Score: " + finalScore);
        Text linesText = createText("Lines Cleared: " + finalLines);
        Text levelText = createText("Level Reached: " + finalLevel);

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Play Again", "View Scoreboard", "Main Menu", "Exit Game"),
            List.of(onPlayAgain, onViewScoreboard, onMainMenu, onExit)
        );

        root.getChildren().addAll(title, scoreText, linesText, levelText);
        root.getChildren().addAll(created);

        return root;
    }

    /**
     * Local Multiplayer Game Over 화면의 UI를 구성하고 반환합니다.
     * @param winner 승자 ("Player 1" 또는 "Player 2")
     * @param onPlayAgain Play Again 버튼 클릭 시 실행될 콜백
     * @param onMainMenu Main Menu 버튼 클릭 시 실행될 콜백
     * @param onExit Exit Game 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(String winner,
                          Runnable onPlayAgain,
                          Runnable onMainMenu,
                          Runnable onExit) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));

        // 승자 표시 (제일 상단)
        Text winnerText = new Text(winner + " Wins!");
        winnerText.setFill(Color.GOLD);
        winnerText.setFont(Font.font("Arial", 48));

        Text title = new Text("GAME OVER");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 42));

        // View Scoreboard를 제외한 버튼들만 생성
        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Play Again", "Main Menu", "Exit Game"),
            List.of(onPlayAgain, onMainMenu, onExit)
        );

        root.getChildren().addAll(winnerText, title);
        root.getChildren().addAll(created);

        return root;
    }

    Text createText(String str) {
        Text text = new Text(str);
        text.setFill(Color.LIGHTGRAY);
        text.setFont(Font.font("Arial", 20));
        return text;
    }
}
