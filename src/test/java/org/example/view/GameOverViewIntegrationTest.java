package org.example.view;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

import org.example.service.ColorManager;

@ExtendWith(ApplicationExtension.class)
class GameOverViewIntegrationTest {

    private GameOverView view;
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
        
        view = new GameOverView();
    }

    @Test
    void testCreateView() throws Exception {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(10000, 50, 5, () -> {}, () -> {}, () -> {}, () -> {});
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0]);
        assertTrue(rootHolder[0].getChildren().size() >= 8);
    }

    @Test
    void testButtonSystemInitialized() throws Exception {
        javafx.application.Platform.runLater(() -> {
            view.createView(1000, 10, 1, () -> {}, () -> {}, () -> {}, () -> {});
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(view.getButtonSystem());
        assertEquals(4, view.getButtonSystem().getButtonCount());
    }

    @Test
    void testCreateViewWithZeroScore() throws Exception {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(0, 0, 1, () -> {}, () -> {}, () -> {}, () -> {});
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0]);
    }

    @Test
    void testCreateViewWithHighScore() throws Exception {
        final VBox[] rootHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            VBox root = view.createView(999999, 999, 99, () -> {}, () -> {}, () -> {}, () -> {});
            rootHolder[0] = root;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(rootHolder[0]);
    }

    @Test
    void testDifferentScoreCombinations() throws Exception {
        javafx.application.Platform.runLater(() -> {
            view.createView(500, 5, 1, () -> {}, () -> {}, () -> {}, () -> {});
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        GameOverView view2 = new GameOverView();
        javafx.application.Platform.runLater(() -> {
            view2.createView(50000, 200, 10, () -> {}, () -> {}, () -> {}, () -> {});
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(view.getButtonSystem());
        assertNotNull(view2.getButtonSystem());
    }
}
