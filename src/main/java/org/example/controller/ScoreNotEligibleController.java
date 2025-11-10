package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import org.example.model.ScoreRecord;
import org.example.view.ScoreNotEligibleView;

/**
 * 점수가 상위 10개에 들지 못한 경우를 처리하는 Controller
 * 게임 오버 화면으로 이동할 수 있습니다.
 */
public class ScoreNotEligibleController extends BaseController {

    private final ScoreRecord record;
    private ScoreNotEligibleView view;

    public ScoreNotEligibleController(ScoreRecord record) {
        this.record = record;
    }

    @Override
    public Scene createScene() {
        view = new ScoreNotEligibleView();
        StackPane root = view.createView(record);

        createDefaultScene(root);
        return scene;
    }

    @Override
    protected void handleKeyInput(KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER ||
            event.getCode() == javafx.scene.input.KeyCode.SPACE ||
            event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
            setState(new GameOverController(record));
        }
    }
}
