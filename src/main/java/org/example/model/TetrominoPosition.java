package org.example.model;

public class TetrominoPosition {
    private int x, y;
    private int rotation;
    private Tetromino type;

    public TetrominoPosition(Tetromino type, int x, int y, int rotation) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getRotation() { return rotation; }
    public Tetromino getType() { return type; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setRotation(int rotation) { 
        this.rotation = Math.floorMod(rotation, 4);
    }

    public int[][] getCurrentShape() {
        return type.getShape(rotation);
    }

    public TetrominoPosition copy() {
        return new TetrominoPosition(type, x, y, rotation);
    }
}