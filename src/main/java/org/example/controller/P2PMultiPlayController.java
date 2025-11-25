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
import org.example.service.ScoreManager;
import org.example.view.P2PMultiPlayView;
import org.example.model.ScoreRecord;

/**
 * P2P MultiPlay의 게임 로직과 입력을 처리하는 Controller
 * 멀티플레이어 게임을 관리하며, 내 게임과 상대방 게임을 모두 처리합니다.
 */
public class P2PMultiPlayController extends BaseController {

    private P2PMultiPlayView multiPlayView;
    private TetrisSystem myTetrisSystem;
    private GameMode gameMode;
    private AnimationTimer gameTimer;

    private long lastDropTime;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<KeyCode> justPressedKeys = new HashSet<>();

    public P2PMultiPlayController(GameMode gameMode, int difficulty) {
        // 내 게임 시스템 초기화
        if (gameMode == GameMode.ITEM) {
            myTetrisSystem = new ItemTetrisSystem();
        } else {
            myTetrisSystem = new TetrisSystem();
        }
        myTetrisSystem.setDifficulty(difficulty);

        this.multiPlayView = new P2PMultiPlayView();
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
        
        var root = multiPlayView.createView(
            gameMode.toString(),
            getDifficultyString(myTetrisSystem.getDifficulty())
        );
        createDefaultScene(root);

        // 높이와 너비 변경 시 캔버스 크기 비율에 맞게 자동 조정
        scene.heightProperty().addListener((_, _, _) -> multiPlayView.updateCanvasSize(scene));
        scene.widthProperty().addListener((_, _, _) -> multiPlayView.updateCanvasSize(scene));
        
        // 초기 캔버스 크기 설정
        multiPlayView.updateCanvasSize(scene);

        // 키 릴리즈 핸들 따로 추가
        scene.setOnKeyReleased(event -> handleKeyReleased(event.getCode()));

        gameTimer.start();
        return scene;
    }

    @Override
    protected void exit() {
        gameTimer.stop();
        // 멀티플레이 모드 비활성화
        org.example.service.DisplayManager.getInstance().setMultiplayerMode(false);
    }

    @Override
    protected void resume() {
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
        if (currentTime - lastDropTime >= myTetrisSystem.getDropInterval()) {
            myTetrisSystem.update();
            lastDropTime = currentTime;
        }

        // Apply any pending board clears (after effect delay)
        myTetrisSystem.getBoard().processPendingClearsIfDue();

        // Update UI through View
        updateDisplay();

        // Check game over
        if (myTetrisSystem.isGameOver()) {
            handleGameOver();
        }
    }

    /**
     * 화면 업데이트
     */
    private void updateDisplay() {
        var ghostPiece = myTetrisSystem.getCurrentPiece() != null
                ? SuperRotationSystem.hardDrop(myTetrisSystem.getCurrentPiece(), myTetrisSystem.getBoard())
                : null;

        multiPlayView.updateDisplay(
                myTetrisSystem.getBoard(),
                myTetrisSystem.getCurrentPiece(),
                ghostPiece,
                myTetrisSystem.getHoldPiece(),
                myTetrisSystem.getNextQueue(),
                myTetrisSystem.getScore(),
                myTetrisSystem.getLines(),
                myTetrisSystem.getLevel());
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
        if (myTetrisSystem == null || myTetrisSystem.isGameOver())
            return;

        // SettingManager를 통해 최신 키 설정 가져오기
        KeyData data = settingManager.getCurrentSettings().controlData;

        // 한 번만 실행되는 입력 처리
        for (KeyCode key : justPressedKeys) {
            if (key == data.hardDrop) {
                myTetrisSystem.hardDrop();
            } else if (key == data.rotateCounterClockwise) {
                myTetrisSystem.rotateCounterClockwise();
            } else if (key == data.rotateClockwise) {
                myTetrisSystem.rotateClockwise();
            } else if (key == data.hold) {
                myTetrisSystem.hold();
            } else if (key == data.pause) {
                handlePause();
            }
        }

        // 연속 실행되는 입력 처리
        for (KeyCode key : pressedKeys) {
            if (key == data.moveLeft) {
                myTetrisSystem.moveLeft();
            } else if (key == data.moveRight) {
                myTetrisSystem.moveRight();
            } else if (key == data.softDrop) {
                myTetrisSystem.moveDown();
            }
        }
    }

    /**
     * 일시정지 처리
     */
    public void handlePause() {
        stackState(new PauseController(() -> myTetrisSystem.reset()));
    }

    /**
     * 게임 오버 처리
     */
    public void handleGameOver() {
        ScoreRecord record = new ScoreRecord(
            myTetrisSystem.getScore(), 
            myTetrisSystem.getLines(),
            myTetrisSystem.getLevel(), 
            myTetrisSystem.getDifficulty(), 
            gameMode, 
            ScoreManager.getInstance().isScoreEligibleForSaving(myTetrisSystem.getScore())
        );

        // 점수가 상위 10개에 드는지에 따라 다른 Controller로 전환
        if (record.isNewAndEligible()) {
            setState(new ScoreInputController(record));
        } else {
            setState(new ScoreNotEligibleController(record));
        }
    }

    /**
     * 상대방을 추가합니다.
     * 네트워크나 AI 상대방이 연결될 때 호출됩니다.
     */
    public void addOpponent() {
        multiPlayView.addOpponentCanvas(scene);
    }

    /**
     * 특정 상대방의 게임 화면을 업데이트합니다.
     * 
     * @param opponentIndex 상대방 인덱스
     * @param board 상대방의 게임 보드
     * @param currentPiece 상대방의 현재 테트로미노
     * @param ghostPiece 상대방의 고스트 테트로미노
     */
    public void updateOpponentDisplay(int opponentIndex,
                                     org.example.model.GameBoard board,
                                     org.example.model.TetrominoPosition currentPiece,
                                     org.example.model.TetrominoPosition ghostPiece) {
        multiPlayView.updateOpponentDisplay(opponentIndex, board, currentPiece, ghostPiece);
    }

    /**
     * 내 게임 로직 반환
     */
    public TetrisSystem getMyGameLogic() {
        return myTetrisSystem;
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
