package app.masterwork.believable.magic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines built-in magic schools, their menu tabs, and subsection descriptions.
 */
public final class MagicSchoolDefinitions {
    public static final String TAB_MAGIC_KEY = "screen.believable.unbelievable_menu.tab.magic";

    public static final String TAB_ELEMENTALISM_KEY = "screen.believable.unbelievable_menu.magic.elementalism";
    public static final String TAB_SPATIAL_KEY = "screen.believable.unbelievable_menu.magic.spatial";
    public static final String TAB_TEMPORAL_KEY = "screen.believable.unbelievable_menu.magic.temporal";
    public static final String TAB_ONEIROMANCY_KEY = "screen.believable.unbelievable_menu.magic.oneiromancy";
    public static final String TAB_ELDRITCH_KEY = "screen.believable.unbelievable_menu.magic.eldritch";
    public static final String TAB_AILMENT_KEY = "screen.believable.unbelievable_menu.magic.ailment";
    public static final String TAB_NECROMANCY_KEY = "screen.believable.unbelievable_menu.magic.necromancy";
    public static final String TAB_BLESSING_KEY = "screen.believable.unbelievable_menu.magic.blessing";
    public static final String TAB_WITCHERY_KEY = "screen.believable.unbelievable_menu.magic.witchery";
    public static final String TAB_ALCHEMIST_KEY = "screen.believable.unbelievable_menu.magic.alchemist";

    public static final String TAB_MAGIC_OVERVIEW_KEY = "screen.believable.unbelievable_menu.magic.sub.overview";
    public static final String TAB_MAGIC_SPELLS_KEY = "screen.believable.unbelievable_menu.magic.sub.spells";

    public static final String TAB_ELEMENTALISM_ARCANE_KEY = "screen.believable.unbelievable_menu.magic.elementalism.arcane";
    public static final String TAB_ELEMENTALISM_EARTH_KEY = "screen.believable.unbelievable_menu.magic.elementalism.earth";
    public static final String TAB_ELEMENTALISM_WATER_KEY = "screen.believable.unbelievable_menu.magic.elementalism.water";
    public static final String TAB_ELEMENTALISM_FIRE_KEY = "screen.believable.unbelievable_menu.magic.elementalism.fire";
    public static final String TAB_ELEMENTALISM_AIR_KEY = "screen.believable.unbelievable_menu.magic.elementalism.air";

    public static final String TAB_AILMENT_PESTILENCE_KEY = "screen.believable.unbelievable_menu.magic.ailment.pestilence";
    public static final String TAB_AILMENT_POISON_KEY = "screen.believable.unbelievable_menu.magic.ailment.poison";

    public static final BasicMagicSchool ELEMENTALISM = new BasicMagicSchool(
        "elementalism",
        "Elementalism",
        "Direct manipulation of natural forces and materials.",
        List.of(
            TAB_ELEMENTALISM_ARCANE_KEY,
            TAB_ELEMENTALISM_EARTH_KEY,
            TAB_ELEMENTALISM_WATER_KEY,
            TAB_ELEMENTALISM_FIRE_KEY,
            TAB_ELEMENTALISM_AIR_KEY
        )
    );
    public static final BasicMagicSchool SPATIAL = new BasicMagicSchool(
        "spatial",
        "Spatial",
        "Warp space, bend gravity, and reposition matter.",
        List.of(TAB_MAGIC_OVERVIEW_KEY, TAB_MAGIC_SPELLS_KEY)
    );
    public static final BasicMagicSchool TEMPORAL = new BasicMagicSchool(
        "temporal",
        "Temporal",
        "Twist time, accelerate decay, and slow moments.",
        List.of(TAB_MAGIC_OVERVIEW_KEY, TAB_MAGIC_SPELLS_KEY)
    );
    public static final BasicMagicSchool ONEIROMANCY = new BasicMagicSchool(
        "oneiromancy",
        "Oneiromancy",
        "Shape dreams and weave illusions into reality.",
        List.of(TAB_MAGIC_OVERVIEW_KEY, TAB_MAGIC_SPELLS_KEY)
    );
    public static final BasicMagicSchool ELDRITCH = new BasicMagicSchool(
        "eldritch",
        "Eldritch",
        "Borrow power from the unknown at a dangerous cost.",
        List.of(TAB_MAGIC_OVERVIEW_KEY, TAB_MAGIC_SPELLS_KEY)
    );
    public static final BasicMagicSchool AILMENT = new BasicMagicSchool(
        "ailment",
        "Ailment",
        "Inflict disease and venom through controlled decay.",
        List.of(TAB_AILMENT_PESTILENCE_KEY, TAB_AILMENT_POISON_KEY)
    );
    public static final BasicMagicSchool NECROMANCY = new BasicMagicSchool(
        "necromancy",
        "Necromancy",
        "Raise the dead and command lingering spirits.",
        List.of(TAB_MAGIC_OVERVIEW_KEY, TAB_MAGIC_SPELLS_KEY)
    );
    public static final BasicMagicSchool BLESSING = new BasicMagicSchool(
        "blessing",
        "Blessing",
        "Heal, cleanse, and ward allies from harm.",
        List.of(TAB_MAGIC_OVERVIEW_KEY, TAB_MAGIC_SPELLS_KEY)
    );
    public static final BasicMagicSchool WITCHERY = new BasicMagicSchool(
        "witchery",
        "Witchery",
        "Brew potions, curses, and volatile concoctions.",
        List.of(TAB_MAGIC_OVERVIEW_KEY, TAB_MAGIC_SPELLS_KEY)
    );
    public static final BasicMagicSchool ALCHEMIST = new BasicMagicSchool(
        "alchemist",
        "Alchemist",
        "Transmute materials and imbue armor with new traits.",
        List.of(TAB_MAGIC_OVERVIEW_KEY, TAB_MAGIC_SPELLS_KEY)
    );

