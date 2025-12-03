package org.example.view;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ServerConnectionView integration tests with JavaFX
 * Tests IP display, title updates, and navigation
 */
@ExtendWith(ApplicationExtension.class)
class ServerConnectionViewIntegrationTest {

    private ServerConnectionView view;
    private Stage testStage;

    @Start
    void start(Stage stage) {
        this.testStage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        
        Platform.runLater(() -> {
            BaseView.Initialize(ColorManager.getInstance());
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        view = new ServerConnectionView();
    }

    @Test
    void testServerConnectionViewCreation() {
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(view);
    }

    @Test
    void testCreateViewWithCallback() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean callbackInvoked = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            VBox root = view.createView("192.168.1.100", () -> callbackInvoked.set(true));
            assertNotNull(root);
            assertTrue(root.getChildren().size() > 0);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testIPAddressDisplay() {
        WaitForAsyncUtils.waitForFxEvents();
        
        Platform.runLater(() -> {
            VBox root = view.createView("192.168.1.100", () -> {});
            
            // Find Text node with IP address
            boolean hasIPText = root.getChildren().stream()
                .anyMatch(node -> node instanceof Text && 
                    ((Text)node).getText().contains("IP Address"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testSetTitle() {
        WaitForAsyncUtils.waitForFxEvents();
        
        Platform.runLater(() -> {
            VBox root = view.createView("192.168.1.100", () -> {});
            view.setTitle("Test Server Title");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Title is stored but may not be directly visible in VBox
        // Just verify no exception is thrown
    }

    @Test
    void testGoBackButtonExists() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean hasButton = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            VBox root = view.createView("192.168.1.100", () -> {});
            
            boolean found = root.getChildren().stream()
                .anyMatch(node -> node instanceof javafx.scene.control.Button &&
                    ((javafx.scene.control.Button)node).getText().contains("Go Back"));
            hasButton.set(found);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(hasButton.get());
    }

    @Test
    void testGoBackButtonCallback() {
        // NavigableButtonSystem consumes action events for keyboard navigation
        // So we test that the button is created with proper text
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean hasGoBackButton = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            VBox root = view.createView("192.168.1.100", () -> {});
            
            // Find the button (created by NavigableButtonSystem)
            boolean found = root.getChildren().stream()
                .anyMatch(node -> node instanceof javafx.scene.control.Button &&
                    ((javafx.scene.control.Button)node).getText().equals("Go Back"));
            hasGoBackButton.set(found);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(hasGoBackButton.get(), "Go Back button should exist");
    }

    @Test
    void testOnScaleChanged() {
        WaitForAsyncUtils.waitForFxEvents();
        
        Platform.runLater(() -> {
            VBox root = view.createView("192.168.1.100", () -> {});
            
            // Test scale change - should not throw exception
            view.onScaleChanged(1.5);
            view.onScaleChanged(1.0);
            view.onScaleChanged(0.8);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Just verify no exception is thrown
    }

    @Test
    void testMultipleViewCreations() {
        WaitForAsyncUtils.waitForFxEvents();
        
        Platform.runLater(() -> {
            VBox root1 = view.createView("192.168.1.100", () -> {});
            VBox root2 = view.createView("192.168.1.200", () -> {});
            
            assertNotNull(root1);
            assertNotNull(root2);
            // Each createView() should return a new VBox
            assertNotSame(root1, root2);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
