package org.example.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.example.model.GameMode;
import org.example.view.component.RadioButtonGroupSystem;
import org.example.view.component.RadioButtonGroupSystem.RadioButtonGroupWrapper;

/**
 * 게임 모드와 난이도를 함께 선택하는 통합 View
 * 1. 게임 모드 라디오 그룹 (Normal / Item)
 * 2. 난이도 라디오 그룹 (Easy / Medium / Hard)
 * 3. Start 버튼
 * 4. Go Back 버튼
 * 
 * 이 뷰는 GameSetupController와 LocalMultiSetupController에서 재사용됨
 */
public class GameSetupView extends BaseView {
    
    private RadioButtonGroupSystem radioButtonSystem;
    private RadioButtonGroupWrapper<GameMode> gameModeRadio;
    private RadioButtonGroupWrapper<Integer> difficultyRadio;
    
    public GameSetupView() {
        super(true); // NavigableButtonSystem 사용
        this.radioButtonSystem = new RadioButtonGroupSystem();
    }
    
    /**
     * 게임 설정 화면의 UI를 구성하고 반환합니다.
     * @param onStart 게임 시작 콜백
     * @param onGoBack 뒤로가기 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(Runnable onStart, Runnable onGoBack) {
        return createView("Game Setup", onStart, onGoBack);
    }
    
    /**
     * 게임 설정 화면의 UI를 구성하고 반환합니다.
     * @param title 화면의 제목
     * @param onStart 게임 시작 콜백
     * @param onGoBack 뒤로가기 콜백
     * @return 구성된 VBox root
     */
    public VBox createView(String title, Runnable onStart, Runnable onGoBack) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));
        root.setPadding(new Insets(20));

        // 타이틀
        Text titleText = new Text(title);
        titleText.setFill(colorManager.getPrimaryTextColor());
        titleText.setFont(Font.font("Arial", 36));

        // 1. 게임 모드 섹션
        VBox gameModeSection = createGameModeSection();
        
        // 2. 난이도 섹션
        VBox difficultySection = createDifficultySection();
        
        // 3 & 4. 버튼 섹션 (Start, Go Back)
        VBox buttonSection = createButtonSection(onStart, onGoBack);

        root.getChildren().addAll(titleText, gameModeSection, difficultySection, buttonSection);

        // 라디오 그룹에 포커스 설정
        radioButtonSystem.focusFirst();
        // 버튼의 포커스 스타일 해제
        buttonSystem.unfocusAll();

        return root;
    }
    
    /**
     * 게임 모드 라디오 섹션을 생성합니다.
     */
    private VBox createGameModeSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        
        // 섹션 라벨
        Text label = new Text("Game Mode");
        label.setFill(colorManager.getPrimaryTextColor());
        label.setFont(Font.font("Arial", 20));
        
        // 라디오 버튼 그룹 생성
        List<GameMode> modes = List.of(GameMode.NORMAL, GameMode.ITEM);
        gameModeRadio = radioButtonSystem.createRadioButtonGroup(
            modes, 
            0, // 기본값: NORMAL
            GameMode::toString
        );
        
        // 라디오 버튼들을 가로로 배치
        HBox radioContainer = new HBox(30);
        radioContainer.setAlignment(Pos.CENTER);
        radioContainer.getChildren().addAll(gameModeRadio.getButtons());
        
        section.getChildren().addAll(label, radioContainer);
        
        return section;
    }
    
    /**
     * 난이도 라디오 섹션을 생성합니다.
     */
    private VBox createDifficultySection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        
        // 섹션 라벨
        Text label = new Text("Difficulty");
        label.setFill(colorManager.getPrimaryTextColor());
        label.setFont(Font.font("Arial", 20));
        
        // 라디오 버튼 그룹 생성 (1=Easy, 2=Medium, 3=Hard)
        List<Integer> difficulties = List.of(1, 2, 3);
        difficultyRadio = radioButtonSystem.createRadioButtonGroup(
            difficulties, 
            0, // 기본값: Easy
            d -> {
                switch (d) {
                    case 1: return "Easy";
                    case 2: return "Medium";
                    case 3: return "Hard";
                    default: return "Unknown";
                }
            }
        );
        
        // 라디오 버튼들을 가로로 배치
        HBox radioContainer = new HBox(30);
        radioContainer.setAlignment(Pos.CENTER);
        radioContainer.getChildren().addAll(difficultyRadio.getButtons());
        
        section.getChildren().addAll(label, radioContainer);
        
        return section;
    }
    
    /**
     * 버튼 섹션을 생성합니다. (Start, Go Back)
     */
    private VBox createButtonSection(Runnable onStart, Runnable onGoBack) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        
        var buttons = buttonSystem.createNavigableButtonFromList(
            List.of("Start", "Go Back"),
            List.of(onStart, onGoBack)
        );
        
        section.getChildren().addAll(buttons);
        
        return section;
    }
    
    /**
     * 현재 선택된 게임 모드를 반환합니다.
     */
    public GameMode getSelectedGameMode() {
        return gameModeRadio.getSelectedOption();
    }
    
    /**
     * 현재 선택된 난이도를 반환합니다. (1=Easy, 2=Medium, 3=Hard)
     */
    public int getSelectedDifficulty() {
        return difficultyRadio.getSelectedOption();
    }
    
    /**
     * 게임 모드 라디오 그룹을 반환합니다.
     */
    public RadioButtonGroupWrapper<GameMode> getGameModeRadio() {
        return gameModeRadio;
    }
    
    /**
     * 난이도 라디오 그룹을 반환합니다.
     */
    public RadioButtonGroupWrapper<Integer> getDifficultyRadio() {
        return difficultyRadio;
    }
    
    /**
     * RadioButtonGroupSystem을 반환합니다.
     */
    public RadioButtonGroupSystem getRadioButtonSystem() {
        return radioButtonSystem;
    }
}
