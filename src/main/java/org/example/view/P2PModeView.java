package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class P2PModeView extends BaseView {

    public P2PModeView() {
        super(true);
    }

    public VBox createView(Runnable onServer, Runnable onClient, Runnable onGoBack) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-dark");

        Text title = new Text("Select Connection Type");
        title.getStyleClass().addAll("text-title-medium", "text-primary");

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Server", "Client", "Go Back"),
            List.of(onServer, onClient, onGoBack)
        );

        root.getChildren().add(title);
        root.getChildren().addAll(created);

        return root;
    }
}
