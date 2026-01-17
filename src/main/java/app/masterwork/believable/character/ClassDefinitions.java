package app.masterwork.believable.character;

import java.util.Map;
import app.masterwork.believable.classes.alchemist.AlchemistClass;
import app.masterwork.believable.classes.architect.ArchitectClass;
import app.masterwork.believable.classes.artificer.ArtificerClass;
import app.masterwork.believable.classes.cartographer.CartographerClass;
import app.masterwork.believable.classes.mage.MageClass;
import app.masterwork.believable.classes.prospector.ProspectorClass;
import app.masterwork.believable.classes.smith.SmithClass;
import app.masterwork.believable.classes.warden.WardenClass;

/**
 * Central registry of built-in classes and their tab keys.
 */
public final class ClassDefinitions {
    public static final String TAB_ARTIFICER_KEY = "screen.believable.unbelievable_menu.class.artificer";
    public static final String TAB_ALCHEMIST_KEY = "screen.believable.unbelievable_menu.class.alchemist";
    public static final String TAB_SMITH_KEY = "screen.believable.unbelievable_menu.class.smith";
    public static final String TAB_MAGE_KEY = "screen.believable.unbelievable_menu.class.mage";
    public static final String TAB_PROSPECTOR_KEY = "screen.believable.unbelievable_menu.class.prospector";
    public static final String TAB_ARCHITECT_KEY = "screen.believable.unbelievable_menu.class.architect";
    public static final String TAB_WARDEN_KEY = "screen.believable.unbelievable_menu.class.warden";
    public static final String TAB_CARTOGRAPHER_KEY = "screen.believable.unbelievable_menu.class.cartographer";

    public static final BasicClass ARTIFICER = ArtificerClass.DEFINITION;
    public static final BasicClass SMITH = SmithClass.DEFINITION;
    public static final BasicClass MAGE = MageClass.DEFINITION;
    public static final BasicClass ALCHEMIST = AlchemistClass.DEFINITION;
    public static final BasicClass PROSPECTOR = ProspectorClass.DEFINITION;
    public static final BasicClass ARCHITECT = ArchitectClass.DEFINITION;
    public static final BasicClass WARDEN = WardenClass.DEFINITION;
    public static final BasicClass CARTOGRAPHER = CartographerClass.DEFINITION;

    private static final Map<String, BasicClass> BY_TAB_KEY = Map.of(
        TAB_ARTIFICER_KEY, ARTIFICER,
        TAB_SMITH_KEY, SMITH,
        TAB_MAGE_KEY, MAGE,
        TAB_ALCHEMIST_KEY, ALCHEMIST,
        TAB_PROSPECTOR_KEY, PROSPECTOR,
        TAB_ARCHITECT_KEY, ARCHITECT,
        TAB_WARDEN_KEY, WARDEN,
        TAB_CARTOGRAPHER_KEY, CARTOGRAPHER
    );

    private static final Map<String, String> TAB_KEY_BY_NAME = Map.of(
        ARTIFICER.name(), TAB_ARTIFICER_KEY,
        SMITH.name(), TAB_SMITH_KEY,
        MAGE.name(), TAB_MAGE_KEY,
        ALCHEMIST.name(), TAB_ALCHEMIST_KEY,
        PROSPECTOR.name(), TAB_PROSPECTOR_KEY,
        ARCHITECT.name(), TAB_ARCHITECT_KEY,
        WARDEN.name(), TAB_WARDEN_KEY,
        CARTOGRAPHER.name(), TAB_CARTOGRAPHER_KEY
    );

    private ClassDefinitions() {
    }

    /**
     * Resolves the menu tab key for the given class instance.
     */
    public static String getTabKey(BasicClass playerClass) {
        if (playerClass == null) {
            return null;
        }
        return TAB_KEY_BY_NAME.get(playerClass.name());
    }

    /**
     * Resolves a class from its menu tab key.
     */
    public static BasicClass getByTabKey(String key) {
        return BY_TAB_KEY.get(key);
    }
}
