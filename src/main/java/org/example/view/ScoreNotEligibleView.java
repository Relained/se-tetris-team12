package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.example.service.FontManager;

/**
 * ScoreNotEligible 화면의 UI를 담당하는 View 클래스
 */
public class ScoreNotEligibleView extends BaseView {
    
    private Text messageText;
    private Text scoreText;

    public ScoreNotEligibleView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    @Override
    protected void onScaleChanged(double scale) {
        // UI 요소가 아직 생성되지 않았으면 스킵
        if (messageText == null || scoreText == null) {
            return;
        }
        
        // 스케일에 맞춰 폰트 크기 조정
        messageText.setFont(fontManager.getFont(FontManager.SIZE_BODY_MESSAGE * scale));
        scoreText.setFont(fontManager.getFont(FontManager.SIZE_BODY_LARGE * scale));
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
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));

        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);

        messageText = new Text("Your score didn't make it to the top 10.\nCheck out the current scoreboard!");
        messageText.setFill(Color.LIGHTGRAY);
        messageText.setFont(fontManager.getFont(FontManager.SIZE_BODY_MESSAGE * currentScale));
        messageText.setTextAlignment(TextAlignment.CENTER);

        scoreText = new Text("Final Score: " + score);
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(fontManager.getFont(FontManager.SIZE_BODY_LARGE * currentScale));
        scoreText.setTextAlignment(TextAlignment.CENTER);

        // NavigableButtonSystem을 사용하여 버튼 생성
        var continueButton = buttonSystem.createNavigableButton("Continue", onContinue);

        messageBox.getChildren().addAll(messageText, scoreText, continueButton);
        root.getChildren().add(messageBox);

        return root;
    }
}
