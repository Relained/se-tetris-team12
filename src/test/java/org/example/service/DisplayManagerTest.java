package org.example.service;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class DisplayManagerTest {
    
    private DisplayManager displayManager;
    
    @BeforeAll
    static void initJavaFX() {
        // JavaFX 초기화
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @BeforeEach
    void setUp() {
        displayManager = DisplayManager.getInstance();
    }
    
    @Test
    @DisplayName("싱글톤 인스턴스 확인")
    void testSingleton() {
        DisplayManager instance1 = DisplayManager.getInstance();
        DisplayManager instance2 = DisplayManager.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    @DisplayName("기본 화면 크기는 MEDIUM")
    void testDefaultSize() {
        assertEquals(ScreenSize.MEDIUM, displayManager.getCurrentSize());
    }
    
    @Test
    @DisplayName("화면 크기 변경 - SMALL")
    void testSetDisplayModeSmall() {
        displayManager.setDisplayMode(ScreenSize.SMALL);
        
        assertEquals(ScreenSize.SMALL, displayManager.getCurrentSize());
    }
    
    @Test
    @DisplayName("화면 크기 변경 - MEDIUM")
    void testSetDisplayModeMedium() {
        displayManager.setDisplayMode(ScreenSize.MEDIUM);
        
        assertEquals(ScreenSize.MEDIUM, displayManager.getCurrentSize());
    }
    
    @Test
    @DisplayName("화면 크기 변경 - LARGE")
    void testSetDisplayModeLarge() {
        displayManager.setDisplayMode(ScreenSize.LARGE);
        
        assertEquals(ScreenSize.LARGE, displayManager.getCurrentSize());
    }
    
    @Test
    @DisplayName("SMALL 크기의 너비 확인")
    void testGetWidthSmall() {
        assertEquals(500, displayManager.getWidth(ScreenSize.SMALL));
    }
    
    @Test
    @DisplayName("MEDIUM 크기의 너비 확인")
    void testGetWidthMedium() {
        assertEquals(700, displayManager.getWidth(ScreenSize.MEDIUM));
    }
    
    @Test
    @DisplayName("LARGE 크기의 너비 확인")
    void testGetWidthLarge() {
        assertEquals(900, displayManager.getWidth(ScreenSize.LARGE));
    }
    
    @Test
    @DisplayName("SMALL 크기의 높이 확인")
    void testGetHeightSmall() {
        assertEquals(800, displayManager.getHeight(ScreenSize.SMALL));
    }
    
    @Test
    @DisplayName("MEDIUM 크기의 높이 확인")
    void testGetHeightMedium() {
        assertEquals(900, displayManager.getHeight(ScreenSize.MEDIUM));
    }
    
    @Test
    @DisplayName("LARGE 크기의 높이 확인")
    void testGetHeightLarge() {
        assertEquals(1000, displayManager.getHeight(ScreenSize.LARGE));
    }
    
    @Test
    @DisplayName("모든 화면 크기 순회 테스트")
    void testAllScreenSizes() {
        for (ScreenSize size : ScreenSize.values()) {
            displayManager.setDisplayMode(size);
            assertEquals(size, displayManager.getCurrentSize());
            
            int width = displayManager.getWidth(size);
            int height = displayManager.getHeight(size);
            
            assertTrue(width > 0);
            assertTrue(height > 0);
        }
    }
    
    @Test
    @DisplayName("Stage 설정 - null이 아닌 경우")
    void testSetPrimaryStage() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            displayManager.setPrimaryStage(stage);
            // Stage가 설정되어도 예외가 발생하지 않아야 함
            assertDoesNotThrow(() -> displayManager.setDisplayMode(ScreenSize.LARGE));
        });
    }
    
    @Test
    @DisplayName("Stage가 null일 때 화면 크기 설정")
    void testSetDisplayModeWithoutStage() {
        displayManager.setPrimaryStage(null);
        
        assertDoesNotThrow(() -> displayManager.setDisplayMode(ScreenSize.SMALL));
        assertEquals(ScreenSize.SMALL, displayManager.getCurrentSize());
    }
    
    @Test
    @DisplayName("applyDisplayMode with Stage 파라미터")
    void testApplyDisplayModeWithStageParameter() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            displayManager.applyDisplayMode(stage, ScreenSize.LARGE);
            
            assertEquals(ScreenSize.LARGE, displayManager.getCurrentSize());
        });
    }
}
