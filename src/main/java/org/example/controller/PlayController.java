package org.example.controller;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

import org.example.model.GameMode;
import org.example.model.KeyData;
import org.example.service.StateManager;
import org.example.service.SuperRotationSystem;
import org.example.service.TetrisSystem;
import org.example.state.PauseState;
import org.example.state.ScoreInputState;
import org.example.state.ScoreNotEligibleState;
import org.example.service.ScoreManager;
import org.example.view.PlayView;
import org.example.model.ScoreRecord;

/**
 * PlayState의 게임 로직과 입력을 처리하는 Controller
 */
public class PlayController {

    private StateManager stateManager;
    private PlayView playView;
    private TetrisSystem tetrisSystem;
    private GameMode gameMode;

    private long lastDropTime;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<KeyCode> justPressedKeys = new HashSet<>();

    public PlayController(StateManager stateManager, PlayView playView, TetrisSystem tetrisSystem, GameMode gameMode) {
        this.stateManager = stateManager;
        this.playView = playView;
        this.tetrisSystem = tetrisSystem;
        this.gameMode = gameMode;
        this.lastDropTime = System.currentTimeMillis();
    }

    /**
     * 게임 업데이트 로직
     */
    public void update(double deltaTime) {
        long currentTime = System.currentTimeMillis();
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

        playView.updateDisplay(
                tetrisSystem.getBoard(),
                tetrisSystem.getCurrentPiece(),
                ghostPiece,
                tetrisSystem.getHoldPiece(),
                tetrisSystem.getNextQueue(),
                tetrisSystem.getScore(),
                tetrisSystem.getLines(),
                tetrisSystem.getLevel());

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
        KeyData data = stateManager.settingManager.getCurrentSettings().controlData;

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
        stateManager.stackState(new PauseState(stateManager, () -> tetrisSystem.reset()));
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

        // 점수가 상위 10개에 드는지에 따라 다른 State로 전환
        if (record.isNewAndEligible()) {
            stateManager.setState(new ScoreInputState(stateManager, record));
        } else {
            stateManager.setState(new ScoreNotEligibleState(stateManager, record));
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
