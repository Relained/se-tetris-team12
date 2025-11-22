package org.example.model;

import javafx.scene.input.KeyCode;

import java.io.Serializable;      

public class KeyData implements Serializable {
    // 싱글 플레이 키 설정
    public KeyCode moveLeft = KeyCode.LEFT;
    public KeyCode moveRight = KeyCode.RIGHT;
    public KeyCode softDrop = KeyCode.DOWN;
    public KeyCode hardDrop = KeyCode.SPACE;
    public KeyCode rotateCounterClockwise = KeyCode.Z;
    public KeyCode rotateClockwise = KeyCode.UP;
    public KeyCode hold = KeyCode.C;
    
    // 멀티플레이 Player 1 키 설정 (화살표 키 기반)
    public KeyCode multi1MoveLeft = KeyCode.LEFT;
    public KeyCode multi1MoveRight = KeyCode.RIGHT;
    public KeyCode multi1SoftDrop = KeyCode.DOWN;
    public KeyCode multi1HardDrop = KeyCode.ENTER;
    public KeyCode multi1RotateCounterClockwise = KeyCode.QUOTE; // ' 키
    public KeyCode multi1RotateClockwise = KeyCode.UP;
    public KeyCode multi1Hold = KeyCode.SHIFT; // Right Shift
    
    // 멀티플레이 Player 2 키 설정 (WASD 기반)
    public KeyCode multi2MoveLeft = KeyCode.A;
    public KeyCode multi2MoveRight = KeyCode.D;
    public KeyCode multi2SoftDrop = KeyCode.S;
    public KeyCode multi2HardDrop = KeyCode.SPACE;
    public KeyCode multi2RotateCounterClockwise = KeyCode.Z;
    public KeyCode multi2RotateClockwise = KeyCode.UP;
    public KeyCode multi2Hold = KeyCode.C;

    public KeyCode pause = KeyCode.ESCAPE;
}