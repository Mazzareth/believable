package app.masterwork.believable.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Builds weighted gear drops and rolls affixes based on player level.
 */
public final class GearDropService {
    private static final EnumMap<DropTier, EnumMap<ItemCategory, List<Item>>> POOLS =
        new EnumMap<>(DropTier.class);
    private static final Map<String, DropTier> ARMOR_PREFIX_TIERS = Map.of(
        "leather_", DropTier.WOOD,
        "chainmail_", DropTier.STONE,
        "golden_", DropTier.STONE,
        "iron_", DropTier.IRON,
        "diamond_", DropTier.DIAMOND,
        "netherite_", DropTier.NETHERITE
    );
    private static final Map<String, DropTier> TOOL_PREFIX_TIERS = Map.of(
        "wooden_", DropTier.WOOD,
        "stone_", DropTier.STONE,
        "golden_", DropTier.STONE,
        "iron_", DropTier.IRON,
        "diamond_", DropTier.DIAMOND,
        "netherite_", DropTier.NETHERITE
    );

    private GearDropService() {
    }

    /**
     * Creates a randomized gear drop for a kill source and level.
     */
    public static ItemStack createRandomDrop(LivingEntity source, int level) {
        ensureCachedLists();
        ItemCategory category = rollCategory(source.getRandom());
        DropTier tier = rollTier(source.getRandom(), category, level);
        if (tier == null) {
            return ItemStack.EMPTY;
        }
        List<Item> pool = getPool(tier, category);
        Item item = pool.get(source.getRandom().nextInt(pool.size()));
        ItemStack stack = new ItemStack(item);
        ItemAffixService.rollAffixes(
            stack,
            category,
            source.getRandom(),
            level,
            ItemAffixService.AffixContext.fromKill(source)
        );
        return stack;
    }

    private static void ensureCachedLists() {
        if (!POOLS.isEmpty()) {
            return;
        }
        for (DropTier tier : DropTier.values()) {
            EnumMap<ItemCategory, List<Item>> tierPools = new EnumMap<>(ItemCategory.class);
            for (ItemCategory category : ItemCategory.values()) {
                tierPools.put(category, new ArrayList<>());
            }
            POOLS.put(tier, tierPools);
        }
        for (Item item : BuiltInRegistries.ITEM) {
            ItemCategory category = resolveCategory(item);
            if (category == null) {
                continue;
            }
            DropTier tier = resolveTier(item, category);
            if (tier == null) {
                continue;
            }
            POOLS.get(tier).get(category).add(item);
        }
    }

    private static ItemCategory rollCategory(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 34) {
            return ItemCategory.WEAPON;
        }
        if (roll < 67) {
            return ItemCategory.TOOL;
        }
        return ItemCategory.ARMOR;
    }

    private static DropTier rollTier(RandomSource random, ItemCategory category, int level) {
        DropTier[] tiers = DropTier.values();
        int[] weights = new int[tiers.length];
        int total = 0;
        for (int i = 0; i < tiers.length; i++) {
            DropTier tier = tiers[i];
            int weight = tier.getWeight(level);
            if (weight <= 0) {
                continue;
            }
            if (getPool(tier, category).isEmpty()) {
                continue;
            }
            weights[i] = weight;
            total += weight;
        }
        if (total <= 0) {
            for (int i = tiers.length - 1; i >= 0; i--) {
                DropTier tier = tiers[i];
                if (!getPool(tier, category).isEmpty()) {
                    return tier;
                }
            }
            return null;
        }
        int roll = random.nextInt(total);
        for (int i = 0; i < tiers.length; i++) {
            int weight = weights[i];
            if (weight <= 0) {
                continue;
            }
            if (roll < weight) {
                return tiers[i];
            }
            roll -= weight;
        }
        return null;
    }

    private static List<Item> getPool(DropTier tier, ItemCategory category) {
        return POOLS.get(tier).get(category);
    }

    private static ItemCategory resolveCategory(Item item) {
        if (item.builtInRegistryHolder().is(ItemTags.SWORDS) || item.builtInRegistryHolder().is(ItemTags.AXES)) {
            return ItemCategory.WEAPON;
        }
        if (item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem) {
            return ItemCategory.WEAPON;
        }
        if (item.builtInRegistryHolder().is(ItemTags.PICKAXES)
            || item.builtInRegistryHolder().is(ItemTags.SHOVELS)
            || item.builtInRegistryHolder().is(ItemTags.HOES)) {
            return ItemCategory.TOOL;
        }
        if (item.builtInRegistryHolder().is(ItemTags.HEAD_ARMOR)
            || item.builtInRegistryHolder().is(ItemTags.CHEST_ARMOR)
            || item.builtInRegistryHolder().is(ItemTags.LEG_ARMOR)
            || item.builtInRegistryHolder().is(ItemTags.FOOT_ARMOR)) {
            return ItemCategory.ARMOR;
        }
        return null;
    }

    private static DropTier resolveTier(Item item, ItemCategory category) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        if (key == null) {
            return null;
        }
        String path = key.getPath();
        DropTier directTier = resolveDirectTier(path);
        if (directTier != null) {
            return directTier;
        }
        if (category == ItemCategory.ARMOR) {
            return resolveTierByPrefix(path, ARMOR_PREFIX_TIERS);
        }
        return resolveTierByPrefix(path, TOOL_PREFIX_TIERS);
    }

    private static DropTier resolveDirectTier(String path) {
        return switch (path) {
            case "bow" -> DropTier.WOOD;
            case "crossbow" -> DropTier.IRON;
            case "trident" -> DropTier.DIAMOND;
            default -> null;
        };
    }

    private static DropTier resolveTierByPrefix(String path, Map<String, DropTier> tiers) {
        for (Map.Entry<String, DropTier> entry : tiers.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private enum DropTier {
        WOOD(1, 10),
        STONE(5, 20),
        IRON(10, 30),
        DIAMOND(20, 40),
        NETHERITE(35, 50);

        private final int minLevel;
        private final int peakLevel;

        DropTier(int minLevel, int peakLevel) {
            this.minLevel = minLevel;
            this.peakLevel = peakLevel;
        }

        int getWeight(int level) {
            if (level < minLevel) {
                return 0;
            }
            int rise = Math.max(1, peakLevel - minLevel);
            if (level <= peakLevel) {
                return level - minLevel + 1;
            }
            int maxLevel = peakLevel + rise;
            int weight = maxLevel - level + 1;
            return Math.max(0, weight);
        }
    }
}
