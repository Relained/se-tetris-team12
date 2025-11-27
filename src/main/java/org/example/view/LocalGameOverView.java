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
 * Local Multiplayer Game Over 화면의 UI를 담당하는 View 클래스
 */
public class LocalGameOverView extends BaseView {
    
    public LocalGameOverView() {
        super(true); // NavigableButtonSystem 사용
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
}
