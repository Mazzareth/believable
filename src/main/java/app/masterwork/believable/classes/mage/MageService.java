package app.masterwork.believable.classes.mage;

import app.masterwork.believable.classes.ClassFeatureGate;
import net.minecraft.world.entity.player.Player;

/**
 * Mage-only feature entry points.
 */
public final class MageService {
    private MageService() {
    }

    public static boolean canUse(Player player) {
        return ClassFeatureGate.isClass(player, MageClass.DEFINITION);
    }
}
