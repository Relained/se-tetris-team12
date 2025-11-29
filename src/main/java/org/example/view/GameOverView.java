package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
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
        root.getStyleClass().add("root-red");

        Text title = new Text("GAME OVER");
        title.getStyleClass().addAll("text-title-medium", "text-primary");

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

    Text createText(String str) {
        Text text = new Text(str);
        text.getStyleClass().addAll("text-body-large", "text-secondary");
        return text;
    }
}