    private static final List<SchoolEntry> SCHOOL_ENTRIES = List.of(
        new SchoolEntry(TAB_ELEMENTALISM_KEY, ELEMENTALISM),
        new SchoolEntry(TAB_SPATIAL_KEY, SPATIAL),
        new SchoolEntry(TAB_TEMPORAL_KEY, TEMPORAL),
        new SchoolEntry(TAB_ONEIROMANCY_KEY, ONEIROMANCY),
        new SchoolEntry(TAB_ELDRITCH_KEY, ELDRITCH),
        new SchoolEntry(TAB_AILMENT_KEY, AILMENT),
        new SchoolEntry(TAB_NECROMANCY_KEY, NECROMANCY),
        new SchoolEntry(TAB_BLESSING_KEY, BLESSING),
        new SchoolEntry(TAB_WITCHERY_KEY, WITCHERY),
        new SchoolEntry(TAB_ALCHEMIST_KEY, ALCHEMIST)
    );

    private static final List<String> SCHOOL_TAB_KEYS = SCHOOL_ENTRIES.stream()
        .map(SchoolEntry::tabKey)
        .toList();

    private static final Map<String, BasicMagicSchool> BY_TAB_KEY = SCHOOL_ENTRIES.stream()
        .collect(Collectors.toMap(SchoolEntry::tabKey, SchoolEntry::school));

    private static final Map<String, String> TAB_KEY_BY_ID = SCHOOL_ENTRIES.stream()
        .collect(Collectors.toMap(entry -> entry.school().id(), SchoolEntry::tabKey));

    private static final Map<String, String> SUBSECTION_DESCRIPTIONS = Map.ofEntries(
        Map.entry(TAB_MAGIC_OVERVIEW_KEY, "Overview of the school and its core mechanics."),
        Map.entry(TAB_MAGIC_SPELLS_KEY, "Known spells and rituals within this school."),
        Map.entry(TAB_ELEMENTALISM_ARCANE_KEY, "Condensed mana shaped into pure force."),
        Map.entry(TAB_ELEMENTALISM_EARTH_KEY, "Control ground, stone, and shifting terrain."),
        Map.entry(TAB_ELEMENTALISM_WATER_KEY, "Guide currents and wield water pressure."),
        Map.entry(TAB_ELEMENTALISM_FIRE_KEY, "Ignite and shape flame into weapons."),
        Map.entry(TAB_ELEMENTALISM_AIR_KEY, "Manipulate wind, lift, and momentum."),
        Map.entry(TAB_AILMENT_PESTILENCE_KEY, "Spread disease through creeping corruption."),
        Map.entry(TAB_AILMENT_POISON_KEY, "Focus on toxins, venoms, and debilitating mixes.")
    );

    private MagicSchoolDefinitions() {
    }

    /**
     * @return ordered list of school tab keys for menu rendering
     */
    public static List<String> getSchoolTabKeys() {
        return SCHOOL_TAB_KEYS;
    }

    /**
     * Resolves a school instance from its menu tab key.
     */
    public static BasicMagicSchool getByTabKey(String key) {
        return BY_TAB_KEY.get(key);
    }

    /**
     * Resolves the menu tab key for a school instance.
     */
    public static String getTabKey(BasicMagicSchool school) {
        if (school == null) {
            return null;
        }
        return TAB_KEY_BY_ID.get(school.id());
    }

    /**
     * @return description text for a subsection tab, or null if unknown
     */
    public static String getSubsectionDescription(String subsectionKey) {
        return SUBSECTION_DESCRIPTIONS.get(subsectionKey);
    }

    private record SchoolEntry(String tabKey, BasicMagicSchool school) {
    }
}
