package app.masterwork.believable.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles affix generation, persistence, and tooltip rendering for gear.
 */
public final class ItemAffixService {
    private static final String ROOT_TAG = "believable_affixes";
    private static final String RARITY_TAG = "rarity";
    private static final String CATEGORY_TAG = "category";
    private static final String AFFIXES_TAG = "affixes";
    private static final String AFFIX_ID_TAG = "id";
    private static final String AFFIX_VALUE_TAG = "value";
    private static final String AFFIX_UID_TAG = "uid";
    private static final int TOOLTIP_GOLD = 0xB79C52;
    private static final int TOOLTIP_GOLD_BRIGHT = 0xE6C97A;
    private static final int TOOLTIP_GOLD_DARK = 0x7A5A24;
    private static final int TOOLTIP_BLUE = 0xB7C6DC;
    private static final int TOOLTIP_BLUE_MUTED = 0x9FB3C8;
    private static final int TOOLTIP_STEEL = 0x8A919B;

    private static final List<AffixRoll> ROLLS = List.of(
        new ItemAffixDefinition(
            "attack_damage",
            "Attack Damage",
            Attributes.ATTACK_DAMAGE,
            4.0D,
            15.0D,
            EnumSet.of(ItemCategory.WEAPON),
            AffixGroup.PREFIX
        ),
        new ItemAffixDefinition(
            "attack_speed",
            "Attack Speed",
            Attributes.ATTACK_SPEED,
            0.1D,
            0.7D,
            EnumSet.of(ItemCategory.WEAPON),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "knockback",
            "Attack Knockback",
            Attributes.ATTACK_KNOCKBACK,
            0.1D,
            0.8D,
            EnumSet.of(ItemCategory.WEAPON),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "sweeping_ratio",
            "Sweeping Damage",
            Attributes.SWEEPING_DAMAGE_RATIO,
            0.05D,
            0.35D,
            EnumSet.of(ItemCategory.WEAPON),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "entity_interaction_range",
            "Melee Reach",
            Attributes.ENTITY_INTERACTION_RANGE,
            0.2D,
            1.0D,
            EnumSet.of(ItemCategory.WEAPON),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "weapon_luck",
            "Luck",
            Attributes.LUCK,
            0.5D,
            3.0D,
            EnumSet.of(ItemCategory.WEAPON),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "weapon_movement_speed",
            "Move Speed",
            Attributes.MOVEMENT_SPEED,
            0.005D,
            0.03D,
            EnumSet.of(ItemCategory.WEAPON),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "mining_efficiency",
            "Mining Efficiency",
            Attributes.MINING_EFFICIENCY,
            0.5D,
            3.0D,
            EnumSet.of(ItemCategory.TOOL),
            AffixGroup.PREFIX
        ),
        new ItemAffixDefinition(
            "break_speed",
            "Block Break Speed",
            Attributes.BLOCK_BREAK_SPEED,
            0.5D,
            3.0D,
            EnumSet.of(ItemCategory.TOOL),
            AffixGroup.PREFIX
        ),
        new ItemAffixDefinition(
            "block_interaction_range",
            "Block Reach",
            Attributes.BLOCK_INTERACTION_RANGE,
            0.2D,
            1.0D,
            EnumSet.of(ItemCategory.TOOL),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "submerged_mining_speed",
            "Underwater Mining",
            Attributes.SUBMERGED_MINING_SPEED,
            0.05D,
            0.25D,
            EnumSet.of(ItemCategory.TOOL),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "tool_luck",
            "Luck",
            Attributes.LUCK,
            0.4D,
            2.0D,
            EnumSet.of(ItemCategory.TOOL),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "armor",
            "Armor",
            Attributes.ARMOR,
            1.0D,
            4.0D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.PREFIX
        ),
        new ItemAffixDefinition(
            "toughness",
            "Armor Toughness",
            Attributes.ARMOR_TOUGHNESS,
            0.5D,
            2.0D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.PREFIX
        ),
        new ItemAffixDefinition(
            "max_health",
            "Max Health",
            Attributes.MAX_HEALTH,
            1.0D,
            5.0D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.PREFIX
        ),
        new ItemAffixDefinition(
            "knockback_resistance",
            "Knockback Resistance",
            Attributes.KNOCKBACK_RESISTANCE,
            0.02D,
            0.1D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "movement_speed",
            "Movement Speed",
            Attributes.MOVEMENT_SPEED,
            0.01D,
            0.05D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "safe_fall_distance",
            "Safe Fall Distance",
            Attributes.SAFE_FALL_DISTANCE,
            0.5D,
            3.0D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "max_absorption",
            "Max Absorption",
            Attributes.MAX_ABSORPTION,
            1.0D,
            4.0D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.PREFIX
        ),
        new ItemAffixDefinition(
            "explosion_knockback_resistance",
            "Explosion Knockback",
            Attributes.EXPLOSION_KNOCKBACK_RESISTANCE,
            0.02D,
            0.1D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "burning_time",
            "Fire Resistance",
            Attributes.BURNING_TIME,
            -0.2D,
            -0.05D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "water_movement_efficiency",
            "Water Mobility",
            Attributes.WATER_MOVEMENT_EFFICIENCY,
            0.02D,
            0.1D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "oxygen_bonus",
            "Oxygen Bonus",
            Attributes.OXYGEN_BONUS,
            1.0D,
            8.0D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.PREFIX
        ),
        new ItemAffixDefinition(
            "sneaking_speed",
            "Sneak Speed",
            Attributes.SNEAKING_SPEED,
            0.02D,
            0.1D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "movement_efficiency",
            "Move Efficiency",
            Attributes.MOVEMENT_EFFICIENCY,
            0.02D,
            0.1D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "step_height",
            "Step Height",
            Attributes.STEP_HEIGHT,
            0.1D,
            0.6D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "jump_strength",
            "Jump Strength",
            Attributes.JUMP_STRENGTH,
            0.05D,
            0.3D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        ),
        new ItemAffixDefinition(
            "armor_luck",
            "Luck",
            Attributes.LUCK,
            0.5D,
            2.5D,
            EnumSet.of(ItemCategory.ARMOR),
            AffixGroup.SUFFIX
        )
    );
    private static final Map<String, AffixRoll> DEFINITIONS_BY_ID = buildDefinitionMap();
    private static final EnumMap<ItemCategory, List<AffixRoll>> ROLLS_BY_CATEGORY = buildCategoryRolls();
    private static final EnumMap<ItemCategory, EnumMap<AffixGroup, List<AffixRoll>>> ROLLS_BY_CATEGORY_GROUP =
        buildCategoryGroupRolls();

