package org.example.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

import org.example.model.GameMode;
import org.example.model.KeyData;
import org.example.service.ItemTetrisSystem;
import org.example.service.SuperRotationSystem;
import org.example.service.TetrisSystem;
import org.example.service.TimeTetrisSystem;
import org.example.service.ScoreManager;
import org.example.view.PlayView;
import org.example.model.ScoreRecord;

/**
 * PlayState의 게임 로직과 입력을 처리하는 Controller
 */
public class PlayController extends BaseController {

    private PlayView playView;
    private TetrisSystem tetrisSystem;
    private GameMode gameMode;
    private AnimationTimer gameTimer;

    private long lastDropTime;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<KeyCode> justPressedKeys = new HashSet<>();

    public PlayController(GameMode gameMode, int difficulty) {
        if (gameMode == GameMode.ITEM) {
            tetrisSystem = new ItemTetrisSystem();
        } else if (gameMode == GameMode.TIME_ATTACK) {
            tetrisSystem = new TimeTetrisSystem();
        } else {
            tetrisSystem = new TetrisSystem();
        }
        tetrisSystem.setDifficulty(difficulty);

        this.playView = new PlayView();
        this.gameMode = gameMode;
        this.lastDropTime = System.currentTimeMillis();

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now / 1_000_000_000.0);
            }
        };
    }

    @Override
    protected Scene createScene() {
        var root = playView.createView(
            gameMode.toString(),
            getDifficultyString(tetrisSystem.getDifficulty())
        );
        createDefaultScene(root);

        // 높이와 너비 변경 시 캔버스 크기 비율에 맞게 자동 조정
        scene.heightProperty().addListener((_, _, _) -> playView.updateCanvasSize(scene));
        scene.widthProperty().addListener((_, _, _) -> playView.updateCanvasSize(scene));
        
        // 초기 캔버스 크기 설정
        playView.updateCanvasSize(scene);

        // TIME_ATTACK 모드일 경우 타이머 표시 활성화
        if (gameMode == GameMode.TIME_ATTACK) {
            playView.setShowTimer(true);
        }

        //키 릴리즈 핸들 따로 추가
        scene.setOnKeyReleased(event -> handleKeyReleased(event.getCode()));

        gameTimer.start();
        return scene;
    }

    @Override
    protected void exit() {
        gameTimer.stop();
        // TIME_ATTACK 모드: 타이머 일시정지
        if (tetrisSystem instanceof TimeTetrisSystem) {
            ((TimeTetrisSystem) tetrisSystem).pauseTimer();
        }
    }

    @Override
    protected void resume() {
        // TIME_ATTACK 모드: 타이머 재개
        if (tetrisSystem instanceof TimeTetrisSystem) {
            ((TimeTetrisSystem) tetrisSystem).resumeTimer();
        }
        // PlayView의 모든 UI 요소 크기 업데이트
        playView.onResume();
        gameTimer.start();
    }

    private String getDifficultyString(int difficulty) {
        switch (difficulty) {
            case 1:
                return "Easy";
            case 2:
                return "Normal";
            case 3:
                return "Hard";
            default:
                return "Unknown";
        }
    }

    /**
     * 게임 업데이트 로직
     */
    public void update(double deltaTime) {
        long currentTime = System.currentTimeMillis();
        
        // TIME_ATTACK 모드: 시간 체크
        if (tetrisSystem instanceof TimeTetrisSystem) {
            TimeTetrisSystem timeSystem = (TimeTetrisSystem) tetrisSystem;
            if (timeSystem.isTimeUp()) {
                handleGameOver();
                return;
            }
        }
        
        if (currentTime - lastDropTime >= tetrisSystem.getDropInterval()) {
            tetrisSystem.update();
            lastDropTime = currentTime;
        }

        // Apply any pending board clears (after effect delay)
        tetrisSystem.getBoard().processPendingClearsIfDue();

        // Update UI through View
        updateDisplay();

        // Check game over
        if (tetrisSystem.isGameOver()) {
            handleGameOver();
        }
    }

    /**
     * 화면 업데이트
     */
    private void updateDisplay() {
        var ghostPiece = tetrisSystem.getCurrentPiece() != null
                ? SuperRotationSystem.hardDrop(tetrisSystem.getCurrentPiece(), tetrisSystem.getBoard())
                : null;

        // TIME_ATTACK 모드일 경우 남은 시간 전달, 아니면 -1
        long remainingMillis = -1;
        if (tetrisSystem instanceof TimeTetrisSystem) {
            remainingMillis = ((TimeTetrisSystem) tetrisSystem).getRemainingTime();
        }

        playView.updateDisplay(
                tetrisSystem.getBoard(),
                tetrisSystem.getCurrentPiece(),
                ghostPiece,
                tetrisSystem.getHoldPiece(),
                tetrisSystem.getNextQueue(),
                tetrisSystem.getScore(),
                tetrisSystem.getLines(),
                tetrisSystem.getLevel(),
                remainingMillis);
    }

    @Override
    protected void handleKeyInput(KeyEvent event) {
        handleKeyPressed(event.getCode());
    }

    /**
     * 키 입력 처리 - 키가 눌렸을 때
     */
    public void handleKeyPressed(KeyCode key) {
        if (!pressedKeys.contains(key)) {
            justPressedKeys.add(key);
        }
        pressedKeys.add(key);
        handleInputs();
        justPressedKeys.clear();
    }

    /**
     * 키 입력 처리 - 키가 떼어졌을 때
     */
    public void handleKeyReleased(KeyCode key) {
        pressedKeys.remove(key);
    }

    /**
     * 입력에 따른 게임 로직 실행
     */
    private void handleInputs() {
        if (tetrisSystem == null || tetrisSystem.isGameOver())
            return;

        // SettingManager를 통해 최신 키 설정 가져오기
        KeyData data = settingManager.getCurrentSettings().controlData;

        // 한 번만 실행되는 입력 처리
        for (KeyCode key : justPressedKeys) {
            if (key == data.hardDrop) {
                tetrisSystem.hardDrop();
            } else if (key == data.rotateCounterClockwise) {
                tetrisSystem.rotateCounterClockwise();
            } else if (key == data.rotateClockwise) {
                tetrisSystem.rotateClockwise();
            } else if (key == data.hold) {
                tetrisSystem.hold();
            } else if (key == data.pause) {
                handlePause();
            }
        }

        // 연속 실행되는 입력 처리
        for (KeyCode key : pressedKeys) {
            if (key == data.moveLeft) {
                tetrisSystem.moveLeft();
            } else if (key == data.moveRight) {
                tetrisSystem.moveRight();
            } else if (key == data.softDrop) {
                tetrisSystem.moveDown();
            }
        }
    }

    /**
     * 일시정지 처리
     */
    public void handlePause() {
        stackState(new PauseController(tetrisSystem::reset));
    }

    /**
     * 게임 오버 처리
     */
    public void handleGameOver() {
        ScoreRecord record = new ScoreRecord(
            tetrisSystem.getScore(), 
            tetrisSystem.getLines(),
            tetrisSystem.getLevel(), 
            tetrisSystem.getDifficulty(), 
            gameMode, 
            ScoreManager.getInstance().isScoreEligibleForSaving(tetrisSystem.getScore())
        );

        // 점수가 상위 10개에 드는지에 따라 다른 Controller로 전환
        if (record.isNewAndEligible()) {
            setState(new ScoreInputController(record));
        } else {
            setState(new ScoreNotEligibleController(record));
        }
    }

    /**
     * 게임 로직 반환
     */
    public TetrisSystem getGameLogic() {
        return tetrisSystem;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * lastDropTime 리셋
     */
    public void resetLastDropTime() {
        this.lastDropTime = System.currentTimeMillis();
    }
}
