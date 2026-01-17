package app.masterwork.believable.stats;

import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;

public final class VeinMinerProgressionService {
    private VeinMinerProgressionService() {
    }

    public static void awardXp(ServerPlayer player, int baseXp) {
        if (baseXp <= 0) {
            return;
        }
        PlayerVeinMinerStorage storage = player.getData(ModAttachments.PLAYER_VEIN_MINER);
        if (storage == null) {
            return;
        }
        int xp = storage.getXp() + baseXp;
        int level = storage.getLevel();
        int xpToNext = LevelingRules.getVeinMinerXpForNextLevel(level);
        while (xp >= xpToNext) {
            xp -= xpToNext;
            level += 1;
            xpToNext = LevelingRules.getVeinMinerXpForNextLevel(level);
        }
        storage.setXp(xp);
        storage.setLevel(level);
        player.syncData(ModAttachments.PLAYER_VEIN_MINER);
    }
}
