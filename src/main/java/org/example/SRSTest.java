package org.example;

/**
 * Simple test class to verify SRS implementation
 */
public class SRSTest {
    public static void main(String[] args) {
        System.out.println("=== SRS (Super Rotation System) Implementation Test ===");
        
        // Test rotation states
        testRotationStates();
        
        // Test wall kick data
        testWallKickData();
        
        // Test tetromino SRS integration
        testTetrominoSRS();
        
        System.out.println("All tests completed successfully!");
        System.out.println("SRS implementation is ready for use.");
    }
    
    private static void testRotationStates() {
        System.out.println("\n1. Testing Rotation States:");
        
        SRSSystem.RotationState spawn = SRSSystem.RotationState.SPAWN;
        SRSSystem.RotationState right = spawn.getNext();
        SRSSystem.RotationState reverse = right.getNext();
        SRSSystem.RotationState left = reverse.getNext();
        SRSSystem.RotationState backToSpawn = left.getNext();
        
        assert right == SRSSystem.RotationState.RIGHT : "Spawn->Right failed";
        assert reverse == SRSSystem.RotationState.REVERSE : "Right->Reverse failed";
        assert left == SRSSystem.RotationState.LEFT : "Reverse->Left failed";
        assert backToSpawn == SRSSystem.RotationState.SPAWN : "Left->Spawn failed";
        
        System.out.println("   ✓ Clockwise rotation cycle works correctly");
        
        // Test counterclockwise
        SRSSystem.RotationState leftFromSpawn = spawn.getPrevious();
        assert leftFromSpawn == SRSSystem.RotationState.LEFT : "Spawn->Left (CCW) failed";
        
        System.out.println("   ✓ Counterclockwise rotation works correctly");
    }
    
    private static void testWallKickData() {
        System.out.println("\n2. Testing Wall Kick Data:");
        
        // Test JLSTZ pieces (J piece = type 2)
        int[][] kicksJLSTZ = SRSSystem.getWallKickTests(
            2, // J piece
            SRSSystem.RotationState.SPAWN,
            SRSSystem.RotationState.RIGHT
        );
        
        assert kicksJLSTZ.length == 5 : "JLSTZ should have 5 kick tests";
        assert kicksJLSTZ[0][0] == 0 && kicksJLSTZ[0][1] == 0 : "First kick test should be (0,0)";
        
        System.out.println("   ✓ JLSTZ wall kick data is correct");
        
        // Test I piece (type 1)
        int[][] kicksI = SRSSystem.getWallKickTests(
            1, // I piece
            SRSSystem.RotationState.SPAWN,
            SRSSystem.RotationState.RIGHT
        );
        
        assert kicksI.length == 5 : "I piece should have 5 kick tests";
        assert kicksI[0][0] == 0 && kicksI[0][1] == 0 : "First kick test should be (0,0)";
        
        System.out.println("   ✓ I piece wall kick data is correct");
        
        // Test O piece (type 4)
        int[][] kicksO = SRSSystem.getWallKickTests(
            4, // O piece
            SRSSystem.RotationState.SPAWN,
            SRSSystem.RotationState.RIGHT
        );
        
        assert kicksO.length == 1 : "O piece should have only 1 kick test";
        assert kicksO[0][0] == 0 && kicksO[0][1] == 0 : "O piece kick test should be (0,0)";
        
        System.out.println("   ✓ O piece wall kick data is correct");
    }
    
    private static void testTetrominoSRS() {
        System.out.println("\n3. Testing Tetromino SRS Integration:");
        
        // Test T piece (type 6)
        Tetromino tPiece = new Tetromino(6);
        
        assert tPiece.getRotationState() == SRSSystem.RotationState.SPAWN : "Initial state should be SPAWN";
        System.out.println("   ✓ Initial rotation state is correct");
        
        // Test clockwise rotation
        tPiece.rotate(true);
        assert tPiece.getRotationState() == SRSSystem.RotationState.RIGHT : "After CW rotation should be RIGHT";
        System.out.println("   ✓ Clockwise rotation state update works");
        
        // Test counterclockwise rotation
        tPiece.rotate(false);
        assert tPiece.getRotationState() == SRSSystem.RotationState.SPAWN : "After CCW rotation should be back to SPAWN";
        System.out.println("   ✓ Counterclockwise rotation state update works");
        
        // Test shape access
        int[][] shape = tPiece.getShape();
        assert shape != null : "Shape should not be null";
        assert shape.length > 0 : "Shape should have rows";
        System.out.println("   ✓ Shape access works correctly");
    }
}