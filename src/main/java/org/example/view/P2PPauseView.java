package org.example.view;

import java.util.ArrayList;
import java.util.List;

import org.example.service.FontManager;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class P2PPauseView extends BaseView {
    
    public P2PPauseView() {
        super(true);
    }

    public Parent createView(Runnable onResume, Runnable onGoWaitingRoom, 
                          Runnable onSettings, Runnable onMainMenu, Runnable onExit, boolean isServer) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("PAUSED");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(FontManager.getInstance().getFont(FontManager.SIZE_TITLE_LARGE));

        List<String> buttonNames = new ArrayList<>(List.of("Resume", "Settings", "Main Menu", "Exit"));
        List<Runnable> buttonActions = new ArrayList<>(List.of(onResume, onSettings, onMainMenu, onExit));
        if (isServer) {
            buttonNames.add(1, "Go Waiting Room");
            buttonActions.add(1, onGoWaitingRoom);
        }

        var created = buttonSystem.createNavigableButtonFromList(buttonNames,buttonActions);

        root.getChildren().add(title);
        root.getChildren().addAll(created);

        return root;
    }
}
