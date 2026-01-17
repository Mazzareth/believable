package app.masterwork.believable.classes.cartographer;

import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatModifier;

import java.util.List;

public final class CartographerClass {
    public static final BasicClass DEFINITION = new BasicClass(
        "Cartographer",
        "Become unrivaled in cartography. Create and share maps, locate biomes and structures, and guide expeditions.",
        List.of(
            new StatModifier(StatDefinitions.BLOCK_BREAK_SPEED.getId(), 0.2D),
            new StatModifier(StatDefinitions.STEP_HEIGHT.getId(), 0.3D),
            new StatModifier(StatDefinitions.FALL_DAMAGE_MULTIPLIER.getId(), -0.25D)
        ),
        List.of(CartographerPathfinderModifier.INSTANCE)
    );

    private CartographerClass() {
    }
}
