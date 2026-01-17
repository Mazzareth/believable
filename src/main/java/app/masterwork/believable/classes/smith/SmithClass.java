package app.masterwork.believable.classes.smith;

import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatModifier;

import java.util.List;

public final class SmithClass {
    public static final BasicClass DEFINITION = new BasicClass(
        "Smith",
        "Dedicate your life to design and creating \"perfect\" high level gear with any resource.",
        List.of(
            new StatModifier(StatDefinitions.SMITHING.getId(), 3.0D),
            new StatModifier(StatDefinitions.ARMOR.getId(), 1.0D)
        ),
        List.of(SmithMasterworkModifier.INSTANCE)
    );

    private SmithClass() {
    }
}
