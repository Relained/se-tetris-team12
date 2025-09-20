package org.example;

/**
 * Super Rotation System (SRS) implementation for Tetris
 * Handles wall kicks and rotation states according to Tetris SRS standard
 */
public class SRSSystem {
    
    // Rotation states: 0 (spawn), R (right), 2 (reverse), L (left)
    public enum RotationState {
        SPAWN(0), RIGHT(1), REVERSE(2), LEFT(3);
        
        private final int value;
        
        RotationState(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        public RotationState getNext() {
            return switch (this) {
                case SPAWN -> RIGHT;
                case RIGHT -> REVERSE;
                case REVERSE -> LEFT;
                case LEFT -> SPAWN;
            };
        }
        
        public RotationState getPrevious() {
            return switch (this) {
                case SPAWN -> LEFT;
                case RIGHT -> SPAWN;
                case REVERSE -> RIGHT;
                case LEFT -> REVERSE;
            };
        }
    }
    
    // Wall kick data for JLSTZ pieces (most pieces except I and O)
    // Format: [from_state][to_state][test_number] = {x_offset, y_offset}
    private static final int[][][][] WALL_KICK_DATA_JLSTZ = {
        // From SPAWN (0)
        {
            {}, // 0->0 (no rotation)
            {{0,0}, {-1,0}, {-1,1}, {0,-2}, {-1,-2}}, // 0->R
            {}, // 0->2 (180 rotation not in standard SRS)
            {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}}     // 0->L
        },
        // From RIGHT (R)
        {
            {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}},     // R->0
            {}, // R->R (no rotation)
            {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}},     // R->2
            {} // R->L (not standard)
        },
        // From REVERSE (2)
        {
            {}, // 2->0 (not standard)
            {{0,0}, {-1,0}, {-1,1}, {0,-2}, {-1,-2}}, // 2->R
            {}, // 2->2 (no rotation)
            {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}}     // 2->L
        },
        // From LEFT (L)
        {
            {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}},  // L->0
            {}, // L->R (not standard)
            {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}},  // L->2
            {} // L->L (no rotation)
        }
    };
    
    // Wall kick data for I piece (different from other pieces)
    private static final int[][][][] WALL_KICK_DATA_I = {
        // From SPAWN (0)
        {
            {}, // 0->0 (no rotation)
            {{0,0}, {-2,0}, {1,0}, {-2,-1}, {1,2}},   // 0->R
            {}, // 0->2 (not standard)
            {{0,0}, {-1,0}, {2,0}, {-1,2}, {2,-1}}    // 0->L
        },
        // From RIGHT (R)
        {
            {{0,0}, {2,0}, {-1,0}, {2,1}, {-1,-2}},   // R->0
            {}, // R->R (no rotation)
            {{0,0}, {-1,0}, {2,0}, {-1,2}, {2,-1}},   // R->2
            {} // R->L (not standard)
        },
        // From REVERSE (2)
        {
            {}, // 2->0 (not standard)
            {{0,0}, {1,0}, {-2,0}, {1,-2}, {-2,1}},   // 2->R
            {}, // 2->2 (no rotation)
            {{0,0}, {2,0}, {-1,0}, {2,1}, {-1,-2}}    // 2->L
        },
        // From LEFT (L)
        {
            {{0,0}, {1,0}, {-2,0}, {1,-2}, {-2,1}},   // L->0
            {}, // L->R (not standard)
            {{0,0}, {-2,0}, {1,0}, {-2,-1}, {1,2}},   // L->2
            {} // L->L (no rotation)
        }
    };
    
    /**
     * Get wall kick tests for a piece type and rotation transition
     * @param pieceType The type of tetromino (1=I, 2=J, 3=L, 4=O, 5=S, 6=T, 7=Z)
     * @param fromState Current rotation state
     * @param toState Target rotation state
     * @return Array of {x, y} offset tests to try
     */
    public static int[][] getWallKickTests(int pieceType, RotationState fromState, RotationState toState) {
        // O piece doesn't need wall kicks (it's symmetrical)
        if (pieceType == 4) {
            return new int[][]{{0, 0}};
        }
        
        // I piece uses different wall kick data
        if (pieceType == 1) {
            return WALL_KICK_DATA_I[fromState.getValue()][toState.getValue()];
        }
        
        // JLSTZ pieces use standard wall kick data
        return WALL_KICK_DATA_JLSTZ[fromState.getValue()][toState.getValue()];
    }
    
    /**
     * Check if a rotation from one state to another is valid (has kick data)
     * @param fromState Current rotation state
     * @param toState Target rotation state
     * @return true if rotation is valid
     */
    public static boolean isValidRotation(RotationState fromState, RotationState toState) {
        // Only allow 90-degree rotations (clockwise and counterclockwise)
        return (fromState.getNext() == toState) || (fromState.getPrevious() == toState);
    }
}