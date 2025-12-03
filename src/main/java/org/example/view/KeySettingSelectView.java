package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.example.view.component.NavigableButtonSystem;

/**
 * Key Setting에서 1P/2P를 선택하는 화면의 UI를 담당하는 View 클래스
 */
public class KeySettingSelectView extends BaseView {
    
    private Text title;
    private NavigableButtonSystem buttonSystem;
    
    public KeySettingSelectView() {
        super(true); // NavigableButtonSystem 사용
    }
    
    /**
     * Key Setting Select 화면의 UI를 구성하고 반환합니다.
     * 
     * @param onPlayer1 Player 1 키 설정 선택 시 실행할 콜백
     * @param onPlayer2 Player 2 키 설정 선택 시 실행할 콜백
     * @param onGoBack 뒤로가기 선택 시 실행할 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(Runnable onPlayer1, Runnable onPlayer2, Runnable onGoBack) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.getStyleClass().add("root-dark");

        // 제목
        title = new Text("Key Settings");
        title.getStyleClass().addAll("text-title-medium", "text-primary");

        // 안내 텍스트
        Text instructionText = new Text("Select player to configure");
        instructionText.getStyleClass().addAll("text-body-medium", "text-secondary");

        // 버튼 시스템 초기화
        buttonSystem = getButtonSystem();

        // 버튼 생성
        java.util.List<String> buttonTexts = java.util.Arrays.asList(
            "Single Settings",
            "2P Settings",
            "Go Back"
        );
        java.util.List<Runnable> buttonActions = java.util.Arrays.asList(
            onPlayer1,
            onPlayer2,
            onGoBack
        );

        java.util.ArrayList<javafx.scene.control.Button> buttons =
            buttonSystem.createNavigableButtonFromList(buttonTexts, buttonActions);

        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(buttons);

        root.getChildren().addAll(title, instructionText, buttonContainer);

        return root;
    }
    
    /**
     * 버튼 시스템에 대한 접근자
     */
    public NavigableButtonSystem getNavigableButtonSystem() {
        return buttonSystem;
    }
}
