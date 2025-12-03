package org.example.view;

import java.util.List;

import org.example.service.DisplayManager;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ServerConnectionView extends BaseView {

    private Text title;
    private Text ipText;

    public ServerConnectionView() {
        super(true);
    }

    public VBox createView(String IPAddress, Runnable onGoBack) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-dark");

        title = new Text("Waiting for Client...");
        title.setTextAlignment(TextAlignment.CENTER);
        title.getStyleClass().addAll("text-primary", "text-title-large");
        var dm = DisplayManager.getInstance();
        title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);

        ipText = new Text("IP Number: " + IPAddress);
        ipText.getStyleClass().addAll("text-secondary", "text-title-medium");

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Go Back"),
            List.of(onGoBack)
        );

        root.getChildren().add(title);
        root.getChildren().add(ipText);
        root.getChildren().addAll(created);

        return root;
    }

    public void setTitle(String text) {
        if (title == null) 
            return;
        title.setText(text);
    }

    @Override
    protected void onScaleChanged(double scale) {
        var dm = DisplayManager.getInstance();
        if (title != null) {
            title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);
        }
    }
}
