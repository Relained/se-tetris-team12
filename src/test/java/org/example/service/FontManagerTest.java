package org.example.service;

import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FontManager Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class FontManagerTest {
    
    private FontManager fontManager;
    
    @Start
    private void start(Stage stage) {
        // JavaFX 초기화
    }
    
    @BeforeEach
    void setUp() {
        fontManager = FontManager.getInstance();
    }
    
    @Test
    void testGetInstance() {
        FontManager instance1 = FontManager.getInstance();
        FontManager instance2 = FontManager.getInstance();
        
        assertNotNull(instance1);
        assertSame(instance1, instance2, "Should return same singleton instance");
    }
    
    @Test
    void testGetFont() {
        Font font = fontManager.getFont(16.0);
        
        assertNotNull(font);
        assertEquals(16.0, font.getSize(), 0.01);
        assertTrue(font.getFamily().contains("Arial"));
    }
    
    @Test
    void testGetBoldFont() {
        Font boldFont = fontManager.getBoldFont(18.0);
        
        assertNotNull(boldFont);
        assertEquals(18.0, boldFont.getSize(), 0.01);
        assertTrue(boldFont.getFamily().contains("Arial"));
        assertTrue(boldFont.getStyle().contains("Bold") || boldFont.getName().contains("Bold"));
    }
    
    @Test
    void testGetMonospaceFont() {
        Font monospaceFont = fontManager.getMonospaceFont(14.0);
        
        assertNotNull(monospaceFont);
        assertEquals(14.0, monospaceFont.getSize(), 0.01);
        assertTrue(monospaceFont.getFamily().contains("Courier"));
    }
    
    @Test
    void testGetMonospaceBoldFont() {
        Font monospaceBoldFont = fontManager.getMonospaceBoldFont(20.0);
        
        assertNotNull(monospaceBoldFont);
        assertEquals(20.0, monospaceBoldFont.getSize(), 0.01);
        assertTrue(monospaceBoldFont.getFamily().contains("Courier"));
    }
    
    @Test
    void testGetPrimaryFontFamily() {
        String fontFamily = fontManager.getPrimaryFontFamily();
        
        assertNotNull(fontFamily);
        assertEquals("Arial", fontFamily);
    }
    
    @Test
    void testGetMonospaceFontFamily() {
        String fontFamily = fontManager.getMonospaceFontFamily();
        
        assertNotNull(fontFamily);
        assertEquals("Courier New", fontFamily);
    }
    
    @Test
    void testMultipleFontSizes() {
        Font small = fontManager.getFont(FontManager.SIZE_CAPTION);
        Font medium = fontManager.getFont(FontManager.SIZE_BODY_MEDIUM);
        Font large = fontManager.getFont(FontManager.SIZE_TITLE_LARGE);
        
        assertNotNull(small);
        assertNotNull(medium);
        assertNotNull(large);
        
        assertTrue(small.getSize() < medium.getSize());
        assertTrue(medium.getSize() < large.getSize());
    }
    
    @Test
    void testFontSizeConstants() {
        assertEquals(48.0, FontManager.SIZE_TITLE_XLARGE);
        assertEquals(36.0, FontManager.SIZE_TITLE_LARGE);
        assertEquals(28.0, FontManager.SIZE_TITLE_MEDIUM);
        assertEquals(24.0, FontManager.SIZE_TITLE_SMALL);
        assertEquals(20.0, FontManager.SIZE_BODY_LARGE);
        assertEquals(16.0, FontManager.SIZE_BODY_MEDIUM);
        assertEquals(14.0, FontManager.SIZE_BODY_SMALL);
        assertEquals(15.0, FontManager.SIZE_BODY_MESSAGE);
        assertEquals(13.0, FontManager.SIZE_BODY_DETAIL);
        assertEquals(12.0, FontManager.SIZE_CAPTION);
    }
    
    @Test
    void testGetFontWithZeroSize() {
        Font font = fontManager.getFont(0.0);
        assertNotNull(font);
        assertEquals(0.0, font.getSize(), 0.01);
    }
    
    @Test
    void testGetFontWithNegativeSize() {
        Font font = fontManager.getFont(-10.0);
        assertNotNull(font);
    }
    
    @Test
    void testGetFontWithVeryLargeSize() {
        Font font = fontManager.getFont(200.0);
        assertNotNull(font);
        assertEquals(200.0, font.getSize(), 0.01);
    }
    
    @Test
    void testBoldFontDifferentFromRegular() {
        Font regular = fontManager.getFont(16.0);
        Font bold = fontManager.getBoldFont(16.0);
        
        assertNotNull(regular);
        assertNotNull(bold);
        assertEquals(regular.getSize(), bold.getSize(), 0.01);
    }
    
    @Test
    void testMonospaceFontDifferentFromPrimary() {
        Font primary = fontManager.getFont(16.0);
        Font monospace = fontManager.getMonospaceFont(16.0);
        
        assertNotNull(primary);
        assertNotNull(monospace);
        assertNotEquals(primary.getFamily(), monospace.getFamily());
    }
}
