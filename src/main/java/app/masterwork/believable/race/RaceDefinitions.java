package app.masterwork.believable.race;

import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatModifier;

import java.util.List;
import java.util.Map;

public final class RaceDefinitions {
    public static final String TAB_GOAT_KEY = "screen.believable.unbelievable_menu.race.goat";
    public static final String TAB_CAT_KEY = "screen.believable.unbelievable_menu.race.cat";
    public static final String TAB_FENNEC_KEY = "screen.believable.unbelievable_menu.race.fennec";
    public static final String TAB_CANID_KEY = "screen.believable.unbelievable_menu.race.canid";
    public static final String TAB_PROTOGEN_KEY = "screen.believable.unbelievable_menu.race.protogen";

    public static final BasicRace GOAT = new BasicRace(
        "Goat",
        "Sturdy, with a touch of natural regeneration.",
        20,
        List.of(
            new StatModifier(StatDefinitions.MAX_HEALTH.getId(), 4.0D),
            new StatModifier(StatDefinitions.HP_REGENERATION.getId(), 0.1D),
            new StatModifier(StatDefinitions.STEP_HEIGHT.getId(), 0.4D)
        ),
        List.of(GoatRegenModifier.INSTANCE, GoatStepUpModifier.INSTANCE)
    );

    public static final BasicRace CAT = new BasicRace(
        "Cat",
        "Graceful and sure-footed.",
        20,
        List.of(
            new StatModifier(StatDefinitions.ATTACK_SPEED.getId(), 0.2D),
            new StatModifier(StatDefinitions.BLOCK_BREAK_SPEED.getId(), 0.1D),
            new StatModifier(StatDefinitions.FALL_DAMAGE_MULTIPLIER.getId(), -0.1D)
        ),
        List.of(CatGraceModifier.INSTANCE, CatProwlModifier.INSTANCE)
    );

    public static final BasicRace FENNEC = new BasicRace(
        "Fennec",
        "Small, nimble, and always on the move.",
        18,
        List.of(
            new StatModifier(StatDefinitions.ATTACK_SPEED.getId(), 0.25D),
            new StatModifier(StatDefinitions.JUMP_STRENGTH.getId(), 0.06D),
            new StatModifier(StatDefinitions.FALL_DAMAGE_MULTIPLIER.getId(), -0.1D),
            new StatModifier(StatDefinitions.ARMOR.getId(), -0.5D)
        ),
        List.of()
    );

    public static final BasicRace CANID = new BasicRace(
        "Canid",
        "Pack-minded hunter with a vicious bite.",
        20,
        List.of(
            new StatModifier(StatDefinitions.ATTACK_DAMAGE.getId(), 0.75D),
            new StatModifier(StatDefinitions.ATTACK_SPEED.getId(), 0.1D),
            new StatModifier(StatDefinitions.ARMOR.getId(), -0.5D)
        ),
        List.of()
    );

    public static final BasicRace PROTOGEN = new BasicRace(
        "Protogen",
        "Synthetic fur and plating built for utility.",
        20,
        List.of(
            new StatModifier(StatDefinitions.SMITHING.getId(), 1.5D),
            new StatModifier(StatDefinitions.ARMOR.getId(), 1.0D),
            new StatModifier(StatDefinitions.BLOCK_BREAK_SPEED.getId(), 0.1D),
            new StatModifier(StatDefinitions.ATTACK_SPEED.getId(), -0.1D)
        ),
        List.of()
    );

    private static final Map<String, BasicRace> BY_TAB_KEY = Map.of(
        TAB_GOAT_KEY, GOAT,
        TAB_CAT_KEY, CAT,
        TAB_FENNEC_KEY, FENNEC,
        TAB_CANID_KEY, CANID,
        TAB_PROTOGEN_KEY, PROTOGEN
    );

    private static final Map<String, String> TAB_KEY_BY_NAME = Map.of(
        GOAT.name(), TAB_GOAT_KEY,
        CAT.name(), TAB_CAT_KEY,
        FENNEC.name(), TAB_FENNEC_KEY,
        CANID.name(), TAB_CANID_KEY,
        PROTOGEN.name(), TAB_PROTOGEN_KEY
    );

    private RaceDefinitions() {
    }

    public static String getTabKey(BasicRace race) {
        if (race == null) {
            return null;
        }
        return TAB_KEY_BY_NAME.get(race.name());
    }

    public static BasicRace getByTabKey(String key) {
        return BY_TAB_KEY.get(key);
    }
}
