package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * BoardSnapshot에서 받은 블럭을 저장하고 GameBoard에 추가하는 클래스.
 * 최대 20줄까지 저장할 수 있으며, 새로운 줄은 아래에 추가됩니다.
 */
public class AdderBoard {
    private static final int MAX_LINES = 20;
    private static final int WIDTH = GameBoard.WIDTH;
    
    private final List<int[]> lines;
    
    public AdderBoard() {
        this.lines = new ArrayList<>();
    }
    
    /**
     * BoardSnapshot에서 받은 라인들을 AdderBoard에 추가합니다.
     * 새로운 블럭을 아래에 바로 추가합니다.
     * 최대 20줄을 초과하는 라인은 무시합니다.
     * 
     * @param newLines 추가할 라인 배열 (컬러 인덱스 8번으로 변환된 상태)
     */
    public void addLines(int[][] newLines) {
        if (newLines == null || newLines.length == 0) {
            return;
        }
        
        // 새로운 라인들을 아래에 추가 (최대 20줄까지만)
        for (int[] line : newLines) {
            if (lines.size() >= MAX_LINES) {
                break; // 최대 줄 수에 도달하면 더 이상 추가하지 않음
            }
            if (line.length == WIDTH) {
                int[] copiedLine = new int[WIDTH];
                System.arraycopy(line, 0, copiedLine, 0, WIDTH);
                lines.add(copiedLine);
            }
        }
    }
    
    /**
     * 저장된 모든 라인을 GameBoard의 아래쪽에 추가합니다.
     * GameBoard의 블럭들을 위로 밀어올리고 AdderBoard의 줄을 아래에서부터 채웁니다.
     * 
     * @param gameBoard 라인을 추가할 GameBoard
     * @return 추가된 라인 수
     */
    public int applyToBoard(GameBoard gameBoard) {
        if (lines.isEmpty()) {
            return 0;
        }
        
        int linesToAdd = lines.size();
        int totalHeight = GameBoard.HEIGHT + GameBoard.BUFFER_ZONE;
        
        // 기존 블럭들을 위로 올림 (linesToAdd만큼)
        // 위에서부터 아래로 복사해야 데이터가 덮어쓰여지지 않음
        for (int row = linesToAdd; row < totalHeight; row++) {
            for (int col = 0; col < WIDTH; col++) {
                int color = gameBoard.getCellColor(row, col);
                gameBoard.setCellColor(row - linesToAdd, col, color);
            }
        }
        
        // AdderBoard의 줄을 아래에서부터 추가
        for (int i = 0; i < linesToAdd; i++) {
            int targetRow = totalHeight - linesToAdd + i;
            if (targetRow >= GameBoard.BUFFER_ZONE && targetRow < totalHeight) {
                int[] line = lines.get(i);
                for (int col = 0; col < WIDTH; col++) {
                    gameBoard.setCellColor(targetRow, col, line[col]);
                }
            }
        }
        
        int addedCount = linesToAdd;
        clear();
        return addedCount;
    }
    
    /**
     * 저장된 라인의 개수를 반환합니다.
     * 
     * @return 현재 저장된 라인 개수
     */
    public int getLineCount() {
        return lines.size();
    }
    
    /**
     * 저장된 모든 라인을 제거합니다.
     */
    public void clear() {
        lines.clear();
    }
    
    /**
     * 저장된 라인들을 고정 20x10 배열로 반환합니다.
     * AdderCanvas에서 렌더링할 수 있도록 빈 줄은 0으로 채워집니다.
     * 
     * @return 20x10 크기의 고정 배열
     */
    public int[][] getLines() {
        int[][] result = new int[MAX_LINES][WIDTH];
        
        // 저장된 라인들을 복사 (위쪽부터)
        for (int i = 0; i < lines.size(); i++) {
            System.arraycopy(lines.get(i), 0, result[i], 0, WIDTH);
        }
        
        // 나머지는 0으로 초기화됨 (자동)
        return result;
    }
}
