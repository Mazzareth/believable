package app.masterwork.believable.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Interface for affix roll definitions and modifier application.
 */
public interface AffixRoll {
    /**
     * @return stable id for storage and lookup
     */
    String id();

    /**
     * @return display name for tooltips
     */
    String displayName();

    /**
     * @return prefix or suffix group
     */
    AffixGroup group();

    /**
     * @return true if the affix can be rolled for the category
     */
    boolean supports(ItemCategory category);

    /**
     * Rolls a value for this affix given rarity and item context.
     */
    double rollValue(RandomSource random, ItemRarity rarity, ItemStack stack, ItemCategory category);

    /**
     * @return minimum value possible for the given rarity and context
     */
    double getMinValue(ItemRarity rarity, ItemStack stack, ItemCategory category);

    /**
     * @return maximum value possible for the given rarity and context
     */
    double getMaxValue(ItemRarity rarity, ItemStack stack, ItemCategory category);

    /**
     * Applies attribute modifiers for this affix, if any, and returns the updated collection.
     */
    default ItemAttributeModifiers applyModifier(
        ItemStack stack,
        ItemCategory category,
        ItemAffixData affix,
        ItemAttributeModifiers modifiers
    ) {
        return modifiers;
    }
}
