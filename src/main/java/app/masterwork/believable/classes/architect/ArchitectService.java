package app.masterwork.believable.classes.architect;

import app.masterwork.believable.classes.ClassFeatureGate;
import net.minecraft.world.entity.player.Player;

/**
 * Architect-only feature entry points.
 */
public final class ArchitectService {
    private ArchitectService() {
    }

    public static boolean canUse(Player player) {
        return ClassFeatureGate.isClass(player, ArchitectClass.DEFINITION);
    }
}
