package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * MultiPlay Mode 선택 화면의 UI를 담당하는 View 클래스
 * 로컬 또는 온라인 멀티플레이 방식을 선택합니다.
 */
public class MultiPlayModeView extends BaseView {

    public MultiPlayModeView() {
        super(true);
    }

    public VBox createView(Runnable onLocal, Runnable onOnline, Runnable onGoBack) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("Select MultiPlay Mode");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 36));

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Local MultiPlay", "Online MultiPlay", "Go Back"),
            List.of(onLocal, onOnline, onGoBack)
        );

        root.getChildren().add(title);
        root.getChildren().addAll(created);

        return root;
    }
}
