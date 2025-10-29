package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
    
    public PlayView() {
        super(false); // NavigableButtonSystem 사용하지 않음
    }
    
    /**
     * Play 화면의 UI를 구성하고 반환합니다.
     * @return 구성된 HBox root
     */
    public HBox createView() {
        // 메인 컨테이너 (좌우 분할)
        HBox root = new HBox(5);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getGameBackgroundColor(), null, null)
        ));
        root.setPadding(new Insets(20));

        // 좌측: 게임 캔버스 영역
        VBox leftContainer = new VBox();
        leftContainer.setAlignment(Pos.CENTER);
        
        gameCanvas = new TetrisCanvas();
        leftContainer.getChildren().add(gameCanvas);
        
        // 우측: 모든 UI 패널들을 VBox로 세로 배치 (Hold, Next, Score 순서)
        VBox rightContainer = new VBox(5);
        rightContainer.setAlignment(Pos.TOP_CENTER);
        rightContainer.setPadding(new Insets(0, 0, 0, 20));
        
        // Hold, Next, Score 패널들 생성 및 추가
        holdPanel = new HoldPanel();
        nextPanel = new NextPiecePanel();
        scorePanel = new ScorePanel();
        
        rightContainer.getChildren().addAll(holdPanel, nextPanel, scorePanel);
        
        // 좌측 영역이 더 많은 공간을 차지하도록 설정
        HBox.setHgrow(leftContainer, Priority.ALWAYS);
        
        root.getChildren().addAll(leftContainer, rightContainer);

        return root;
    }
    
    /**
     * 캔버스 크기를 씬 크기에 맞게 업데이트합니다.
     */
    public void updateCanvasSize(Scene scene) {
        if (gameCanvas == null || scene == null) return;
        
        // 사용 가능한 공간 계산 (여백 고려)
        double availableWidth = scene.getWidth() * 0.65 - 40; // 좌측 65% 영역에서 여백 제외
        double availableHeight = scene.getHeight() - 40; // 상하 여백 제외
        
        // 최소 크기 보장
        availableWidth = Math.max(300, availableWidth);
        availableHeight = Math.max(400, availableHeight);
        
        // 캔버스 크기를 비율에 맞게 조정
        gameCanvas.setCanvasSize(availableWidth, availableHeight);
    }
    
    /**
     * 게임 화면을 업데이트합니다.
     */
    public void updateDisplay(org.example.model.GameBoard board, 
                             org.example.model.TetrominoPosition currentPiece,
                             org.example.model.TetrominoPosition ghostPiece,
                             org.example.model.TetrominoPosition holdPiece,
                             java.util.List<org.example.model.TetrominoPosition> nextQueue,
                             int score, int lines, int level) {
        if (gameCanvas != null) {
            gameCanvas.updateBoard(board, currentPiece, ghostPiece);
            holdPanel.updateHoldPiece(holdPiece);
            nextPanel.updateNextPieces(nextQueue);
            scorePanel.updateStats(score, lines, level);
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
