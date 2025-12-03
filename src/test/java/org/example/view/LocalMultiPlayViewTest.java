package org.example.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LocalMultiPlayView unit tests
 * Tests view initialization without JavaFX dependency
 */
class LocalMultiPlayViewTest {

    @Test
    @DisplayName("LocalMultiPlayView initializes correctly")
    void testInitialization() {
        LocalMultiPlayView view = new LocalMultiPlayView();

        assertNotNull(view, "View should be initialized");
    }

    @Test
    @DisplayName("LocalMultiPlayView can be instantiated")
    void testInstantiation() {
        assertDoesNotThrow(() -> new LocalMultiPlayView(), 
            "Creating view should not throw exception");
    }
}
