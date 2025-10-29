package org.example.view;

import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import static org.junit.jupiter.api.Assertions.*;

class SettingViewTest extends ApplicationTest {
    
    private SettingView settingView;
    
    @BeforeEach
    void setUp() {
        settingView = new SettingView();
    }
    
    @Test
    @DisplayName("SettingView 생성 테스트")
    void testConstructor() {
        assertNotNull(settingView);
        assertNotNull(settingView.getButtonSystem());
    }
    
    @Test
    @DisplayName("createView - 6개 버튼 생성")
    void testCreateViewButtons() {
        javafx.application.Platform.runLater(() -> {
            VBox root = settingView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertNotNull(root);
            assertEquals(6, settingView.getButtonSystem().getButtons().size());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 버튼 텍스트")
    void testCreateViewButtonTexts() {
        javafx.application.Platform.runLater(() -> {
            settingView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            var buttons = settingView.getButtonSystem().getButtons();
            assertEquals("Screen Size", buttons.get(0).getText());
            assertEquals("Controls", buttons.get(1).getText());
            assertEquals("Color Blind Setting", buttons.get(2).getText());
            assertEquals("Reset Score Board", buttons.get(3).getText());
            assertEquals("Reset All Setting", buttons.get(4).getText());
            assertEquals("Go Back", buttons.get(5).getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Screen Size 액션")
    void testScreenSizeAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] opened = {false};
            settingView.createView(() -> opened[0] = true, () -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            Button button = settingView.getButtonSystem().getButtons().get(0);
            ((Runnable) button.getUserData()).run();
            
            assertTrue(opened[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Controls 액션")
    void testControlsAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] opened = {false};
            settingView.createView(() -> {}, () -> opened[0] = true, () -> {}, () -> {}, () -> {}, () -> {});
            
            Button button = settingView.getButtonSystem().getButtons().get(1);
            ((Runnable) button.getUserData()).run();
            
            assertTrue(opened[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Color Blind Setting 액션")
    void testColorBlindSettingAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] opened = {false};
            settingView.createView(() -> {}, () -> {}, () -> opened[0] = true, () -> {}, () -> {}, () -> {});
            
            Button button = settingView.getButtonSystem().getButtons().get(2);
            ((Runnable) button.getUserData()).run();
            
            assertTrue(opened[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Reset Score Board 액션")
    void testResetScoreBoardAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] reset = {false};
            settingView.createView(() -> {}, () -> {}, () -> {}, () -> reset[0] = true, () -> {}, () -> {});
            
            Button button = settingView.getButtonSystem().getButtons().get(3);
            ((Runnable) button.getUserData()).run();
            
            assertTrue(reset[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Reset All Setting 액션")
    void testResetAllSettingAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] reset = {false};
            settingView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> reset[0] = true, () -> {});
            
            Button button = settingView.getButtonSystem().getButtons().get(4);
            ((Runnable) button.getUserData()).run();
            
            assertTrue(reset[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - Go Back 액션")
    void testGoBackAction() {
        javafx.application.Platform.runLater(() -> {
            boolean[] back = {false};
            settingView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {}, () -> back[0] = true);
            
            Button button = settingView.getButtonSystem().getButtons().get(5);
            ((Runnable) button.getUserData()).run();
            
            assertTrue(back[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 타이틀 텍스트")
    void testCreateViewTitle() {
        javafx.application.Platform.runLater(() -> {
            VBox root = settingView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            Text title = (Text) root.getChildren().get(0);
            assertEquals("Settings", title.getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
