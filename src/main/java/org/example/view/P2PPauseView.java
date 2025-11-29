package org.example.view;

import java.util.List;

import org.example.service.FontManager;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class P2PPauseView extends BaseView {
    
    public P2PPauseView() {
        super(true);
    }

    public VBox createView(Runnable onResume, Runnable onGoWaitingRoom, 
                          Runnable onSettings, Runnable onMainMenu, Runnable onExit) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("PAUSED");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(FontManager.getInstance().getFont(FontManager.SIZE_TITLE_LARGE));

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Resume", "Go Waiting Room", "Settings", "Main Menu", "Exit"),
            List.of(onResume, onGoWaitingRoom, onSettings, onMainMenu, onExit)
        );

        root.getChildren().add(title);
        root.getChildren().addAll(created);

        return root;
    }
}
