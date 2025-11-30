package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AdderBoardSync {
    private static final int MAX_LINES = 10;
    private static final int WIDTH = GameBoard.WIDTH;

    private final GameBoard gameBoardRef;
    private final List<int[]> lines;
    private final int[][] drawBuffer = new int[MAX_LINES][WIDTH];
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public AdderBoardSync(GameBoard gameBoardRef) {
        this.gameBoardRef = gameBoardRef;
        this.lines = new ArrayList<>();
    }

    public void addLines(int[][] newLines) {
        rwLock.writeLock().lock();
        try {
            int cnt = Math.min(newLines.length, MAX_LINES - lines.size());
            for (int i = 0; i < cnt; i++) {
                lines.add(newLines[i]);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void applyToBoard() {
        rwLock.readLock().lock();
        try {
            int linesToAdd = lines.size();
            int totalHeight = GameBoard.HEIGHT + GameBoard.BUFFER_ZONE;

            for (int row = linesToAdd; row < totalHeight; row++) {
                for (int col = 0; col < WIDTH; col++) {
                    int color = gameBoardRef.getCellColor(row, col);
                    gameBoardRef.setCellColor(row - linesToAdd, col, color);
                }
            }

            for (int i = 0; i < linesToAdd; i++) {
                int targetRow = totalHeight - linesToAdd + i;
                if (targetRow >= GameBoard.BUFFER_ZONE && targetRow < totalHeight) {
                    int[] line = lines.get(i);
                    for (int col = 0; col < WIDTH; col++) {
                        gameBoardRef.setCellColor(targetRow, col, line[col]);
                    }
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
        rwLock.writeLock().lock();
        try {
            lines.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean isEmpty() {
        rwLock.readLock().lock();
        try {
            return lines.isEmpty();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public int[][] getDrawBuffer() {
        rwLock.readLock().lock();
        try {
            int lineCount = lines.size();
            for (int i = 0; i < MAX_LINES - lineCount; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    drawBuffer[i][j] = 0;
                }
            }
            for (int i = MAX_LINES - lineCount, j = 0; i < MAX_LINES; i++, j++) {
                System.arraycopy(lines.get(j), 0, drawBuffer[i], 0, WIDTH);
            }
            return drawBuffer;
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
