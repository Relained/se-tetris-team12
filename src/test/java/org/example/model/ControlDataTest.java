package org.example.model;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ControlDataTest {

    @Test
    @DisplayName("ControlData 기본 키 매핑 테스트")
    void testDefaultKeyMapping() {
        ControlData controls = new ControlData();
        
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
        ControlData controls = new ControlData();
        controls.moveLeft = KeyCode.A;
        
        assertEquals(KeyCode.A, controls.moveLeft);
    }

    @Test
    @DisplayName("moveRight 키 변경 테스트")
    void testSetMoveRight() {
        ControlData controls = new ControlData();
        controls.moveRight = KeyCode.D;
        
        assertEquals(KeyCode.D, controls.moveRight);
    }

    @Test
    @DisplayName("softDrop 키 변경 테스트")
    void testSetSoftDrop() {
        ControlData controls = new ControlData();
        controls.softDrop = KeyCode.S;
        
        assertEquals(KeyCode.S, controls.softDrop);
    }

    @Test
    @DisplayName("hardDrop 키 변경 테스트")
    void testSetHardDrop() {
        ControlData controls = new ControlData();
        controls.hardDrop = KeyCode.W;
        
        assertEquals(KeyCode.W, controls.hardDrop);
    }

    @Test
    @DisplayName("rotateCounterClockwise 키 변경 테스트")
    void testSetRotateCounterClockwise() {
        ControlData controls = new ControlData();
        controls.rotateCounterClockwise = KeyCode.Q;
        
        assertEquals(KeyCode.Q, controls.rotateCounterClockwise);
    }

    @Test
    @DisplayName("rotateClockwise 키 변경 테스트")
    void testSetRotateClockwise() {
        ControlData controls = new ControlData();
        controls.rotateClockwise = KeyCode.E;
        
        assertEquals(KeyCode.E, controls.rotateClockwise);
    }

    @Test
    @DisplayName("hold 키 변경 테스트")
    void testSetHold() {
        ControlData controls = new ControlData();
        controls.hold = KeyCode.SHIFT;
        
        assertEquals(KeyCode.SHIFT, controls.hold);
    }

    @Test
    @DisplayName("pause 키 변경 테스트")
    void testSetPause() {
        ControlData controls = new ControlData();
        controls.pause = KeyCode.P;
        
        assertEquals(KeyCode.P, controls.pause);
    }

    @Test
    @DisplayName("Serializable 인터페이스 구현 확인")
    void testSerializable() {
        ControlData controls = new ControlData();
        assertTrue(controls instanceof java.io.Serializable);
    }

    @Test
    @DisplayName("모든 키가 null이 아님")
    void testAllKeysNotNull() {
        ControlData controls = new ControlData();
        
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
