package org.example.view;

import java.util.List;

import org.example.service.FontManager;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ServerConnectionView extends BaseView {

    public ServerConnectionView() {
        super(true);
    }

    public VBox createView(String IPAddress, Runnable onGoBack) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("Waiting for Client...");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", FontManager.SIZE_TITLE_LARGE));

        Text ipText = new Text("IP Number: " + IPAddress);
        ipText.setFill(colorManager.getSecondaryTextColor());
        ipText.setFont(Font.font("Arial", FontManager.SIZE_TITLE_MEDIUM));

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Go Back"),
            List.of(onGoBack)
        );

        root.getChildren().add(title);
        root.getChildren().add(ipText);
        root.getChildren().addAll(created);

        return root;
    }
}
