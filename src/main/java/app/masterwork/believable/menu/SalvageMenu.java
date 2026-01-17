package app.masterwork.believable.menu;

import app.masterwork.believable.attachment.PlayerSalvageStorage;
import app.masterwork.believable.item.ItemAffixService;
import app.masterwork.believable.registry.ModMenus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SalvageMenu extends AbstractContainerMenu {
    public static final int ROWS = 3;
    public static final int COLUMNS = 9;
    public static final int SALVAGE_SLOTS = ROWS * COLUMNS;
    public static final int BUTTON_TOGGLE_TRASH = 0;
    public static final int EXTRA_GAP = 18;

    private final Container salvageContainer;
    private boolean trashMode;

    public SalvageMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new PlayerSalvageStorage());
    }

    public SalvageMenu(int containerId, Inventory inventory, Container salvageContainer) {
        super(ModMenus.SALVAGE_MENU.get(), containerId);
        checkContainerSize(salvageContainer, SALVAGE_SLOTS);
        this.salvageContainer = salvageContainer;
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return trashMode ? 1 : 0;
            }

            @Override
            public void set(int value) {
                trashMode = value != 0;
            }
        });

        int slotIndex = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                addSlot(new SalvageSlot(salvageContainer, slotIndex++, x, y));
            }
        }

        int playerStartY = 103 + (ROWS - 4) * 18 + EXTRA_GAP;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, playerStartY + row * 18));
            }
        }
        int hotbarY = playerStartY + 58;
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, hotbarY));
        }
    }

    public int getRowCount() {
        return ROWS;
    }

    public boolean isTrashMode() {
        return trashMode;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_TOGGLE_TRASH) {
            trashMode = !trashMode;
            return true;
        }
        return false;
    }

    @Override
    public void removed(Player player) {
        if (player instanceof ServerPlayer serverPlayer && salvageContainer instanceof PlayerSalvageStorage storage) {
            if (trashMode) {
                storage.clearContent();
            } else {
                storage.salvageAll(serverPlayer);
            }
        }
        super.removed(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return result;
        }
        ItemStack stack = slot.getItem();
        result = stack.copy();
        if (index < SALVAGE_SLOTS) {
            if (!moveItemStackTo(stack, SALVAGE_SLOTS, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, SALVAGE_SLOTS, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return salvageContainer.stillValid(player);
    }

    private final class SalvageSlot extends Slot {
        private SalvageSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return trashMode || ItemAffixService.canRollAffixes(stack);
        }
    }
}