    private static final int DEFAULT_AFFIX_WEIGHT = 2;

    private static final Map<String, Integer> HEAD_WEIGHTS = Map.of(
        "oxygen_bonus", 4,
        "max_absorption", 3,
        "armor", 3,
        "toughness", 3,
        "sneaking_speed", 3,
        "burning_time", 3
    );
    private static final Map<String, Integer> CHEST_WEIGHTS = Map.of(
        "armor", 4,
        "toughness", 4,
        "max_health", 4,
        "knockback_resistance", 3,
        "explosion_knockback_resistance", 3,
        "max_absorption", 3,
        "burning_time", 3
    );
    private static final Map<String, Integer> LEGS_WEIGHTS = Map.of(
        "movement_speed", 3,
        "movement_efficiency", 3,
        "armor", 3,
        "toughness", 3,
        "sneaking_speed", 3
    );
    private static final Map<String, Integer> FEET_WEIGHTS = Map.of(
        "movement_speed", 4,
        "safe_fall_distance", 4,
        "step_height", 3,
        "water_movement_efficiency", 3,
        "sneaking_speed", 3,
        "movement_efficiency", 3,
        "burning_time", 3
    );
    private static final Map<String, Integer> SWORD_WEIGHTS = Map.of(
        "attack_speed", 3,
        "sweeping_ratio", 3,
        "attack_damage", 3
    );
    private static final Map<String, Integer> AXE_WEIGHTS = Map.of(
        "attack_damage", 4,
        "knockback", 3
    );
    private static final Map<String, Integer> PICKAXE_WEIGHTS = Map.of(
        "mining_efficiency", 4,
        "break_speed", 3,
        "block_interaction_range", 3
    );
    private static final Map<String, Integer> SHOVEL_WEIGHTS = Map.of(
        "break_speed", 4,
        "block_interaction_range", 3,
        "mining_efficiency", 3
    );
    private static final Map<String, Integer> HOE_WEIGHTS = Map.of(
        "tool_luck", 3,
        "block_interaction_range", 3,
        "submerged_mining_speed", 3
    );

    private static final Map<String, Integer> WATER_AFFIX_BONUSES = Map.of(
        "water_movement_efficiency", 4,
        "oxygen_bonus", 3,
        "submerged_mining_speed", 3
    );
    private static final Map<String, Integer> FIRE_AFFIX_BONUSES = Map.of(
        "burning_time", 4,
        "explosion_knockback_resistance", 2
    );

    private ItemAffixService() {
    }

    private static Map<String, AffixRoll> buildDefinitionMap() {
        Map<String, AffixRoll> map = new HashMap<>();
        for (AffixRoll roll : ROLLS) {
            map.put(roll.id(), roll);
        }
        return Map.copyOf(map);
    }

    private static EnumMap<ItemCategory, List<AffixRoll>> buildCategoryRolls() {
        EnumMap<ItemCategory, List<AffixRoll>> map = new EnumMap<>(ItemCategory.class);
        for (ItemCategory category : ItemCategory.values()) {
            List<AffixRoll> rolls = new ArrayList<>();
            for (AffixRoll roll : ROLLS) {
                if (roll.supports(category)) {
                    rolls.add(roll);
                }
            }
            map.put(category, List.copyOf(rolls));
        }
        return map;
    }

    private static EnumMap<ItemCategory, EnumMap<AffixGroup, List<AffixRoll>>> buildCategoryGroupRolls() {
        EnumMap<ItemCategory, EnumMap<AffixGroup, List<AffixRoll>>> map = new EnumMap<>(ItemCategory.class);
        for (ItemCategory category : ItemCategory.values()) {
            EnumMap<AffixGroup, List<AffixRoll>> groupMap = new EnumMap<>(AffixGroup.class);
            for (AffixGroup group : AffixGroup.values()) {
                List<AffixRoll> rolls = new ArrayList<>();
                for (AffixRoll roll : ROLLS_BY_CATEGORY.get(category)) {
                    if (roll.group() == group) {
                        rolls.add(roll);
                    }
                }
                groupMap.put(group, List.copyOf(rolls));
            }
            map.put(category, groupMap);
        }
        return map;
    }

    /**
     * @return true when the stack contains affix data.
     */
    public static boolean hasAffixes(ItemStack stack) {
        CompoundTag root = getRootTag(stack);
        return root != null;
    }

    /**
     * Resolves the stored rarity or returns common when absent.
     */
    public static ItemRarity getRarity(ItemStack stack) {
        CompoundTag root = getRootTag(stack);
        if (root == null) {
            return ItemRarity.COMMON;
        }
        return ItemRarity.fromId(root.getString(RARITY_TAG));
    }

    /**
     * Resolves the stored affix category or defaults to weapon.
     */
    public static ItemCategory getCategory(ItemStack stack) {
        CompoundTag root = getRootTag(stack);
        if (root == null) {
            return ItemCategory.WEAPON;
        }
        String id = root.getString(CATEGORY_TAG);
        try {
            return ItemCategory.valueOf(id);
        } catch (IllegalArgumentException ex) {
            return ItemCategory.WEAPON;
        }
    }

