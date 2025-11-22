package org.example.view;

import java.util.function.Consumer;

import org.example.service.DisplayManager;
import org.example.service.FontManager;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ClientConnectionView extends BaseView {

    private Text title;
    private TextField ipAddressField;

    public ClientConnectionView() {
        super(false);
    }

    public VBox createView(Consumer<String> onIpSubmit) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
                new BackgroundFill(colorManager.getBackgroundColor(), null, null)));

        title = new Text("Please enter server's IP address:");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_LARGE * currentScale));
        var dm = DisplayManager.getInstance();
        title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        ipAddressField = new TextField();
        ipAddressField.setPromptText("Enter IP address");
        ipAddressField.setMaxWidth(300);
        ipAddressField.setAlignment(Pos.CENTER);
        ipAddressField.setFont(fontManager.getMonospaceBoldFont(FontManager.SIZE_BODY_LARGE * currentScale));
        ipAddressField.setStyle("-fx-padding: 10px;");
        ipAddressField.setOnAction(event -> {
            onIpSubmit.accept(ipAddressField.getText().trim());
        });

        Text hint = new Text("Press ESC to Go Back");
        hint.setFill(colorManager.getSecondaryTextColor());
        hint.setFont(fontManager.getFont(FontManager.SIZE_BODY_SMALL * currentScale));

        root.getChildren().add(title);
        root.getChildren().add(ipAddressField);
        root.getChildren().add(hint);

        return root;
    }

    public void setTitleToConnecting() {
        if (title == null)
            return;
        title.setText("Trying to connect...");
    }

    public void setTitleToInvalidIP() {
        if (title == null)
            return;
        title.setText("Invalid IP address.\nPlease try again.");
    }

    public void setTitleText(String text) {
        if (title == null)
            return;
        title.setText(text);
    }

    public void clearIpField() {
        if (ipAddressField == null)
            return;
        ipAddressField.clear();
    }

    @Override
    protected void onScaleChanged(double scale) {
        if (title != null) {
            title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_LARGE * scale));
            var dm = DisplayManager.getInstance();
            title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);
        }
        if (ipAddressField != null) {
            ipAddressField.setFont(fontManager.getMonospaceBoldFont(FontManager.SIZE_BODY_LARGE * scale));
        }
    }
}
