package app.masterwork.believable.stats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StatDefinitionsTest {
    @Test
    void statsRegisterOnceLoaded() {
        StatDefinitions.init();
        assertNotNull(StatRegistry.get(StatDefinitions.MAX_HEALTH.getId()));
        assertFalse(StatRegistry.getAll().isEmpty());
    }
}

