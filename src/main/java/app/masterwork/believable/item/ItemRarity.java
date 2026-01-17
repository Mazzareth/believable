package app.masterwork.believable.item;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;

import java.util.Locale;

/**
 * Rarity metadata for items and affix rolls.
 */
public enum ItemRarity {
    COMMON("Common", ChatFormatting.GRAY, 50, 1, 1, 1.0D),
    UNCOMMON("Uncommon", ChatFormatting.GREEN, 30, 1, 2, 1.2D),
    RARE("Rare", ChatFormatting.BLUE, 15, 2, 3, 1.5D),
    LEGENDARY("Legendary", ChatFormatting.GOLD, 4, 3, 4, 2.0D),
    UNIQUE("Unique", ChatFormatting.LIGHT_PURPLE, 1, 4, 5, 2.5D);

    private final String displayName;
    private final ChatFormatting color;
    private final int weight;
    private final int minAffixes;
    private final int maxAffixes;
    private final double statMultiplier;

    public static final Codec<ItemRarity> CODEC = Codec.STRING.xmap(ItemRarity::fromId, ItemRarity::getId);

    ItemRarity(
        String displayName,
        ChatFormatting color,
        int weight,
        int minAffixes,
        int maxAffixes,
        double statMultiplier
    ) {
        this.displayName = displayName;
        this.color = color;
        this.weight = weight;
        this.minAffixes = minAffixes;
        this.maxAffixes = maxAffixes;
        this.statMultiplier = statMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public int getMinAffixes() {
        return minAffixes;
    }

    public int getMaxAffixes() {
        return maxAffixes;
    }

    public double getStatMultiplier() {
        return statMultiplier;
    }

    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Resolves a rarity by string id, defaulting to common.
     */
    public static ItemRarity fromId(String id) {
        if (id == null || id.isBlank()) {
            return COMMON;
        }
        for (ItemRarity rarity : values()) {
            if (rarity.getId().equals(id)) {
                return rarity;
            }
        }
        return COMMON;
    }

    /**
     * Rolls a rarity using level-based weighting.
     */
    public static ItemRarity roll(RandomSource random, int level) {
        int bonus = Math.max(0, level / 10);
        int commonWeight = COMMON.weight;
        int uncommonWeight = UNCOMMON.weight + bonus;
        int rareWeight = RARE.weight + bonus * 2;
        int legendaryWeight = LEGENDARY.weight + Math.max(0, bonus - 1);
        int uniqueWeight = UNIQUE.weight + Math.max(0, bonus - 2);
        int total = commonWeight + uncommonWeight + rareWeight + legendaryWeight + uniqueWeight;
        int roll = random.nextInt(total);
        if ((roll -= commonWeight) < 0) {
            return COMMON;
        }
        if ((roll -= uncommonWeight) < 0) {
            return UNCOMMON;
        }
        if ((roll -= rareWeight) < 0) {
            return RARE;
        }
        if ((roll -= legendaryWeight) < 0) {
            return LEGENDARY;
        }
        return UNIQUE;
    }
}
