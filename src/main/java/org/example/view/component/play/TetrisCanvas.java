package org.example.view.component.play;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.example.service.ColorManager;
import org.example.model.GameBoard;
import org.example.model.TetrominoPosition;

/**
 * 테트리스 메인 컴포넌트입니다.
 * 창의 높이에 따라 동적으로 크기가 조정됩니다.
 */
public class TetrisCanvas extends Canvas {
    private double cellSize = 30;
    private final ColorManager colorManager;

    private static final Color BORDER_COLOR = Color.WHITE;
    private static final Color GHOST_COLOR = Color.GRAY;
    private final Color BACKGROUND_COLOR;

    private GameBoard board;
    private TetrominoPosition currentPiece;
    private TetrominoPosition ghostPiece;

    public TetrisCanvas() {
        super(GameBoard.WIDTH * 30, GameBoard.HEIGHT * 30);
        this.colorManager = ColorManager.getInstance();
        this.BACKGROUND_COLOR = colorManager.getBackgroundColor();
    }

    /**
     * 캔버스 크기를 다시 계산합니다. (외부에서 명시적으로 호출)
     */
    public void updateCanvasSize() {
        // 현재 높이 기반으로 cell size 재계산
        double currentHeight = getHeight();
        if (currentHeight > 0) {
            cellSize = currentHeight / GameBoard.HEIGHT;
            setWidth(GameBoard.WIDTH * cellSize);
            draw();
        }
    }

    /**
     * 일시정지 해제(resume) 시 반드시 updateCanvasSize()를 호출해야 함
     */
    public void onResume() {
        updateCanvasSize();
    }
    
    public void setCanvasHeight(double height) {
        setHeight(height);
    }
    
    public void setCanvasSize(double width, double height) {
        // 테트리스 비율 (가로:세로 = 1:2) 유지
        double widthBasedHeight = width * 2;
        double heightBasedWidth = height * 0.5;
        
        // 더 작은 값으로 조정하여 비율 유지
        if (widthBasedHeight <= height) {
            setWidth(width);
            setHeight(widthBasedHeight);
            cellSize = width / GameBoard.WIDTH;
        } else {
            setWidth(heightBasedWidth);
            setHeight(height);
            cellSize = height / GameBoard.HEIGHT;
        }
        draw();
    }

    public void updateBoard(GameBoard board, TetrominoPosition currentPiece, TetrominoPosition ghostPiece) {
        this.board = board;
        this.currentPiece = currentPiece;
        this.ghostPiece = ghostPiece;
        draw();
    }

    protected void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Draw background
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, getWidth(), getHeight());

        if (board == null) return;

        // Draw placed blocks
        int[][] visibleBoard = board.getVisibleBoard();
        for (int row = 0; row < GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                int v = visibleBoard[row][col];
                if (v == 0) continue;
                if (v == GameBoard.CLEAR_MARK) {
                    // pending-clear cells: draw white, no item mark
                    drawCell(gc, col, row, Color.WHITE);
                    continue;
                }
                Color color = colorManager.getColorFromIndex(v);
                drawCell(gc, col, row, color);

                // 아이템 마크 표시 (board 값이 아이템 char인 경우)
                if (org.example.model.ItemBlock.isItemValue(v)) {
                    drawItemMark(gc, col, row, (char) v);
                }
            }
        }

        // Draw ghost piece
        if (ghostPiece != null) {
            drawPiece(gc, ghostPiece, GHOST_COLOR, true);
        }

        // Draw current piece
        if (currentPiece != null) {
            Color pieceColor = currentPiece.getDisplayColor(colorManager);
            drawPiece(gc, currentPiece, pieceColor, false);
        }

    // No separate overlay; pending cells are drawn white directly

        // Draw grid
        drawGrid(gc);
    }

    // overlay method removed in marker-based approach

    private void drawPiece(GraphicsContext gc, TetrominoPosition piece, Color color, boolean isGhost) {
        int[][] shape = piece.getCurrentShape();
        int startX = piece.getX();
        int startY = piece.getY() - GameBoard.BUFFER_ZONE; // Adjust for buffer zone

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int x = startX + col;
                    int y = startY + row;
                    if (x >= 0 && x < GameBoard.WIDTH && y >= 0 && y < GameBoard.HEIGHT) {
                        if (isGhost) {
                            drawGhostCell(gc, x, y);
                        } else {
                            drawCell(gc, x, y, color);
                            
                            org.example.model.ItemBlock item = piece.getItemAt(row, col);
                            if (item != null && item.isItem()) {
                                drawItemMark(gc, x, y, item.getSymbol());
                            }

                            // 특수 조각(WEIGHT/BOMB) 표식 표시
                            var special = piece.getSpecialKind();
                            if (special == TetrominoPosition.SpecialKind.WEIGHT) {
                                drawItemMark(gc, x, y, 'W');
                            } else if (special == TetrominoPosition.SpecialKind.BOMB) {
                                drawItemMark(gc, x, y, 'B');
                            }
                        }
                    }
                }
            }
        }
    }

    protected void drawCell(GraphicsContext gc, int x, int y, Color color) {
        double pixelX = x * cellSize;
        double pixelY = y * cellSize;

        gc.setFill(color);
        gc.fillRect(pixelX, pixelY, cellSize, cellSize);

        gc.setStroke(BORDER_COLOR);
        gc.setLineWidth(1);
        gc.strokeRect(pixelX, pixelY, cellSize, cellSize);
    }

    protected void drawGhostCell(GraphicsContext gc, int x, int y) {
        double pixelX = x * cellSize;
        double pixelY = y * cellSize;

        gc.setStroke(GHOST_COLOR);
        gc.setLineWidth(2);
        gc.strokeRect(pixelX + 2, pixelY + 2, cellSize - 4, cellSize - 4);
    }

    protected void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);

        for (int i = 0; i <= GameBoard.WIDTH; i++) {
            double x = i * cellSize;
            gc.strokeLine(x, 0, x, getHeight());
        }

        for (int i = 0; i <= GameBoard.HEIGHT; i++) {
            double y = i * cellSize;
            gc.strokeLine(0, y, getWidth(), y);
        }
    }

    protected void drawItemMark(GraphicsContext gc, int x, int y, char symbol) {
        double pixelX = x * cellSize;
        double pixelY = y * cellSize;
        
        // 텍스트 설정
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        
        // 폰트 크기를 셀 크기에 맞게 조정
        double fontSize = cellSize * 0.7;
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize));
        
        // 텍스트 중앙 정렬을 위한 계산
        javafx.scene.text.Text text = new javafx.scene.text.Text(String.valueOf(symbol));
        text.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize));
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        
        double textX = pixelX + (cellSize - textWidth) / 2;
        double textY = pixelY + (cellSize + textHeight) / 2 - 2;
        
        // 검은 테두리
        gc.strokeText(String.valueOf(symbol), textX, textY);
        // 흰색 글자
        gc.fillText(String.valueOf(symbol), textX, textY);
    }
}
