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
import org.example.view.component.play.HoldPanel;
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
    private HoldPanel player1HoldPanel;
    private AdderCanvas player1AdderCanvas;
    private ScorePanel player1ScorePanel;
    
    // Player 2 (우측)
    private TetrisCanvas player2Canvas;
    private ShortNextPiecePanel player2NextPanel;
    private HoldPanel player2HoldPanel;
    private AdderCanvas player2AdderCanvas;
    private ScorePanel player2ScorePanel;
    
    // 위젯 컨테이너
    private VBox player1WidgetContainer;
    private VBox player2WidgetContainer;
    
    private HBox root;
    
    // 게임 모드 정보
    private String gameModeName;
    private String difficultyName;
    
    public LocalMultiPlayView() {
        super(false); // NavigableButtonSystem 사용하지 않음
    }
    
    /**
     * LocalMultiPlay 화면의 UI를 구성하고 반환합니다.
     * 
     * @param mode 게임 모드 이름
     * @param difficulty 난이도 이름
     * @return 구성된 HBox root
     */
    public HBox createView(String mode, String difficulty) {
        this.gameModeName = mode;
        this.difficultyName = difficulty;
        // 메인 컨테이너 (플레이어2 위젯 | 플레이어2 캔버스 | 플레이어1 캔버스 | 플레이어1 위젯)
        root = new HBox(20);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getGameBackgroundColor(), null, null)
        ));
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        // Player 2 영역 (좌측)
        player2WidgetContainer = createPlayerWidgets(false);
        player2Canvas = new TetrisCanvas();
        HBox.setHgrow(player2Canvas, Priority.NEVER);
        
        // Player 1 영역 (우측)
        player1Canvas = new TetrisCanvas();
        HBox.setHgrow(player1Canvas, Priority.NEVER);
        player1WidgetContainer = createPlayerWidgets(true);
        
        root.getChildren().addAll(player2WidgetContainer, player2Canvas, player1Canvas, player1WidgetContainer);
        
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
        HoldPanel holdPanel = new HoldPanel();
        AdderCanvas adderCanvas = new AdderCanvas();
        VBox spacer = new VBox(); // 빈 공간을 채우기 위한 스페이서
        ScorePanel scorePanel = new ScorePanel(gameModeName, difficultyName);
        
        if (isPlayer1) {
            player1NextPanel = nextPanel;
            player1HoldPanel = holdPanel;
            player1AdderCanvas = adderCanvas;
            player1ScorePanel = scorePanel;
        } else {
            player2NextPanel = nextPanel;
            player2HoldPanel = holdPanel;
            player2AdderCanvas = adderCanvas;
            player2ScorePanel = scorePanel;
        }
        
        widgetContainer.getChildren().addAll(nextPanel, holdPanel, adderCanvas, spacer, scorePanel);
        VBox.setVgrow(nextPanel, Priority.NEVER);
        VBox.setVgrow(holdPanel, Priority.NEVER);
        VBox.setVgrow(adderCanvas, Priority.NEVER);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(scorePanel, Priority.NEVER);
        HBox.setHgrow(widgetContainer, Priority.NEVER);
        
        // AdderCanvas 크기가 컨테이너를 따르도록 리스너 설정
        widgetContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            double containerWidth = newVal.doubleValue() - 20; // 패딩 고려
            if (containerWidth > 0) {
                adderCanvas.setCanvasSize(containerWidth, adderCanvas.getHeight());
            }
        });
        
        widgetContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            double containerWidth = widgetContainer.getWidth() - 20;
            if (containerWidth > 0) {
                adderCanvas.setCanvasSize(containerWidth, adderCanvas.getHeight());
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
        
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();
        double padding = 40; // 상하 패딩
        double spacing = 20; // 간격
        
        // 사용 가능한 높이 전체를 캔버스에 할당
        double availableHeight = sceneHeight - padding;
        double canvasWidth = availableHeight * 0.5; // 1:2 비율 유지
        
        player1Canvas.setCanvasSize(canvasWidth, availableHeight);
        player2Canvas.setCanvasSize(canvasWidth, availableHeight);
        
        // 위젯 컨테이너 크기 조정 (창 너비에서 캔버스 2개와 패딩/간격을 빼고 2로 나눔)
        double totalCanvasWidth = canvasWidth * 2;
        double availableWidgetWidth = (sceneWidth - totalCanvasWidth - padding - spacing * 3) / 2;
        double widgetWidth = Math.max(100, Math.min(180, availableWidgetWidth));
        
        if (player1WidgetContainer != null) {
            player1WidgetContainer.setPrefWidth(widgetWidth);
            player1WidgetContainer.setMinWidth(widgetWidth);
            player1WidgetContainer.setMaxWidth(widgetWidth);
        }
        if (player2WidgetContainer != null) {
            player2WidgetContainer.setPrefWidth(widgetWidth);
            player2WidgetContainer.setMinWidth(widgetWidth);
            player2WidgetContainer.setMaxWidth(widgetWidth);
        }
        
        // AdderCanvas 크기도 맞춤 (위젯 컨테이너 너비 기준)
        double adderSize = widgetWidth - 20; // 패딩 고려
        player1AdderCanvas.setCanvasSize(adderSize, adderSize);
        player2AdderCanvas.setCanvasSize(adderSize, adderSize);
        
        // NextPanel 크기 조정
        double nextPanelSize = Math.max(60, adderSize * 0.8);
        player1NextPanel.setPreferredCanvasSize(nextPanelSize);
        player2NextPanel.setPreferredCanvasSize(nextPanelSize);
    }
    
    /**
     * Player 1의 게임 화면을 업데이트합니다.
     * @param remainingMillis TIME_ATTACK 모드에서 남은 시간 (밀리초), -1이면 타이머 업데이트 안함
     */
    public void updatePlayer1Display(org.example.model.GameBoard board,
                                     TetrominoPosition currentPiece,
                                     TetrominoPosition ghostPiece,
                                     TetrominoPosition holdPiece,
                                     TetrominoPosition nextPiece,
                                     org.example.model.AdderBoard adderBoard,
                                     int score, int lines, int level,
                                     long remainingMillis) {
        player1Canvas.updateBoard(board, currentPiece, ghostPiece);
        player1HoldPanel.updateHoldPiece(holdPiece);
        player1NextPanel.updateNextPiece(nextPiece);
        if (adderBoard != null) {
            player1AdderCanvas.updateBoard(adderBoard);
        }
        player1ScorePanel.updateStats(score, lines, level);
        
        if (remainingMillis >= 0) {
            player1ScorePanel.updateTimer(remainingMillis);
        }
    }
    
    /**
     * Player 2의 게임 화면을 업데이트합니다.
     * @param remainingMillis TIME_ATTACK 모드에서 남은 시간 (밀리초), -1이면 타이머 업데이트 안함
     */
    public void updatePlayer2Display(org.example.model.GameBoard board,
                                     TetrominoPosition currentPiece,
                                     TetrominoPosition ghostPiece,
                                     TetrominoPosition holdPiece,
                                     TetrominoPosition nextPiece,
                                     org.example.model.AdderBoard adderBoard,
                                     int score, int lines, int level,
                                     long remainingMillis) {
        player2Canvas.updateBoard(board, currentPiece, ghostPiece);
        player2HoldPanel.updateHoldPiece(holdPiece);
        player2NextPanel.updateNextPiece(nextPiece);
        if (adderBoard != null) {
            player2AdderCanvas.updateBoard(adderBoard);
        }
        player2ScorePanel.updateStats(score, lines, level);
        
        if (remainingMillis >= 0) {
            player2ScorePanel.updateTimer(remainingMillis);
        }
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
    
    /**
     * 타이머 표시 활성화 (양 플레이어 모두)
     */
    public void setShowTimer(boolean show) {
        player1ScorePanel.setShowTimer(show);
        player2ScorePanel.setShowTimer(show);
    }
    
    public ShortNextPiecePanel getPlayer1NextPanel() {
        return player1NextPanel;
    }
    
    public ShortNextPiecePanel getPlayer2NextPanel() {
        return player2NextPanel;
    }
    
    public HoldPanel getPlayer1HoldPanel() {
        return player1HoldPanel;
    }
    
    public HoldPanel getPlayer2HoldPanel() {
        return player2HoldPanel;
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
