package org.example.view;

import java.util.List;

import org.example.service.DisplayManager;
import org.example.service.FontManager;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        title = new Text("Waiting for Client...");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_LARGE * currentScale));
        var dm = DisplayManager.getInstance();
        title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);

        ipText = new Text("IP Number: " + IPAddress);
        ipText.setFill(colorManager.getSecondaryTextColor());
        ipText.setFont(fontManager.getFont(FontManager.SIZE_TITLE_MEDIUM * currentScale));

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
            title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_LARGE * scale));
        }
        if (ipText != null) {
            ipText.setFont(fontManager.getFont(FontManager.SIZE_TITLE_MEDIUM * scale));
        }
    }
}
