package app.masterwork.believable.stats;

public final class LevelingRules {
    public static final int BASE_XP_TO_LEVEL = 100;
    public static final int XP_PER_LEVEL = 50;
    public static final int SKILL_POINTS_PER_LEVEL = 1;
    public static final int ORE_MINING_XP = 6;
    public static final int BIOME_DISCOVERY_XP = 20;
    public static final int VEIN_MINER_BASE_XP_TO_LEVEL = 12;
    public static final int VEIN_MINER_XP_PER_LEVEL = 6;
    public static final int VEIN_MINER_BLOCK_XP = 1;
    public static final int SMITHING_BASE_XP_TO_LEVEL = 20;
    public static final int SMITHING_XP_PER_LEVEL = 10;

    private LevelingRules() {
    }

    public static int getXpForNextLevel(int level) {
        int clampedLevel = Math.max(1, level);
        return BASE_XP_TO_LEVEL + (clampedLevel - 1) * XP_PER_LEVEL;
    }

    public static int getSkillPointsForLevel(int level) {
        return SKILL_POINTS_PER_LEVEL;
    }

    public static int getVeinMinerXpForNextLevel(int level) {
        int clampedLevel = Math.max(1, level);
        return VEIN_MINER_BASE_XP_TO_LEVEL + (clampedLevel - 1) * VEIN_MINER_XP_PER_LEVEL;
    }

    public static int getSmithingXpForNextLevel(int level) {
        int clampedLevel = Math.max(1, level);
        return SMITHING_BASE_XP_TO_LEVEL + (clampedLevel - 1) * SMITHING_XP_PER_LEVEL;
    }
}
