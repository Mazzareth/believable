package app.masterwork.believable.classes.smith;

import app.masterwork.believable.classes.ClassFeatureGate;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.stats.CharacterStatDefinitions;
import app.masterwork.believable.stats.PlayerStatStorage;
import app.masterwork.believable.stats.StatValueCalculator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Smith-only feature entry points.
 */
public final class SmithService {
    private static final double DURABILITY_PER_STRENGTH = 0.02D;
    private static final double MAX_DURABILITY_BONUS = 0.75D;
    private static final double QUALITY_LEVELS_PER_DEXTERITY = 0.5D;
    private static final int MAX_QUALITY_BONUS = 10;
    private static final int SALVAGE_CRAFT_COST = 2;
    private static final int SALVAGE_ROLL_BONUS = 6;

    private SmithService() {
    }

    public static boolean canUse(Player player) {
        return ClassFeatureGate.isClass(player, SmithClass.DEFINITION);
    }

    public static double getDurabilityPerStrength() {
        return DURABILITY_PER_STRENGTH;
    }

    public static double getMaxDurabilityBonus() {
        return MAX_DURABILITY_BONUS;
    }

    public static double getQualityLevelsPerDexterity() {
        return QUALITY_LEVELS_PER_DEXTERITY;
    }

    public static int getMaxQualityBonus() {
        return MAX_QUALITY_BONUS;
    }

    public static int getSalvageCraftCost() {
        return SALVAGE_CRAFT_COST;
    }

    public static int getSalvageRollBonus() {
        return SALVAGE_ROLL_BONUS;
    }

    public static int getQualityRollLevel(Player player, int baseLevel) {
        PlayerStatStorage statStorage = player != null ? player.getData(ModAttachments.PLAYER_STATS) : null;
        return getQualityRollLevel(player, statStorage, baseLevel);
    }

    public static int getQualityRollLevel(Player player, PlayerStatStorage statStorage, int baseLevel) {
        if (player == null || !canUse(player)) {
            return baseLevel;
        }
        int bonus = getDexterityQualityBonus(statStorage, player);
        if (bonus <= 0) {
            return baseLevel;
        }
        return baseLevel + bonus;
    }

    public static int getDexterityQualityBonus(PlayerStatStorage statStorage, Player player) {
        double dexterity = StatValueCalculator.getCharacterStatLevel(
            CharacterStatDefinitions.DEXTERITY,
            statStorage,
            player
        );
        double base = CharacterStatDefinitions.DEXTERITY.defaultValue();
        double bonusLevels = Math.max(0.0D, dexterity - base);
        int bonus = (int) Math.floor(bonusLevels * QUALITY_LEVELS_PER_DEXTERITY);
        return Math.min(MAX_QUALITY_BONUS, Math.max(0, bonus));
    }

    public static double getStrengthDurabilityBonus(PlayerStatStorage statStorage, Player player) {
        double strength = StatValueCalculator.getCharacterStatLevel(
            CharacterStatDefinitions.STRENGTH,
            statStorage,
            player
        );
        double base = CharacterStatDefinitions.STRENGTH.defaultValue();
        double bonusLevels = Math.max(0.0D, strength - base);
        double bonus = bonusLevels * DURABILITY_PER_STRENGTH;
        return Math.min(MAX_DURABILITY_BONUS, Math.max(0.0D, bonus));
    }

    public static boolean applyDurabilityBonus(Player player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return false;
        }
        if (!canUse(player) || !stack.isDamageableItem()) {
            return false;
        }
        PlayerStatStorage statStorage = player.getData(ModAttachments.PLAYER_STATS);
        double bonus = getStrengthDurabilityBonus(statStorage, player);
        if (bonus <= 0.0D) {
            return false;
        }
        return applyDurabilityBonus(stack, bonus);
    }

    private static boolean applyDurabilityBonus(ItemStack stack, double bonusPercent) {
        int baseMax = stack.getMaxDamage();
        if (baseMax <= 0) {
            return false;
        }
        int boostedMax = (int) Math.floor(baseMax * (1.0D + bonusPercent));
        if (boostedMax <= baseMax) {
            return false;
        }
        int damage = stack.getDamageValue();
        int newDamage = damage;
        if (damage > 0) {
            newDamage = (int) Math.round((damage / (double) baseMax) * boostedMax);
            newDamage = Math.min(boostedMax - 1, Math.max(0, newDamage));
        }
        stack.set(DataComponents.MAX_DAMAGE, boostedMax);
        stack.setDamageValue(newDamage);
        return true;
    }

    public static boolean isSalvageInfusionEnabled(Player player) {
        if (player == null) {
            return false;
        }
        var storage = player.getData(ModAttachments.PLAYER_STORAGE);
        return storage != null && storage.isSmithingSalvageEnabled();
    }

    public static int consumeSalvageForCraft(Player player) {
        if (player == null || !canUse(player)) {
            return 0;
        }
        var storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null || !storage.isSmithingSalvageEnabled()) {
            return 0;
        }
        int salvage = storage.getSalvage();
        if (salvage < SALVAGE_CRAFT_COST) {
            return 0;
        }
        storage.setSalvage(salvage - SALVAGE_CRAFT_COST);
        player.syncData(ModAttachments.PLAYER_STORAGE);
        return SALVAGE_ROLL_BONUS;
    }
}
