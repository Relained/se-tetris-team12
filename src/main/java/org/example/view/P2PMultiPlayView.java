package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.example.model.AdderBoardSync;
import org.example.model.TetrominoPosition;
import org.example.view.component.play.AdderCanvas;
import org.example.view.component.play.DummyTetrisCanvas;
import org.example.view.component.play.HoldPanel;
import org.example.view.component.play.ScorePanel;
import org.example.view.component.play.ShortNextPiecePanel;
import org.example.view.component.play.TetrisCanvas;


/**
 * P2P MultiPlay 화면의 UI를 담당하는 View 클래스
 */
public class P2PMultiPlayView extends BaseView{
    private TetrisCanvas myGameCanvas;
    private DummyTetrisCanvas opGameCanvas;
    private ShortNextPiecePanel nextPanel;
    private HoldPanel holdPanel;
    private AdderCanvas adderCanvas;
    private ScorePanel scorePanel;
    private HBox root;
    private VBox widgetsContainer;

    public P2PMultiPlayView() {
        super(false); // NavigableButtonSystem 사용하지 않음
    }

    /**
     * MultiPlay 화면의 UI를 구성하고 반환합니다.
     * @param mode 게임 모드
     * @param difficulty 난이도
     * @return 구성된 HBox root
     */
    public HBox createView(String mode, String difficulty) {
        root = new HBox(20);
        root.getStyleClass().add("root-dark");
        root.setPadding(new Insets(20));

        opGameCanvas = new DummyTetrisCanvas();
        myGameCanvas = new TetrisCanvas();
        HBox.setHgrow(opGameCanvas, Priority.NEVER);
        HBox.setHgrow(myGameCanvas, Priority.NEVER);
        
        // 캔버스 사이 공간을 위한 스페이서
        VBox canvasSpacer = new VBox();
        HBox.setHgrow(canvasSpacer, Priority.ALWAYS);
        
        widgetsContainer = createPlayerWidgets(mode, difficulty);

        root.getChildren().addAll(opGameCanvas, canvasSpacer, myGameCanvas, widgetsContainer);

        return root;
    }

    private VBox createPlayerWidgets(String mode, String difficulty) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("widget-container");
        container.setMinWidth(150);
        container.setPrefWidth(150);

        nextPanel = new ShortNextPiecePanel();
        holdPanel = new HoldPanel();
        adderCanvas = new AdderCanvas();
        VBox spacer = new VBox();
        scorePanel = new ScorePanel(mode, difficulty);

        // Network Delay 표시 영역
        networkDelayContainer = new VBox(2);
        networkDelayContainer.setAlignment(Pos.CENTER);
        networkDelayContainer.setPadding(new Insets(5));
        networkDelayContainer.getStyleClass().add("network-delay-container");
        
        Label titleLabel = new Label("Network Delay");
        titleLabel.getStyleClass().addAll("label-secondary", "text-caption");
        
        networkDelayLabel = new Label("0 ms");
        networkDelayLabel.getStyleClass().addAll("label-primary", "text-body-small");
        networkDelayLabel.setTextFill(Color.LIME);
        
        networkDelayContainer.getChildren().addAll(titleLabel, networkDelayLabel);

        container.getChildren().addAll(nextPanel, holdPanel, adderCanvas, spacer, networkDelayContainer, scorePanel);
        VBox.setVgrow(nextPanel, Priority.NEVER);
        VBox.setVgrow(holdPanel, Priority.NEVER);
        VBox.setVgrow(adderCanvas, Priority.NEVER);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(scorePanel, Priority.NEVER);
        HBox.setHgrow(container, Priority.NEVER);

        return container;
    }

    /**
     * 캔버스 크기를 씬 크기에 맞게 업데이트합니다.
     * 멀티플레이에서는 화면이 2배 넓으므로 고려하여 크기 조정
     */
    public void updateCanvasSize(Scene scene) {
        if (myGameCanvas == null || scene == null) return;

        double sceneWidth = scene.getWidth();
        double padding = 40;
        double spacing = 20;
        
        // 사용 가능한 높이 전체를 캔버스에 할당
        double availableHeight = scene.getHeight() - 40; // 상하 패딩
        double canvasWidth = availableHeight * 0.5; // 1:2 비율 유지
        
        myGameCanvas.setCanvasSize(canvasWidth, availableHeight);
        opGameCanvas.setCanvasSize(canvasWidth, availableHeight);

        double totalCanvasWidth = canvasWidth * 2;
        double availableWidgetWidth = (sceneWidth - totalCanvasWidth - padding - spacing * 3) / 2;
        double widgetWidth = Math.max(100, Math.min(180, availableWidgetWidth));
        
        if (widgetsContainer != null) {
            widgetsContainer.setPrefWidth(widgetWidth);
            widgetsContainer.setMinWidth(widgetWidth);
            widgetsContainer.setMaxWidth(widgetWidth);
        }
        
        double adderSize = widgetWidth - 20; // 패딩 고려
        adderCanvas.setCanvasSize(adderSize, adderSize);
        
        double nextPanelSize = Math.max(60, adderSize * 0.8);
        nextPanel.setPreferredCanvasSize(nextPanelSize);
    }

    /**
     * 내 게임 화면을 업데이트합니다.
     */
    public void updateDisplay(org.example.model.GameBoard board, 
                             TetrominoPosition currentPiece,
                             TetrominoPosition ghostPiece,
                             TetrominoPosition holdPiece,
                             TetrominoPosition nextPiece,
                             AdderBoardSync adderBoard,
                             int score, int lines, int level,
                            long remainingMillis) {
        myGameCanvas.updateBoard(board, currentPiece, ghostPiece);
        holdPanel.updateHoldPiece(holdPiece);
        nextPanel.updateNextPiece(nextPiece);
        adderCanvas.updateBoard(adderBoard.getDrawBuffer());
        scorePanel.updateStats(score, lines, level);
        scorePanel.updateTimer(remainingMillis);
    }

    // 상대방 화면 업데이트
    public void updateOpponentDisplay(int[][] board) {
        opGameCanvas.updateBoard(board);
    }

    public void setShowTimer(boolean show) {
        scorePanel.setShowTimer(show);
    }
}
