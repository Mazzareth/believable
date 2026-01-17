package app.masterwork.believable.classes.alchemist;

import app.masterwork.believable.classes.ClassFeatureGate;
import net.minecraft.world.entity.player.Player;

/**
 * Alchemist-only feature entry points.
 */
public final class AlchemistService {
    private AlchemistService() {
    }

    public static boolean canUse(Player player) {
        return ClassFeatureGate.isClass(player, AlchemistClass.DEFINITION);
    }
}
