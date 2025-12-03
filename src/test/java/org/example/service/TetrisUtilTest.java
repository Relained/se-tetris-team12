package org.example.service;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TetrisUtil Unit Test
 */
class TetrisUtilTest {
    
    @Test
    void testGetAppDataPathNotNull() {
        String path = TetrisUtil.getAppDataPath();
        assertNotNull(path);
    }
    
    @Test
    void testGetAppDataPathEndsWithSeparator() {
        String path = TetrisUtil.getAppDataPath();
        assertTrue(path.endsWith(File.separator));
    }
    
    @Test
    void testGetAppDataPathContainsSE12Tetris() {
        String path = TetrisUtil.getAppDataPath();
        assertTrue(path.contains("SE12Tetris"));
    }
    
    @Test
    void testGetAppDataPathCreatesDirectory() {
        String path = TetrisUtil.getAppDataPath();
        String dirPath = path.substring(0, path.length() - 1); // Remove trailing separator
        File dir = new File(dirPath);
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
    }
    
    @Test
    void testGetAppDataPathConsistency() {
        String path1 = TetrisUtil.getAppDataPath();
        String path2 = TetrisUtil.getAppDataPath();
        assertEquals(path1, path2);
    }
    
    @Test
    void testGetAppDataPathIsAbsolute() {
        String path = TetrisUtil.getAppDataPath();
        File file = new File(path);
        assertTrue(file.isAbsolute());
    }
    
    @Test
    void testGetAppDataPathBasedOnOS() {
        String path = TetrisUtil.getAppDataPath();
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            assertTrue(path.contains("AppData") || path.contains("Roaming"));
        } else if (os.contains("mac")) {
            assertTrue(path.contains("Library"));
        } else {
            // Linux
            assertTrue(path.contains(".config") || path.contains(".local"));
        }
    }
}
