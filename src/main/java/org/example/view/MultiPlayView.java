package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

import org.example.model.TetrominoPosition;
import org.example.view.component.play.HoldPanel;
import org.example.view.component.play.NextPiecePanel;
import org.example.view.component.play.ScorePanel;
import org.example.view.component.play.TetrisCanvas;


/**
 * MultiPlay 화면의 UI를 담당하는 View 클래스
 */
public class MultiPlayView extends BaseView{

    private TetrisCanvas myGameCanvas;
    private ArrayList<TetrisCanvas> opGameCanvases;
    private HoldPanel holdPanel;
    private NextPiecePanel nextPanel;
    private ScorePanel scorePanel;
    private HBox root; // HBox 참조 저장
    private GridPane opponentsContainer; // GridPane 참조 저장

    public MultiPlayView() {
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

        // 중간: 게임 캔버스
        myGameCanvas = new TetrisCanvas();
        // 게임 캔버스는 확장되지 않도록 설정
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

        topContainer.getChildren().addAll(holdPanel, nextPanel); 
        VBox.setVgrow(topContainer, Priority.ALWAYS);

        // 하단 영역: Score
        scorePanel = new ScorePanel(mode, difficulty);

        rightContainer.getChildren().addAll(topContainer, scorePanel);

        HBox.setHgrow(rightContainer, Priority.ALWAYS);
        
        // 좌측: 상대방 게임 캔버스들 (초기에는 빈 GridPane)
        opGameCanvases = new ArrayList<>();
        opponentsContainer = createOpponentsGrid();

        root.getChildren().addAll(opponentsContainer, myGameCanvas, rightContainer);

        return root;
    }

    /**
     * 상대방 수에 따라 동적으로 GridPane 레이아웃을 생성합니다.
     * - 1명: 1x1 (전체 크기)
     * - 2-4명: 2x2
     * - 5-9명: 3x3
     * - 10-16명: 4x4
     * 
     * @return 구성된 GridPane
     */
    private GridPane createOpponentsGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);

        int opponentCount = opGameCanvases.size();
        int gridSize = calculateGridSize(opponentCount);

        for (int i = 0; i < opponentCount; i++) {
            int row = i / gridSize;
            int col = i % gridSize;
            grid.add(opGameCanvases.get(i), col, row);
        }

        return grid;
    }

    /**
     * 상대방 수에 따라 그리드 크기를 계산합니다.
     * 
     * @param opponentCount 상대방 수
     * @return 그리드 한 변의 크기 (1, 2, 3, 또는 4)
     */
    private int calculateGridSize(int opponentCount) {
        if (opponentCount <= 1) {
            return 1;
        } else if (opponentCount <= 4) {
            return 2;
        } else if (opponentCount <= 9) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * 상대방 캔버스를 추가하고 레이아웃을 업데이트합니다.
     * 
     * @param scene 현재 씬 (캔버스 크기 조정용)
     */
    public void addOpponentCanvas(Scene scene) {
        TetrisCanvas opCanvas = new TetrisCanvas();
        opGameCanvases.add(opCanvas);
        updateOpponentsGrid();
        
        // 캔버스 크기도 재조정
        if (scene != null) {
            updateCanvasSize(scene);
        }
    }

    /**
     * 상대방 GridPane 레이아웃을 현재 ArrayList 크기에 맞게 업데이트합니다.
     * 기존 GridPane을 제거하고 새로운 GridPane으로 교체합니다.
     */
    public void updateOpponentsGrid() {
        if (root == null || opponentsContainer == null) {
            return;
        }

        // 기존 GridPane의 인덱스 찾기 (첫 번째 자식)
        int gridIndex = root.getChildren().indexOf(opponentsContainer);
        
        // 기존 GridPane 제거
        root.getChildren().remove(opponentsContainer);
        
        // 새로운 GridPane 생성
        opponentsContainer = createOpponentsGrid();
        
        // 같은 위치에 새 GridPane 추가
        root.getChildren().add(gridIndex, opponentsContainer);
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
        
        // 상대방 캔버스들 크기 조정 - 그리드 크기에 따라 동적으로 계산
        int opponentCount = opGameCanvases.size();
        if (opponentCount > 0) {
            int gridSize = calculateGridSize(opponentCount);
            
            // 그리드 크기에 따라 각 캔버스 크기 계산
            // 사용 가능한 공간을 그리드 크기로 나눔
            double gridGap = 5.0; // hgap, vgap
            double totalGridWidth = scene.getWidth() / 3.0 - 40; // 화면의 1/3 정도 할당
            double totalGridHeight = availableHeight;
            
            // 간격을 고려한 캔버스 크기 계산
            double opponentCanvasWidth = (totalGridWidth - (gridSize - 1) * gridGap) / gridSize;
            double opponentCanvasHeight = (totalGridHeight - (gridSize - 1) * gridGap) / gridSize;
            
            // 1:2 비율 유지하면서 더 작은 값에 맞춤
            double maxWidth = opponentCanvasHeight * 0.5;
            if (opponentCanvasWidth > maxWidth) {
                opponentCanvasWidth = maxWidth;
            } else {
                opponentCanvasHeight = opponentCanvasWidth * 2.0;
            }
            
            // 모든 상대방 캔버스에 동일한 크기 적용
            for (TetrisCanvas opCanvas : opGameCanvases) {
                opCanvas.setCanvasSize(opponentCanvasWidth, opponentCanvasHeight);
            }
        }
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

    /**
     * 특정 상대방의 게임 화면을 업데이트합니다.
     * 
     * @param opponentIndex 상대방 인덱스 (0부터 시작)
     * @param board 게임 보드
     * @param currentPiece 현재 테트로미노
     * @param ghostPiece 고스트 테트로미노
     */
    public void updateOpponentDisplay(int opponentIndex, 
                                     org.example.model.GameBoard board,
                                     TetrominoPosition currentPiece,
                                     TetrominoPosition ghostPiece) {
        if (opponentIndex >= 0 && opponentIndex < opGameCanvases.size()) {
            opGameCanvases.get(opponentIndex).updateBoard(board, currentPiece, ghostPiece);
        }
    }

    // Getters
    public TetrisCanvas getMyGameCanvas() {
        return myGameCanvas;
    }

    public ArrayList<TetrisCanvas> getOpponentCanvases() {
        return opGameCanvases;
    }

    public TetrisCanvas getOpponentCanvas(int index) {
        if (index >= 0 && index < opGameCanvases.size()) {
            return opGameCanvases.get(index);
        }
        return null;
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
