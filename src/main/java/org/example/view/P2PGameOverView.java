package org.example.view;


import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * P2P Game Over 화면의 UI를 담당하는 View 클래스
 * winnerText만 받아서 표시, Go Waiting Room 버튼 제공
 */
public class P2PGameOverView extends GameOverView {
    public P2PGameOverView() {
        super();
    }

    /**
     * P2P Game Over 화면의 UI를 구성하고 반환합니다.
     * @param winnerText 화면에 표시할 텍스트
     * @param isServer 서버 여부 (true일 경우 Play Again, Go Waiting Room 버튼 표시)
     * @param onPlayAgain Play Again 버튼 클릭 시 실행될 콜백
     * @param onGoWaitingRoom Go Waiting Room 버튼 클릭 시 실행될 콜백
     * @param onMainMenu Main Menu 버튼 클릭 시 실행될 콜백
     * @param onExit Exit Game 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public Parent createView(String winnerText,
                            boolean isServer,
                            Runnable onPlayAgain,
                            Runnable onGoWaitingRoom,
                            Runnable onMainMenu,
                            Runnable onExit) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.DARKSLATEBLUE, null, null)));

        Text winnerDisplay = new Text(winnerText);
        winnerDisplay.setFill(Color.GOLD);
        winnerDisplay.setFont(Font.font("Arial", 48));

        root.getChildren().add(winnerDisplay);

        if (isServer) {
            root.getChildren().addAll(
                buttonSystem.createNavigableButton("Play Again", onPlayAgain),
                buttonSystem.createNavigableButton("Go Waiting Room", onGoWaitingRoom)
            );
        }
        
        root.getChildren().addAll(
            buttonSystem.createNavigableButton("Main Menu", onMainMenu),
            buttonSystem.createNavigableButton("Exit Game", onExit)
        );

        return root;
    }
}