package org.example.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.model.GameMode;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class WaitingRoomViewIntegrationTest {

    private WaitingRoomView serverView;
    private WaitingRoomView clientView;
    private VBox serverRoot;
    private VBox clientRoot;
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
        
        serverView = new WaitingRoomView(true);
        clientView = new WaitingRoomView(false);
        
        // Create default views with dummy callbacks
        serverRoot = serverView.createView(
            "192.168.1.1",
            mode -> {},
            difficulty -> {},
            () -> {},
            msg -> {},
            () -> {}
        );
        
        clientRoot = clientView.createView(
            "192.168.1.2",
            mode -> {},
            difficulty -> {},
            () -> {},
            msg -> {},
            () -> {}
        );
    }

    @Test
    void testServerViewCreation() {
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(serverRoot);
        assertTrue(serverRoot.getChildren().size() >= 5);
    }

    @Test
    void testClientViewCreation() {
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(clientRoot);
        assertTrue(clientRoot.getChildren().size() >= 5);
    }

    @Test
    void testServerViewHasGameModeSelection() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean hasRadioButtons = new AtomicBoolean(false);
        Platform.runLater(() -> {
            boolean found = serverRoot.getChildren().stream()
                .anyMatch(node -> node instanceof VBox && 
                    ((VBox)node).getChildren().stream()
                        .anyMatch(child -> child instanceof javafx.scene.layout.HBox &&
                            ((javafx.scene.layout.HBox)child).getChildren().stream()
                                .anyMatch(rbNode -> rbNode instanceof RadioButton)));
            hasRadioButtons.set(found);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(hasRadioButtons.get());
    }

    @Test
    void testClientViewHasGameModeDisplay() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean hasGameModeText = new AtomicBoolean(false);
        Platform.runLater(() -> {
            boolean found = clientRoot.getChildren().stream()
                .flatMap(node -> {
                    if (node instanceof VBox) {
                        return ((VBox)node).getChildren().stream();
                    }
                    return java.util.stream.Stream.empty();
                })
                .anyMatch(child -> child.toString().contains("Text"));
            hasGameModeText.set(found);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(hasGameModeText.get());
    }

    @Test
    void testReadyButtonExists() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean hasReadyButton = new AtomicBoolean(false);
        Platform.runLater(() -> {
            boolean found = serverRoot.getChildren().stream()
                .anyMatch(node -> node instanceof Button && 
                    ((Button)node).getText().contains("Ready"));
            hasReadyButton.set(found);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(hasReadyButton.get());
    }

    @Test
    void testReadyButtonToggleStyle() {
        WaitForAsyncUtils.waitForFxEvents();
        
        Platform.runLater(() -> {
            serverView.updateToggleButtonStyle(false);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicReference<String> unreadyText = new AtomicReference<>();
        Platform.runLater(() -> {
            Button readyButton = (Button) serverRoot.getChildren().stream()
                .filter(node -> node instanceof Button && 
                    ((Button)node).getText().contains("Ready"))
                .findFirst()
                .orElse(null);
            if (readyButton != null) {
                unreadyText.set(readyButton.getText());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals("Ready", unreadyText.get());
        
        Platform.runLater(() -> {
            serverView.updateToggleButtonStyle(true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicReference<String> readyText = new AtomicReference<>();
        Platform.runLater(() -> {
            Button readyButton = (Button) serverRoot.getChildren().stream()
                .filter(node -> node instanceof Button && 
                    ((Button)node).getText().contains("Ready"))
                .findFirst()
                .orElse(null);
            if (readyButton != null) {
                readyText.set(readyButton.getText());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals("Ready ✓", readyText.get());
    }

    @Test
    void testGameModeChangeCallback() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicReference<String> selectedMode = new AtomicReference<>();
        
        WaitingRoomView testView = new WaitingRoomView(true);
        VBox testRoot = testView.createView(
            "127.0.0.1",
            selectedMode::set,
            difficulty -> {},
            () -> {},
            msg -> {},
            () -> {}
        );
        
        Platform.runLater(() -> {
            VBox container = (VBox) testRoot.getChildren().get(1);
            javafx.scene.layout.HBox radioBox = (javafx.scene.layout.HBox) container.getChildren().get(0);
            RadioButton itemRadio = (RadioButton) radioBox.getChildren().get(1);
            itemRadio.setSelected(true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(GameMode.ITEM.toString(), selectedMode.get());
    }

    @Test
    void testDifficultyChangeCallback() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicInteger selectedDifficulty = new AtomicInteger(0);
        
        WaitingRoomView testView = new WaitingRoomView(true);
        VBox testRoot = testView.createView(
            "127.0.0.1",
            mode -> {},
            selectedDifficulty::set,
            () -> {},
            msg -> {},
            () -> {}
        );
        
        Platform.runLater(() -> {
            VBox container = (VBox) testRoot.getChildren().get(2);
            javafx.scene.layout.HBox radioBox = (javafx.scene.layout.HBox) container.getChildren().get(0);
            RadioButton hardRadio = (RadioButton) radioBox.getChildren().get(2);
            hardRadio.setSelected(true);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(3, selectedDifficulty.get()); // 3 = Hard
    }

    @Test
    void testReadyButtonCallback() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean readyClicked = new AtomicBoolean(false);
        
        WaitingRoomView testView = new WaitingRoomView(true);
        VBox testRoot = testView.createView(
            "127.0.0.1",
            mode -> {},
            difficulty -> {},
            () -> readyClicked.set(true),
            msg -> {},
            () -> {}
        );
        
        Platform.runLater(() -> {
            Button readyButton = (Button) testRoot.getChildren().stream()
                .filter(node -> node instanceof Button && 
                    ((Button)node).getText().contains("Ready"))
                .findFirst()
                .orElse(null);
            if (readyButton != null) {
                readyButton.fire();
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(readyClicked.get());
    }

    @Test
    void testSetGameModeText() {
        WaitForAsyncUtils.waitForFxEvents();
        
        Platform.runLater(() -> {
            clientView.setGameModeText("ITEM", "Hard");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean foundModeText = new AtomicBoolean(false);
        Platform.runLater(() -> {
            boolean found = clientRoot.getChildren().stream()
                .flatMap(node -> {
                    if (node instanceof VBox) {
                        return ((VBox)node).getChildren().stream();
                    }
                    return java.util.stream.Stream.empty();
                })
                .flatMap(node -> {
                    if (node instanceof javafx.scene.layout.HBox) {
                        return ((javafx.scene.layout.HBox)node).getChildren().stream();
                    }
                    return java.util.stream.Stream.empty();
                })
                .flatMap(node -> {
                    if (node instanceof javafx.scene.layout.HBox) {
                        return ((javafx.scene.layout.HBox)node).getChildren().stream();
                    }
                    return java.util.stream.Stream.empty();
                })
                .filter(child -> child instanceof Text)
                .anyMatch(text -> ((Text)text).getText().equals("ITEM") || 
                                  ((Text)text).getText().equals("Hard"));
            foundModeText.set(found);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(foundModeText.get());
    }

    @Test
    void testNavigationMethods() {
        WaitForAsyncUtils.waitForFxEvents();
        
        Platform.runLater(() -> {
            serverView.navigateUp();
            serverView.navigateDown();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Navigation methods should not throw exceptions
        assertTrue(true);
    }

    @Test
    void testLeaveButtonExists() {
        WaitForAsyncUtils.waitForFxEvents();
        
        AtomicBoolean hasLeaveButton = new AtomicBoolean(false);
        Platform.runLater(() -> {
            boolean found = serverRoot.getChildren().stream()
                .anyMatch(node -> node instanceof Button && 
                    ((Button)node).getText().contains("Leave"));
            hasLeaveButton.set(found);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(hasLeaveButton.get());
    }
}
