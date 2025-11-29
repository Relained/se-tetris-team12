package org.example.model;
import java.util.List;


/**
 * GameBoard의 상태를 저장하는 스냅샷 클래스.
 * 버퍼존을 제외한 보이는 영역만 저장합니다.
 */
public class BoardSnapshot {
    private final int[][] boardState;
    private final int width;
    private final int height;
    
    /**
     * GameBoard에서 버퍼존을 제외한 영역을 복사하여 스냅샷을 생성합니다.
     * 
     * @param gameBoard 스냅샷을 생성할 GameBoard
     */
    public BoardSnapshot(GameBoard gameBoard) {
        this.height = GameBoard.HEIGHT;
        this.width = GameBoard.WIDTH;
        
        // 버퍼존을 제외한 보드 상태를 깊은 복사
        this.boardState = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                boardState[row][col] = gameBoard.getCellColor(row + GameBoard.BUFFER_ZONE, col);
            }
        }
    }
    
    /**
     * 주어진 인덱스 리스트의 라인들을 이어붙여 반환합니다.
     * 0과 -1이 아닌 모든 블럭의 컬러를 8번(회색)으로 변경하여 반환합니다.
     * 
     * @param lineIndices 가져올 라인의 인덱스 리스트 (0부터 시작)
     * @return 해당 라인들을 연결한 2D 배열 (행 개수 = lineIndices.size(), 열 개수 = width)
     */
    public int[][] getLines(List<Integer> lineIndices) {
        int[][] result = new int[lineIndices.size()][width];
        
        for (int i = 0; i < lineIndices.size(); i++) {
            int lineIndex = lineIndices.get(i);
            if (lineIndex >= 0 && lineIndex < height) {
                for (int col = 0; col < width; col++) {
                    int color = boardState[lineIndex][col];
                    // 0과 -1이 아닌 모든 값을 8번(회색) 인덱스로 변경, -1은 0으로 처리
                    if (color <= 0) {
                        result[i][col] = 0;
                    } else {
                        result[i][col] = 8;
                    }
                }
            }
        }
        
        return result;
    }
}
