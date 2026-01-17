package app.masterwork.believable.stats;

import app.masterwork.believable.Believable;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class CharacterStatDefinitions {
    public static final CharacterStat CONSTITUTION = register(
        "constitution",
        "Constitution",
        "Hardiness that increases your maximum health.",
        List.of(effect(StatDefinitions.MAX_HEALTH, 1.0D))
    );

    public static final CharacterStat VITALITY = register(
        "vitality",
        "Vitality",
        "Life force that fuels health regeneration.",
        List.of(effect(StatDefinitions.HP_REGENERATION, 0.05D))
    );

    public static final CharacterStat FORTITUDE = register(
        "fortitude",
        "Fortitude",
        "Defensive resilience against incoming damage.",
        List.of(
            effect(StatDefinitions.ARMOR, 0.25D),
            effect(StatDefinitions.ARMOR_TOUGHNESS, 0.25D)
        )
    );

    public static final CharacterStat STRENGTH = register(
        "strength",
        "Strength",
        "Raw power that boosts melee damage and mining.",
        List.of(
            effect(StatDefinitions.ATTACK_DAMAGE, 1.0D),
            effect(StatDefinitions.BLOCK_BREAK_SPEED, 0.02D)
        )
    );

    public static final CharacterStat DEXTERITY = register(
        "dexterity",
        "Dexterity",
        "Precision and quickness that improve attack speed and step-up height.",
        List.of(
            effect(StatDefinitions.ATTACK_SPEED, 0.05D),
            effect(StatDefinitions.STEP_HEIGHT, 0.03D)
        )
    );

    public static final CharacterStat AGILITY = register(
        "agility",
        "Agility",
        "Balance and athleticism that aid jumping and reduce fall impact.",
        List.of(
            effect(StatDefinitions.JUMP_STRENGTH, 0.02D),
            effect(StatDefinitions.FALL_DAMAGE_MULTIPLIER, -0.02D)
        )
    );

    public static final CharacterStat ATTUNEMENT = register(
        "attunement",
        "Attunement",
        "Connection to the arcane that increases your mana capacity.",
        List.of(
            effect(StatDefinitions.MAX_MANA, 5.0D)
        )
    );

    public static final CharacterStat INTELLIGENCE = register(
        "intelligence",
        "Intelligence",
        "Arcane understanding that amplifies spell power.",
        List.of(effect(StatDefinitions.SPELL_POWER, 0.1D))
    );

    public static final CharacterStat WISDOM = register(
        "wisdom",
        "Wisdom",
        "Discipline that governs mana regeneration.",
        List.of(effect(StatDefinitions.MANA_REGENERATION, 0.05D))
    );

    public static final CharacterStat INSIGHT = register(
        "insight",
        "Insight",
        "Mental clarity that expands spell complexity limits.",
        List.of(effect(StatDefinitions.MAX_COMPLEXITY, 1.0D))
    );

    private CharacterStatDefinitions() {
    }

    public static void init() {
        // Ensures static registration runs.
    }

    private static CharacterStat register(String idPath, String name, String description, List<CharacterStatEffect> effects) {
        CharacterStat stat = new CharacterStat(
            ResourceLocation.parse(Believable.MODID + ":" + idPath),
            name,
            description,
            1.0D,
            effects
        );
        CharacterStatRegistry.register(stat);
        return stat;
    }

    private static CharacterStatEffect effect(Stat stat, double perLevel) {
        return new CharacterStatEffect(stat.getId(), perLevel);
    }
}
