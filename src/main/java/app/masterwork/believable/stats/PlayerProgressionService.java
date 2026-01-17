package app.masterwork.believable.stats;

import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerProgressionService {
    private PlayerProgressionService() {
    }

    public static void awardXp(ServerPlayer player, int baseXp, XpSource source) {
        if (baseXp <= 0) {
            return;
        }
        if (source == null) {
            return;
        }
        PlayerProgressionStorage progression = player.getData(ModAttachments.PLAYER_PROGRESSION);
        if (progression == null) {
            return;
        }
        int gainedXp = applyXpMultiplier(progression, baseXp);
        if (gainedXp <= 0) {
            return;
        }
        int xp = progression.getXp() + gainedXp;
        int level = progression.getLevel();
        int skillPoints = progression.getSkillPoints();
        int xpToNext = LevelingRules.getXpForNextLevel(level);
        while (xp >= xpToNext) {
            xp -= xpToNext;
            level += 1;
            skillPoints += LevelingRules.getSkillPointsForLevel(level);
            xpToNext = LevelingRules.getXpForNextLevel(level);
        }
        progression.setXp(xp);
        progression.setLevel(level);
        progression.setSkillPoints(skillPoints);
        player.syncData(ModAttachments.PLAYER_PROGRESSION);
    }

    private static int applyXpMultiplier(PlayerProgressionStorage progression, int baseXp) {
        double multiplier = progression.getXpGain();
        if (multiplier <= 0.0D) {
            return 0;
        }
        int gained = (int) Math.round(baseXp * multiplier);
        return Math.max(1, gained);
    }
}
