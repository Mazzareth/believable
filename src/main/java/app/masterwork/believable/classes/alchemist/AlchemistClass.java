package app.masterwork.believable.classes.alchemist;

import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatModifier;

import java.util.List;

public final class AlchemistClass {
    public static final BasicClass DEFINITION = new BasicClass(
        "Alchemist",
        "Dedicate your life to learning the law of equivalent exchange. Craft circles of alchemy to transmute perfect materials, make potions which alter the bodies of those who consume them.",
        List.of(
            new StatModifier(StatDefinitions.HP_REGENERATION.getId(), 0.4D),
            new StatModifier(StatDefinitions.MANA_REGENERATION.getId(), 0.6D),
            new StatModifier(StatDefinitions.MAX_MANA.getId(), 5.0D)
        ),
        List.of(AlchemistTransmutationModifier.INSTANCE)
    );

    private AlchemistClass() {
    }
}
