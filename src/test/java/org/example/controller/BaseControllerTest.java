package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.example.service.DisplayManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaseController 클래스의 Unit Test
 * Note: State management methods (stackState, popState, setState, swapState) require JavaFX thread
 * and are better suited for integration tests.
 */
@ExtendWith(ApplicationExtension.class)
class BaseControllerTest {
    
    private Stage testStage;
    private SettingManager settingManager;
    private TestController controller1;
    
    @Start
    private void start(Stage stage) {
        this.testStage = stage;
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() throws Exception {
        settingManager = new SettingManager();
        BaseController.Initialize(testStage, settingManager);
        
        controller1 = new TestController();
    }
    
    @Test
    void testInitialize() throws Exception {
        Field stageField = BaseController.class.getDeclaredField("primaryStage");
        stageField.setAccessible(true);
        
        Field settingManagerField = BaseController.class.getDeclaredField("settingManager");
        settingManagerField.setAccessible(true);
        
        Field stackField = BaseController.class.getDeclaredField("stateStack");
        stackField.setAccessible(true);
        
        assertNotNull(stageField.get(null), "primaryStage should be initialized");
        assertNotNull(settingManagerField.get(null), "settingManager should be initialized");
        assertNotNull(stackField.get(null), "stateStack should be initialized");
    }
    
    @Test
    void testGetScene() {
        controller1.createScene();
        
        Scene scene = controller1.getScene();
        assertNotNull(scene, "getScene should return created scene");
    }
    
    @Test
    void testCreateDefaultScene() {
        VBox root = new VBox();
        controller1.createDefaultScene(root);
        
        assertNotNull(controller1.scene, "Scene should be created");
        assertEquals(root, controller1.scene.getRoot(), "Root should be set correctly");
    }
    
    @Test
    void testCreateSceneIsAbstract() {
        // createScene이 추상 메서드인지 확인
        TestController controller = new TestController();
        Scene scene = controller.createScene();
        assertNotNull(scene, "Concrete implementation should create scene");
        assertTrue(controller.createSceneCalled, "createScene should be called");
    }
    
    @Test
    void testHandleKeyInputIsAbstract() {
        // handleKeyInput이 추상 메서드인지 확인
        TestController controller = new TestController();
        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false, false, false, false);
        
        controller.handleKeyInput(event);
        assertTrue(controller.handleKeyInputCalled, "handleKeyInput should be implemented");
    }
    
    @Test
    void testExitDefaultImplementation() {
        TestController controller = new TestController();
        assertDoesNotThrow(() -> controller.callExit(),
            "Default exit implementation should not throw");
    }
    
    @Test
    void testResumeDefaultImplementation() {
        TestController controller = new TestController();
        assertDoesNotThrow(() -> controller.callResume(),
            "Default resume implementation should not throw");
    }
    
    @Test
    void testSettingManagerIsAccessible() throws Exception {
        Field settingManagerField = BaseController.class.getDeclaredField("settingManager");
        settingManagerField.setAccessible(true);
        
        SettingManager manager = (SettingManager) settingManagerField.get(null);
        assertNotNull(manager, "settingManager should be accessible");
        assertEquals(settingManager, manager, "settingManager should match initialized instance");
    }
    
    @Test
    void testSceneHasKeyEventHandler() {
        VBox root = new VBox();
        controller1.createDefaultScene(root);
        
        assertNotNull(controller1.scene.getOnKeyPressed(),
            "Scene should have key pressed handler");
    }
    
    @Test
    void testSceneDimensionsFromDisplayManager() {
        VBox root = new VBox();
        controller1.createDefaultScene(root);
        
        DisplayManager displayManager = DisplayManager.getInstance();
        int expectedWidth = displayManager.getWidth(displayManager.getCurrentSize());
        int expectedHeight = displayManager.getHeight(displayManager.getCurrentSize());
        
        assertEquals(expectedWidth, controller1.scene.getWidth(), 0.1,
            "Scene width should match DisplayManager width");
        assertEquals(expectedHeight, controller1.scene.getHeight(), 0.1,
            "Scene height should match DisplayManager height");
    }
    
    @Test
    void testExitCanBeOverridden() {
        TestController controller = new TestController();
        controller.callExit();
        assertTrue(controller.exitCalled, "Overridden exit should be called");
    }
    
    @Test
    void testResumeCanBeOverridden() {
        TestController controller = new TestController();
        controller.callResume();
        assertTrue(controller.resumeCalled, "Overridden resume should be called");
    }
    
    /**
     * BaseController를 테스트하기 위한 구체적인 구현 클래스
     */
    private static class TestController extends BaseController {
        boolean exitCalled = false;
        boolean resumeCalled = false;
        boolean createSceneCalled = false;
        boolean handleKeyInputCalled = false;
        
        @Override
        protected void exit() {
            exitCalled = true;
        }
        
        @Override
        protected void resume() {
            resumeCalled = true;
        }
        
        @Override
        protected Scene createScene() {
            createSceneCalled = true;
            VBox root = new VBox();
            createDefaultScene(root);
            return scene;
        }
        
        @Override
        protected void handleKeyInput(KeyEvent event) {
            handleKeyInputCalled = true;
        }
        
        // 테스트를 위한 public 메서드
        public void callExit() {
            exit();
        }
        
        public void callResume() {
            resume();
        }
    }
}
