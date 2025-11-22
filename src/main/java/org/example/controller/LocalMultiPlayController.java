package org.example.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

import org.example.model.KeyData;
import org.example.service.ItemTetrisSystem;
import org.example.service.SuperRotationSystem;
import org.example.service.TetrisSystem;
import org.example.view.LocalMultiPlayView;

/**
 * LocalMultiPlayState의 게임 로직과 입력을 처리하는 Controller
 * 두 명의 플레이어가 같은 화면에서 대전합니다.
 */
public class LocalMultiPlayController extends BaseController {

    private LocalMultiPlayView localMultiPlayView;
    private TetrisSystem player1System;
    private TetrisSystem player2System;
    private AnimationTimer gameTimer;

    private long lastDropTime1;
    private long lastDropTime2;
    
    // Player 1 키 입력
    private final Set<KeyCode> player1PressedKeys = new HashSet<>();
    private final Set<KeyCode> player1JustPressedKeys = new HashSet<>();
    
    // Player 2 키 입력 (WASD + 별도 키)
    private final Set<KeyCode> player2PressedKeys = new HashSet<>();
    private final Set<KeyCode> player2JustPressedKeys = new HashSet<>();
    
    // 게임 모드 정보
    private final boolean isItemMode;
    private final int difficulty;

    public LocalMultiPlayController(boolean isItemMode, int difficulty) {
        this.isItemMode = isItemMode;
        this.difficulty = difficulty;
        // Player 1 시스템 초기화
        if (isItemMode) {
            player1System = new ItemTetrisSystem();
        } else {
            player1System = new TetrisSystem();
        }
        player1System.setDifficulty(difficulty);
        
        // Player 2 시스템 초기화
        if (isItemMode) {
            player2System = new ItemTetrisSystem();
        } else {
            player2System = new TetrisSystem();
        }
        player2System.setDifficulty(difficulty);

        this.localMultiPlayView = new LocalMultiPlayView();
        this.lastDropTime1 = System.currentTimeMillis();
        this.lastDropTime2 = System.currentTimeMillis();

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now / 1_000_000_000.0);
            }
        };
    }

    @Override
    protected Scene createScene() {
        // 멀티플레이 모드 활성화
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(true);
        
        var root = localMultiPlayView.createView();
        createDefaultScene(root);

        // 높이와 너비 변경 시 캔버스 크기 비율에 맞게 자동 조정
        scene.heightProperty().addListener((_, _, _) -> localMultiPlayView.updateCanvasSize(scene));
        scene.widthProperty().addListener((_, _, _) -> localMultiPlayView.updateCanvasSize(scene));
        
        // 초기 캔버스 크기 설정
        localMultiPlayView.updateCanvasSize(scene);

        // 키 릴리즈 핸들 따로 추가
        scene.setOnKeyReleased(event -> handleKeyReleased(event.getCode()));

        gameTimer.start();
        return scene;
    }

    @Override
    protected void exit() {
        gameTimer.stop();
    }

    @Override
    protected void resume() {
        // 키 입력 상태 초기화
        player1PressedKeys.clear();
        player1JustPressedKeys.clear();
        player2PressedKeys.clear();
        player2JustPressedKeys.clear();
        
        gameTimer.start();
    }

    /**
     * 게임 업데이트 로직
     */
    public void update(double deltaTime) {
        long currentTime = System.currentTimeMillis();
        
        // Player 1 업데이트
        if (currentTime - lastDropTime1 >= player1System.getDropInterval()) {
            player1System.update();
            lastDropTime1 = currentTime;
        }
        player1System.getBoard().processPendingClearsIfDue();
        
        // Player 2 업데이트
        if (currentTime - lastDropTime2 >= player2System.getDropInterval()) {
            player2System.update();
            lastDropTime2 = currentTime;
        }
        player2System.getBoard().processPendingClearsIfDue();

        // 화면 업데이트
        updateDisplay();

        // 게임 오버 체크
        checkGameOver();
    }

    /**
     * 화면 업데이트
     */
    private void updateDisplay() {
        // Player 1 화면 업데이트
        var ghostPiece1 = player1System.getCurrentPiece() != null
                ? SuperRotationSystem.hardDrop(player1System.getCurrentPiece(), player1System.getBoard())
                : null;
        
        var nextPiece1 = !player1System.getNextQueue().isEmpty() 
                ? player1System.getNextQueue().get(0) 
                : null;

        localMultiPlayView.updatePlayer1Display(
                player1System.getBoard(),
                player1System.getCurrentPiece(),
                ghostPiece1,
                nextPiece1);

        // Player 2 화면 업데이트
        var ghostPiece2 = player2System.getCurrentPiece() != null
                ? SuperRotationSystem.hardDrop(player2System.getCurrentPiece(), player2System.getBoard())
                : null;
        
        var nextPiece2 = !player2System.getNextQueue().isEmpty() 
                ? player2System.getNextQueue().get(0) 
                : null;

        localMultiPlayView.updatePlayer2Display(
                player2System.getBoard(),
                player2System.getCurrentPiece(),
                ghostPiece2,
                nextPiece2);
    }

    @Override
    protected void handleKeyInput(KeyEvent event) {
        handleKeyPressed(event.getCode());
    }

    /**
     * 키 입력 처리 - 키가 눌렸을 때
     */
    public void handleKeyPressed(KeyCode key) {
        KeyData data = settingManager.getCurrentSettings().controlData;
        
        // Player 1 키 처리
        if (isPlayer1Key(key, data)) {
            if (!player1PressedKeys.contains(key)) {
                player1JustPressedKeys.add(key);
            }
            player1PressedKeys.add(key);
        }
        
        // Player 2 키 처리 (WASD + 별도 키)
        if (isPlayer2Key(key)) {
            if (!player2PressedKeys.contains(key)) {
                player2JustPressedKeys.add(key);
            }
            player2PressedKeys.add(key);
        }
        
        handleInputs();
        player1JustPressedKeys.clear();
        player2JustPressedKeys.clear();
    }

    /**
     * 키 입력 처리 - 키가 떼어졌을 때
     */
    public void handleKeyReleased(KeyCode key) {
        player1PressedKeys.remove(key);
        player2PressedKeys.remove(key);
    }
    
    /**
     * Player 1의 키인지 확인
     */
    private boolean isPlayer1Key(KeyCode key, KeyData data) {
        return key == data.multi1MoveLeft || key == data.multi1MoveRight || 
               key == data.multi1SoftDrop || key == data.multi1HardDrop ||
               key == data.multi1RotateClockwise || key == data.multi1RotateCounterClockwise ||
               key == data.multi1Hold || key == data.pause;
    }
    
    /**
     * Player 2의 키인지 확인
     */
    private boolean isPlayer2Key(KeyCode key) {
        KeyData data = settingManager.getCurrentSettings().controlData;
        return key == data.multi2MoveLeft || key == data.multi2MoveRight || 
               key == data.multi2SoftDrop || key == data.multi2HardDrop ||
               key == data.multi2RotateClockwise || key == data.multi2RotateCounterClockwise ||
               key == data.multi2Hold || key == data.pause;
    }

    /**
     * 입력에 따른 게임 로직 실행
     */
    private void handleInputs() {
        if (player1System.isGameOver() && player2System.isGameOver())
            return;

        KeyData data = settingManager.getCurrentSettings().controlData;

        // Pause 키는 한 번만 처리 (공유 키)
        if (player1JustPressedKeys.contains(data.pause) || player2JustPressedKeys.contains(data.pause)) {
            handlePause();
            return; // Pause 처리 후 다른 입력은 처리하지 않음
        }

        // Player 1 입력 처리
        if (!player1System.isGameOver()) {
            handlePlayer1Inputs(data);
        }
        
        // Player 2 입력 처리
        if (!player2System.isGameOver()) {
            handlePlayer2Inputs();
        }
    }
    
    /**
     * Player 1 입력 처리 (멀티플레이 전용 키 사용)
     */
    private void handlePlayer1Inputs(KeyData data) {
        // 한 번만 실행되는 입력
        for (KeyCode key : player1JustPressedKeys) {
            if (key == data.multi1HardDrop) {
                player1System.hardDrop();
            } else if (key == data.multi1RotateCounterClockwise) {
                player1System.rotateCounterClockwise();
            } else if (key == data.multi1RotateClockwise) {
                player1System.rotateClockwise();
            } else if (key == data.multi1Hold) {
                player1System.hold();
            }
            // pause 키 제거 - handleInputs()에서 공통 처리
        }

        // 연속 실행되는 입력
        for (KeyCode key : player1PressedKeys) {
            if (key == data.multi1MoveLeft) {
                player1System.moveLeft();
            } else if (key == data.multi1MoveRight) {
                player1System.moveRight();
            } else if (key == data.multi1SoftDrop) {
                player1System.moveDown();
            }
        }
    }
    
    /**
     * Player 2 입력 처리 (멀티플레이 전용 키 사용)
     */
    private void handlePlayer2Inputs() {
        KeyData data = settingManager.getCurrentSettings().controlData;
        
        // 한 번만 실행되는 입력
        for (KeyCode key : player2JustPressedKeys) {
            if (key == data.multi2HardDrop) {
                player2System.hardDrop();
            } else if (key == data.multi2RotateCounterClockwise) {
                player2System.rotateCounterClockwise();
            } else if (key == data.multi2RotateClockwise) {
                player2System.rotateClockwise();
            } else if (key == data.multi2Hold) {
                player2System.hold();
            }
            // pause 키 제거 - handleInputs()에서 공통 처리
        }

        // 연속 실행되는 입력
        for (KeyCode key : player2PressedKeys) {
            if (key == data.multi2MoveLeft) {
                player2System.moveLeft();
            } else if (key == data.multi2MoveRight) {
                player2System.moveRight();
            } else if (key == data.multi2SoftDrop) {
                player2System.moveDown();
            }
        }
    }

    /**
     * 일시정지 처리
     */
    public void handlePause() {
        stackState(new LocalMultiPauseController(isItemMode, difficulty));
    }

    /**
     * 게임 오버 체크 및 처리
     */
    private void checkGameOver() {
        if (player1System.isGameOver() && player2System.isGameOver()) {
            handleGameOver("Draw");
        } else if (player1System.isGameOver()) {
            handleGameOver("Player 2");
        } else if (player2System.isGameOver()) {
            handleGameOver("Player 1");
        }
    }

    /**
     * 게임 오버 처리
     */
    private void handleGameOver(String winner) {
        gameTimer.stop();
        // TODO: LocalMultiGameOverController 구현 후 활성화
        // GameOver 화면으로 전환 (승자 표시)
        // setState(new LocalMultiGameOverController(winner, player1System, player2System));
        
        // 임시: 시작 화면으로 돌아가기
        setState(new StartController());
    }

    /**
     * 게임 로직 반환
     */
    public TetrisSystem getPlayer1System() {
        return player1System;
    }

    public TetrisSystem getPlayer2System() {
        return player2System;
    }

    /**
     * lastDropTime 리셋
     */
    public void resetLastDropTime() {
        this.lastDropTime1 = System.currentTimeMillis();
        this.lastDropTime2 = System.currentTimeMillis();
    }
}
