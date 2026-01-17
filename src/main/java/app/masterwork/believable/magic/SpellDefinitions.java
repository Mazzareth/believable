package app.masterwork.believable.magic;

import app.masterwork.believable.Believable;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines all built-in spells and indexes them by school and subsection.
 */
public final class SpellDefinitions {
    private static final List<SpellSpec> SPELL_SPECS = List.of(
        new SpellSpec(
            "arcane_bolt",
            "Arcane Bolt",
            "Condensed mana fired as a focused projectile.",
            MagicSchoolDefinitions.ELEMENTALISM.id(),
            MagicSchoolDefinitions.TAB_ELEMENTALISM_ARCANE_KEY,
            8,
            1
        ),
        new SpellSpec(
            "stone_fling",
            "Stone Fling",
            "Tear a chunk of earth free and hurl it.",
            MagicSchoolDefinitions.ELEMENTALISM.id(),
            MagicSchoolDefinitions.TAB_ELEMENTALISM_EARTH_KEY,
            10,
            2
        ),
        new SpellSpec(
            "water_jet",
            "Water Jet",
            "Blast a pressurized stream of water.",
            MagicSchoolDefinitions.ELEMENTALISM.id(),
            MagicSchoolDefinitions.TAB_ELEMENTALISM_WATER_KEY,
            9,
            2
        ),
        new SpellSpec(
            "fireball",
            "Fireball",
            "Launch a volatile sphere of flame.",
            MagicSchoolDefinitions.ELEMENTALISM.id(),
            MagicSchoolDefinitions.TAB_ELEMENTALISM_FIRE_KEY,
            12,
            3
        ),
        new SpellSpec(
            "air_burst",
            "Air Burst",
            "Release a shockwave of compressed air.",
            MagicSchoolDefinitions.ELEMENTALISM.id(),
            MagicSchoolDefinitions.TAB_ELEMENTALISM_AIR_KEY,
            9,
            2
        ),
        new SpellSpec(
            "spatial_pull",
            "Spatial Pull",
            "Snap a target toward you with a gravity tug.",
            MagicSchoolDefinitions.SPATIAL.id(),
            MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY,
            14,
            3
        ),
        new SpellSpec(
            "time_dilation",
            "Time Dilation",
            "Slow a target's movement for a brief moment.",
            MagicSchoolDefinitions.TEMPORAL.id(),
            MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY,
            16,
            4
        ),
        new SpellSpec(
            "lucid_mirage",
            "Lucid Mirage",
            "Manifest a dreamlike decoy to distract foes.",
            MagicSchoolDefinitions.ONEIROMANCY.id(),
            MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY,
            12,
            3
        ),
        new SpellSpec(
            "eldritch_whisper",
            "Eldritch Whisper",
            "Stagger foes with a maddening pulse.",
            MagicSchoolDefinitions.ELDRITCH.id(),
            MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY,
            18,
            4
        ),
        new SpellSpec(
            "plague_touch",
            "Plague Touch",
            "Afflict a target with creeping sickness.",
            MagicSchoolDefinitions.AILMENT.id(),
            MagicSchoolDefinitions.TAB_AILMENT_PESTILENCE_KEY,
            11,
            3
        ),
        new SpellSpec(
            "venom_surge",
            "Venom Surge",
            "Coat a strike with concentrated poison.",
            MagicSchoolDefinitions.AILMENT.id(),
            MagicSchoolDefinitions.TAB_AILMENT_POISON_KEY,
            11,
            3
        ),
        new SpellSpec(
            "grave_bond",
            "Grave Bond",
            "Call a skeletal ally to your side.",
            MagicSchoolDefinitions.NECROMANCY.id(),
            MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY,
            20,
            5
        ),
        new SpellSpec(
            "restoring_light",
            "Restoring Light",
            "Cleanse minor wounds and restore vitality.",
            MagicSchoolDefinitions.BLESSING.id(),
            MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY,
            10,
            2
        ),
        new SpellSpec(
            "hex_brew",
            "Hex Brew",
            "Throw a bottle of disruptive witchfire.",
            MagicSchoolDefinitions.WITCHERY.id(),
            MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY,
            13,
            3
        ),
        new SpellSpec(
            "transmute_fuel",
            "Transmute Fuel",
            "Convert materials into usable energy.",
            MagicSchoolDefinitions.ALCHEMIST.id(),
            MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY,
            9,
            2
        )
    );
    private static final List<Spell> ALL = buildSpells();
    private static final Map<String, List<Spell>> BY_SCHOOL = buildBySchool();
    private static final Map<SchoolSubsectionKey, List<Spell>> BY_SCHOOL_SUBSECTION = buildBySchoolAndSubsection();

    private SpellDefinitions() {
    }

    /**
     * @return immutable list of all spells
     */
    public static List<Spell> getAll() {
        return ALL;
    }

    /**
     * @return spells in the given school, or empty when unknown
     */
    public static List<Spell> getBySchoolId(String schoolId) {
        if (schoolId == null) {
            return List.of();
        }
        return BY_SCHOOL.getOrDefault(schoolId, List.of());
    }

    /**
     * @return spells for a school and subsection, or empty when unknown
     */
    public static List<Spell> getBySchoolAndSubsection(String schoolId, String subsectionKey) {
        if (schoolId == null) {
            return List.of();
        }
        if (subsectionKey == null) {
            return getBySchoolId(schoolId);
        }
        return BY_SCHOOL_SUBSECTION.getOrDefault(new SchoolSubsectionKey(schoolId, subsectionKey), List.of());
    }

    private static List<Spell> buildSpells() {
        List<Spell> spells = new ArrayList<>();
        for (SpellSpec spec : SPELL_SPECS) {
            spells.add(createSpell(spec));
        }
        return List.copyOf(spells);
    }

    private static Map<String, List<Spell>> buildBySchool() {
        Map<String, List<Spell>> map = new HashMap<>();
        for (Spell spell : ALL) {
            map.computeIfAbsent(spell.schoolId(), key -> new ArrayList<>()).add(spell);
        }
        Map<String, List<Spell>> output = new HashMap<>();
        for (Map.Entry<String, List<Spell>> entry : map.entrySet()) {
            output.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Map.copyOf(output);
    }

    private static Map<SchoolSubsectionKey, List<Spell>> buildBySchoolAndSubsection() {
        Map<SchoolSubsectionKey, List<Spell>> map = new HashMap<>();
        for (Spell spell : ALL) {
            SchoolSubsectionKey key = new SchoolSubsectionKey(spell.schoolId(), spell.subsectionKey());
            map.computeIfAbsent(key, id -> new ArrayList<>()).add(spell);
        }
        Map<SchoolSubsectionKey, List<Spell>> output = new HashMap<>();
        for (Map.Entry<SchoolSubsectionKey, List<Spell>> entry : map.entrySet()) {
            output.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Map.copyOf(output);
    }

    private static Spell createSpell(SpellSpec spec) {
        ResourceLocation key = ResourceLocation.parse(Believable.MODID + ":" + spec.id());
        return new BasicSpell(
            key,
            spec.name(),
            spec.description(),
            spec.schoolId(),
            spec.subsectionKey(),
            spec.baseManaCost(),
            spec.complexity()
        );
    }

    private record SpellSpec(String id,
                             String name,
                             String description,
                             String schoolId,
                             String subsectionKey,
                             int baseManaCost,
                             int complexity) {
    }

    private record SchoolSubsectionKey(String schoolId, String subsectionKey) {
    }
}
