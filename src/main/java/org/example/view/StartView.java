package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.service.FontManager;

public class StartView extends BaseView {
    
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
        title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_XLARGE * scale));
        subtitle.setFont(fontManager.getFont(FontManager.SIZE_BODY_MEDIUM * scale));
        controls.setFont(fontManager.getFont(FontManager.SIZE_BODY_SMALL * scale));
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
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        title = new Text("TETRIS");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_XLARGE * currentScale));

        subtitle = new Text("Team 12 Edition");
        subtitle.setFill(colorManager.getSecondaryTextColor());
        subtitle.setFont(fontManager.getFont(FontManager.SIZE_BODY_MEDIUM * currentScale));

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Start Game", "MultiPlay", "View Scoreboard", "Setting", "Exit"),
            List.of(onStartGame, onMultiPlay, onViewScoreboard, onSetting, onExit)
        );

        controls = new Text("Controls:\n" +
                "← → Move\n" +
                "↓ Soft Drop\n" +
                "Space Hard Drop\n" +
                "Z/X Rotate\n" +
                "C Hold\n" +
                "ESC Pause");
        controls.setFill(colorManager.getSecondaryTextColor());
        controls.setFont(fontManager.getFont(FontManager.SIZE_BODY_SMALL * currentScale));

        root.getChildren().addAll(title, subtitle);
        root.getChildren().addAll(created);
        root.getChildren().add(controls);

        return root;
    }
}
