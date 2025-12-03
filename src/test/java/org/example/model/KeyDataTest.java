package org.example.model;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KeyData 클래스의 Unit Test
 */
class KeyDataTest {
    
    private KeyData keyData;
    
    @BeforeEach
    void setUp() {
        keyData = new KeyData();
    }
    
    @Test
    void testDefaultSinglePlayerKeys() {
        // 싱글 플레이 기본 키 설정 확인
        assertEquals(KeyCode.LEFT, keyData.moveLeft);
        assertEquals(KeyCode.RIGHT, keyData.moveRight);
        assertEquals(KeyCode.DOWN, keyData.softDrop);
        assertEquals(KeyCode.SPACE, keyData.hardDrop);
        assertEquals(KeyCode.Z, keyData.rotateCounterClockwise);
        assertEquals(KeyCode.UP, keyData.rotateClockwise);
        assertEquals(KeyCode.C, keyData.hold);
    }
    
    @Test
    void testDefaultMultiPlayer1Keys() {
        // 멀티플레이 Player 1 기본 키 설정 확인 (화살표 키 기반)
        assertEquals(KeyCode.LEFT, keyData.multi1MoveLeft);
        assertEquals(KeyCode.RIGHT, keyData.multi1MoveRight);
        assertEquals(KeyCode.DOWN, keyData.multi1SoftDrop);
        assertEquals(KeyCode.ENTER, keyData.multi1HardDrop);
        assertEquals(KeyCode.QUOTE, keyData.multi1RotateCounterClockwise);
        assertEquals(KeyCode.UP, keyData.multi1RotateClockwise);
        assertEquals(KeyCode.SHIFT, keyData.multi1Hold);
    }
    
    @Test
    void testDefaultMultiPlayer2Keys() {
        // 멀티플레이 Player 2 기본 키 설정 확인 (WASD 기반)
        assertEquals(KeyCode.A, keyData.multi2MoveLeft);
        assertEquals(KeyCode.D, keyData.multi2MoveRight);
        assertEquals(KeyCode.S, keyData.multi2SoftDrop);
        assertEquals(KeyCode.SPACE, keyData.multi2HardDrop);
        assertEquals(KeyCode.Z, keyData.multi2RotateCounterClockwise);
        assertEquals(KeyCode.W, keyData.multi2RotateClockwise);
        assertEquals(KeyCode.C, keyData.multi2Hold);
    }
    
    @Test
    void testDefaultPauseKey() {
        assertEquals(KeyCode.ESCAPE, keyData.pause);
    }
    
    @Test
    void testModifySinglePlayerKeys() {
        // 싱글 플레이 키 수정 가능 확인
        keyData.moveLeft = KeyCode.A;
        keyData.moveRight = KeyCode.D;
        keyData.softDrop = KeyCode.S;
        keyData.hardDrop = KeyCode.W;
        
        assertEquals(KeyCode.A, keyData.moveLeft);
        assertEquals(KeyCode.D, keyData.moveRight);
        assertEquals(KeyCode.S, keyData.softDrop);
        assertEquals(KeyCode.W, keyData.hardDrop);
    }
    
    @Test
    void testModifyMultiPlayer1Keys() {
        // 멀티플레이 Player 1 키 수정 가능 확인
        keyData.multi1MoveLeft = KeyCode.NUMPAD4;
        keyData.multi1MoveRight = KeyCode.NUMPAD6;
        keyData.multi1SoftDrop = KeyCode.NUMPAD5;
        
        assertEquals(KeyCode.NUMPAD4, keyData.multi1MoveLeft);
        assertEquals(KeyCode.NUMPAD6, keyData.multi1MoveRight);
        assertEquals(KeyCode.NUMPAD5, keyData.multi1SoftDrop);
    }
    
    @Test
    void testModifyMultiPlayer2Keys() {
        // 멀티플레이 Player 2 키 수정 가능 확인
        keyData.multi2MoveLeft = KeyCode.J;
        keyData.multi2MoveRight = KeyCode.L;
        keyData.multi2SoftDrop = KeyCode.K;
        
        assertEquals(KeyCode.J, keyData.multi2MoveLeft);
        assertEquals(KeyCode.L, keyData.multi2MoveRight);
        assertEquals(KeyCode.K, keyData.multi2SoftDrop);
    }
    
    @Test
    void testModifyPauseKey() {
        keyData.pause = KeyCode.P;
        assertEquals(KeyCode.P, keyData.pause);
    }
    
    @Test
    void testSerializable() throws IOException, ClassNotFoundException {
        // Serializable 인터페이스 구현 확인
        keyData.moveLeft = KeyCode.A;
        keyData.multi1MoveLeft = KeyCode.J;
        keyData.pause = KeyCode.P;
        
        // 직렬화
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(keyData);
        oos.close();
        
        // 역직렬화
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        KeyData deserializedKeyData = (KeyData) ois.readObject();
        ois.close();
        
        // 값 확인
        assertEquals(KeyCode.A, deserializedKeyData.moveLeft);
        assertEquals(KeyCode.J, deserializedKeyData.multi1MoveLeft);
        assertEquals(KeyCode.P, deserializedKeyData.pause);
    }
    
    @Test
    void testAllFieldsArePublic() {
        // 모든 필드가 public이므로 직접 접근 가능한지 확인
        assertDoesNotThrow(() -> {
            keyData.moveLeft = KeyCode.A;
            keyData.moveRight = KeyCode.D;
            keyData.softDrop = KeyCode.S;
            keyData.hardDrop = KeyCode.W;
            keyData.rotateCounterClockwise = KeyCode.Q;
            keyData.rotateClockwise = KeyCode.E;
            keyData.hold = KeyCode.SHIFT;
            keyData.pause = KeyCode.ESCAPE;
            
            keyData.multi1MoveLeft = KeyCode.LEFT;
            keyData.multi1MoveRight = KeyCode.RIGHT;
            keyData.multi1SoftDrop = KeyCode.DOWN;
            keyData.multi1HardDrop = KeyCode.ENTER;
            keyData.multi1RotateCounterClockwise = KeyCode.QUOTE;
            keyData.multi1RotateClockwise = KeyCode.UP;
            keyData.multi1Hold = KeyCode.SHIFT;
            
            keyData.multi2MoveLeft = KeyCode.A;
            keyData.multi2MoveRight = KeyCode.D;
            keyData.multi2SoftDrop = KeyCode.S;
            keyData.multi2HardDrop = KeyCode.SPACE;
            keyData.multi2RotateCounterClockwise = KeyCode.Z;
            keyData.multi2RotateClockwise = KeyCode.W;
            keyData.multi2Hold = KeyCode.C;
        });
    }
    
    @Test
    void testMultipleInstancesAreIndependent() {
        // 여러 인스턴스가 독립적인지 확인
        KeyData keyData1 = new KeyData();
        KeyData keyData2 = new KeyData();
        
        keyData1.moveLeft = KeyCode.A;
        keyData2.moveLeft = KeyCode.D;
        
        assertEquals(KeyCode.A, keyData1.moveLeft);
        assertEquals(KeyCode.D, keyData2.moveLeft);
        assertNotEquals(keyData1.moveLeft, keyData2.moveLeft);
    }
}
