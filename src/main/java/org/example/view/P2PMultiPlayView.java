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
import org.example.view.component.play.DummyTetrisCanvas;
import org.example.view.component.play.HoldPanel;
import org.example.view.component.play.NextPiecePanel;
import org.example.view.component.play.ScorePanel;
import org.example.view.component.play.TetrisCanvas;


/**
 * P2P MultiPlay 화면의 UI를 담당하는 View 클래스
 */
public class P2PMultiPlayView extends BaseView{

    private TetrisCanvas myGameCanvas;
    private DummyTetrisCanvas opGameCanvas;
    private HoldPanel holdPanel;
    private NextPiecePanel nextPanel;
    //private AdderCanvas adderCanvas;
    private ScorePanel scorePanel;
    private HBox root; // HBox 참조 저장

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
        // 메인 컨테이너 (상대 | 내 게임 캔버스 | 우측 패널)
        root = new HBox(20);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getGameBackgroundColor(), null, null)
        ));
        root.setPadding(new Insets(20));

        opGameCanvas = new DummyTetrisCanvas();
        myGameCanvas = new TetrisCanvas();
        HBox.setHgrow(myGameCanvas, Priority.NEVER);

        // 우측: VBox로 상하 분할
        VBox rightContainer = new VBox(10);
        rightContainer.setAlignment(Pos.TOP_CENTER);

        // 상단 영역: Hold와 Next
        VBox topContainer = new VBox(10);
        topContainer.setAlignment(Pos.TOP_CENTER);

        holdPanel = new HoldPanel();
        nextPanel = new NextPiecePanel();
        nextPanel.setHorizontalMode(false); // 수직 모드
        // adderCanvas = new AdderCanvas(); // 에더캔버스 10 x 10으로 수정 및 크기를 컨테이너 따라가도록 수정 필요

        topContainer.getChildren().addAll(holdPanel, nextPanel); 
        VBox.setVgrow(topContainer, Priority.ALWAYS);

        // 하단 영역: Score
        scorePanel = new ScorePanel(mode, difficulty);

        rightContainer.getChildren().addAll(topContainer, scorePanel);

        HBox.setHgrow(rightContainer, Priority.ALWAYS);
        
        root.getChildren().addAll(opGameCanvas, myGameCanvas, rightContainer);

        return root;
    }

    /**
     * 캔버스 크기를 씬 크기에 맞게 업데이트합니다.
     * 멀티플레이에서는 화면이 2배 넓으므로 고려하여 크기 조정
     */
    public void updateCanvasSize(Scene scene) {
        if (myGameCanvas == null || scene == null) return;
        
        // 사용 가능한 높이 전체를 캔버스에 할당
        double availableHeight = scene.getHeight() - 40; // 상하 패딩
        double canvasWidth = availableHeight * 0.5; // 1:2 비율 유지
        
        myGameCanvas.setCanvasSize(canvasWidth, availableHeight);
        opGameCanvas.setCanvasSize(canvasWidth, availableHeight);
        // adderCanvas.setCanvasSize(canvasWidth, availableHeight * 0.2);
    }

    /**
     * 내 게임 화면을 업데이트합니다.
     */
    public void updateDisplay(org.example.model.GameBoard board, 
                             TetrominoPosition currentPiece,
                             TetrominoPosition ghostPiece,
                             TetrominoPosition holdPiece,
                             java.util.List<TetrominoPosition> nextQueue,
                             int score, int lines, int level) {
        myGameCanvas.updateBoard(board, currentPiece, ghostPiece);
        holdPanel.updateHoldPiece(holdPiece);
        nextPanel.updateNextPieces(nextQueue);
        scorePanel.updateStats(score, lines, level);
    }

    // 상대방 화면 업데이트
    public void updateOpponentDisplay(int[][] board) {
        opGameCanvas.updateBoard(board);
    }
}
