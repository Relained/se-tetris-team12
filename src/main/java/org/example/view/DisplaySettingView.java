package org.example.view;

import java.util.List;

import org.example.model.SettingData.ScreenSize;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Display Setting 화면의 UI를 담당하는 View 클래스
 */
public class DisplaySettingView extends BaseView {
    
    private Text title;
    
    public DisplaySettingView() {
        super(true); // NavigableButtonSystem 사용
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
        root.getStyleClass().add("root-dark");

        title = new Text("Display Settings\nCurrent: " + currentSize.name());
        title.getStyleClass().addAll("text-title-medium", "text-primary");

        var created = buttonSystem.createNavigableButtonFromList(
            List.of(
                String.format("Small"),
                String.format("Medium"),
                String.format("Large"),
                "Go Back"
            ),
            List.of(onSmall, onMedium, onLarge, onGoBack)
        );

        root.getChildren().add(title);
        root.getChildren().addAll(created);

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
