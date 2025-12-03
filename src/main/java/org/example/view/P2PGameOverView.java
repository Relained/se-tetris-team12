package org.example.view;


import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * P2P Game Over 화면의 UI를 담당하는 View 클래스
 * 점수를 받아서 승패 결과와 점수를 표시, Go Waiting Room 버튼 제공
 */
public class P2PGameOverView extends GameOverView {
    public P2PGameOverView() {
        super();
        buttonSystem.setStylePrefix("nav-button");
    }

    /**
     * P2P Game Over 화면의 UI를 구성하고 반환합니다.
     * @param myScore 내 점수
     * @param opponentScore 상대 점수
     * @param isGameOver 게임오버 여부 (true일 경우 무조건 패배, false일 경우 점수 비교)
     * @param isServer 서버 여부 (true일 경우 Play Again, Go Waiting Room 버튼 표시)
     * @param onPlayAgain Play Again 버튼 클릭 시 실행될 콜백
     * @param onGoWaitingRoom Go Waiting Room 버튼 클릭 시 실행될 콜백
     * @param onMainMenu Main Menu 버튼 클릭 시 실행될 콜백
     * @param onExit Exit Game 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public Parent createView(int myScore,
                            int opponentScore,
                            byte gameOverStatus,
                            boolean isServer,
                            Runnable onPlayAgain,
                            Runnable onGoWaitingRoom,
                            Runnable onMainMenu,
                            Runnable onExit) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("game-over-root");

        String resultText = determineResultText(myScore, opponentScore, gameOverStatus);
        Text winnerDisplay = new Text(resultText);
        winnerDisplay.getStyleClass().addAll("text-gold", "text-title-large");

        Text myScoreText = createText("Your Score: " + myScore);
        Text opponentScoreText = createText("Opponent Score: " + opponentScore);

        root.getChildren().addAll(winnerDisplay, myScoreText, opponentScoreText);

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

    /**
     * 두 점수를 비교하여 결과 텍스트를 반환합니다.
     * @param myScore 내 점수
     * @param opponentScore 상대 점수
     * @param isGameOver 게임오버 여부 (true일 경우 무조건 패배)
     * @return 승패 결과 텍스트
     */
    public static String determineResultText(int myScore, int opponentScore, byte gameOverStatus) {
        if (gameOverStatus == 1) {
            return "You Lose!";
        } else if (gameOverStatus == 2) {
            return "You Win!";
        }
        if (myScore > opponentScore) {
            return "You Win!";
        } else if (myScore < opponentScore) {
            return "You Lose!";
        } else {
            return "Draw!";
        }
    }
}