    /**
     * Reads the affix list stored on the item.
     */
    public static List<ItemAffixData> getAffixes(ItemStack stack) {
        CompoundTag root = getRootTag(stack);
        if (root == null || !root.contains(AFFIXES_TAG, Tag.TAG_LIST)) {
            return List.of();
        }
        ListTag list = root.getList(AFFIXES_TAG, Tag.TAG_COMPOUND);
        List<ItemAffixData> affixes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            String id = entry.getString(AFFIX_ID_TAG);
            double value = entry.getDouble(AFFIX_VALUE_TAG);
            int uid = entry.getInt(AFFIX_UID_TAG);
            affixes.add(new ItemAffixData(id, value, uid));
        }
        return affixes;
    }

    /**
     * Rolls affixes using default context and writes them to the item.
     */
    public static void rollAffixes(ItemStack stack, ItemCategory category, RandomSource random, int level) {
        rollAffixes(stack, category, random, level, AffixContext.EMPTY);
    }

    /**
     * Rolls affixes using the given context and writes them to the item.
     */
    public static void rollAffixes(
        ItemStack stack,
        ItemCategory category,
        RandomSource random,
        int level,
        AffixContext context
    ) {
        ItemRarity rarity = ItemRarity.roll(random, level);
        List<AffixRoll> candidates = ROLLS_BY_CATEGORY.getOrDefault(category, List.of());
        List<ItemAffixData> rolled = new ArrayList<>();
        Set<String> existingIds = new HashSet<>();
        addImplicitAffixes(stack, category, rarity, random, rolled, existingIds);
        if (candidates.isEmpty() && rolled.isEmpty()) {
            return;
        }
        GroupRolls groupRolls = getGroupRolls(rarity, random);
        rollGroup(
            random,
            stack,
            category,
            rarity,
            candidates,
            getGroupCandidates(category, AffixGroup.PREFIX),
            groupRolls.prefix(),
            rolled,
            context,
            existingIds
        );
        rollGroup(
            random,
            stack,
            category,
            rarity,
            candidates,
            getGroupCandidates(category, AffixGroup.SUFFIX),
            groupRolls.suffix(),
            rolled,
            context,
            existingIds
        );
        writeAffixes(stack, category, rarity, rolled);
        rebuildAttributeModifiers(stack, category, rolled);
    }

    /**
     * Rolls affixes using a fixed rarity, replacing any previous affix data.
     */
    public static void rollAffixesWithRarity(
        ItemStack stack,
        ItemCategory category,
        RandomSource random,
        ItemRarity rarity,
        AffixContext context
    ) {
        if (rarity == null) {
            return;
        }
        List<AffixRoll> candidates = ROLLS_BY_CATEGORY.getOrDefault(category, List.of());
        List<ItemAffixData> rolled = new ArrayList<>();
        Set<String> existingIds = new HashSet<>();
        addImplicitAffixes(stack, category, rarity, random, rolled, existingIds);
        if (candidates.isEmpty() && rolled.isEmpty()) {
            return;
        }
        GroupRolls groupRolls = getGroupRolls(rarity, random);
        rollGroup(
            random,
            stack,
            category,
            rarity,
            candidates,
            getGroupCandidates(category, AffixGroup.PREFIX),
            groupRolls.prefix(),
            rolled,
            context,
            existingIds
        );
        rollGroup(
            random,
            stack,
            category,
            rarity,
            candidates,
            getGroupCandidates(category, AffixGroup.SUFFIX),
            groupRolls.suffix(),
            rolled,
            context,
            existingIds
        );
        writeAffixes(stack, category, rarity, rolled);
        rebuildAttributeModifiers(stack, category, rolled);
    }

    /**
     * Rerolls existing affix values while keeping the same affix ids and rarity.
     */
    public static void rerollAffixValues(ItemStack stack, RandomSource random) {
        if (stack == null || stack.isEmpty() || random == null || !hasAffixes(stack)) {
            return;
        }
        ItemRarity rarity = getRarity(stack);
        ItemCategory category = getCategory(stack);
        List<ItemAffixData> current = getAffixes(stack);
        if (current.isEmpty()) {
            return;
        }
        List<ItemAffixData> updated = new ArrayList<>();
        for (ItemAffixData affix : current) {
            AffixRoll definition = getDefinition(affix.id());
            if (definition == null) {
                continue;
            }
            double value = definition.rollValue(random, rarity, stack, category);
            updated.add(new ItemAffixData(affix.id(), value, affix.uid()));
        }
        writeAffixes(stack, category, rarity, updated);
        rebuildAttributeModifiers(stack, category, updated);
    }

    /**
     * Ensures implicit affixes exist for affixed gear, rolling missing entries if needed.
     */
    public static void ensureImplicitAffixes(ItemStack stack, RandomSource random) {
        if (stack == null || stack.isEmpty() || random == null || !hasAffixes(stack)) {
            return;
        }
        ItemCategory category = getCategory(stack);
        if (category == null) {
            return;
        }
        ItemRarity rarity = getRarity(stack);
        List<ItemAffixData> current = getAffixes(stack);
        if (current.isEmpty()) {
            return;
        }
        Set<String> existingIds = new HashSet<>();
        for (ItemAffixData affix : current) {
            existingIds.add(affix.id());
        }
        List<ItemAffixData> updated = new ArrayList<>(current);
        addImplicitAffixes(stack, category, rarity, random, updated, existingIds);
        if (updated.size() == current.size()) {
            return;
        }
        writeAffixes(stack, category, rarity, updated);
        rebuildAttributeModifiers(stack, category, updated);
    }

    /**
     * Rerolls a single affix entry while keeping the rest of the affix list intact.
     */
    public static boolean rerollSingleAffix(ItemStack stack, int index, RandomSource random) {
        if (stack == null || stack.isEmpty() || random == null || !hasAffixes(stack)) {
            return false;
        }
        List<ItemAffixData> current = getAffixes(stack);
        if (index < 0 || index >= current.size()) {
            return false;
        }
        ItemRarity rarity = getRarity(stack);
        ItemCategory category = getCategory(stack);
        ItemAffixData existing = current.get(index);
        AffixRoll existingDefinition = getDefinition(existing.id());
        List<AffixRoll> candidates = existingDefinition == null
            ? ROLLS_BY_CATEGORY.getOrDefault(category, List.of())
            : getGroupCandidates(category, existingDefinition.group());
        if (candidates.isEmpty()) {
            return false;
        }
        Set<String> excludedIds = new HashSet<>();
        for (int i = 0; i < current.size(); i++) {
            if (i == index) {
                continue;
            }
            excludedIds.add(current.get(i).id());
        }
        AffixRoll chosen = null;
        for (int attempt = 0; attempt < 5; attempt++) {
            AffixRoll candidate = pickRoll(random, candidates, stack, category, AffixContext.EMPTY, excludedIds);
            if (candidate == null) {
                break;
            }
            if (!candidate.id().equals(existing.id()) || candidates.size() == 1) {
                chosen = candidate;
                break;
            }
            chosen = candidate;
        }
        if (chosen == null) {
            return false;
        }
        double value = chosen.rollValue(random, rarity, stack, category);
        int uid = random.nextInt(Integer.MAX_VALUE);
        List<ItemAffixData> updated = new ArrayList<>(current);
        updated.set(index, new ItemAffixData(chosen.id(), value, uid));
        writeAffixes(stack, category, rarity, updated);
        rebuildAttributeModifiers(stack, category, updated);
        return true;
    }

    /**
     * Adds a random affix to the stack if possible.
     *
     * @return true if a new affix was added.
     */
    public static boolean addRandomAffix(ItemStack stack, RandomSource random) {
        if (stack == null || stack.isEmpty() || random == null || !hasAffixes(stack)) {
            return false;
        }
        ItemRarity rarity = getRarity(stack);
        ItemCategory category = getCategory(stack);
        List<ItemAffixData> current = getAffixes(stack);
        if (current.size() >= rarity.getMaxAffixes()) {
            return false;
        }
        List<AffixRoll> allCandidates = ROLLS_BY_CATEGORY.getOrDefault(category, List.of());
        if (allCandidates.isEmpty()) {
            return false;
        }
        List<String> existingIds = new ArrayList<>();
        for (ItemAffixData affix : current) {
            existingIds.add(affix.id());
        }
        List<AffixRoll> candidates = new ArrayList<>();
        for (AffixRoll roll : allCandidates) {
            if (!existingIds.contains(roll.id())) {
                candidates.add(roll);
            }
        }
        if (candidates.isEmpty()) {
            return false;
        }
        AffixRoll chosen = pickRoll(random, candidates, stack, category, AffixContext.EMPTY, Set.of());
        if (chosen == null) {
            return false;
        }
        double value = chosen.rollValue(random, rarity, stack, category);
        int uid = random.nextInt(Integer.MAX_VALUE);
        List<ItemAffixData> updated = new ArrayList<>(current);
        updated.add(new ItemAffixData(chosen.id(), value, uid));
        writeAffixes(stack, category, rarity, updated);
        rebuildAttributeModifiers(stack, category, updated);
        return true;
    }

    /**
     * Applies attribute modifiers derived from affixes to the stack.
     */
    public static void applyAttributeModifiers(ItemStack stack, ItemCategory category, List<ItemAffixData> affixes) {
        if (affixes.isEmpty()) {
            return;
        }
        ItemAttributeModifiers modifiers = ItemAttributeModifiers.EMPTY;
        for (ItemAffixData affix : affixes) {
            AffixRoll definition = getDefinition(affix.id());
            if (definition == null) {
                continue;
            }
            modifiers = definition.applyModifier(stack, category, affix, modifiers);
        }
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
    }

    private static void rebuildAttributeModifiers(ItemStack stack, ItemCategory category, List<ItemAffixData> affixes) {
        ItemAttributeModifiers modifiers = ItemAttributeModifiers.EMPTY;
        for (ItemAffixData affix : affixes) {
            AffixRoll definition = getDefinition(affix.id());
            if (definition == null) {
                continue;
            }
            modifiers = definition.applyModifier(stack, category, affix, modifiers);
        }
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
    }

    /**
     * Copies affix data and derived attribute modifiers from one stack to another.
     */
    public static boolean copyAffixes(ItemStack source, ItemStack target) {
        if (source == null || target == null || source == target) {
            return false;
        }
        if (source.isEmpty() || target.isEmpty()) {
            return false;
        }
        if (hasAffixes(target)) {
            return false;
        }
        CompoundTag root = getRootTag(source);
        if (root == null) {
            return false;
        }
        CustomData.update(DataComponents.CUSTOM_DATA, target, tag -> tag.put(ROOT_TAG, root.copy()));
        applyAttributeModifiers(target, getCategory(source), getAffixes(source));
        return true;
    }

    /**
     * Replaces the tooltip with the affix-aware tooltip layout when applicable.
     */
    public static void appendTooltipLines(ItemStack stack, List<Component> tooltip) {
        if (!shouldCustomizeTooltip(stack)) {
            return;
        }
        tooltip.clear();
        tooltip.addAll(buildFullTooltip(stack));
    }

    public static String getAffixDisplayName(String id) {
        AffixRoll definition = getDefinition(id);
        return definition == null ? id : definition.displayName();
    }

    private static AffixRoll getDefinition(String id) {
        return DEFINITIONS_BY_ID.get(id);
    }

    private static String formatValue(double value) {
        return formatSignedValue(value);
    }

    private static String formatRange(double minValue, double maxValue) {
        return "(" + formatStatValue(minValue) + "-" + formatStatValue(maxValue) + ")";
    }

    private static String formatSignedValue(double value) {
        String magnitude = formatStatValue(Math.abs(value));
        return (value >= 0 ? "+" : "-") + magnitude;
    }

    /**
     * @return true if the tooltip should be replaced with the affix layout.
     */
    public static boolean shouldCustomizeTooltip(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return hasAffixes(stack);
    }

    /**
     * Determines which category should receive affixes for the stack.
     */
    public static ItemCategory getCategoryForAffixes(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        if (stack.getItem() instanceof ArmorItem) {
            return ItemCategory.ARMOR;
        }
        var holder = stack.getItem().builtInRegistryHolder();
        if (holder.is(ItemTags.SWORDS) || holder.is(ItemTags.AXES) || isRangedWeapon(stack)) {
            return ItemCategory.WEAPON;
        }
        if (holder.is(ItemTags.PICKAXES) || holder.is(ItemTags.SHOVELS) || holder.is(ItemTags.HOES)) {
            return ItemCategory.TOOL;
        }
        return null;
    }

    /**
     * @return true when the item can receive affixes.
     */
    public static boolean canRollAffixes(ItemStack stack) {
        return getCategoryForAffixes(stack) != null;
    }

    private static boolean isSupportedGear(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            return true;
        }
        var holder = stack.getItem().builtInRegistryHolder();
        if (holder.is(ItemTags.SWORDS) || holder.is(ItemTags.AXES) || isRangedWeapon(stack)) {
            return true;
        }
        return holder.is(ItemTags.PICKAXES) || holder.is(ItemTags.SHOVELS) || holder.is(ItemTags.HOES);
    }

    private static boolean hasAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(
            DataComponents.ATTRIBUTE_MODIFIERS,
            stack.getItem().getDefaultAttributeModifiers()
        );
        return !modifiers.modifiers().isEmpty();
    }

    public static List<Component> buildFullTooltip(ItemStack stack) {
        List<Component> lines = new ArrayList<>();
        ItemCategory category = resolveTooltipCategory(stack);
        ItemRarity rarity = hasAffixes(stack) ? getRarity(stack) : ItemRarity.COMMON;
        lines.add(stack.getHoverName().copy().withStyle(rarityStyle(rarity, true)));
        lines.add(Component.literal(rarity.getDisplayName() + " - " + getCategoryLabel(category))
            .withStyle(rarityStyle(rarity, false)));
        lines.add(Component.literal(getSlotLabel(resolveTooltipSlot(stack, category)))
            .withStyle(style(TOOLTIP_STEEL).withItalic(true)));
        addRule(lines);

        Map<Holder<Attribute>, Double> totals = resolveAttributeTotals(stack, category);
        if (!totals.isEmpty()) {
            lines.add(Component.literal("Stats").withStyle(style(TOOLTIP_GOLD)));
            for (Map.Entry<Holder<Attribute>, Double> entry : totals.entrySet()) {
                String icon = getAttributeIcon(entry.getKey());
                String label = getAttributeLabel(entry.getKey());
                String value = formatStatValue(entry.getValue());
                String prefix = icon == null ? "" : icon + " ";
                lines.add(Component.literal(prefix + label + ": " + value)
                    .withStyle(style(TOOLTIP_BLUE)));
            }
        }

        List<ItemAffixData> affixes = hasAffixes(stack) ? getAffixes(stack) : List.of();
        if (!affixes.isEmpty()) {
            if (!totals.isEmpty()) {
                addSpacer(lines);
            }
            lines.add(Component.literal("Affixes").withStyle(style(TOOLTIP_GOLD)));
            for (ItemAffixData affix : affixes) {
                AffixRoll definition = getDefinition(affix.id());
                if (definition == null) {
                    continue;
                }
                String valueText = formatValue(affix.value());
                String rangeText = formatRange(
                    definition.getMinValue(rarity, stack, category),
                    definition.getMaxValue(rarity, stack, category)
                );
                MutableComponent line = Component.literal(definition.displayName() + " " + valueText + " ")
                    .withStyle(style(TOOLTIP_BLUE));
                line.append(Component.literal(rangeText).withStyle(style(TOOLTIP_BLUE_MUTED)));
                lines.add(line);
            }
        }

        if (stack.isDamageableItem()) {
            if (!totals.isEmpty() || !affixes.isEmpty()) {
                addSpacer(lines);
            }
            int maxDurability = stack.getMaxDamage();
            int currentDurability = Math.max(0, maxDurability - stack.getDamageValue());
            lines.add(Component.literal("Durability: " + currentDurability + " / " + maxDurability)
                .withStyle(style(TOOLTIP_BLUE_MUTED)));
        }

        addRule(lines);
        return lines;
    }

    private static ItemCategory resolveTooltipCategory(ItemStack stack) {
        if (hasAffixes(stack)) {
            return getCategory(stack);
        }
        if (stack.getItem() instanceof ArmorItem) {
            return ItemCategory.ARMOR;
        }
        var holder = stack.getItem().builtInRegistryHolder();
        if (holder.is(ItemTags.SWORDS) || holder.is(ItemTags.AXES) || isRangedWeapon(stack)) {
            return ItemCategory.WEAPON;
        }
        if (holder.is(ItemTags.PICKAXES) || holder.is(ItemTags.SHOVELS) || holder.is(ItemTags.HOES)) {
            return ItemCategory.TOOL;
        }
        return ItemCategory.WEAPON;
    }

    private static EquipmentSlot resolveTooltipSlot(ItemStack stack, ItemCategory category) {
        if (category == ItemCategory.ARMOR && stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getEquipmentSlot();
        }
        return EquipmentSlot.MAINHAND;
    }

    private static Map<Holder<Attribute>, Double> resolveAttributeTotals(ItemStack stack, ItemCategory category) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(
            DataComponents.ATTRIBUTE_MODIFIERS,
            stack.getItem().getDefaultAttributeModifiers()
        );
        if (modifiers.modifiers().isEmpty()) {
            return Map.of();
        }
        EquipmentSlot slot = resolveTooltipSlot(stack, category);
        Map<Holder<Attribute>, List<AttributeModifier>> byAttribute = new LinkedHashMap<>();
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.slot().test(slot)) {
                continue;
            }
            byAttribute.computeIfAbsent(entry.attribute(), key -> new ArrayList<>()).add(entry.modifier());
        }
        if (byAttribute.isEmpty()) {
            return Map.of();
        }
        Map<Holder<Attribute>, Double> totals = new LinkedHashMap<>();
        for (Map.Entry<Holder<Attribute>, List<AttributeModifier>> entry : byAttribute.entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            double baseValue = attribute.value().getDefaultValue();
            double total = applyModifiers(baseValue, entry.getValue());
            totals.put(attribute, total);
        }
        return totals;
    }

    private static double applyModifiers(double baseValue, List<AttributeModifier> modifiers) {
        double add = 0.0D;
        double addBase = 0.0D;
        double multiplyTotal = 0.0D;
        for (AttributeModifier modifier : modifiers) {
            String op = modifier.operation().name();
            if ("ADD_VALUE".equals(op)) {
                add += modifier.amount();
            } else if ("ADD_MULTIPLIED_BASE".equals(op)) {
                addBase += modifier.amount();
            } else if ("ADD_MULTIPLIED_TOTAL".equals(op) || "MULTIPLY_TOTAL".equals(op)) {
                multiplyTotal += modifier.amount();
            }
        }
        return (baseValue + add) * (1.0D + multiplyTotal) + (baseValue * addBase);
    }

    private static String getCategoryLabel(ItemCategory category) {
        return switch (category) {
            case ARMOR -> "Armor";
            case TOOL -> "Tool";
            case WEAPON -> "Weapon";
        };
    }

    private static String getAttributeLabel(Holder<Attribute> attribute) {
        ResourceLocation key = BuiltInRegistries.ATTRIBUTE.getKey(attribute.value());
        if (key == null) {
            return "Attribute";
        }
        String path = key.getPath();
        if (path.startsWith("generic.")) {
            path = path.substring("generic.".length());
        }
        return switch (path) {
            case "attack_damage" -> "Damage";
            case "attack_speed" -> "Attack Speed";
            case "armor" -> "Armor";
            case "armor_toughness" -> "Armor Toughness";
            case "max_health" -> "Max Health";
            case "knockback_resistance" -> "Knockback Resistance";
            case "movement_speed" -> "Move Speed";
            case "safe_fall_distance" -> "Safe Fall Distance";
            case "step_height" -> "Step Height";
            case "jump_strength" -> "Jump Strength";
            case "block_break_speed" -> "Block Break Speed";
            case "mining_efficiency" -> "Mining Efficiency";
            case "submerged_mining_speed" -> "Underwater Mining";
            case "block_interaction_range" -> "Block Reach";
            case "entity_interaction_range" -> "Melee Reach";
            case "attack_knockback" -> "Attack Knockback";
            case "sweeping_damage_ratio" -> "Sweeping Damage";
            case "luck" -> "Luck";
            case "max_absorption" -> "Max Absorption";
            case "water_movement_efficiency" -> "Water Mobility";
            case "oxygen_bonus" -> "Oxygen Bonus";
            case "sneaking_speed" -> "Sneak Speed";
            case "movement_efficiency" -> "Move Efficiency";
            case "explosion_knockback_resistance" -> "Explosion Knockback";
            case "burning_time" -> "Fire Resistance";
            default -> toTitleCase(path.replace('_', ' '));
        };
    }

    private static String getAttributeIcon(Holder<Attribute> attribute) {
        ResourceLocation key = BuiltInRegistries.ATTRIBUTE.getKey(attribute.value());
        if (key == null) {
            return null;
        }
        String path = key.getPath();
        if (path.startsWith("generic.")) {
            path = path.substring("generic.".length());
        }
        return switch (path) {
            case "attack_damage" -> "⚔";
            case "attack_speed" -> "⏱";
            case "armor" -> "🛡";
            case "armor_toughness" -> "⛨";
            case "max_health" -> "❤";
            case "knockback_resistance" -> "⛓";
            case "movement_speed" -> "➜";
            case "safe_fall_distance" -> "⇣";
            case "step_height" -> "⇧";
            case "jump_strength" -> "⬆";
            case "block_break_speed" -> "⛏";
            case "mining_efficiency" -> "⛏";
            case "submerged_mining_speed" -> "🌊";
            case "block_interaction_range" -> "▣";
            case "entity_interaction_range" -> "◎";
            case "attack_knockback" -> "✸";
            case "sweeping_damage_ratio" -> "◐";
            case "luck" -> "✧";
            case "max_absorption" -> "⬡";
            case "water_movement_efficiency" -> "≈";
            case "oxygen_bonus" -> "◍";
            case "sneaking_speed" -> "❖";
            case "movement_efficiency" -> "➤";
            case "explosion_knockback_resistance" -> "✹";
            case "burning_time" -> "F";
            default -> null;
        };
    }

    private static String formatStatValue(double value) {
        String text = String.format(Locale.ROOT, "%.2f", value);
        int trim = text.length();
        while (trim > 0 && text.charAt(trim - 1) == '0') {
            trim--;
        }
        if (trim > 0 && text.charAt(trim - 1) == '.') {
            trim--;
        }
        return text.substring(0, trim);
    }

    private static String toTitleCase(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String[] parts = input.replace('.', ' ').split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }

    private static Style style(int rgb) {
        return Style.EMPTY.withColor(TextColor.fromRgb(rgb & 0xFFFFFF));
    }

    private static Style rarityStyle(ItemRarity rarity, boolean bold) {
        TextColor color = TextColor.fromLegacyFormat(rarity.getColor());
        Style style = color == null ? Style.EMPTY : Style.EMPTY.withColor(color);
        return bold ? style.withBold(true) : style;
    }

    private static void addRule(List<Component> lines) {
        lines.add(Component.literal("------------------------------")
            .withStyle(style(TOOLTIP_GOLD_DARK)));
    }

    private static void addSpacer(List<Component> lines) {
        lines.add(Component.literal(" "));
    }

    private static String getSlotLabel(EquipmentSlot slot) {
        if (slot == null) {
            return "Slot: Unknown";
        }
        return switch (slot) {
            case MAINHAND -> "Slot: Main Hand";
            case OFFHAND -> "Slot: Off Hand";
            case HEAD -> "Slot: Head";
            case CHEST -> "Slot: Chest";
            case LEGS -> "Slot: Legs";
            case FEET -> "Slot: Feet";
            default -> "Slot: Other";
        };
    }

    private static void rollGroup(
        RandomSource random,
        ItemStack stack,
        ItemCategory category,
        ItemRarity rarity,
        List<AffixRoll> candidates,
        List<AffixRoll> groupCandidates,
        int rolls,
        List<ItemAffixData> output,
        AffixContext context,
        Set<String> existingIds
    ) {
        if (rolls <= 0) {
            return;
        }
        List<AffixRoll> pool = groupCandidates.isEmpty() ? candidates : groupCandidates;
        for (int i = 0; i < rolls; i++) {
            AffixRoll definition = pickRoll(random, pool, stack, category, context, existingIds);
            if (definition == null) {
                break;
            }
            double value = definition.rollValue(random, rarity, stack, category);
            int uid = random.nextInt(Integer.MAX_VALUE);
            output.add(new ItemAffixData(definition.id(), value, uid));
            existingIds.add(definition.id());
        }
    }

    private static GroupRolls getGroupRolls(ItemRarity rarity, RandomSource random) {
        int min = Math.max(0, rarity.getMinAffixes());
        int max = Math.max(min, rarity.getMaxAffixes());
        if (max == 0) {
            return new GroupRolls(0, 0);
        }
        int total = max == min ? max : random.nextInt(max - min + 1) + min;
        if (total <= 0) {
            return new GroupRolls(0, 0);
        }
        int prefix = Math.max(1, total / 2);
        int suffix = total - prefix;
        return new GroupRolls(prefix, suffix);
    }

    private static List<AffixRoll> getGroupCandidates(ItemCategory category, AffixGroup group) {
        EnumMap<AffixGroup, List<AffixRoll>> groupMap = ROLLS_BY_CATEGORY_GROUP.get(category);
        if (groupMap == null) {
            return List.of();
        }
        return groupMap.getOrDefault(group, List.of());
    }

    private static AffixRoll pickRoll(
        RandomSource random,
        List<AffixRoll> candidates,
        ItemStack stack,
        ItemCategory category,
        AffixContext context,
        Set<String> excludedIds
    ) {
        int total = 0;
        int[] weights = new int[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            AffixRoll roll = candidates.get(i);
            if (excludedIds.contains(roll.id())) {
                continue;
            }
            int weight = getRollWeight(roll, stack, category, context);
            if (weight <= 0) {
                continue;
            }
            weights[i] = weight;
            total += weight;
        }
        if (total <= 0) {
            return null;
        }
        int roll = random.nextInt(total);
        for (int i = 0; i < candidates.size(); i++) {
            int weight = weights[i];
            if (weight <= 0) {
                continue;
            }
            if (roll < weight) {
                return candidates.get(i);
            }
            roll -= weight;
        }
        return null;
    }

    private static void addImplicitAffixes(
        ItemStack stack,
        ItemCategory category,
        ItemRarity rarity,
        RandomSource random,
        List<ItemAffixData> output,
        Set<String> existingIds
    ) {
        List<AffixRoll> implicit = getImplicitRolls(stack, category);
        if (implicit.isEmpty()) {
            return;
        }
        for (AffixRoll roll : implicit) {
            if (!existingIds.add(roll.id())) {
                continue;
            }
            double value = roll.rollValue(random, rarity, stack, category);
            int uid = random.nextInt(Integer.MAX_VALUE);
            output.add(new ItemAffixData(roll.id(), value, uid));
        }
    }

    private static List<AffixRoll> getImplicitRolls(ItemStack stack, ItemCategory category) {
        if (stack == null || stack.isEmpty()) {
            return List.of();
        }
        List<AffixRoll> rolls = new ArrayList<>();
        if (category == ItemCategory.WEAPON) {
            boolean isMelee = isMeleeWeapon(stack);
            if (isMelee) {
                addImplicitRoll(rolls, "attack_damage");
                addImplicitRoll(rolls, "attack_speed");
            }
            return rolls;
        }
        if (category == ItemCategory.ARMOR) {
            addImplicitRoll(rolls, "armor");
            if (shouldIncludeToughness(stack)) {
                addImplicitRoll(rolls, "toughness");
            }
            return rolls;
        }
        if (category == ItemCategory.TOOL) {
            addImplicitRoll(rolls, "mining_efficiency");
            return rolls;
        }
        return rolls;
    }

    private static void addImplicitRoll(List<AffixRoll> rolls, String id) {
        AffixRoll roll = getDefinition(id);
        if (roll != null) {
            rolls.add(roll);
        }
    }

    private static boolean isMeleeWeapon(ItemStack stack) {
        var holder = stack.getItem().builtInRegistryHolder();
        return holder.is(ItemTags.SWORDS)
            || holder.is(ItemTags.AXES)
            || stack.getItem() instanceof TridentItem;
    }

    private static boolean shouldIncludeToughness(ItemStack stack) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (key == null) {
            return false;
        }
        String path = key.getPath();
        return path.startsWith("diamond_") || path.startsWith("netherite_");
    }

    private static int getRollWeight(AffixRoll roll, ItemStack stack, ItemCategory category, AffixContext context) {
        int baseWeight = getBaseRollWeight(roll, stack, category);
        return applyContextWeight(baseWeight, roll.id(), context);
    }

    private static int getBaseRollWeight(AffixRoll roll, ItemStack stack, ItemCategory category) {
        String id = roll.id();
        if (category == ItemCategory.ARMOR && stack.getItem() instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getEquipmentSlot();
            return switch (slot) {
                case HEAD -> HEAD_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
                case CHEST -> CHEST_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
                case LEGS -> LEGS_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
                case FEET -> FEET_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
                default -> DEFAULT_AFFIX_WEIGHT;
            };
        }
        if (category == ItemCategory.WEAPON) {
            return weaponWeight(id, stack);
        }
        if (category == ItemCategory.TOOL) {
            return toolWeight(id, stack);
        }
        return DEFAULT_AFFIX_WEIGHT;
    }

    private static int weaponWeight(String id, ItemStack stack) {
        boolean sword = stack.getItem().builtInRegistryHolder().is(ItemTags.SWORDS);
        boolean axe = stack.getItem().builtInRegistryHolder().is(ItemTags.AXES);
        if (sword) {
            return SWORD_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
        }
        if (axe) {
            return AXE_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
        }
        return DEFAULT_AFFIX_WEIGHT;
    }

    private static boolean isRangedWeapon(ItemStack stack) {
        return stack.getItem() instanceof BowItem
            || stack.getItem() instanceof CrossbowItem
            || stack.getItem() instanceof TridentItem;
    }

    private static int toolWeight(String id, ItemStack stack) {
        boolean pickaxe = stack.getItem().builtInRegistryHolder().is(ItemTags.PICKAXES);
        boolean shovel = stack.getItem().builtInRegistryHolder().is(ItemTags.SHOVELS);
        boolean hoe = stack.getItem().builtInRegistryHolder().is(ItemTags.HOES);
        if (pickaxe) {
            return PICKAXE_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
        }
        if (shovel) {
            return SHOVEL_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
        }
        if (hoe) {
            return HOE_WEIGHTS.getOrDefault(id, DEFAULT_AFFIX_WEIGHT);
        }
        return DEFAULT_AFFIX_WEIGHT;
    }

    private static int applyContextWeight(int baseWeight, String id, AffixContext context) {
        if (context == null) {
            return baseWeight;
        }
        int weight = baseWeight;
        if (context.waterAffinity()) {
            weight += WATER_AFFIX_BONUSES.getOrDefault(id, 0);
        }
        if (context.fireAffinity()) {
            weight += FIRE_AFFIX_BONUSES.getOrDefault(id, 0);
        }
        return weight;
    }

    public record AffixContext(boolean waterAffinity, boolean fireAffinity) {
        public static final AffixContext EMPTY = new AffixContext(false, false);

        public static AffixContext fromKill(LivingEntity source) {
            if (source == null) {
                return EMPTY;
            }
            boolean water = isWaterMob(source) || isWaterBiome(source);
            boolean fire = isFireMob(source) || isFireBiome(source);
            if (!water && !fire) {
                return EMPTY;
            }
            return new AffixContext(water, fire);
        }
    }

    private static boolean isWaterMob(LivingEntity source) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(source.getType());
        if (id == null) {
            return false;
        }
        String path = id.getPath();
        return path.endsWith("_fish")
            || "cod".equals(path)
            || "salmon".equals(path)
            || "pufferfish".equals(path)
            || "tropical_fish".equals(path)
            || "guardian".equals(path)
            || "elder_guardian".equals(path)
            || "drowned".equals(path);
    }

    private static boolean isFireMob(LivingEntity source) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(source.getType());
        if (id == null) {
            return false;
        }
        String path = id.getPath();
        return "blaze".equals(path)
            || "ghast".equals(path)
            || "magma_cube".equals(path);
    }

    private static boolean isWaterBiome(LivingEntity source) {
        Holder<Biome> biome = source.level().getBiome(source.blockPosition());
        return biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_RIVER);
    }

    private static boolean isFireBiome(LivingEntity source) {
        Holder<Biome> biome = source.level().getBiome(source.blockPosition());
        return biome.is(BiomeTags.IS_NETHER);
    }

    private record GroupRolls(int prefix, int suffix) {
    }

    private static void writeAffixes(
        ItemStack stack,
        ItemCategory category,
        ItemRarity rarity,
        List<ItemAffixData> affixes
    ) {
        CompoundTag root = new CompoundTag();
        root.putString(RARITY_TAG, rarity.getId());
        root.putString(CATEGORY_TAG, category.name());
        ListTag list = new ListTag();
        for (ItemAffixData affix : affixes) {
            CompoundTag entry = new CompoundTag();
            entry.putString(AFFIX_ID_TAG, affix.id());
            entry.putDouble(AFFIX_VALUE_TAG, affix.value());
            entry.putInt(AFFIX_UID_TAG, affix.uid());
            list.add(entry);
        }
        root.put(AFFIXES_TAG, list);
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(ROOT_TAG, root));
    }

    private static CompoundTag getRootTag(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.getUnsafe();
        if (tag == null || !tag.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
            return null;
        }
        return tag.getCompound(ROOT_TAG);
    }
}
