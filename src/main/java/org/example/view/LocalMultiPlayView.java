package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.example.model.TetrominoPosition;
import org.example.view.component.play.AdderCanvas;
import org.example.view.component.play.ScorePanel;
import org.example.view.component.play.ShortNextPiecePanel;
import org.example.view.component.play.TetrisCanvas;

/**
 * LocalMultiPlay 화면의 UI를 담당하는 View 클래스
 * 좌우에 각각 플레이어의 게임 화면과 위젯을 배치합니다.
 */
public class LocalMultiPlayView extends BaseView {
    
    // Player 1 (좌측)
    private TetrisCanvas player1Canvas;
    private ShortNextPiecePanel player1NextPanel;
    private AdderCanvas player1AdderCanvas;
    private ScorePanel player1ScorePanel;
    
    // Player 2 (우측)
    private TetrisCanvas player2Canvas;
    private ShortNextPiecePanel player2NextPanel;
    private AdderCanvas player2AdderCanvas;
    private ScorePanel player2ScorePanel;
    
    private HBox root;
    
    public LocalMultiPlayView() {
        super(false); // NavigableButtonSystem 사용하지 않음
    }
    
    /**
     * LocalMultiPlay 화면의 UI를 구성하고 반환합니다.
     * 
     * @return 구성된 HBox root
     */
    public HBox createView() {
        // 메인 컨테이너 (플레이어2 위젯 | 플레이어2 캔버스 | 플레이어1 캔버스 | 플레이어1 위젯)
        root = new HBox(20);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getGameBackgroundColor(), null, null)
        ));
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        // Player 2 영역 (좌측)
        VBox player2Widgets = createPlayerWidgets(false);
        player2Canvas = new TetrisCanvas();
        HBox.setHgrow(player2Canvas, Priority.NEVER);
        
        // Player 1 영역 (우측)
        player1Canvas = new TetrisCanvas();
        HBox.setHgrow(player1Canvas, Priority.NEVER);
        VBox player1Widgets = createPlayerWidgets(true);
        
        root.getChildren().addAll(player2Widgets, player2Canvas, player1Canvas, player1Widgets);
        
        return root;
    }
    
    /**
     * 플레이어별 위젯 영역을 생성합니다.
     * 
     * @param isPlayer1 플레이어1인지 여부
     * @return 구성된 VBox
     */
    private VBox createPlayerWidgets(boolean isPlayer1) {
        VBox widgetContainer = new VBox(10);
        widgetContainer.setAlignment(Pos.TOP_CENTER);
        widgetContainer.setPadding(new Insets(10));
        widgetContainer.setStyle("-fx-background-color: #333;");
        widgetContainer.setMinWidth(150);
        widgetContainer.setPrefWidth(150);
        
        ShortNextPiecePanel nextPanel = new ShortNextPiecePanel();
        AdderCanvas adderCanvas = new AdderCanvas();
        ScorePanel scorePanel = new ScorePanel("Local Multi", "Normal");
        
        // AdderCanvas를 감싸는 VBox 생성 (크기 조절 가능하도록)
        VBox adderContainer = new VBox(adderCanvas);
        adderContainer.setAlignment(Pos.TOP_CENTER);
        adderContainer.setMinHeight(100);
        adderContainer.setPrefHeight(200);
        adderContainer.setStyle("-fx-background-color: #222;");
        
        if (isPlayer1) {
            player1NextPanel = nextPanel;
            player1AdderCanvas = adderCanvas;
            player1ScorePanel = scorePanel;
        } else {
            player2NextPanel = nextPanel;
            player2AdderCanvas = adderCanvas;
            player2ScorePanel = scorePanel;
        }
        
        widgetContainer.getChildren().addAll(nextPanel, adderContainer, scorePanel);
        VBox.setVgrow(nextPanel, Priority.NEVER);
        VBox.setVgrow(adderContainer, Priority.ALWAYS);
        VBox.setVgrow(scorePanel, Priority.NEVER);
        HBox.setHgrow(widgetContainer, Priority.NEVER);
        
        // AdderCanvas 크기가 컨테이너를 따르도록 리스너 설정
        widgetContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            double containerWidth = newVal.doubleValue() - 20; // 패딩 고려
            if (containerWidth > 0) {
                adderCanvas.setCanvasSize(containerWidth, adderContainer.getHeight());
            }
        });
        
        adderContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            double containerWidth = widgetContainer.getWidth() - 20;
            if (containerWidth > 0) {
                adderCanvas.setCanvasSize(containerWidth, newVal.doubleValue());
            }
        });
        
        return widgetContainer;
    }
    
    /**
     * 캔버스 크기를 씬 크기에 맞게 업데이트합니다.
     * LocalMultiPlay에서는 화면이 2배 넓으므로 고려하여 크기 조정
     */
    public void updateCanvasSize(Scene scene) {
        if (player1Canvas == null || player2Canvas == null || scene == null) return;
        
        // 사용 가능한 높이 전체를 캔버스에 할당
        double availableHeight = scene.getHeight() - 40; // 상하 패딩
        double canvasWidth = availableHeight * 0.5; // 1:2 비율 유지
        
        player1Canvas.setCanvasSize(canvasWidth, availableHeight);
        player2Canvas.setCanvasSize(canvasWidth, availableHeight);
        
        // AdderCanvas 크기도 맞춤
        double adderHeight = availableHeight * 0.5; // AdderCanvas는 절반 높이
        player1AdderCanvas.setCanvasSize(canvasWidth, adderHeight);
        player2AdderCanvas.setCanvasSize(canvasWidth, adderHeight);
        
        // 위젯 크기 조정
        double widgetSize = Math.min(canvasWidth * 0.8, 120);
        player1NextPanel.setPreferredCanvasSize(widgetSize);
        player2NextPanel.setPreferredCanvasSize(widgetSize);
    }
    
    /**
     * Player 1의 게임 화면을 업데이트합니다.
     */
    public void updatePlayer1Display(org.example.model.GameBoard board,
                                     TetrominoPosition currentPiece,
                                     TetrominoPosition ghostPiece,
                                     TetrominoPosition nextPiece,
                                     org.example.model.AdderBoard adderBoard,
                                     int score, int lines, int level) {
        player1Canvas.updateBoard(board, currentPiece, ghostPiece);
        player1NextPanel.updateNextPiece(nextPiece);
        if (adderBoard != null) {
            player1AdderCanvas.updateBoard(adderBoard);
        }
        player1ScorePanel.updateStats(score, lines, level);
    }
    
    /**
     * Player 2의 게임 화면을 업데이트합니다.
     */
    public void updatePlayer2Display(org.example.model.GameBoard board,
                                     TetrominoPosition currentPiece,
                                     TetrominoPosition ghostPiece,
                                     TetrominoPosition nextPiece,
                                     org.example.model.AdderBoard adderBoard,
                                     int score, int lines, int level) {
        player2Canvas.updateBoard(board, currentPiece, ghostPiece);
        player2NextPanel.updateNextPiece(nextPiece);
        if (adderBoard != null) {
            player2AdderCanvas.updateBoard(adderBoard);
        }
        player2ScorePanel.updateStats(score, lines, level);
    }
    
    /**
     * Player 1의 AdderBoard를 업데이트합니다.
     */
    public void updatePlayer1AdderBoard(org.example.model.AdderBoard adderBoard) {
        if (adderBoard != null) {
            player1AdderCanvas.updateBoard(adderBoard);
        }
    }
    
    /**
     * Player 2의 AdderBoard를 업데이트합니다.
     */
    public void updatePlayer2AdderBoard(org.example.model.AdderBoard adderBoard) {
        if (adderBoard != null) {
            player2AdderCanvas.updateBoard(adderBoard);
        }
    }
    
    // Getters
    public TetrisCanvas getPlayer1Canvas() {
        return player1Canvas;
    }
    
    public TetrisCanvas getPlayer2Canvas() {
        return player2Canvas;
    }
    
    public ShortNextPiecePanel getPlayer1NextPanel() {
        return player1NextPanel;
    }
    
    public ShortNextPiecePanel getPlayer2NextPanel() {
        return player2NextPanel;
    }
    
    public AdderCanvas getPlayer1AdderCanvas() {
        return player1AdderCanvas;
    }
    
    public AdderCanvas getPlayer2AdderCanvas() {
        return player2AdderCanvas;
    }
    
    public ScorePanel getPlayer1ScorePanel() {
        return player1ScorePanel;
    }
    
    public ScorePanel getPlayer2ScorePanel() {
        return player2ScorePanel;
    }
}
