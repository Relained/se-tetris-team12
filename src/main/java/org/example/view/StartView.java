package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class StartView extends BaseView {

    public StartView() {
        super(true); // NavigableButtonSystem 사용
    }
    /**
     * Start 화면의 UI를 구성하고 반환합니다.
     * @param onStartGame Start Game 버튼 클릭 시 실행될 콜백
     * @param onMultiPlay MultiPlay 버튼 클릭 시 실행될 콜백
     * @param onViewScoreboard View Scoreboard 버튼 클릭 시 실행될 콜백
     * @param onSetting Setting 버튼 클릭 시 실행될 콜백
     * @param onExit Exit 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(Runnable onStartGame, Runnable onMultiPlay, Runnable onViewScoreboard, Runnable onSetting, Runnable onExit) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-dark");

        Text title = new Text("TETRIS");
        title.getStyleClass().addAll("text-title-large", "text-primary");

        Text subtitle = new Text("Team 12 Edition");
        subtitle.getStyleClass().addAll("text-body-medium", "text-secondary");

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Start Game", "MultiPlay", "View Scoreboard", "Setting", "Exit"),
            List.of(onStartGame, onMultiPlay, onViewScoreboard, onSetting, onExit)
        );

        Text controls = new Text("Controls:\n" +
                "← → Move\n" +
                "↓ Soft Drop\n" +
                "Space Hard Drop\n" +
                "Z/X Rotate\n" +
                "C Hold\n" +
                "ESC Pause");
        controls.getStyleClass().addAll("text-body-small", "text-secondary");

        root.getChildren().addAll(title, subtitle);
        root.getChildren().addAll(created);
        root.getChildren().add(controls);

        return root;
    }
}
