package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StartView extends BaseView {
    
    // 기준 폰트 크기 (MEDIUM 기준)
    private static final double BASE_TITLE_FONT_SIZE = 48.0;
    private static final double BASE_SUBTITLE_FONT_SIZE = 16.0;
    private static final double BASE_CONTROLS_FONT_SIZE = 14.0;
    
    private Text title;
    private Text subtitle;
    private Text controls;
    
    public StartView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    @Override
    protected void onScaleChanged(double scale) {
        // UI 요소가 아직 생성되지 않았으면 스킵
        if (title == null || subtitle == null || controls == null) {
            return;
        }
        
        // 스케일에 맞춰 폰트 크기 조정
        title.setFont(Font.font("Arial", BASE_TITLE_FONT_SIZE * scale));
        subtitle.setFont(Font.font("Arial", BASE_SUBTITLE_FONT_SIZE * scale));
        controls.setFont(Font.font("Arial", BASE_CONTROLS_FONT_SIZE * scale));
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

        title = new Text("TETRIS");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", BASE_TITLE_FONT_SIZE * currentScale));

        subtitle = new Text("Team 12 Edition");
        subtitle.setFill(colorManager.getSecondaryTextColor());
        subtitle.setFont(Font.font("Arial", BASE_SUBTITLE_FONT_SIZE * currentScale));

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Start Game", "View Scoreboard", "Setting", "Exit"),
            List.of(onStartGame, onViewScoreboard, onSetting, onExit)
        );

        controls = new Text("Controls:\n" +
                "← → Move\n" +
                "↓ Soft Drop\n" +
                "Space Hard Drop\n" +
                "Z/X Rotate\n" +
                "C Hold\n" +
                "ESC Pause");
        controls.setFill(colorManager.getSecondaryTextColor());
        controls.setFont(Font.font("Arial", BASE_CONTROLS_FONT_SIZE * currentScale));

        root.getChildren().addAll(title, subtitle);
        root.getChildren().addAll(created);
        root.getChildren().add(controls);

        return root;
    }
}
