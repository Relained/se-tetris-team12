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
 * Pause 화면의 UI를 담당하는 View 클래스
 */
public class PauseView {
    
    private NavigableButtonSystem buttonSystem;
    private ColorManager colorManager;
    
    public PauseView() {
        this.buttonSystem = new NavigableButtonSystem();
        this.colorManager = ColorManager.getInstance();
    }
    
    /**
     * Pause 화면의 UI를 구성하고 반환합니다.
     * @param onResume Resume 버튼 클릭 시 실행될 콜백
     * @param onRestart Restart 버튼 클릭 시 실행될 콜백
     * @param onSettings Settings 버튼 클릭 시 실행될 콜백
     * @param onMainMenu Main Menu 버튼 클릭 시 실행될 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(Runnable onResume, Runnable onRestart, 
                          Runnable onSettings, Runnable onMainMenu) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("PAUSED");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 36));

        var resumeButton = buttonSystem.createNavigableButton("Resume", onResume);
        var restartButton = buttonSystem.createNavigableButton("Restart", onRestart);
        var settingsButton = buttonSystem.createNavigableButton("Settings", onSettings);
        var mainMenuButton = buttonSystem.createNavigableButton("Main Menu", onMainMenu);

        root.getChildren().add(title);
        root.getChildren().addAll(resumeButton, restartButton, settingsButton, mainMenuButton);

        return root;
    }
    
    /**
     * NavigableButtonSystem을 반환합니다.
     * Controller에서 키보드 입력을 처리하는 데 사용됩니다.
     */
    public NavigableButtonSystem getButtonSystem() {
        return buttonSystem;
    }
    
    /**
     * 색상 설정이 변경되었을 때 호출하여 UI를 갱신합니다.
     */
    public void refreshColors() {
        // 색상이 변경되면 ColorManager에서 자동으로 새 색상을 가져옴
        // 필요시 UI 컴포넌트 재생성
    }
}
