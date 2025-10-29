package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DifficultyView extends BaseView {
    
    public DifficultyView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    /**
     * Difficulty 화면의 UI를 구성하고 반환합니다.
     * @param onEasy Easy 난이도 선택
     * @param onMedium Medium 난이도 선택
     * @param onHard Hard 난이도 선택
     * @param onGoBack 뒤로가기 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(Runnable onEasy, Runnable onMedium, Runnable onHard, Runnable onGoBack) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("Select Difficulty");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 36));

        var easyButton = buttonSystem.createNavigableButton("Easy", onEasy);
        var mediumButton = buttonSystem.createNavigableButton("Medium", onMedium);
        var hardButton = buttonSystem.createNavigableButton("Hard", onHard);
        var goBackButton = buttonSystem.createNavigableButton("Go Back", onGoBack);

        root.getChildren().addAll(title, easyButton, mediumButton, hardButton, goBackButton);

        return root;
    }
}
