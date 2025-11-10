package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Setting 화면의 UI를 담당하는 View 클래스
 */
public class SettingView extends BaseView {
    
    public SettingView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    /**
     * Setting 화면의 UI를 구성하고 반환합니다.
     * @param onScreenSize Screen Size 버튼 클릭 시 실행될 콜백
     * @param onControls Controls 버튼 클릭 시 실행될 콜백
     * @param onColorBlindSetting Color Blind Setting 버튼 클릭 시 실행될 콜백
     * @param onResetScoreBoard Reset Score Board 버튼 클릭 시 실행될 콜백
     * @param onResetAllSetting Reset All Setting 버튼 클릭 시 실행될 콜백
     * @param onGoBack Go Back 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(Runnable onScreenSize, 
                          Runnable onControls,
                          Runnable onColorBlindSetting,
                          Runnable onResetScoreBoard,
                          Runnable onResetAllSetting,
                          Runnable onGoBack) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("Settings");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 36));

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Screen Size", "Controls", "Color Blind Setting", 
                    "Reset Score Board", "Reset All Setting", "Go Back"),
            List.of(onScreenSize, onControls, onColorBlindSetting, 
                    onResetScoreBoard, onResetAllSetting, onGoBack)
        );

        root.getChildren().add(title);
        root.getChildren().addAll(created);

        return root;
    }
}
