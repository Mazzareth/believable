package app.masterwork.believable.magic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MagicSchoolDefinitionsTest {
    @Test
    void tabKeysResolveToSchools() {
        for (String tabKey : MagicSchoolDefinitions.getSchoolTabKeys()) {
            assertNotNull(MagicSchoolDefinitions.getByTabKey(tabKey));
        }
    }

    @Test
    void tabKeyRoundTrip() {
        assertEquals(
            MagicSchoolDefinitions.TAB_ELEMENTALISM_KEY,
            MagicSchoolDefinitions.getTabKey(MagicSchoolDefinitions.ELEMENTALISM)
        );
    }
}
