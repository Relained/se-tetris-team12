package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * ScoreNotEligible 화면의 UI를 담당하는 View 클래스
 */
public class ScoreNotEligibleView extends BaseView {

    public ScoreNotEligibleView() {
        super(true); // NavigableButtonSystem 사용
    }

    /**
     * ScoreNotEligible 화면의 UI를 구성하고 반환합니다.
     * @param score 최종 점수
     * @param onContinue Continue 버튼 클릭 시 실행될 콜백
     * @return 구성된 StackPane root
     */
    public StackPane createView(int score, Runnable onContinue) {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-dark");

        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);

        Text messageText = new Text("Your score didn't make it to the top 10.\nCheck out the current scoreboard!");
        messageText.getStyleClass().addAll("text-body-message", "text-secondary");
        messageText.setTextAlignment(TextAlignment.CENTER);

        Text scoreText = new Text("Final Score: " + score);
        scoreText.getStyleClass().addAll("text-body-large", "text-yellow");
        scoreText.setTextAlignment(TextAlignment.CENTER);

        // NavigableButtonSystem을 사용하여 버튼 생성
        var continueButton = buttonSystem.createNavigableButton("Continue", onContinue);

        messageBox.getChildren().addAll(messageText, scoreText, continueButton);
        root.getChildren().add(messageBox);

        return root;
    }
}
