package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.example.model.ScoreRecord;
import org.example.view.component.NavigableButtonSystem;

/**
 * 점수가 상위 10개에 들지 못한 경우를 표시하는 View 클래스
 */
public class ScoreNotEligibleView extends BaseView {

    public ScoreNotEligibleView() {
        super(false);
    }

    /**
     * ScoreNotEligible 화면의 UI를 구성하고 반환합니다.
     * @param record 표시할 점수 기록
     * @return 구성된 StackPane root
     */
    public StackPane createView(ScoreRecord record) {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));

        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(40));

        Text messageText = new Text("Your score didn't make it to the top 10.\nCheck out the current scoreboard!");
        messageText.setFill(Color.LIGHTGRAY);
        messageText.setFont(Font.font("Arial", 18));
        messageText.setTextAlignment(TextAlignment.CENTER);

        Text scoreText = new Text("Final Score: " + record.getScore());
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setTextAlignment(TextAlignment.CENTER);

        // NavigableButtonSystem의 독립 버튼 생성 메서드 사용
        Button continueButton = NavigableButtonSystem.createStandaloneButton("Continue");

        messageBox.getChildren().addAll(messageText, scoreText, continueButton);
        root.getChildren().add(messageBox);

        return root;
    }
}
