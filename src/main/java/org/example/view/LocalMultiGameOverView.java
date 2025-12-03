package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Local Multiplayer Game Over 화면의 UI를 담당하는 View 클래스
 * GameOverView를 상속받아 파란색 배경과 승자 표시만 제공합니다.
 */
public class LocalMultiGameOverView extends GameOverView {

    public LocalMultiGameOverView() {
        super();
        buttonSystem.setStylePrefix("nav-button");
    }

    /**
     * Local Multiplayer Game Over 화면의 UI를 구성하고 반환합니다.
     * GAME OVER 텍스트 없이 승자만 표시하고, 파란색 배경을 사용합니다.
     * @param winner 승자 ("Player 1" 또는 "Player 2" 또는 "Draw")
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
        root.getStyleClass().add("root-blue");

        // 승자 표시 - 무승부 케이스도 처리
        String resultMessage = winner.equals("Draw") ? "It's a Draw!" : winner + " Wins!";
        Text winnerText = new Text(resultMessage);
        winnerText.getStyleClass().addAll("text-title-large", "text-gold");

        // View Scoreboard를 제외한 버튼들만 생성
        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Play Again", "Main Menu", "Exit Game"),
            List.of(onPlayAgain, onMainMenu, onExit)
        );

        root.getChildren().add(winnerText);
        root.getChildren().addAll(created);

        return root;
    }
}
