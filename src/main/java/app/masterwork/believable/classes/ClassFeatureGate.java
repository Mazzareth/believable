package app.masterwork.believable.classes;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.world.entity.player.Player;

/**
 * Centralized helpers for class-locked feature checks.
 */
public final class ClassFeatureGate {
    private ClassFeatureGate() {
    }

    public static boolean isClass(Player player, BasicClass expected) {
        if (player == null || expected == null) {
            return false;
        }
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return false;
        }
        BasicClass selected = storage.getPlayerClass();
        return selected != null && expected.name().equals(selected.name());
    }
}
