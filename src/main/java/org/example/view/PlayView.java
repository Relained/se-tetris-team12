package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.example.model.TetrominoPosition;
import org.example.view.component.play.HoldPanel;
import org.example.view.component.play.NextPiecePanel;
import org.example.view.component.play.ScorePanel;
import org.example.view.component.play.TetrisCanvas;

/**
 * Play 화면의 UI를 담당하는 View 클래스
 */
public class PlayView extends BaseView {
    
    private TetrisCanvas gameCanvas;
    private HoldPanel holdPanel;
    private NextPiecePanel nextPanel;
    private ScorePanel scorePanel;
    private VBox widgetContainer;
    
    public PlayView() {
        super(false); // NavigableButtonSystem 사용하지 않음
    }
    
    /**
     * Play 화면의 UI를 구성하고 반환합니다.
     * @return 구성된 HBox root
     */
    public HBox createView(String mode, String difficulty) {
        // 메인 컨테이너 (좌우 분할)
        HBox root = new HBox(20);
        root.getStyleClass().add("root-dark");
        root.setPadding(new Insets(20));
        
        // 좌측: 게임 캔버스
        gameCanvas = new TetrisCanvas();
        // 게임 캔버스는 확장되지 않도록 설정
        HBox.setHgrow(gameCanvas, Priority.NEVER);
        
        // 우측: 위젯 컨테이너
        widgetContainer = new VBox(10);
        widgetContainer.setAlignment(Pos.TOP_CENTER);
        widgetContainer.setPadding(new Insets(10));
        widgetContainer.getStyleClass().add("panel-widget");
        
        holdPanel = new HoldPanel();
        nextPanel = new NextPiecePanel();
        nextPanel.setHorizontalMode(false); // 수직 모드
        VBox spacer = new VBox(); // 빈 공간을 채우기 위한 스페이서
        scorePanel = new ScorePanel(mode, difficulty);
        
        widgetContainer.getChildren().addAll(holdPanel, nextPanel, spacer, scorePanel);
        VBox.setVgrow(holdPanel, Priority.NEVER);
        VBox.setVgrow(nextPanel, Priority.NEVER);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(scorePanel, Priority.NEVER);
        
        // 우측 컨테이너가 나머지 공간을 꽉 채우도록 설정
        HBox.setHgrow(widgetContainer, Priority.ALWAYS);
        
        root.getChildren().addAll(gameCanvas, widgetContainer);
        
        return root;
    }
    
    /**
     * 캔버스 크기를 씬 크기에 맞게 업데이트합니다.
     */
    public void updateCanvasSize(Scene scene) {
        if (gameCanvas == null || scene == null) return;
        
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();
        double padding = 40; // 상하 패딩
        double spacing = 20; // 간격
        
        // 사용 가능한 높이 전체를 캔버스에 할당
        double availableHeight = sceneHeight - padding;
        double canvasWidth = availableHeight * 0.5; // 1:2 비율 유지
        
        gameCanvas.setCanvasSize(canvasWidth, availableHeight);
        
        // 위젯 컨테이너 크기 조정 (창 너비에서 캔버스 너비를 빼고 나머지)
        if (widgetContainer != null) {
            double widgetWidth = sceneWidth - canvasWidth - padding - spacing;
            widgetWidth = Math.max(120, Math.min(250, widgetWidth)); // 최소 120, 최대 250
            widgetContainer.setPrefWidth(widgetWidth);
            widgetContainer.setMinWidth(widgetWidth);
            widgetContainer.setMaxWidth(widgetWidth);
        }
    }
    
    /**
     * 게임 화면을 업데이트합니다.
     * @param remainingMillis TIME_ATTACK 모드에서 남은 시간 (밀리초), -1이면 타이머 업데이트 안함
     */
    public void updateDisplay(org.example.model.GameBoard board, 
                             TetrominoPosition currentPiece,
                             TetrominoPosition ghostPiece,
                             TetrominoPosition holdPiece,
                             java.util.List<TetrominoPosition> nextQueue,
                             int score, int lines, int level,
                             long remainingMillis) {
        gameCanvas.updateBoard(board, currentPiece, ghostPiece);
        holdPanel.updateHoldPiece(holdPiece);
        nextPanel.updateNextPieces(nextQueue);
        scorePanel.updateStats(score, lines, level);
        
        if (remainingMillis >= 0) {
            scorePanel.updateTimer(remainingMillis);
        }
    }

    /**
     * 타이머 표시 활성화
     */
    public void setShowTimer(boolean show) {
        scorePanel.setShowTimer(show);
    }

    /**
     * Play 재개 시 모든 UI 요소의 크기를 업데이트합니다.
     */
    public void onResume() {
        if (gameCanvas != null) {
            gameCanvas.onResume();
        }
        if (holdPanel != null) {
            holdPanel.onResume();
        }
        if (nextPanel != null) {
            nextPanel.onResume();
        }
        if (scorePanel != null) {
            scorePanel.onResume();
        }
    }
    
    public TetrisCanvas getGameCanvas() {
        return gameCanvas;
    }
    
    public HoldPanel getHoldPanel() {
        return holdPanel;
    }
    
    public NextPiecePanel getNextPanel() {
        return nextPanel;
    }
    
    public ScorePanel getScorePanel() {
        return scorePanel;
    }
}
