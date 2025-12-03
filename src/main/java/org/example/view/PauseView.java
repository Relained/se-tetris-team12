package org.example.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Pause 화면의 UI를 담당하는 View 클래스
 */
public class PauseView extends BaseView {
    
    public PauseView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    /**
     * Pause 화면의 UI를 구성하고 반환합니다.
     * @param onResume Resume 버튼 클릭 시 실행될 콜백
     * @param onRestart Restart 버튼 클릭 시 실행될 콜백
     * @param onSettings Settings 버튼 클릭 시 실행될 콜백
     * @param onMainMenu Main Menu 버튼 클릭 시 실행될 콜백
     * @param onExit Exit 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(Runnable onResume, Runnable onRestart,
                          Runnable onSettings, Runnable onMainMenu, Runnable onExit) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-dark");

        Text title = new Text("PAUSED");
        title.getStyleClass().addAll("text-title-medium", "text-primary");

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Resume", "Restart", "Settings", "Main Menu", "Exit"),
            List.of(onResume, onRestart, onSettings, onMainMenu, onExit)
        );

        root.getChildren().add(title);
        root.getChildren().addAll(created);

        return root;
    }
}
