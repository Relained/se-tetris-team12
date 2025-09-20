package org.example;

import java.util.Random;

public class Tetromino {
    private int x, y;
    private int type;
    private SRSSystem.RotationState rotationState;
    private static final int[][][][] SHAPES = {
        // I-piece
        {
            {{1, 1, 1, 1}},
            {{1}, {1}, {1}, {1}},
            {{1, 1, 1, 1}},
            {{1}, {1}, {1}, {1}}
        },
        // J-piece
        {
            {{1, 0, 0}, {1, 1, 1}},
            {{1, 1}, {1, 0}, {1, 0}},
            {{1, 1, 1}, {0, 0, 1}},
            {{0, 1}, {0, 1}, {1, 1}}
        },
        // L-piece
        {
            {{0, 0, 1}, {1, 1, 1}},
            {{1, 0}, {1, 0}, {1, 1}},
            {{1, 1, 1}, {1, 0, 0}},
            {{1, 1}, {0, 1}, {0, 1}}
        },
        // O-piece
        {
            {{1, 1}, {1, 1}},
            {{1, 1}, {1, 1}},
            {{1, 1}, {1, 1}},
            {{1, 1}, {1, 1}}
        },
        // S-piece
        {
            {{0, 1, 1}, {1, 1, 0}},
            {{1, 0}, {1, 1}, {0, 1}},
            {{0, 1, 1}, {1, 1, 0}},
            {{1, 0}, {1, 1}, {0, 1}}
        },
        // T-piece
        {
            {{0, 1, 0}, {1, 1, 1}},
            {{1, 0}, {1, 1}, {1, 0}},
            {{1, 1, 1}, {0, 1, 0}},
            {{0, 1}, {1, 1}, {0, 1}}
        },
        // Z-piece
        {
            {{1, 1, 0}, {0, 1, 1}},
            {{0, 1}, {1, 1}, {1, 0}},
            {{1, 1, 0}, {0, 1, 1}},
            {{0, 1}, {1, 1}, {1, 0}}
        }
    };

    public Tetromino(int type) {
        this.type = type;
        this.rotationState = SRSSystem.RotationState.SPAWN;
        this.x = 0;
        this.y = 0;
    }

    public static Tetromino getRandomPiece(Random random) {
        int type = random.nextInt(7) + 1; // Types 1-7
        return new Tetromino(type);
    }

    public int[][] getShape() {
        return SHAPES[type - 1][rotationState.getValue()];
    }

    public int[][] getRotatedShape(boolean clockwise) {
        SRSSystem.RotationState targetState = clockwise ? 
            rotationState.getNext() : rotationState.getPrevious();
        return SHAPES[type - 1][targetState.getValue()];
    }

    public void rotate(boolean clockwise) {
        rotationState = clockwise ? rotationState.getNext() : rotationState.getPrevious();
    }

    // Backwards compatibility - default to clockwise rotation
    public int[][] getRotatedShape() {
        return getRotatedShape(true);
    }

    public void rotate() {
        rotate(true);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getType() {
        return type;
    }

    public SRSSystem.RotationState getRotationState() {
        return rotationState;
    }

    public void setRotationState(SRSSystem.RotationState rotationState) {
        this.rotationState = rotationState;
    }

    // Backwards compatibility
    public int getRotation() {
        return rotationState.getValue();
    }
}