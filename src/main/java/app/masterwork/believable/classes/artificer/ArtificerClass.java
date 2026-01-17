package app.masterwork.believable.classes.artificer;

import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatModifier;

import java.util.List;

public final class ArtificerClass {
    public static final BasicClass DEFINITION = new BasicClass(
        "Artificer",
        "Enhance tools with magic, allowing common folk to use simple magics or manipulate magic imbued in items to your will.",
        List.of(
            new StatModifier(StatDefinitions.MAX_COMPLEXITY.getId(), 6.0D),
            new StatModifier(StatDefinitions.SPELL_POWER.getId(), 1.5D),
            new StatModifier(StatDefinitions.MANA_REGENERATION.getId(), 0.5D)
        ),
        List.of(ArtificerSalvageModifier.INSTANCE)
    );

    private ArtificerClass() {
    }
}
