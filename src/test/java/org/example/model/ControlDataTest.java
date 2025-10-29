package org.example.model;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ControlDataTest {

    @Test
    @DisplayName("ControlData 기본 키 매핑 테스트")
    void testDefaultKeyMapping() {
        KeyData controls = new KeyData();
        
        assertEquals(KeyCode.LEFT, controls.moveLeft);
        assertEquals(KeyCode.RIGHT, controls.moveRight);
        assertEquals(KeyCode.DOWN, controls.softDrop);
        assertEquals(KeyCode.SPACE, controls.hardDrop);
        assertEquals(KeyCode.Z, controls.rotateCounterClockwise);
        assertEquals(KeyCode.UP, controls.rotateClockwise);
        assertEquals(KeyCode.C, controls.hold);
        assertEquals(KeyCode.ESCAPE, controls.pause);
    }

    @Test
    @DisplayName("moveLeft 키 변경 테스트")
    void testSetMoveLeft() {
        KeyData controls = new KeyData();
        controls.moveLeft = KeyCode.A;
        
        assertEquals(KeyCode.A, controls.moveLeft);
    }

    @Test
    @DisplayName("moveRight 키 변경 테스트")
    void testSetMoveRight() {
        KeyData controls = new KeyData();
        controls.moveRight = KeyCode.D;
        
        assertEquals(KeyCode.D, controls.moveRight);
    }

    @Test
    @DisplayName("softDrop 키 변경 테스트")
    void testSetSoftDrop() {
        KeyData controls = new KeyData();
        controls.softDrop = KeyCode.S;
        
        assertEquals(KeyCode.S, controls.softDrop);
    }

    @Test
    @DisplayName("hardDrop 키 변경 테스트")
    void testSetHardDrop() {
        KeyData controls = new KeyData();
        controls.hardDrop = KeyCode.W;
        
        assertEquals(KeyCode.W, controls.hardDrop);
    }

    @Test
    @DisplayName("rotateCounterClockwise 키 변경 테스트")
    void testSetRotateCounterClockwise() {
        KeyData controls = new KeyData();
        controls.rotateCounterClockwise = KeyCode.Q;
        
        assertEquals(KeyCode.Q, controls.rotateCounterClockwise);
    }

    @Test
    @DisplayName("rotateClockwise 키 변경 테스트")
    void testSetRotateClockwise() {
        KeyData controls = new KeyData();
        controls.rotateClockwise = KeyCode.E;
        
        assertEquals(KeyCode.E, controls.rotateClockwise);
    }

    @Test
    @DisplayName("hold 키 변경 테스트")
    void testSetHold() {
        KeyData controls = new KeyData();
        controls.hold = KeyCode.SHIFT;
        
        assertEquals(KeyCode.SHIFT, controls.hold);
    }

    @Test
    @DisplayName("pause 키 변경 테스트")
    void testSetPause() {
        KeyData controls = new KeyData();
        controls.pause = KeyCode.P;
        
        assertEquals(KeyCode.P, controls.pause);
    }

    @Test
    @DisplayName("Serializable 인터페이스 구현 확인")
    void testSerializable() {
        KeyData controls = new KeyData();
        assertTrue(controls instanceof java.io.Serializable);
    }

    @Test
    @DisplayName("모든 키가 null이 아님")
    void testAllKeysNotNull() {
        KeyData controls = new KeyData();
        
        assertNotNull(controls.moveLeft);
        assertNotNull(controls.moveRight);
        assertNotNull(controls.softDrop);
        assertNotNull(controls.hardDrop);
        assertNotNull(controls.rotateCounterClockwise);
        assertNotNull(controls.rotateClockwise);
        assertNotNull(controls.hold);
        assertNotNull(controls.pause);
    }
}
