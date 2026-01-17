package app.masterwork.believable.classes.warden;

import app.masterwork.believable.classes.ClassFeatureGate;
import net.minecraft.world.entity.player.Player;

/**
 * Warden-only feature entry points.
 */
public final class WardenService {
    private WardenService() {
    }

    public static boolean canUse(Player player) {
        return ClassFeatureGate.isClass(player, WardenClass.DEFINITION);
    }
}
