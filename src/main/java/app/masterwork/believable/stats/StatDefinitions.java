package app.masterwork.believable.stats;

import app.masterwork.believable.Believable;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StatDefinitions {
    private static final List<AttributeSpec> ATTRIBUTE_SPECS = List.of(
        new AttributeSpec(
            "max_health",
            "Max Health",
            "Controls how much damage you can take before dying.",
            "minecraft:generic.max_health",
            1.0D,
            1024.0D,
            20.0D
        ),
        new AttributeSpec(
            "armor",
            "Armor",
            "Reduces incoming damage.",
            "minecraft:generic.armor",
            0.0D,
            2048.0D,
            0.0D
        ),
        new AttributeSpec(
            "armor_toughness",
            "Armor Toughness",
            "Improves armor effectiveness against stronger hits.",
            "minecraft:generic.armor_toughness",
            0.0D,
            2048.0D,
            0.0D
        ),
        new AttributeSpec(
            "step_height",
            "Step Height",
            "How tall a step you can climb without jumping.",
            "minecraft:generic.step_height",
            0.0D,
            10.0D,
            0.6D
        ),
        new AttributeSpec(
            "attack_damage",
            "Attack Damage",
            "Base melee damage dealt on hit.",
            "minecraft:generic.attack_damage",
            0.0D,
            2048.0D,
            1.0D
        ),
        new AttributeSpec(
            "attack_speed",
            "Attack Speed",
            "How quickly you can attack.",
            "minecraft:generic.attack_speed",
            0.0D,
            2048.0D,
            4.0D
        ),
        new AttributeSpec(
            "block_break_speed",
            "Block Break Speed",
            "How quickly you mine blocks.",
            "minecraft:generic.block_break_speed",
            0.0D,
            2048.0D,
            1.0D
        ),
        new AttributeSpec(
            "fall_damage_multiplier",
            "Fall Damage Multiplier",
            "Scales fall damage taken.",
            "minecraft:generic.fall_damage_multiplier",
            0.0D,
            10.0D,
            1.0D
        ),
        new AttributeSpec(
            "jump_strength",
            "Jump Strength",
            "How high you can jump.",
            "minecraft:generic.jump_strength",
            0.0D,
            10.0D,
            0.42D
        )
    );

    private static final List<SimpleSpec> SIMPLE_SPECS = List.of(
        new SimpleSpec(
            "hp_regeneration",
            "HP Regeneration",
            "Restores health over time.",
            0.0D,
            1024.0D,
            0.0D
        ),
        new SimpleSpec(
            "max_mana",
            "Max Mana",
            "Your total mana capacity.",
            0.0D,
            1024.0D,
            0.0D
        ),
        new SimpleSpec(
            "current_mana",
            "Current Mana",
            "Your available mana right now.",
            0.0D,
            1024.0D,
            0.0D
        ),
        new SimpleSpec(
            "mana_regeneration",
            "Mana Regeneration",
            "How quickly your mana restores.",
            0.0D,
            1024.0D,
            0.0D
        ),
        new SimpleSpec(
            "spell_power",
            "Spell Power",
            "Amplifies spell effectiveness.",
            0.0D,
            1024.0D,
            0.0D
        ),
        new SimpleSpec(
            "max_complexity",
            "Max Complexity",
            "The most complex spells you can handle.",
            0.0D,
            1024.0D,
            0.0D
        ),
        new SimpleSpec(
            "smithing",
            "Smithing",
            "Crafting skill that improves the attributes on crafted gear.",
            1.0D,
            1000.0D,
            1.0D
        )
    );

    private static final Map<String, Stat> BY_PATH = new LinkedHashMap<>();

    static {
        for (AttributeSpec spec : ATTRIBUTE_SPECS) {
            BY_PATH.put(spec.idPath(), registerAttribute(spec));
        }
        for (SimpleSpec spec : SIMPLE_SPECS) {
            BY_PATH.put(spec.idPath(), registerSimple(spec));
        }
    }

    public static final AttributeStat MAX_HEALTH = getAttribute("max_health");
    public static final SimpleStat HP_REGENERATION = getSimple("hp_regeneration");
    public static final AttributeStat ARMOR = getAttribute("armor");
    public static final AttributeStat ARMOR_TOUGHNESS = getAttribute("armor_toughness");
    public static final AttributeStat STEP_HEIGHT = getAttribute("step_height");
    public static final AttributeStat ATTACK_DAMAGE = getAttribute("attack_damage");
    public static final AttributeStat ATTACK_SPEED = getAttribute("attack_speed");
    public static final AttributeStat BLOCK_BREAK_SPEED = getAttribute("block_break_speed");
    public static final AttributeStat FALL_DAMAGE_MULTIPLIER = getAttribute("fall_damage_multiplier");
    public static final AttributeStat JUMP_STRENGTH = getAttribute("jump_strength");
    public static final SimpleStat MAX_MANA = getSimple("max_mana");
    public static final SimpleStat CURRENT_MANA = getSimple("current_mana");
    public static final SimpleStat MANA_REGENERATION = getSimple("mana_regeneration");
    public static final SimpleStat SPELL_POWER = getSimple("spell_power");
    public static final SimpleStat MAX_COMPLEXITY = getSimple("max_complexity");
    public static final SimpleStat SMITHING = getSimple("smithing");

    private StatDefinitions() {
    }

    public static void init() {
        // Ensures static registration runs.
    }

    private static AttributeStat getAttribute(String idPath) {
        return (AttributeStat) BY_PATH.get(idPath);
    }

    private static SimpleStat getSimple(String idPath) {
        return (SimpleStat) BY_PATH.get(idPath);
    }

    private static AttributeStat registerAttribute(AttributeSpec spec) {
        AttributeStat stat = new AttributeStat(
            ResourceLocation.parse(Believable.MODID + ":" + spec.idPath()),
            spec.name(),
            spec.description(),
            ResourceLocation.parse(spec.attributeId()),
            spec.minValue(),
            spec.maxValue(),
            spec.fallbackDefault()
        );
        StatRegistry.register(stat);
        return stat;
    }

    private static SimpleStat registerSimple(SimpleSpec spec) {
        SimpleStat stat = new SimpleStat(
            ResourceLocation.parse(Believable.MODID + ":" + spec.idPath()),
            spec.name(),
            spec.description(),
            spec.minValue(),
            spec.maxValue(),
            spec.fallbackDefault()
        );
        StatRegistry.register(stat);
        return stat;
    }

    private record AttributeSpec(String idPath,
                                 String name,
                                 String description,
                                 String attributeId,
                                 double minValue,
                                 double maxValue,
                                 double fallbackDefault) {
    }

    private record SimpleSpec(String idPath,
                              String name,
                              String description,
                              double minValue,
                              double maxValue,
                              double fallbackDefault) {
    }
}
