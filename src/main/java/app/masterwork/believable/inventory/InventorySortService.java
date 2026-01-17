package app.masterwork.believable.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class InventorySortService {
    private InventorySortService() {
    }

    public static void sortPlayerInventory(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        int startIndex = Inventory.getSelectionSize();
        int size = inventory.items.size();
        if (startIndex >= size) {
            return;
        }
        List<ItemStack> merged = mergeStacks(inventory.items.subList(startIndex, size));
        merged.sort(INVENTORY_COMPARATOR);

        for (int i = startIndex; i < size; i++) {
            int index = i - startIndex;
            ItemStack stack = index < merged.size() ? merged.get(index) : ItemStack.EMPTY;
            inventory.items.set(i, stack);
        }
        inventory.setChanged();
        player.inventoryMenu.broadcastChanges();
        if (player.containerMenu != player.inventoryMenu) {
            player.containerMenu.broadcastChanges();
        }
    }

    private static List<ItemStack> mergeStacks(List<ItemStack> stacks) {
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            if (!stack.isStackable()) {
                merged.add(stack.copy());
                continue;
            }
            int remaining = stack.getCount();
            for (ItemStack existing : merged) {
                if (remaining <= 0) {
                    break;
                }
                if (!existing.isStackable()) {
                    continue;
                }
                if (!ItemStack.isSameItemSameComponents(existing, stack)) {
                    continue;
                }
                int maxSize = existing.getMaxStackSize();
                int space = maxSize - existing.getCount();
                if (space <= 0) {
                    continue;
                }
                int move = Math.min(space, remaining);
                existing.grow(move);
                remaining -= move;
            }
            while (remaining > 0) {
                ItemStack split = stack.copy();
                int maxSize = split.getMaxStackSize();
                int count = Math.min(maxSize, remaining);
                split.setCount(count);
                merged.add(split);
                remaining -= count;
            }
        }
        return merged;
    }

    private static final Comparator<ItemStack> INVENTORY_COMPARATOR =
        Comparator.comparingInt(InventorySortService::getCategoryWeight)
            .thenComparing(InventorySortService::getRegistryKey)
            .thenComparingInt(InventorySortService::getDamageValue)
            .thenComparingInt(stack -> -stack.getCount());

    private static int getCategoryWeight(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ArmorItem) {
            return 0;
        }
        var holder = item.builtInRegistryHolder();
        if (holder.is(ItemTags.SWORDS) || holder.is(ItemTags.AXES)) {
            return 0;
        }
        if (holder.is(ItemTags.PICKAXES) || holder.is(ItemTags.SHOVELS) || holder.is(ItemTags.HOES)) {
            return 0;
        }
        if (item instanceof BlockItem) {
            return 1;
        }
        if (stack.has(DataComponents.FOOD)) {
            return 2;
        }
        return 3;
    }

    private static String getRegistryKey(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static int getDamageValue(ItemStack stack) {
        return stack.isDamageableItem() ? stack.getDamageValue() : 0;
    }
}
