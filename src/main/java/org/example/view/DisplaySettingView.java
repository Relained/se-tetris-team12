package org.example.view;

import org.example.model.SettingData.ScreenSize;
import org.example.service.DisplayManager;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Display Setting 화면의 UI를 담당하는 View 클래스
 */
public class DisplaySettingView extends BaseView {
    
    private Text title;
    private DisplayManager displayManager;
    
    public DisplaySettingView() {
        super(true); // NavigableButtonSystem 사용
        this.displayManager = DisplayManager.getInstance();
    }
    
    /**
     * Display Setting 화면의 UI를 구성하고 반환합니다.
     * @param currentSize 현재 선택된 화면 크기
     * @param onSmall Small 버튼 클릭 시 실행될 콜백
     * @param onMedium Medium 버튼 클릭 시 실행될 콜백
     * @param onLarge Large 버튼 클릭 시 실행될 콜백
     * @param onGoBack Go Back 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(ScreenSize currentSize,
                          Runnable onSmall,
                          Runnable onMedium,
                          Runnable onLarge,
                          Runnable onGoBack) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        title = new Text("Display Settings\nCurrent: " + currentSize.name());
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 36));

        // DisplayManager를 활용하여 각 크기의 실제 해상도 표시
        int smallW = displayManager.getWidth(ScreenSize.SMALL);
        int smallH = displayManager.getHeight(ScreenSize.SMALL);
        int mediumW = displayManager.getWidth(ScreenSize.MEDIUM);
        int mediumH = displayManager.getHeight(ScreenSize.MEDIUM);
        int largeW = displayManager.getWidth(ScreenSize.LARGE);
        int largeH = displayManager.getHeight(ScreenSize.LARGE);

        var smallButton = buttonSystem.createNavigableButton(
            String.format("Small (%dx%d)", smallW, smallH), onSmall);
        var mediumButton = buttonSystem.createNavigableButton(
            String.format("Medium (%dx%d)", mediumW, mediumH), onMedium);
        var largeButton = buttonSystem.createNavigableButton(
            String.format("Large (%dx%d)", largeW, largeH), onLarge);
        var goBackButton = buttonSystem.createNavigableButton("Go Back", onGoBack);

        root.getChildren().add(title);
        root.getChildren().addAll(
            smallButton,
            mediumButton,
            largeButton,
            goBackButton
        );

        return root;
    }
    
    /**
     * 현재 선택된 화면 크기를 표시하도록 제목을 업데이트합니다.
     * @param size 업데이트할 화면 크기
     */
    public void updateCurrentSize(ScreenSize size) {
        if (title != null) {
            title.setText("Display Settings\nCurrent: " + size.name());
        }
    }
}
