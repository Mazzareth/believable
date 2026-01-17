package app.masterwork.believable.stats;

import app.masterwork.believable.item.ItemRarity;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public final class SmithingProgressionService {
    private SmithingProgressionService() {
    }

    public static int getSmithingLevel(ServerPlayer player) {
        PlayerSmithingStorage storage = player.getData(ModAttachments.PLAYER_SMITHING);
        return storage == null ? 1 : storage.getLevel();
    }

    public static void awardCraftXp(ServerPlayer player, ItemStack result, Container inventory) {
        int xp = calculateCraftXp(result, inventory);
        awardXp(player, xp);
    }

    public static void ensureSmithingStat(ServerPlayer player) {
        PlayerSmithingStorage storage = player.getData(ModAttachments.PLAYER_SMITHING);
        if (storage == null) {
            return;
        }
        PlayerStatStorage statStorage = player.getData(ModAttachments.PLAYER_STATS);
        if (statStorage == null) {
            return;
        }
        int level = storage.getLevel();
        double current = statStorage.getValue(StatDefinitions.SMITHING, player);
        if (Double.compare(current, level) != 0) {
            statStorage.setValue(StatDefinitions.SMITHING, level);
            player.syncData(ModAttachments.PLAYER_STATS);
        }
    }

    public static void awardSalvageXp(ServerPlayer player, ItemStack stack, ItemRarity rarity) {
        if (stack == null || stack.isEmpty() || rarity == null) {
            return;
        }
        int baseTier = getTierValue(stack);
        int rarityMultiplier = switch (rarity) {
            case COMMON -> 1;
            case UNCOMMON -> 2;
            case RARE -> 4;
            case LEGENDARY -> 7;
            case UNIQUE -> 10;
        };
        int xp = Math.max(1, (baseTier * rarityMultiplier) / 2);
        awardXp(player, xp);
    }

    private static void awardXp(ServerPlayer player, int baseXp) {
        if (baseXp <= 0) {
            return;
        }
        PlayerSmithingStorage storage = player.getData(ModAttachments.PLAYER_SMITHING);
        if (storage == null) {
            return;
        }
        int xp = storage.getXp() + baseXp;
        int level = storage.getLevel();
        int xpToNext = LevelingRules.getSmithingXpForNextLevel(level);
        while (xp >= xpToNext) {
            xp -= xpToNext;
            level += 1;
            xpToNext = LevelingRules.getSmithingXpForNextLevel(level);
        }
        storage.setXp(xp);
        storage.setLevel(level);
        player.syncData(ModAttachments.PLAYER_SMITHING);
        syncSmithingStat(player, level);
    }

    private static void syncSmithingStat(ServerPlayer player, int level) {
        PlayerStatStorage statStorage = player.getData(ModAttachments.PLAYER_STATS);
        if (statStorage == null) {
            return;
        }
        double current = statStorage.getValue(StatDefinitions.SMITHING, player);
        if (Double.compare(current, level) != 0) {
            statStorage.setValue(StatDefinitions.SMITHING, level);
            player.syncData(ModAttachments.PLAYER_STATS);
        }
    }

    private static int calculateCraftXp(ItemStack result, Container inventory) {
        if (result == null || result.isEmpty()) {
            return 0;
        }
        int ingredientCount = 0;
        if (inventory != null) {
            int size = inventory.getContainerSize();
            for (int i = 0; i < size; i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    ingredientCount += Math.max(1, stack.getCount());
                }
            }
        }
        ingredientCount = Math.max(1, ingredientCount);
        int tierValue = getTierValue(result);
        int outputCount = Math.max(1, result.getCount());
        int xp = (ingredientCount * tierValue) / outputCount;
        return Math.max(1, xp);
    }

    private static int getTierValue(ItemStack result) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(result.getItem());
        if (key == null) {
            return 1;
        }
        String path = key.getPath();
        if (path.startsWith("netherite_")) {
            return 10;
        }
        if (path.startsWith("diamond_")) {
            return 8;
        }
        if (path.startsWith("iron_")) {
            return 5;
        }
        if (path.startsWith("golden_") || path.startsWith("chainmail_")) {
            return 4;
        }
        if (path.startsWith("stone_")) {
            return 3;
        }
        if (path.startsWith("wooden_") || path.startsWith("leather_")) {
            return 2;
        }
        return 1;
    }
}
