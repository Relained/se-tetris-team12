package org.example.view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
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
        root.getStyleClass().add("root-dark");

        Text title = new Text("PAUSED");
        title.getStyleClass().addAll("text-primary", "text-title-large");

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
