package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Local MultiPlay Game Mode 선택 화면의 UI를 담당하는 View 클래스
 */
public class LocalMultiGameModeView extends BaseView {

    public LocalMultiGameModeView() {
        super(true);
    }

    public VBox createView(Runnable onNormal, Runnable onItem, Runnable onGoBack) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("Select Local MultiPlay Mode");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 36));

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Normal", "Item", "Go Back"),
            List.of(onNormal, onItem, onGoBack)
        );

        root.getChildren().add(title);
        root.getChildren().addAll(created);

        return root;
    }
}
