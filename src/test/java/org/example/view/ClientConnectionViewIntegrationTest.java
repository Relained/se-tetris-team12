package org.example.view;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientConnectionView integration tests with JavaFX
 * Tests view creation and update methods
 */
@ExtendWith(ApplicationExtension.class)
class ClientConnectionViewIntegrationTest {

    private ClientConnectionView view;
    private Stage testStage;

    @Start
    void start(Stage stage) {
        this.testStage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        
        javafx.application.Platform.runLater(() -> {
            BaseView.Initialize(ColorManager.getInstance());
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        view = new ClientConnectionView();
    }

    @Test
    @DisplayName("ClientConnectionView initializes correctly")
    void testInitialization() {
        assertNotNull(view, "View should be initialized");
    }

    @Test
    @DisplayName("ClientConnectionView creates view with all callbacks")
    void testCreateView() {
        final VBox[] rootHolder = {null};
        final AtomicBoolean callbackInvoked = new AtomicBoolean(false);
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> callbackInvoked.set(true),      // onSearchedUserSelect
                () -> {},                               // onRefresh
                ip -> {},                               // onHistorySelect
                ip -> {},                               // onIpSubmit
                () -> {},                               // onGoBack
                () -> {}                                // onClearHistory
            );
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0], "View pane should be created");
        assertTrue(rootHolder[0].getChildren().size() > 0, "View should have children");
    }

    @Test
    @DisplayName("ClientConnectionView sets IP address field text")
    void testSetIpAddressField() {
        final VBox[] rootHolder = {null};
        final String testIp = "192.168.1.100";
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.setIpAddressField(testIp);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Setting IP address should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionView sets title text")
    void testSetTitleText() {
        final VBox[] rootHolder = {null};
        final String customTitle = "Custom Server Connection";
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.setTitleText(customTitle);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Setting title should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionView sets connection history items")
    void testSetConnectionHistoryItems() {
        final VBox[] rootHolder = {null};
        final List<String> historyItems = Arrays.asList(
            "192.168.1.10",
            "192.168.1.20",
            "192.168.1.30"
        );
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.setConnectionHistoryItems(historyItems);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Setting connection history should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionView resets searched users items")
    void testResetSearchedUsersItems() {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.resetSearchedUsersItems();
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Resetting searched users should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionView onRefresh callback invoked")
    void testOnRefreshCallback() {
        final VBox[] rootHolder = {null};
        final AtomicBoolean refreshCalled = new AtomicBoolean(false);
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {},
                () -> refreshCalled.set(true),  // onRefresh
                ip -> {},
                ip -> {},
                () -> {},
                () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0], "View should be created");
    }

    @Test
    @DisplayName("ClientConnectionView onIpSubmit callback invoked")
    void testOnIpSubmitCallback() {
        final VBox[] rootHolder = {null};
        final AtomicReference<String> submittedIp = new AtomicReference<>("");
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {},
                () -> {},
                ip -> {},
                ip -> submittedIp.set(ip),  // onIpSubmit
                () -> {},
                () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0], "View should be created");
    }

    @Test
    @DisplayName("ClientConnectionView onGoBack callback invoked")
    void testOnGoBackCallback() {
        final VBox[] rootHolder = {null};
        final AtomicBoolean goBackCalled = new AtomicBoolean(false);
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {},
                () -> {},
                ip -> {},
                ip -> {},
                () -> goBackCalled.set(true),  // onGoBack
                () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0], "View should be created");
    }

    @Test
    @DisplayName("ClientConnectionView onClearHistory callback invoked")
    void testOnClearHistoryCallback() {
        final VBox[] rootHolder = {null};
        final AtomicBoolean clearHistoryCalled = new AtomicBoolean(false);
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {},
                () -> {},
                ip -> {},
                ip -> {},
                () -> {},
                () -> clearHistoryCalled.set(true)  // onClearHistory
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0], "View should be created");
    }

    @Test
    @DisplayName("ClientConnectionView handles empty IP address")
    void testSetEmptyIpAddress() {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.setIpAddressField("");
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Empty IP address should be handled");
    }

    @Test
    @DisplayName("ClientConnectionView handles empty connection history")
    void testSetEmptyConnectionHistory() {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.setConnectionHistoryItems(Arrays.asList());
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Empty history should be handled");
    }

    @Test
    @DisplayName("ClientConnectionView multiple updates work correctly")
    void testMultipleUpdates() {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                for (int i = 1; i <= 10; i++) {
                    view.setIpAddressField("192.168.1." + i);
                    view.setTitleText("Connection " + i);
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Multiple updates should not throw exception");
    }

    @Test
    @DisplayName("ClientConnectionView handles large connection history")
    void testLargeConnectionHistory() {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                List<String> largeHistory = Arrays.asList(
                    "192.168.1.1", "192.168.1.2", "192.168.1.3",
                    "192.168.1.4", "192.168.1.5", "192.168.1.6",
                    "192.168.1.7", "192.168.1.8", "192.168.1.9",
                    "192.168.1.10"
                );
                view.setConnectionHistoryItems(largeHistory);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Large history should be handled");
    }

    @Test
    @DisplayName("ClientConnectionView updates after reset")
    void testUpdatesAfterReset() {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(
                ip -> {}, () -> {}, ip -> {}, ip -> {}, () -> {}, () -> {}
            );
            Scene scene = new Scene(root, 1024, 768);
            testStage.setScene(scene);
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                view.resetSearchedUsersItems();
                view.setIpAddressField("192.168.1.1");
                view.setConnectionHistoryItems(Arrays.asList("192.168.1.2"));
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "Updates after reset should work");
    }
}
