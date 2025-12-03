package org.example.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

import org.example.model.AdderBoard;
import org.example.model.GameMode;
import org.example.model.KeyData;
import org.example.service.ItemTetrisSystem;
import org.example.service.SuperRotationSystem;
import org.example.service.TetrisSystem;
import org.example.service.TimeTetrisSystem;
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
    private long lastKeyProcessTime;
    
    // AdderBoard for each player
    private AdderBoard player1AdderBoard;
    private AdderBoard player2AdderBoard;
    
    // Player 1 키 입력
    private final Set<KeyCode> player1PressedKeys = new HashSet<>();
    private final Set<KeyCode> player1JustPressedKeys = new HashSet<>();
    
    // Player 2 키 입력 (WASD + 별도 키)
    private final Set<KeyCode> player2PressedKeys = new HashSet<>();
    private final Set<KeyCode> player2JustPressedKeys = new HashSet<>();
    
    // 게임 모드 정보
    private final GameMode gameMode;
    private final int difficulty;

    public LocalMultiPlayController(GameMode gameMode, int difficulty) {
        this.gameMode = gameMode;
        this.difficulty = difficulty;
        
        // Player 1 시스템 초기화
        if (gameMode == GameMode.ITEM) {
            player1System = new ItemTetrisSystem();
        } else if (gameMode == GameMode.TIME_ATTACK) {
            player1System = new TimeTetrisSystem();
        } else {
            player1System = new TetrisSystem();
        }
        player1System.setDifficulty(difficulty);
        
        // Player 2 시스템 초기화
        if (gameMode == GameMode.ITEM) {
            player2System = new ItemTetrisSystem();
        } else if (gameMode == GameMode.TIME_ATTACK) {
            player2System = new TimeTetrisSystem();
        } else {
            player2System = new TetrisSystem();
        }
        player2System.setDifficulty(difficulty);

        this.localMultiPlayView = new LocalMultiPlayView();
        this.lastDropTime1 = System.currentTimeMillis();
        this.lastDropTime2 = System.currentTimeMillis();
        
        // AdderBoard 초기화
        this.player1AdderBoard = new AdderBoard();
        this.player2AdderBoard = new AdderBoard();
        
        // lockPiece 후 콜백 설정: 2줄 이상 완성 시 상대방 AdderBoard에 추가
        player1System.setOnPieceLocked(() -> {
            // Player 1이 2줄 이상 완성한 경우에만 Player 2의 AdderBoard에 추가
            // previousSnapshot 사용 (이전 턴의 보드 상태, 빈 칸 있는 상태)
            var completedLines = player1System.getCompletedLineIndices();
            if (completedLines.size() >= 2 && player1System.getPreviousSnapshot() != null) {
                int[][] lines = player1System.getPreviousSnapshot().getLines(completedLines);
                player2AdderBoard.addLines(lines);
            }
            // Player 1의 AdderBoard 라인을 게임보드에 적용
            if (player1AdderBoard.getLineCount() > 0) {
                player1AdderBoard.applyToBoard(player1System.getBoard());
            }
        });

        player2System.setOnPieceLocked(() -> {
            // Player 2가 2줄 이상 완성한 경우에만 Player 1의 AdderBoard에 추가
            // previousSnapshot 사용 (이전 턴의 보드 상태, 빈 칸 있는 상태)
            var completedLines = player2System.getCompletedLineIndices();
            if (completedLines.size() >= 2 && player2System.getPreviousSnapshot() != null) {
                int[][] lines = player2System.getPreviousSnapshot().getLines(completedLines);
                player1AdderBoard.addLines(lines);
            }
            // Player 2의 AdderBoard 라인을 게임보드에 적용
            if (player2AdderBoard.getLineCount() > 0) {
                player2AdderBoard.applyToBoard(player2System.getBoard());
            }
        });

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
        
        // 게임 모드 이름 변환
        String modeName = switch (gameMode) {
            case ITEM -> "Item";
            case TIME_ATTACK -> "Time Attack";
            default -> "Normal";
        };
        
        // 난이도 이름 변환 (1=Easy, 2=Normal, 3=Hard)
        String difficultyName = switch (difficulty) {
            case 1 -> "Easy";
            case 2 -> "Normal";
            case 3 -> "Hard";
            default -> "Normal";
        };
        
        var root = localMultiPlayView.createView(modeName, difficultyName);
        createDefaultScene(root);

        // 높이와 너비 변경 시 캔버스 크기 비율에 맞게 자동 조정
        scene.heightProperty().addListener((_, _, _) -> localMultiPlayView.updateCanvasSize(scene));
        scene.widthProperty().addListener((_, _, _) -> localMultiPlayView.updateCanvasSize(scene));
        
        // 초기 캔버스 크기 설정
        localMultiPlayView.updateCanvasSize(scene);

        // TIME_ATTACK 모드일 경우 타이머 표시 활성화
        if (gameMode == GameMode.TIME_ATTACK) {
            localMultiPlayView.setShowTimer(true);
        }

        // 키 릴리즈 핸들 따로 추가
        scene.setOnKeyReleased(event -> handleKeyReleased(event.getCode()));

        gameTimer.start();
        return scene;
    }

    @Override
    protected void exit() {
        gameTimer.stop();
        // TIME_ATTACK 모드: 타이머 일시정지
        if (player1System instanceof TimeTetrisSystem) {
            ((TimeTetrisSystem) player1System).pauseTimer();
        }
        if (player2System instanceof TimeTetrisSystem) {
            ((TimeTetrisSystem) player2System).pauseTimer();
        }
    }

    @Override
    protected void resume() {
        // 키 입력 상태 초기화
        player1PressedKeys.clear();
        player1JustPressedKeys.clear();
        player2PressedKeys.clear();
        player2JustPressedKeys.clear();
        
        // TIME_ATTACK 모드: 타이머 재개
        if (player1System instanceof TimeTetrisSystem) {
            ((TimeTetrisSystem) player1System).resumeTimer();
        }
        if (player2System instanceof TimeTetrisSystem) {
            ((TimeTetrisSystem) player2System).resumeTimer();
        }
        
        gameTimer.start();
    }

    /**
     * 게임 업데이트 로직
     */
    public void update(double deltaTime) {
        long currentTime = System.currentTimeMillis();
        
        // TIME_ATTACK 모드: 시간 체크 (두 플레이어 중 하나만 체크 - 동기화됨)
        if (player1System instanceof TimeTetrisSystem) {
            TimeTetrisSystem timeSystem = (TimeTetrisSystem) player1System;
            if (timeSystem.isTimeUp()) {
                handleTimeAttackEnd();
                return;
            }
        }

        if (currentTime - lastKeyProcessTime >= 50) {
            handleInputs();
            player1JustPressedKeys.clear();
            player2JustPressedKeys.clear();
            lastKeyProcessTime = currentTime;
        }
        
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
        
        var holdPiece1 = player1System.getHoldPiece();
        
        var nextPiece1 = !player1System.getNextQueue().isEmpty() 
                ? player1System.getNextQueue().get(0) 
                : null;

        localMultiPlayView.updatePlayer1Display(
                player1System.getBoard(),
                player1System.getCurrentPiece(),
                ghostPiece1,
                holdPiece1,
                nextPiece1,
                player1AdderBoard,
                player1System.getScore(),
                player1System.getLines(),
                player1System.getLevel(),
                player1System.getRemainingTime());

        // Player 2 화면 업데이트
        var ghostPiece2 = player2System.getCurrentPiece() != null
                ? SuperRotationSystem.hardDrop(player2System.getCurrentPiece(), player2System.getBoard())
                : null;
        
        var holdPiece2 = player2System.getHoldPiece();
        
        var nextPiece2 = !player2System.getNextQueue().isEmpty() 
                ? player2System.getNextQueue().get(0) 
                : null;

        localMultiPlayView.updatePlayer2Display(
                player2System.getBoard(),
                player2System.getCurrentPiece(),
                ghostPiece2,
                holdPiece2,
                nextPiece2,
                player2AdderBoard,
                player2System.getScore(),
                player2System.getLines(),
                player2System.getLevel(),
                player1System.getRemainingTime());
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
        stackState(new LocalMultiPauseController(gameMode, difficulty));
    }

    /**
     * TIME_ATTACK 모드 시간 종료 처리
     */
    private void handleTimeAttackEnd() {
        gameTimer.stop();
        
        // 점수 비교로 승자 결정
        String winner;
        int score1 = player1System.getScore();
        int score2 = player2System.getScore();
        
        if (score1 > score2) {
            winner = "Player 1";
        } else if (score2 > score1) {
            winner = "Player 2";
        } else {
            winner = "Draw";
        }
        
        setState(new LocalMultiGameOverController(winner, gameMode, difficulty));
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
        // GameOver 화면으로 전환 (승자 표시)
        setState(new LocalMultiGameOverController(winner, gameMode, difficulty));
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
    
    /**
     * Player 1의 AdderBoard 반환
     */
    public AdderBoard getPlayer1AdderBoard() {
        return player1AdderBoard;
    }
    
    /**
     * Player 2의 AdderBoard 반환
     */
    public AdderBoard getPlayer2AdderBoard() {
        return player2AdderBoard;
    }
}
