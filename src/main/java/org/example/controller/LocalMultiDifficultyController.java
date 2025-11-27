package org.example.controller.DEPRECATED;

import org.example.controller.BaseController;
import org.example.controller.LocalMultiPlayController;
import org.example.controller.LocalMultiSetupController;
import org.example.view.DEPRECATED.LocalMultiDifficultyView;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

/**
 * Local MultiPlay Difficulty 화면의 입력을 처리하는 Controller
 * 
 * @deprecated Use {@link LocalMultiSetupController} instead. This controller has been merged
 *             with LocalMultiGameModeController into a unified setup flow.
 * @see LocalMultiSetupController
 */
@Deprecated(since = "1.0", forRemoval = true)
public class LocalMultiDifficultyController extends BaseController {

    private LocalMultiDifficultyView view;
    private boolean isItemMode;

    public LocalMultiDifficultyController(boolean isItemMode) {
        this.view = new LocalMultiDifficultyView();
        this.isItemMode = isItemMode;
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            this::handleEasy,
            this::handleMedium,
            this::handleHard,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }

    /**
     * Easy 난이도 선택 처리
     */
    public void handleEasy() {
        setState(new LocalMultiPlayController(isItemMode, 1));
    }

    /**
     * Medium 난이도 선택 처리
     */
    public void handleMedium() {
        setState(new LocalMultiPlayController(isItemMode, 2));
    }

    /**
     * Hard 난이도 선택 처리
     */
    public void handleHard() {
        setState(new LocalMultiPlayController(isItemMode, 3));
    }

    /**
     * Go Back 버튼 클릭 시 처리
     */
    public void handleGoBack() {
        popState();
    }

    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
