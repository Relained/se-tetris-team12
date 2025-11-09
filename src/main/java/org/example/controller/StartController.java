package org.example.controller;

import org.example.view.StartView;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * Start 화면의 입력을 처리하는 Controller
 * State 기능을 통합하여 Scene 생성 및 생명주기 관리
 */
public class StartController extends BaseController {
    
    private StartView startView;

    public StartController() {
        this.startView = new StartView();
    }
    
    @Override
    protected Scene createScene() {
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = startView.createView(
            () -> handleStartGame(),       // Start Game 버튼 콜백
            () -> handleViewScoreboard(),  // View Scoreboard 버튼 콜백
            () -> handleSetting(),         // Setting 버튼 콜백
            () -> handleExit()             // Exit 버튼 콜백
        );

        scene = new Scene(root, 1000, 700);
        // Scene 레벨에서 배경색 설정하여 플리커링 방지
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());

        // 키보드 입력은 Controller를 통해 처리
        scene.setOnKeyPressed(event -> handleKeyInput(event));

        return scene;
    }

    /**
     * Start Game 버튼 클릭 시 처리
     */
    public void handleStartGame() {
        stackState(new GameModeController());
    }
    
    /**
     * View Scoreboard 버튼 클릭 시 처리
     */
    public void handleViewScoreboard() {
        stackState(new ScoreboardController());
    }

    /**
     * Setting 버튼 클릭 시 처리
     */
    public void handleSetting() {
        stackState(new SettingController());
    }
    
    /**
     * Exit 버튼 클릭 시 처리
     */
    public void handleExit() {
        System.exit(0);
    }
    
    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        startView.getButtonSystem().handleInput(event);
    }
}

