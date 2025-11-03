package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import org.example.controller.GameModeController;
import org.example.service.StateManager;
import org.example.view.GameModeView;

public class GameModeState extends BaseState {

	private GameModeView view;
	private GameModeController controller;

	public GameModeState(StateManager stateManager) {
		super(stateManager);
		view = new GameModeView();
		controller = new GameModeController(stateManager, view);
	}

	@Override
	public void exit() {
		// no-op
	}

	@Override
	public void resume() {
		// no-op
	}

	@Override
	public Scene createScene() {
		VBox root = view.createView(
			() -> controller.handleNormal(),
			() -> controller.handleItem(),
			() -> controller.handleGoBack()
		);

		scene = new Scene(root, 1000, 700);
		scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());
		scene.setOnKeyPressed(event -> controller.handleKeyInput(event));
		scene.getRoot().setFocusTraversable(true);
		scene.getRoot().requestFocus();
		return scene;
	}
}
