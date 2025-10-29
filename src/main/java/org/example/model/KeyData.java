package org.example.model;

import javafx.scene.input.KeyCode;

import java.io.Serializable;      

public class KeyData implements Serializable {
    public KeyCode moveLeft = KeyCode.LEFT;
    public KeyCode moveRight = KeyCode.RIGHT;
    public KeyCode softDrop = KeyCode.DOWN;
    public KeyCode hardDrop = KeyCode.SPACE;
    public KeyCode rotateCounterClockwise = KeyCode.Z;
    public KeyCode rotateClockwise = KeyCode.UP;
    public KeyCode hold = KeyCode.C;
    public KeyCode pause = KeyCode.ESCAPE;
}