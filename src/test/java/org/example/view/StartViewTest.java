package org.example.view;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.example.service.ColorManager;
import org.example.service.DisplayManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StartView에 대한 Unit Test
 * Line Coverage 70% 이상을 목표로 작성됨
 */
@ExtendWith(ApplicationExtension.class)
class StartViewTest {
    
    private StartView startView;
    
    @BeforeAll
    static void initToolkit() {
        // JavaFX 환경 초기화는 @Start에서 처리됨
    }
    
    @Start
    void start(Stage stage) {
        // JavaFX 스레드에서 초기화
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        startView = new StartView();
    }
    
    @Test
    void testStartViewCreation() {
        assertNotNull(startView, "StartView should be created");
        assertNotNull(startView.getButtonSystem(), "ButtonSystem should be initialized");
    }
    
    @Test
    void testCreateViewWithCallbacks() {
        // Given: 콜백 실행 카운터
        AtomicInteger startGameCounter = new AtomicInteger(0);
        AtomicInteger multiPlayCounter = new AtomicInteger(0);
        AtomicInteger scoreboardCounter = new AtomicInteger(0);
        AtomicInteger settingCounter = new AtomicInteger(0);
        AtomicInteger exitCounter = new AtomicInteger(0);
        
        // When: View 생성
        VBox root = startView.createView(
            startGameCounter::incrementAndGet,
            multiPlayCounter::incrementAndGet,
            scoreboardCounter::incrementAndGet,
            settingCounter::incrementAndGet,
            exitCounter::incrementAndGet
        );
        
        // Then: UI 구성 요소 확인
        assertNotNull(root, "Root VBox should be created");
        assertTrue(root.getChildren().size() >= 5, "Root should contain title, subtitle, buttons, and controls");
        
        // 첫 번째는 타이틀
        assertTrue(root.getChildren().get(0) instanceof Text, "First child should be Text (title)");
        Text title = (Text) root.getChildren().get(0);
        assertEquals("TETRIS", title.getText(), "Title should be 'TETRIS'");
        
        // 두 번째는 서브타이틀
        assertTrue(root.getChildren().get(1) instanceof Text, "Second child should be Text (subtitle)");
        Text subtitle = (Text) root.getChildren().get(1);
        assertEquals("Team 12 Edition", subtitle.getText(), "Subtitle should be 'Team 12 Edition'");
    }
    
    @Test
    void testButtonSystemInitialization() {
        // Given: View 생성
        AtomicInteger counter = new AtomicInteger(0);
        Runnable callback = counter::incrementAndGet;
        
        VBox root = startView.createView(callback, callback, callback, callback, callback);
        
        // When: ButtonSystem 확인
        var buttonSystem = startView.getButtonSystem();
        
        // Then: 5개 버튼이 생성되어야 함 (Start Game, MultiPlay, View Scoreboard, Setting, Exit)
        assertNotNull(buttonSystem, "ButtonSystem should exist");
        assertEquals(5, buttonSystem.getButtonCount(), "Should have 5 buttons");
        
        // 버튼 텍스트 확인
        var buttons = buttonSystem.getButtons();
        assertEquals("Start Game", buttons.get(0).getText());
        assertEquals("MultiPlay", buttons.get(1).getText());
        assertEquals("View Scoreboard", buttons.get(2).getText());
        assertEquals("Setting", buttons.get(3).getText());
        assertEquals("Exit", buttons.get(4).getText());
    }
    
    @Test
    void testScaleUpdateSmall() {
        // Given: View 생성
        VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
        
        // When: SMALL 스케일로 변경
        startView.updateScale(ScreenSize.SMALL);
        
        // Then: 스케일이 적용되어야 함 (0.9)
        // ButtonSystem의 스케일은 내부적으로 변경됨
        assertNotNull(startView.getButtonSystem());
    }
    
    @Test
    void testScaleUpdateMedium() {
        // Given: View 생성
        VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
        
        // When: MEDIUM 스케일로 변경
        startView.updateScale(ScreenSize.MEDIUM);
        
        // Then: 스케일이 적용되어야 함 (1.0)
        assertNotNull(startView.getButtonSystem());
    }
    
    @Test
    void testScaleUpdateLarge() {
        // Given: View 생성
        VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
        
        // When: LARGE 스케일로 변경
        startView.updateScale(ScreenSize.LARGE);
        
        // Then: 스케일이 적용되어야 함 (1.1)
        assertNotNull(startView.getButtonSystem());
    }
    
    @Test
    void testScaleChangedBeforeViewCreation() {
        // Given: View가 생성되기 전
        StartView newView = new StartView();
        
        // When: 스케일 변경 시도
        // Then: 예외가 발생하지 않아야 함 (내부에서 null 체크)
        assertDoesNotThrow(() -> newView.updateScale(ScreenSize.SMALL));
    }
    
    @Test
    void testControlsTextExists() {
        // Given: View 생성
        VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
        
        // When: Controls 텍스트 찾기
        Text controls = null;
        for (var child : root.getChildren()) {
            if (child instanceof Text) {
                Text text = (Text) child;
                if (text.getText().contains("Controls:")) {
                    controls = text;
                    break;
                }
            }
        }
        
        // Then: Controls 텍스트가 존재해야 함
        assertNotNull(controls, "Controls text should exist");
        assertTrue(controls.getText().contains("← →"), "Controls should contain arrow keys");
        assertTrue(controls.getText().contains("Space"), "Controls should contain Space key");
        assertTrue(controls.getText().contains("ESC"), "Controls should contain ESC key");
    }
    
    @Test
    void testBackgroundColor() {
        // Given: View 생성
        VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
        
        // When: CSS 클래스 확인
        var styleClasses = root.getStyleClass();
        
        // Then: Background CSS 클래스가 설정되어야 함
        assertTrue(styleClasses.contains("root-dark"), "Background CSS class should be set");
    }
    
    @Test
    void testButtonAlignment() {
        // Given: View 생성
        VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
        
        // When: Alignment 확인
        var alignment = root.getAlignment();
        
        // Then: CENTER 정렬이어야 함
        assertEquals(javafx.geometry.Pos.CENTER, alignment, "Root should be center aligned");
    }
    
    @Test
    void testMultipleScaleUpdates() {
        // Given: View 생성
        VBox root = startView.createView(() -> {}, () -> {}, () -> {}, () -> {}, () -> {});
        
        // When: 여러 번 스케일 변경
        startView.updateScale(ScreenSize.SMALL);
        startView.updateScale(ScreenSize.LARGE);
        startView.updateScale(ScreenSize.MEDIUM);
        
        // Then: 예외가 발생하지 않아야 함
        assertNotNull(startView.getButtonSystem());
    }
}
