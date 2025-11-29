package org.example.view.component.play;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.model.TetrominoPosition;

import java.util.List;

public class NextPiecePanel extends VBox {
    private Canvas[] nextCanvases;
    private Text title;
    private double largeCellSize = 20;
    private double smallCellSize = 12;
    private boolean horizontalMode = false;
    private HBox canvasContainer;
    private double lastAppliedWidth = -1;

    public NextPiecePanel() {
        super(5);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10));

        // 테두리 스타일
        getStyleClass().add("panel-next-piece");

        this.title = new Text("Next");
        title.setFill(Color.WHITE);
        title.setFont(Font.font(16));
        getChildren().add(title);

        this.nextCanvases = new Canvas[5];
        this.canvasContainer = new HBox(10);
        canvasContainer.setAlignment(Pos.CENTER);

        // 캔버스 초기화
        initializeCanvases();
    }
    
    /**
     * NextPiecePanel의 크기에 맞게 캔버스 크기를 조정합니다. (외부에서 명시적으로 호출)
     */
    public void updateCanvasSize() {
        double currentWidth = getWidth();
        if (currentWidth > 0 && Math.abs(currentWidth - lastAppliedWidth) > 2) {
            adjustCanvasSizeByWidth(currentWidth);
            lastAppliedWidth = currentWidth;
        }
    }

    /**
     * 일시정지 해제(resume) 시 반드시 updateCanvasSize()를 호출해야 함
     */
    public void onResume() {
        updateCanvasSize();
    }

    public void setHorizontalMode(boolean horizontal) {
        this.horizontalMode = horizontal;
        updateLayout();
    }
    
    private void initializeCanvases() {
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                nextCanvases[i] = new Canvas(4 * largeCellSize + 8, 4 * largeCellSize + 8);
            } else {
                nextCanvases[i] = new Canvas(4 * smallCellSize + 4, 4 * smallCellSize + 4);
            }
        }
        updateLayout();
    }
    
    private void updateLayout() {
        getChildren().clear();
        getChildren().add(title);
        
        if (horizontalMode) {
            // 수평 모드: 모든 캔버스를 HBox에 추가
            canvasContainer.getChildren().clear();
            for (Canvas canvas : nextCanvases) {
                canvasContainer.getChildren().add(canvas);
            }
            getChildren().add(canvasContainer);
        } else {
            // 수직 모드: 캔버스를 직접 추가
            for (Canvas canvas : nextCanvases) {
                getChildren().add(canvas);
            }
        }
    }
    
    /**
     * 가로 크기를 기준으로 캔버스 크기를 조정합니다.
     */
    private void adjustCanvasSizeByWidth(double containerWidth) {
        double padding = 24; // 좌우 패딩 + 테두리
        double availableWidth = containerWidth - padding;
        
        if (availableWidth <= 0) return;
        
        double largeCanvasSize;
        double smallCanvasSize;
        
        if (horizontalMode) {
            // 수평 모드: 너비를 5개의 캔버스로 나눔
            largeCanvasSize = Math.max(50, (availableWidth / 5) - 10);
            smallCanvasSize = largeCanvasSize; // 수평 모드에서는 모두 같은 크기
        } else {
            // 수직 모드: 가로 크기의 80%로 제한하여 늘어남 방지
            largeCanvasSize = Math.max(60, Math.min(availableWidth * 0.85, 150));
            smallCanvasSize = Math.max(40, largeCanvasSize * 0.55);
        }
        
        nextCanvases[0].setWidth(largeCanvasSize);
        nextCanvases[0].setHeight(largeCanvasSize);
        largeCellSize = (largeCanvasSize - 8) / 4;
        
        smallCellSize = (smallCanvasSize - 4) / 4;
        
        for (int i = 1; i < 5; i++) {
            nextCanvases[i].setWidth(smallCanvasSize);
            nextCanvases[i].setHeight(smallCanvasSize);
        }
        
        // 폰트 크기도 비례하여 조정
        double fontSize = Math.max(10, Math.min(18, largeCanvasSize / 4));
        title.setFont(Font.font(fontSize));
    }

    public void updateNextPieces(List<TetrominoPosition> nextPieces) {
        for (int i = 0; i < nextCanvases.length && i < nextPieces.size(); i++) {
            drawTetromino(nextCanvases[i], nextPieces.get(i), i);
        }
    }

    private void drawTetromino(Canvas canvas, TetrominoPosition piece, int index) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 첫 번째 피스인 경우 창틀 효과를 위한 배경 그리기
        if (index == 0) {
            gc.setFill(Color.web("#444"));
            gc.fillRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 5, 5);
            gc.setStroke(Color.web("#666"));
            gc.setLineWidth(2);
            gc.strokeRoundRect(1, 1, canvas.getWidth() - 2, canvas.getHeight() - 2, 5, 5);
        }

        int[][] shape = piece.getCurrentShape();
        Color color = piece.getDisplayColor(org.example.service.ColorManager.getInstance());

        // 인덱스에 따라 다른 셀 크기 사용
        double cellSize = (index == 0) ? largeCellSize : smallCellSize;
        
        // Center the piece in the canvas
        double offsetX = (canvas.getWidth() - shape[0].length * cellSize) / 2;
        double offsetY = (canvas.getHeight() - shape.length * cellSize) / 2;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    double x = offsetX + col * cellSize;
                    double y = offsetY + row * cellSize;

                    gc.setFill(color);
                    gc.fillRect(x, y, cellSize, cellSize);

                    gc.setStroke(Color.DARKGRAY);
                    gc.setLineWidth(1);
                    gc.strokeRect(x, y, cellSize, cellSize);
                    
                    // 아이템 글자 표시
                    org.example.model.ItemBlock item = piece.getItemAt(row, col);
                    if (item != null && item.isItem()) {
                        gc.setFill(Color.WHITE);
                        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, cellSize * 0.8));
                        gc.fillText(String.valueOf(item.getSymbol()), 
                                  x + cellSize * 0.2, 
                                  y + cellSize * 0.8);
                    }
                }
            }
        }
    }
}