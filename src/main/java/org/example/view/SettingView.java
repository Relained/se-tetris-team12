package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.example.service.ColorManager;
import org.example.view.component.NavigableButtonSystem;

/**
 * Setting 화면의 UI를 담당하는 View 클래스
 */
public class SettingView {
    
    private NavigableButtonSystem buttonSystem;
    private ColorManager colorManager;
    
    public SettingView() {
        this.buttonSystem = new NavigableButtonSystem();
        this.colorManager = ColorManager.getInstance();
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

        var screenSizeButton = buttonSystem.createNavigableButton("Screen Size", onScreenSize);
        var controlsButton = buttonSystem.createNavigableButton("Controls", onControls);
        var colorBlindButton = buttonSystem.createNavigableButton("Color Blind Setting", onColorBlindSetting);
        var resetScoreButton = buttonSystem.createNavigableButton("Reset Score Board", onResetScoreBoard);
        var resetAllButton = buttonSystem.createNavigableButton("Reset All Setting", onResetAllSetting);
        var goBackButton = buttonSystem.createNavigableButton("Go Back", onGoBack);

        root.getChildren().add(title);
        root.getChildren().addAll(
            screenSizeButton,
            controlsButton,
            colorBlindButton,
            resetScoreButton,
            resetAllButton,
            goBackButton
        );

        return root;
    }
    
    /**
     * NavigableButtonSystem을 반환합니다.
     * Controller에서 키보드 입력을 처리하는 데 사용됩니다.
     */
    public NavigableButtonSystem getButtonSystem() {
        return buttonSystem;
    }
}
