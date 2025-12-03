package org.example.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ColorSet Unit Test
 */
class ColorSetTest {
    
    @Test
    void testDefaultColorSetExists() {
        assertNotNull(ColorSet.DEFAULT);
        assertEquals(7, ColorSet.DEFAULT.length, "Default color set should have 7 colors");
    }
    
    @Test
    void testProtanopiaColorSetExists() {
        assertNotNull(ColorSet.PROTANOPIA);
        assertEquals(7, ColorSet.PROTANOPIA.length, "Protanopia color set should have 7 colors");
    }
    
    @Test
    void testDeuteranopiaColorSetExists() {
        assertNotNull(ColorSet.DEUTERANOPIA);
        assertEquals(7, ColorSet.DEUTERANOPIA.length, "Deuteranopia color set should have 7 colors");
    }
    
    @Test
    void testTritanopiaColorSetExists() {
        assertNotNull(ColorSet.TRITANOPIA);
        assertEquals(7, ColorSet.TRITANOPIA.length, "Tritanopia color set should have 7 colors");
    }
    
    @Test
    void testDefaultColorSetColors() {
        assertEquals(Color.CYAN, ColorSet.DEFAULT[0], "I piece should be Cyan");
        assertEquals(Color.YELLOW, ColorSet.DEFAULT[1], "O piece should be Yellow");
        assertEquals(Color.PURPLE, ColorSet.DEFAULT[2], "T piece should be Purple");
        assertEquals(Color.LIME, ColorSet.DEFAULT[3], "S piece should be Lime");
        assertEquals(Color.RED, ColorSet.DEFAULT[4], "Z piece should be Red");
        assertEquals(Color.BLUE, ColorSet.DEFAULT[5], "J piece should be Blue");
        assertEquals(Color.ORANGE, ColorSet.DEFAULT[6], "L piece should be Orange");
    }
    
    @Test
    void testAllColorSetsHaveSameLength() {
        assertEquals(ColorSet.DEFAULT.length, ColorSet.PROTANOPIA.length);
        assertEquals(ColorSet.DEFAULT.length, ColorSet.DEUTERANOPIA.length);
        assertEquals(ColorSet.DEFAULT.length, ColorSet.TRITANOPIA.length);
    }
    
    @Test
    void testProtanopiaAndDeuteranopiaAreSame() {
        // Protanopia and Deuteranopia use the same color palette
        for (int i = 0; i < ColorSet.PROTANOPIA.length; i++) {
            assertEquals(ColorSet.PROTANOPIA[i], ColorSet.DEUTERANOPIA[i],
                "Protanopia and Deuteranopia should have same color at index " + i);
        }
    }
    
    @Test
    void testColorSetsAreDifferent() {
        // Default and Protanopia should be different
        boolean foundDifference = false;
        for (int i = 0; i < ColorSet.DEFAULT.length; i++) {
            if (!ColorSet.DEFAULT[i].equals(ColorSet.PROTANOPIA[i])) {
                foundDifference = true;
                break;
            }
        }
        assertTrue(foundDifference, "Default and Protanopia color sets should be different");
    }
    
    @Test
    void testWebColorFormat() {
        // Test that web colors are valid
        Color testColor = ColorSet.PROTANOPIA[0];
        assertNotNull(testColor);
        assertTrue(testColor.getOpacity() >= 0 && testColor.getOpacity() <= 1);
    }
}
