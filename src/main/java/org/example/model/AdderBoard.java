package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * BoardSnapshot에서 받은 블럭을 저장하고 GameBoard에 추가하는 클래스.
 * 최대 10줄까지 저장할 수 있으며, 새로운 줄은 뒤쪽(큰 인덱스)에 추가됩니다.
 * 세마포어를 사용하여 addLines/getLines는 다른 메소드 실행 완료 후 실행됩니다.
 */
public class AdderBoard {
    private static final int MAX_LINES = 10;
    private static final int WIDTH = GameBoard.WIDTH;
    
    private final List<int[]> lines;
    private final Semaphore semaphore;
    
    public AdderBoard() {
        this.lines = new ArrayList<>();
        this.semaphore = new Semaphore(1);
    }
    
    /**
     * BoardSnapshot에서 받은 라인들을 AdderBoard에 추가합니다.
     * 새로운 라인은 오름차순으로 뒤쪽에 추가됩니다 (append).
     * 예: 20, 22, 23번 라인이 제거됨 -> 현재 인덱스 3, 4, 5에 순서대로 추가
     * 최대 10줄까지만 저장되며, 초과 시 최신 라인(인덱스 10 이상)을 잘라냅니다.
     * 
     * @param newLines 추가할 라인 배열 (컴러 인덱스 8번으로 변환된 상태)
     *                 newLines[0] -> lines에 먼저 추가
     *                 newLines[length-1] -> lines에 나중에 추가
     */
    public void addLines(int[][] newLines) {
        if (newLines == null || newLines.length == 0) {
            return;
        }
        
        semaphore.acquireUninterruptibly();
        try {
            // 새로운 라인들을 순서대로 뒤쪽에 추가 (append)
            // newLines[0] (예: 20번 라인) -> lines에 먼저 추가
            // newLines[length-1] (예: 23번 라인) -> lines에 나중에 추가
            for (int[] line : newLines) {
                if (line.length == WIDTH) {
                    int[] copiedLine = new int[WIDTH];
                    System.arraycopy(line, 0, copiedLine, 0, WIDTH);
                    lines.add(copiedLine); // 뒤쪽에 추가
                }
            }
            
            // 10줄을 초과하면 최신 라인(인덱스 10 이상)을 잘라냄
            while (lines.size() > MAX_LINES) {
                lines.remove(lines.size() - 1);
            }
        } finally {
            semaphore.release();
        }
    }
    
    /**
     * 저장된 모든 라인을 GameBoard의 아래쪽에 추가합니다.
     * GameBoard의 블럭들을 위로 밀어올리고 AdderBoard의 줄을 아래에서부터 채웁니다.
     * 오름차순으로 대입: lines[0] -> 위쪽, lines[size-1] -> 23번(맨 아래)
     * 
     * @param gameBoard 라인을 추가할 GameBoard
     * @return 추가된 라인 수
     */
    public int applyToBoard(GameBoard gameBoard) {
        semaphore.acquireUninterruptibly();
        try {
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
            
            // AdderBoard의 줄을 아래에서부터 추가 (오름차순)
            // lines[0] -> totalHeight - linesToAdd
            // lines[size-1] -> totalHeight - 1 (23번)
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
            lines.clear();
            return addedCount;
        } finally {
            semaphore.release();
        }
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
     * 저장된 라인들을 고정 10x10 배열로 반환합니다.
     * AdderCanvas에서 렌더링할 수 있도록 빈 줄은 0으로 채워집니다.
     * 아래부터 채워서 제일 아래 라인이 비어있지 않도록 합니다.
     * 
     * 배열 구조:
     * - result[0] = 빈 줄 (라인이 10개 미만일 경우)
     * - result[MAX_LINES - lineCount] = lines[0] = 가장 먼저 추가된 라인
     * - result[MAX_LINES - 1] = lines[lineCount-1] = 가장 최근 라인 (제일 아래)
     * 
     * @return 10x10 크기의 고정 배열
     */
    public int[][] getLines() {
        int[][] result = new int[MAX_LINES][WIDTH];
        
        semaphore.acquireUninterruptibly();
        try {
            // 아래부터 채움: lines[0] -> result[MAX_LINES - lineCount], lines[size-1] -> result[MAX_LINES - 1]
            int lineCount = lines.size();
            for (int i = 0; i < lineCount; i++) {
                int targetIndex = MAX_LINES - lineCount + i;
                System.arraycopy(lines.get(i), 0, result[targetIndex], 0, WIDTH);
            }
        } finally {
            semaphore.release();
        }
        
        // 나머지는 0으로 초기화됨 (자동)
        return result;
    }
    
    /**
     * 최대 라인 수를 반환합니다.
     */
    public int getMaxLines() {
        return MAX_LINES;
    }
}
