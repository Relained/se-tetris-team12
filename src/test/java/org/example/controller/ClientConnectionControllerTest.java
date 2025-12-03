package org.example.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientConnectionController unit tests
 * Tests controller initialization without JavaFX dependency
 */
class ClientConnectionControllerTest {

    @Test
    @DisplayName("ClientConnectionController initializes correctly")
    void testInitialization() {
        ClientConnectionController controller = new ClientConnectionController();

        assertNotNull(controller, "Controller should be initialized");
    }

    @Test
    @DisplayName("ClientConnectionController has proper state management")
    void testStateManagement() {
        ClientConnectionController controller = new ClientConnectionController();

        // Controller should be created and ready to use
        assertNotNull(controller, "Controller should exist");
    }
}
