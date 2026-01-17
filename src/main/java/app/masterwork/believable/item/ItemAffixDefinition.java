package app.masterwork.believable.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.EnumSet;

/**
 * Defines an attribute-based affix roll and how to apply it to items.
 */
public record ItemAffixDefinition(
    String id,
    String displayName,
    Holder<Attribute> attribute,
    double minValue,
    double maxValue,
    EnumSet<ItemCategory> categories,
    AffixGroup group
) implements AffixRoll {
    @Override
    public boolean supports(ItemCategory category) {
        return categories.contains(category);
    }

    @Override
    public double rollValue(RandomSource random, ItemRarity rarity, ItemStack stack, ItemCategory category) {
        double min = getMinValue(rarity, stack, category);
        double max = getMaxValue(rarity, stack, category);
        double biased = applyRarityBias(random.nextDouble(), rarity);
        double base = Mth.lerp(biased, min, max);
        double lower = Math.min(min, max);
        double upper = Math.max(min, max);
        return Mth.clamp(base, lower, upper);
    }

    @Override
    public double getMinValue(ItemRarity rarity, ItemStack stack, ItemCategory category) {
        return minValue * rarity.getStatMultiplier() * getTierMultiplier(stack, category);
    }

    @Override
    public double getMaxValue(ItemRarity rarity, ItemStack stack, ItemCategory category) {
        return maxValue * rarity.getStatMultiplier() * getTierMultiplier(stack, category);
    }

    @Override
    public ItemAttributeModifiers applyModifier(
        ItemStack stack,
        ItemCategory category,
        ItemAffixData affix,
        ItemAttributeModifiers modifiers
    ) {
        if (attribute == null) {
            return modifiers;
        }
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
            app.masterwork.believable.Believable.MODID,
            "affix/" + id() + "/" + affix.uid()
        );
        AttributeModifier modifier = new AttributeModifier(id, affix.value(), AttributeModifier.Operation.ADD_VALUE);
        EquipmentSlotGroup slotGroup = resolveSlotGroup(stack, category);
        return modifiers.withModifierAdded(attribute, modifier, slotGroup);
    }

    private EquipmentSlotGroup resolveSlotGroup(ItemStack stack, ItemCategory category) {
        if (category == ItemCategory.ARMOR && stack.getItem() instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getEquipmentSlot();
            return EquipmentSlotGroup.bySlot(slot);
        }
        return EquipmentSlotGroup.MAINHAND;
    }

    private double getTierMultiplier(ItemStack stack, ItemCategory category) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (key == null) {
            return 1.0D;
        }
        String path = key.getPath();
        if (category == ItemCategory.ARMOR) {
            if (path.startsWith("leather_")) {
                return 1.0D;
            }
            if (path.startsWith("chainmail_")) {
                return 1.05D;
            }
            if (path.startsWith("golden_")) {
                return 1.1D;
            }
            if (path.startsWith("iron_")) {
                return 1.15D;
            }
            if (path.startsWith("diamond_")) {
                return 1.3D;
            }
            if (path.startsWith("netherite_")) {
                return 1.5D;
            }
            return 1.0D;
        }
        if (path.startsWith("wooden_")) {
            return 1.0D;
        }
        if (path.startsWith("stone_")) {
            return 1.05D;
        }
        if (path.startsWith("golden_")) {
            return 1.1D;
        }
        if (path.startsWith("iron_")) {
            return 1.15D;
        }
        if (path.startsWith("diamond_")) {
            return 1.3D;
        }
        if (path.startsWith("netherite_")) {
            return 1.5D;
        }
        return 1.0D;
    }

    private double applyRarityBias(double roll, ItemRarity rarity) {
        double power = switch (rarity) {
            case COMMON -> 2.0D;
            case UNCOMMON -> 1.5D;
            case RARE -> 1.1D;
            case LEGENDARY -> 0.85D;
            case UNIQUE -> 0.7D;
        };
        return Math.pow(roll, power);
    }
}
