package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StartView extends BaseView {
    
    public StartView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    /**
     * Start 화면의 UI를 구성하고 반환합니다.
     * @param onStartGame Start Game 버튼 클릭 시 실행될 콜백
     * @param onViewScoreboard View Scoreboard 버튼 클릭 시 실행될 콜백
     * @param onSetting Setting 버튼 클릭 시 실행될 콜백
     * @param onExit Exit 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(Runnable onStartGame, Runnable onViewScoreboard, Runnable onSetting, Runnable onExit) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("TETRIS");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 48));

        Text subtitle = new Text("Team 12 Edition");
        subtitle.setFill(colorManager.getSecondaryTextColor());
        subtitle.setFont(Font.font("Arial", 16));

    var startButton = buttonSystem.createNavigableButton("Start Game", onStartGame);
        var scoreboardButton = buttonSystem.createNavigableButton("View Scoreboard", onViewScoreboard);
        var settingButton = buttonSystem.createNavigableButton("Setting", onSetting);
        var exitButton = buttonSystem.createNavigableButton("Exit", onExit);

        Text controls = new Text("Controls:\n" +
                "← → Move\n" +
                "↓ Soft Drop\n" +
                "Space Hard Drop\n" +
                "Z/X Rotate\n" +
                "C Hold\n" +
                "ESC Pause");
        controls.setFill(colorManager.getSecondaryTextColor());
        controls.setFont(Font.font("Arial", 14));

        root.getChildren().addAll(title, subtitle, startButton, scoreboardButton, settingButton, exitButton, controls);

        return root;
    }
}
