package org.example.view;

import org.example.model.SettingData.ColorBlindMode;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Color Setting 화면의 UI를 담당하는 View 클래스
 */
public class ColorSettingView extends BaseView {
    
    private Text title;
    
    public ColorSettingView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    /**
     * Color Setting 화면의 UI를 구성하고 반환합니다.
     * @param currentMode 현재 선택된 색맹 모드
     * @param onDefault Default 버튼 클릭 시 실행될 콜백
     * @param onProtanopia Protanopia 버튼 클릭 시 실행될 콜백
     * @param onDeuteranopia Deuteranopia 버튼 클릭 시 실행될 콜백
     * @param onTritanopia Tritanopia 버튼 클릭 시 실행될 콜백
     * @param onGoBack Go Back 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(ColorBlindMode currentMode,
                          Runnable onDefault,
                          Runnable onProtanopia,
                          Runnable onDeuteranopia,
                          Runnable onTritanopia,
                          Runnable onGoBack) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        title = new Text("Color Settings\nCurrent: " + currentMode.name());
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 36));

        var defaultButton = buttonSystem.createNavigableButton("Default", onDefault);
        var protanopiaButton = buttonSystem.createNavigableButton("PROTANOPIA", onProtanopia);
        var deuteranopiaButton = buttonSystem.createNavigableButton("DEUTERANOPIA", onDeuteranopia);
        var tritanopiaButton = buttonSystem.createNavigableButton("TRITANOPIA", onTritanopia);
        var goBackButton = buttonSystem.createNavigableButton("Go Back", onGoBack);

        root.getChildren().add(title);
        root.getChildren().addAll(
            defaultButton,
            protanopiaButton,
            deuteranopiaButton,
            tritanopiaButton,
            goBackButton
        );

        return root;
    }
    
    /**
     * 현재 선택된 색맹 모드를 표시하도록 제목을 업데이트합니다.
     * @param mode 업데이트할 색맹 모드
     */
    public void updateCurrentMode(ColorBlindMode mode) {
        if (title != null) {
            title.setText("Color Settings\nCurrent: " + mode.name());
        }
    }
}
