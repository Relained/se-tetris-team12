package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameModeView extends BaseView {

    public GameModeView() {
        super(true);
    }

    public VBox createView(Runnable onNormal, Runnable onItem, Runnable onGoBack) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("Select Game Mode");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 36));

        var normalButton = buttonSystem.createNavigableButton("Normal", onNormal);
        var itemButton = buttonSystem.createNavigableButton("Item", onItem);
        var goBackButton = buttonSystem.createNavigableButton("Go Back", onGoBack);

        root.getChildren().addAll(title, normalButton, itemButton, goBackButton);

        return root;
    }
}
