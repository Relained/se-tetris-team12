package org.example.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientConnectionView unit tests
 * Tests view initialization without JavaFX dependency
 */
class ClientConnectionViewTest {

    @Test
    @DisplayName("ClientConnectionView initializes correctly")
    void testInitialization() {
        ClientConnectionView view = new ClientConnectionView();

        assertNotNull(view, "View should be initialized");
    }

    @Test
    @DisplayName("ClientConnectionView can be instantiated")
    void testInstantiation() {
        assertDoesNotThrow(() -> new ClientConnectionView(), 
            "Creating view should not throw exception");
    }
}
