package org.example.service;

import org.example.model.GameBoard;
import org.example.model.TetrominoPosition;
import org.example.model.Tetromino;

public class SuperRotationSystem {
    // SRS Wall Kick Data - kicks for J,L,S,T,Z pieces
    private static final int[][][] JLSTZ_KICKS = {
        // 0->R (0->1)
        {{0,0}, {-1,0}, {-1,1}, {0,-2}, {-1,-2}},
        // R->2 (1->2)
        {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}},
        // 2->L (2->3)
        {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}},
        // L->0 (3->0)
        {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}},
    };

    // I piece has different kick data
    private static final int[][][] I_KICKS = {
        // 0->R (0->1)
        {{0,0}, {-2,0}, {1,0}, {-2,-1}, {1,2}},
        // R->2 (1->2)
        {{0,0}, {-1,0}, {2,0}, {-1,2}, {2,-1}},
        // 2->L (2->3)
        {{0,0}, {2,0}, {-1,0}, {2,1}, {-1,-2}},
        // L->0 (3->0)
        {{0,0}, {1,0}, {-2,0}, {1,-2}, {-2,1}},
    };

    public static class RotationResult {
        private final TetrominoPosition position;
        private final boolean isTSpin;
        private final int kickUsed;

        public RotationResult(TetrominoPosition position, boolean isTSpin, int kickUsed) {
            this.position = position;
            this.isTSpin = isTSpin;
            this.kickUsed = kickUsed;
        }

        public TetrominoPosition getPosition() { return position; }
        public boolean isTSpin() { return isTSpin; }
        public int getKickUsed() { return kickUsed; }
    }

    public static RotationResult attemptRotationWithTSpinCheck(TetrominoPosition current, GameBoard board, boolean clockwise) {
        TetrominoPosition newPos = current.copy();
        int oldRotation = current.getRotation();
        int newRotation = clockwise ?
            Math.floorMod(oldRotation + 1, 4) :
            Math.floorMod(oldRotation - 1, 4);

        newPos.setRotation(newRotation);

        // O piece doesn't need wall kicks, but still needs to rotate for item tracking
        if (current.getType() == Tetromino.O) {
            // Always succeed rotation for O block (no wall kicks needed)
            // This allows item blocks to rotate position even though the shape looks the same
            if (board.isValidPosition(newPos)) {
                return new RotationResult(newPos, false, 0);
            }
            return new RotationResult(null, false, -1);
        }

        // Determine which kick table to use
        int[][][] kickTable = (current.getType() == Tetromino.I) ? I_KICKS : JLSTZ_KICKS;

        // Get kick index based on the transition
        int kickIndex;
        if (clockwise) {
            kickIndex = oldRotation; // 0->1, 1->2, 2->3, 3->0
        } else {
            // For counterclockwise, we need the reverse transition
            kickIndex = Math.floorMod(oldRotation - 1, 4); // 0->3, 1->0, 2->1, 3->2
        }

        // Try each kick offset
        for (int kickIdx = 0; kickIdx < kickTable[kickIndex].length; kickIdx++) {
            int[] kick = kickTable[kickIndex][kickIdx];
            TetrominoPosition testPos = newPos.copy();
            int xOffset = clockwise ? kick[0] : -kick[0];
            int yOffset = clockwise ? kick[1] : -kick[1];

            testPos.setX(newPos.getX() + xOffset);
            testPos.setY(newPos.getY() + yOffset);

            if (board.isValidPosition(testPos)) {
                // Check for T-spin
                boolean isTSpin = false;
                if (current.getType() == Tetromino.T && kickIdx > 0) {
                    isTSpin = isTSpinRotation(testPos, board);
                }
                return new RotationResult(testPos, isTSpin, kickIdx);
            }
        }

        return new RotationResult(null, false, -1); // Rotation failed
    }

    public static TetrominoPosition attemptRotation(TetrominoPosition current, GameBoard board, boolean clockwise) {
        RotationResult result = attemptRotationWithTSpinCheck(current, board, clockwise);
        return result.getPosition();
    }

    public static boolean isTSpinRotation(TetrominoPosition tPiece, GameBoard board) {
        if (tPiece.getType() != Tetromino.T) {
            return false;
        }

        // T-spin detection: check the 4 corners around the T piece center
        int centerX = tPiece.getX() + 1; // T piece center is at (1,1) in its 4x4 grid
        int centerY = tPiece.getY() + 1;

        // Check corners by examining board cells directly
        boolean[] corners = new boolean[4];
        corners[0] = board.getCellColor(centerY - 1, centerX - 1) != 0; // top-left
        corners[1] = board.getCellColor(centerY - 1, centerX + 1) != 0; // top-right
        corners[2] = board.getCellColor(centerY + 1, centerX - 1) != 0; // bottom-left
        corners[3] = board.getCellColor(centerY + 1, centerX + 1) != 0; // bottom-right

        // Count filled corners
        int filledCorners = 0;
        for (boolean corner : corners) {
            if (corner) filledCorners++;
        }

        // T-spin requires at least 3 corners to be filled
        if (filledCorners < 3) {
            return false;
        }

        // Check if the two corners facing the T's front are filled
        int rotation = tPiece.getRotation();
        boolean frontCornersBlocked = false;

        switch (rotation) {
            case 0: // T pointing up
                frontCornersBlocked = corners[0] && corners[1]; // top corners
                break;
            case 1: // T pointing right
                frontCornersBlocked = corners[1] && corners[3]; // right corners
                break;
            case 2: // T pointing down
                frontCornersBlocked = corners[2] && corners[3]; // bottom corners
                break;
            case 3: // T pointing left
                frontCornersBlocked = corners[0] && corners[2]; // left corners
                break;
        }

        return frontCornersBlocked;
    }

    public static TetrominoPosition moveLeft(TetrominoPosition current, GameBoard board) {
        TetrominoPosition newPos = current.copy();
        newPos.setX(current.getX() - 1);
        return board.isValidPosition(newPos) ? newPos : null;
    }

    public static TetrominoPosition moveRight(TetrominoPosition current, GameBoard board) {
        TetrominoPosition newPos = current.copy();
        newPos.setX(current.getX() + 1);
        return board.isValidPosition(newPos) ? newPos : null;
    }

    public static TetrominoPosition moveDown(TetrominoPosition current, GameBoard board) {
        TetrominoPosition newPos = current.copy();
        newPos.setY(current.getY() + 1);
        return board.isValidPosition(newPos) ? newPos : null;
    }

    public static TetrominoPosition hardDrop(TetrominoPosition current, GameBoard board) {
        TetrominoPosition dropPos = current.copy();
        TetrominoPosition testPos;

        do {
            testPos = moveDown(dropPos, board);
            if (testPos != null) {
                dropPos = testPos;
            }
        } while (testPos != null);

        return dropPos;
    }
}
