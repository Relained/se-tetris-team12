package org.example.controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
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

/**
 * 점수가 상위 10개에 들지 못한 경우를 표시하는 State
 * 게임 오버 화면으로 이동할 수 있습니다.
 */
public class ScoreNotEligibleController extends BaseController {
    
    final private ScoreRecord record;

    public ScoreNotEligibleController(ScoreRecord record) {
        this.record = record;
    }

    @Override
    public Scene createScene() {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));

        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);
        
        Text messageText = new Text("Your score didn't make it to the top 10.\nCheck out the current scoreboard!");
        messageText.setFill(Color.LIGHTGRAY);
        messageText.setFont(Font.font("Arial", 18));
        messageText.setTextAlignment(TextAlignment.CENTER);
        
        Text scoreText = new Text("Final Score: " + record.getScore());
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setTextAlignment(TextAlignment.CENTER);
        
        // NavigableButtonSystem 스타일과 동일한 버튼 생성
        Button continueButton = createStyledButton("Continue");
        
        messageBox.getChildren().addAll(messageText, scoreText, continueButton);
        root.getChildren().add(messageBox);
        
        scene = new Scene(root, 800, 600);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER || 
                event.getCode() == javafx.scene.input.KeyCode.SPACE ||
                event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                setState(new GameOverController(record));
            }
        });
        
        root.setFocusTraversable(true);
        root.requestFocus();
        
        return scene;
    }

    /**
     * NavigableButtonSystem과 동일한 스타일의 버튼을 생성합니다.
     * 
     * @param text 버튼에 표시할 텍스트
     * @return 스타일이 적용된 버튼
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(200, 50);
        button.setFocusTraversable(false);
        
        // 마우스 클릭 비활성화 - 클릭 이벤트를 consume하여 무효화
        button.setOnMouseClicked(event -> event.consume());
        button.setOnAction(event -> event.consume());
        
        // 선택된 스타일 적용 (버튼이 하나뿐이므로 항상 선택된 상태)
        button.setStyle("-fx-font-size: 18px; " +
                "-fx-background-color: #6a6a6a; " +
                "-fx-text-fill: yellow; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px; " +
                "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.7), 10, 0, 0, 0);");
        
        return button;
    }
}
