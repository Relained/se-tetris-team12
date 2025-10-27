package org.example.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ColorSetTest {

    @Test
    @DisplayName("DEFAULT 색상 세트는 7개의 색상을 가짐")
    void testDefaultColorSetSize() {
        assertEquals(7, ColorSet.DEFAULT.length);
    }

    @Test
    @DisplayName("PROTANOPIA 색상 세트는 7개의 색상을 가짐")
    void testProtanopiaColorSetSize() {
        assertEquals(7, ColorSet.PROTANOPIA.length);
    }

    @Test
    @DisplayName("DEUTERANOPIA 색상 세트는 7개의 색상을 가짐")
    void testDeuteranopiaColorSetSize() {
        assertEquals(7, ColorSet.DEUTERANOPIA.length);
    }

    @Test
    @DisplayName("TRITANOPIA 색상 세트는 7개의 색상을 가짐")
    void testTritanopiaColorSetSize() {
        assertEquals(7, ColorSet.TRITANOPIA.length);
    }

    @Test
    @DisplayName("DEFAULT 색상 세트의 모든 색상이 null이 아님")
    void testDefaultColorSetNotNull() {
        for (Color color : ColorSet.DEFAULT) {
            assertNotNull(color);
        }
    }

    @Test
    @DisplayName("PROTANOPIA 색상 세트의 모든 색상이 null이 아님")
    void testProtanopiaColorSetNotNull() {
        for (Color color : ColorSet.PROTANOPIA) {
            assertNotNull(color);
        }
    }

    @Test
    @DisplayName("DEUTERANOPIA 색상 세트의 모든 색상이 null이 아님")
    void testDeuteranopiaColorSetNotNull() {
        for (Color color : ColorSet.DEUTERANOPIA) {
            assertNotNull(color);
        }
    }

    @Test
    @DisplayName("TRITANOPIA 색상 세트의 모든 색상이 null이 아님")
    void testTritanopiaColorSetNotNull() {
        for (Color color : ColorSet.TRITANOPIA) {
            assertNotNull(color);
        }
    }

    @Test
    @DisplayName("DEFAULT 색상 세트 - 첫 번째 색상은 CYAN")
    void testDefaultFirstColor() {
        assertEquals(Color.CYAN, ColorSet.DEFAULT[0]);
    }

    @Test
    @DisplayName("DEFAULT 색상 세트 - 두 번째 색상은 YELLOW")
    void testDefaultSecondColor() {
        assertEquals(Color.YELLOW, ColorSet.DEFAULT[1]);
    }

    @Test
    @DisplayName("DEFAULT 색상 세트 - 세 번째 색상은 PURPLE")
    void testDefaultThirdColor() {
        assertEquals(Color.PURPLE, ColorSet.DEFAULT[2]);
    }

    @Test
    @DisplayName("DEFAULT 색상 세트 - 네 번째 색상은 LIME")
    void testDefaultFourthColor() {
        assertEquals(Color.LIME, ColorSet.DEFAULT[3]);
    }

    @Test
    @DisplayName("DEFAULT 색상 세트 - 다섯 번째 색상은 RED")
    void testDefaultFifthColor() {
        assertEquals(Color.RED, ColorSet.DEFAULT[4]);
    }

    @Test
    @DisplayName("DEFAULT 색상 세트 - 여섯 번째 색상은 BLUE")
    void testDefaultSixthColor() {
        assertEquals(Color.BLUE, ColorSet.DEFAULT[5]);
    }

    @Test
    @DisplayName("DEFAULT 색상 세트 - 일곱 번째 색상은 ORANGE")
    void testDefaultSeventhColor() {
        assertEquals(Color.ORANGE, ColorSet.DEFAULT[6]);
    }

    @Test
    @DisplayName("TRITANOPIA 색상 세트 - 두 번째 색상은 WHITE")
    void testTritanopiaSecondColor() {
        assertEquals(Color.WHITE, ColorSet.TRITANOPIA[1]);
    }

    @Test
    @DisplayName("모든 색상 세트가 동일한 크기를 가짐")
    void testAllColorSetsHaveSameSize() {
        int expectedSize = 7;
        assertEquals(expectedSize, ColorSet.DEFAULT.length);
        assertEquals(expectedSize, ColorSet.PROTANOPIA.length);
        assertEquals(expectedSize, ColorSet.DEUTERANOPIA.length);
        assertEquals(expectedSize, ColorSet.TRITANOPIA.length);
    }
}
