package app.masterwork.believable.classes.prospector;

import app.masterwork.believable.classes.ClassFeatureGate;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.stats.PlayerProgressionStorage;
import net.minecraft.world.entity.player.Player;

/**
 * Prospector-only feature entry points.
 */
public final class ProspectorService {
    private static final int BASE_RANGE = 8;
    private static final int RANGE_PER_LEVEL = 1;
    private static final int MAX_RANGE = 24;

    private ProspectorService() {
    }

    public static boolean canUse(Player player) {
        return ClassFeatureGate.isClass(player, ProspectorClass.DEFINITION);
    }

    public static int getVisionRange(Player player) {
        if (player == null) {
            return 0;
        }
        PlayerProgressionStorage progression = player.getData(ModAttachments.PLAYER_PROGRESSION);
        int level = progression != null ? progression.getLevel() : 1;
        int scaled = BASE_RANGE + Math.max(0, level - 1) * RANGE_PER_LEVEL;
        return Math.min(MAX_RANGE, scaled);
    }
}
