package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.view.component.NavigableButtonSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Multiplayer Key Setting 화면의 UI를 담당하는 View 클래스
 * Player 1/2의 멀티플레이 전용 키를 좌우 그리드 레이아웃으로 배치합니다.
 */
public class KeySettingView2 extends BaseView {
    
    private Text title;
    private Text statusText;
    
    // Player 1/2 키 바인딩 텍스트
    private Map<String, Text> player1KeyTexts;
    private Map<String, Text> player2KeyTexts;
    
    // NavigableButtonSystem for 2D grid navigation
    private NavigableButtonSystem navSystem;
    
    // 현재 키 입력 대기 중인 플레이어와 액션
    private Integer waitingPlayer = null; // 1 or 2
    private String waitingAction = null;
    
    public KeySettingView2() {
        super(true); // NavigableButtonSystem 사용
        this.player1KeyTexts = new HashMap<>();
        this.player2KeyTexts = new HashMap<>();
    }
    
    /**
     * Multiplayer Key Setting 화면의 UI를 구성하고 반환합니다.
     * Player 1/2의 키를 좌우 2열 그리드로 배치합니다.
     * @param player1Bindings Player 1의 키 바인딩
     * @param player2Bindings Player 2의 키 바인딩
     * @param onResetToDefault 기본값으로 리셋 콜백
     * @param onGoBack 뒤로가기 콜백
     */
    public VBox createView(Map<String, KeyCode> player1Bindings, 
                          Map<String, KeyCode> player2Bindings,
                          Runnable onResetToDefault, Runnable onGoBack) {
        
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        // 제목
        title = new Text("Multiplayer Key Settings");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 36));

        // 상태 텍스트
        statusText = new Text("Use ↑↓←→ to navigate, ENTER to change key, ESC to go back");
        statusText.setFill(Color.LIGHTGRAY);
        statusText.setFont(Font.font("Arial", 16));

        // GridPane으로 Player 1과 Player 2의 키를 좌우로 배치
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(40);
        gridPane.setVgap(10);
        
        // Player 1 컬럼
        Text p1Header = new Text("Player 1");
        p1Header.setFill(colorManager.getPrimaryTextColor());
        p1Header.setFont(Font.font("Arial", 24));
        gridPane.add(p1Header, 0, 0);
        
        // Player 2 컬럼
        Text p2Header = new Text("Player 2");
        p2Header.setFill(colorManager.getPrimaryTextColor());
        p2Header.setFont(Font.font("Arial", 24));
        gridPane.add(p2Header, 1, 0);
        
        // NavigableButtonSystem 초기화 (2열 그리드)
        navSystem = buttonSystem;
        navSystem.setGridColumns(2);
        navSystem.setHorizontalNavigation(true);
        
        // 각 액션에 대한 버튼 생성 (Player 1, Player 2 순서로)
        String[] actions = getActions();
        int row = 1;
        for (String action : actions) {
            String displayName = getActionDisplayName(action);
            
            // Player 1 버튼
            KeyCode p1Key = player1Bindings.get(action);
            Button p1Button = navSystem.createNavigableButton(
                displayName + ": " + (p1Key != null ? p1Key.getName() : "Not Set"),
                () -> startKeyBinding(1, action)
            );
            gridPane.add(p1Button, 0, row);
            player1KeyTexts.put(action, null); // 버튼 자체가 텍스트를 포함
            
            // Player 2 버튼
            KeyCode p2Key = player2Bindings.get(action);
            Button p2Button = navSystem.createNavigableButton(
                displayName + ": " + (p2Key != null ? p2Key.getName() : "Not Set"),
                () -> startKeyBinding(2, action)
            );
            gridPane.add(p2Button, 1, row);
            player2KeyTexts.put(action, null);
            
            row++;
        }
        
        // 하단 버튼들
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button resetButton = navSystem.createNavigableButton("Reset to Default", onResetToDefault);
        Button backButton = navSystem.createNavigableButton("Go Back", onGoBack);
        
        buttonBox.getChildren().addAll(resetButton, backButton);

        root.getChildren().addAll(
            title,
            statusText,
            gridPane,
            buttonBox
        );

        return root;
    }
    
    /**
     * 액션 목록을 반환합니다.
     */
    public static String[] getActions() {
        return new String[] {
            "moveLeft",
            "moveRight",
            "softDrop",
            "hardDrop",
            "rotateCounterClockwise",
            "rotateClockwise",
            "hold",
            "pause"
        };
    }
    
    /**
     * 액션의 표시 이름을 반환합니다.
     */
    public static String getActionDisplayName(String action) {
        switch (action) {
            case "moveLeft": return "Move Left";
            case "moveRight": return "Move Right";
            case "softDrop": return "Soft Drop";
            case "hardDrop": return "Hard Drop";
            case "rotateCounterClockwise": return "Rotate CCW";
            case "rotateClockwise": return "Rotate CW";
            case "hold": return "Hold";
            case "pause": return "Pause";
            default: return action;
        }
    }
    
    /**
     * 키 바인딩 입력 대기를 시작합니다.
     */
    private void startKeyBinding(int playerNumber, String action) {
        waitingPlayer = playerNumber;
        waitingAction = action;
        statusText.setText("Player " + playerNumber + " - Press a key for " + 
                          getActionDisplayName(action) + " (ESC to cancel)");
        statusText.setFill(Color.ORANGE);
    }
    
    /**
     * 현재 키 입력 대기 중인지 확인합니다.
     */
    public boolean isWaitingForKey() {
        return waitingPlayer != null && waitingAction != null;
    }
    
    /**
     * 키 입력 대기 중인 플레이어 번호를 반환합니다.
     */
    public Integer getWaitingPlayer() {
        return waitingPlayer;
    }
    
    /**
     * 키 입력 대기 중인 액션을 반환합니다.
     */
    public String getWaitingAction() {
        return waitingAction;
    }
    
    /**
     * 키 입력 대기를 취소합니다.
     */
    public void cancelKeyBinding() {
        waitingPlayer = null;
        waitingAction = null;
        hideWaitingForKey();
    }
    
    /**
     * 키 입력 대기 상태 표시를 숨기고 기본 메시지로 복구합니다.
     */
    public void hideWaitingForKey() {
        statusText.setText("Use ↑↓←→ to navigate, ENTER to change key, ESC to go back");
        statusText.setFill(Color.LIGHTGRAY);
    }
    
    /**
     * 중복 키 에러를 표시합니다.
     */
    public void showDuplicateKeyError(KeyCode keyCode) {
        statusText.setText("Key " + keyCode.getName() + " is already assigned! Try another key.");
        statusText.setFill(Color.RED);
        
        // 3초 후 메시지 숨기기
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> hideWaitingForKey());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * 특정 플레이어의 액션 키 바인딩 표시를 업데이트합니다.
     */
    public void updateKeyBinding(int playerNumber, String action, KeyCode keyCode) {
        // NavigableButtonSystem의 버튼 텍스트 업데이트
        ArrayList<Button> buttons = navSystem.getButtons();
        int buttonIndex = getButtonIndex(playerNumber, action);
        if (buttonIndex >= 0 && buttonIndex < buttons.size()) {
            Button button = buttons.get(buttonIndex);
            button.setText(getActionDisplayName(action) + ": " + keyCode.getName());
        }
        
        // 키 입력 대기 상태 초기화
        waitingPlayer = null;
        waitingAction = null;
        hideWaitingForKey();
    }
    
    /**
     * 특정 플레이어의 액션에 해당하는 버튼 인덱스를 계산합니다.
     */
    private int getButtonIndex(int playerNumber, String action) {
        String[] actions = getActions();
        int actionIndex = -1;
        for (int i = 0; i < actions.length; i++) {
            if (actions[i].equals(action)) {
                actionIndex = i;
                break;
            }
        }
        
        if (actionIndex == -1) return -1;
        
        // 2열 그리드에서 (Player 1 버튼, Player 2 버튼) 순서
        // 각 행에 2개 버튼: P1은 짝수 인덱스(0,2,4...), P2는 홀수 인덱스(1,3,5...)
        return actionIndex * 2 + (playerNumber - 1);
    }
    
    /**
     * 모든 키 바인딩 표시를 업데이트합니다.
     */
    public void updateAllKeyBindings(Map<String, KeyCode> player1Bindings, 
                                     Map<String, KeyCode> player2Bindings) {
        for (String action : getActions()) {
            KeyCode p1Key = player1Bindings.get(action);
            if (p1Key != null) {
                updateKeyBinding(1, action, p1Key);
            }
            
            KeyCode p2Key = player2Bindings.get(action);
            if (p2Key != null) {
                updateKeyBinding(2, action, p2Key);
            }
        }
    }
}

