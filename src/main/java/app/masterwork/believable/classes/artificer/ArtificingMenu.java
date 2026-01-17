package app.masterwork.believable.classes.artificer;

import app.masterwork.believable.classes.smith.SmithService;
import app.masterwork.believable.item.ItemAffixData;
import app.masterwork.believable.item.ItemAffixService;
import app.masterwork.believable.item.ItemCategory;
import app.masterwork.believable.item.ItemRarity;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.registry.ModMenus;
import app.masterwork.believable.stats.PlayerProgressionStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class ArtificingMenu extends AbstractContainerMenu {
    public static final int SLOT_SIZE = 18;
    public static final int SLOT_X = 80;
    public static final int SLOT_Y = 54;
    public static final int PLAYER_INV_START_Y = 208;
    public static final int HOTBAR_START_Y = PLAYER_INV_START_Y + 58;
    public static final int BUTTON_REROLL_RANDOM = 0;
    public static final int BUTTON_REROLL_SAME_RARITY = 1;
    public static final int BUTTON_REROLL_VALUES = 2;
    public static final int BUTTON_ADD_AFFIX = 3;
    public static final int BUTTON_SELECT_AFFIX = 4;
    public static final int BUTTON_REROLL_SELECTED = 5;

    private static final int COMPLEXITY_SAME_RARITY = 2;
    private static final int COMPLEXITY_REROLL_VALUES = 4;
    private static final int COMPLEXITY_ADD_AFFIX = 6;
    private static final int COMPLEXITY_REROLL_SELECTED = 8;

    private static final int COST_REROLL_RANDOM = 2;
    private static final int COST_REROLL_SAME_RARITY = 3;
    private static final int COST_REROLL_VALUES = 2;
    private static final int COST_ADD_AFFIX = 5;
    private static final int COST_REROLL_SELECTED = 4;

    private final Container targetContainer;
    private final Player player;
    private int syncedSalvage;
    private int syncedComplexity;
    private int selectedAffixIndex = -1;

    public ArtificingMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(1));
    }

    public ArtificingMenu(int containerId, Inventory inventory, Container targetContainer) {
        super(ModMenus.ARTIFICING_MENU.get(), containerId);
        this.player = inventory.player;
        this.targetContainer = targetContainer;
        checkContainerSize(targetContainer, 1);

        addSlot(new ArtificingSlot(targetContainer, 0, SLOT_X, SLOT_Y));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * SLOT_SIZE, PLAYER_INV_START_Y + row * SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * SLOT_SIZE, HOTBAR_START_Y));
        }

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return syncedSalvage;
            }

            @Override
            public void set(int value) {
                syncedSalvage = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return syncedComplexity;
            }

            @Override
            public void set(int value) {
                syncedComplexity = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return selectedAffixIndex;
            }

            @Override
            public void set(int value) {
                selectedAffixIndex = value;
            }
        });
        if (inventory.player instanceof ServerPlayer serverPlayer) {
            refreshSyncedValues(serverPlayer);
        }
        updateSelectedAffixIndex();
    }

    @Override
    public void broadcastChanges() {
        if (player instanceof ServerPlayer serverPlayer) {
            refreshSyncedValues(serverPlayer);
        }
        updateSelectedAffixIndex();
        super.broadcastChanges();
    }

    public int getSyncedSalvage() {
        return syncedSalvage;
    }

    public int getSyncedComplexity() {
        return syncedComplexity;
    }

    public int getSelectedAffixIndex() {
        return selectedAffixIndex;
    }

    public static int getCostForButton(int id) {
        return switch (id) {
            case BUTTON_REROLL_RANDOM -> COST_REROLL_RANDOM;
            case BUTTON_REROLL_SAME_RARITY -> COST_REROLL_SAME_RARITY;
            case BUTTON_REROLL_VALUES -> COST_REROLL_VALUES;
            case BUTTON_ADD_AFFIX -> COST_ADD_AFFIX;
            case BUTTON_REROLL_SELECTED -> COST_REROLL_SELECTED;
            default -> 0;
        };
    }

    public static int getRequiredComplexity(int id) {
        return switch (id) {
            case BUTTON_REROLL_RANDOM -> 0;
            case BUTTON_REROLL_SAME_RARITY -> COMPLEXITY_SAME_RARITY;
            case BUTTON_REROLL_VALUES -> COMPLEXITY_REROLL_VALUES;
            case BUTTON_ADD_AFFIX -> COMPLEXITY_ADD_AFFIX;
            case BUTTON_SELECT_AFFIX -> 0;
            case BUTTON_REROLL_SELECTED -> COMPLEXITY_REROLL_SELECTED;
            default -> Integer.MAX_VALUE;
        };
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        if (!ArtificerService.isArtificer(serverPlayer)) {
            serverPlayer.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("message.believable.artificing.not_artificer"),
                true
            );
            return false;
        }
        int complexity = ArtificerService.getComplexity(serverPlayer);
        int required = getRequiredComplexity(id);
        if (complexity < required) {
            serverPlayer.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("message.believable.artificing.low_complexity"),
                true
            );
            return false;
        }
        ItemStack stack = targetContainer.getItem(0);
        if (stack.isEmpty() || !ItemAffixService.canRollAffixes(stack)) {
            serverPlayer.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("message.believable.artificing.invalid_item"),
                true
            );
            return false;
        }
        if (id == BUTTON_SELECT_AFFIX) {
            if (!ItemAffixService.hasAffixes(stack)) {
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.believable.artificing.no_affixes"),
                    true
                );
                return false;
            }
            cycleSelectedAffix(stack);
            return true;
        }
        if (id == BUTTON_REROLL_VALUES && !ItemAffixService.hasAffixes(stack)) {
            serverPlayer.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("message.believable.artificing.no_affixes"),
                true
            );
            return false;
        }
        if (id == BUTTON_REROLL_SELECTED) {
            if (!ItemAffixService.hasAffixes(stack)) {
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.believable.artificing.no_affixes"),
                    true
                );
                return false;
            }
            List<ItemAffixData> affixes = ItemAffixService.getAffixes(stack);
            if (affixes.isEmpty() || selectedAffixIndex < 0 || selectedAffixIndex >= affixes.size()) {
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.believable.artificing.no_affixes"),
                    true
                );
                return false;
            }
        }
        if (id == BUTTON_ADD_AFFIX && ItemAffixService.hasAffixes(stack)) {
            int maxAffixes = ItemAffixService.getRarity(stack).getMaxAffixes();
            if (ItemAffixService.getAffixes(stack).size() >= maxAffixes) {
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.believable.artificing.full_affixes"),
                    true
                );
                return false;
            }
        }
        int cost = getCostForButton(id);
        if (cost <= 0 || !ArtificerService.spendSalvage(serverPlayer, cost)) {
            serverPlayer.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("message.believable.artificing.no_salvage"),
                true
            );
            return false;
        }
        ItemCategory category = ItemAffixService.hasAffixes(stack)
            ? ItemAffixService.getCategory(stack)
            : ItemAffixService.getCategoryForAffixes(stack);
        if (category == null) {
            return false;
        }
        int playerLevel = getPlayerLevel(serverPlayer);
        int rollLevel = SmithService.getQualityRollLevel(serverPlayer, playerLevel);
        boolean applied = true;
        switch (id) {
            case BUTTON_REROLL_RANDOM -> ItemAffixService.rollAffixes(
                stack,
                category,
                serverPlayer.getRandom(),
                rollLevel
            );
            case BUTTON_REROLL_SAME_RARITY -> {
                ItemRarity rarity = ItemAffixService.hasAffixes(stack)
                    ? ItemAffixService.getRarity(stack)
                    : ItemRarity.roll(serverPlayer.getRandom(), rollLevel);
                ItemAffixService.rollAffixesWithRarity(
                    stack,
                    category,
                    serverPlayer.getRandom(),
                    rarity,
                    ItemAffixService.AffixContext.EMPTY
                );
            }
            case BUTTON_REROLL_VALUES -> ItemAffixService.rerollAffixValues(stack, serverPlayer.getRandom());
            case BUTTON_ADD_AFFIX -> {
                if (!ItemAffixService.hasAffixes(stack)) {
                    ItemAffixService.rollAffixes(stack, category, serverPlayer.getRandom(), rollLevel);
                } else {
                    applied = ItemAffixService.addRandomAffix(stack, serverPlayer.getRandom());
                }
            }
            case BUTTON_REROLL_SELECTED -> applied = ItemAffixService.rerollSingleAffix(
                stack,
                selectedAffixIndex,
                serverPlayer.getRandom()
            );
            default -> {
                return false;
            }
        }
        if (!applied) {
            ArtificerService.addSalvage(serverPlayer, cost);
            return false;
        }
        targetContainer.setChanged();
        return true;
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
        if (index == 0) {
            if (!moveItemStackTo(stack, 1, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, 1, false)) {
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
    public void removed(Player player) {
        if (!player.level().isClientSide()) {
            ItemStack stack = targetContainer.removeItemNoUpdate(0);
            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
        super.removed(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private void refreshSyncedValues(ServerPlayer serverPlayer) {
        syncedSalvage = ArtificerService.getSalvage(serverPlayer);
        syncedComplexity = ArtificerService.getComplexity(serverPlayer);
    }

    private void updateSelectedAffixIndex() {
        ItemStack stack = targetContainer.getItem(0);
        if (stack.isEmpty() || !ItemAffixService.hasAffixes(stack)) {
            selectedAffixIndex = -1;
            return;
        }
        List<ItemAffixData> affixes = ItemAffixService.getAffixes(stack);
        if (affixes.isEmpty()) {
            selectedAffixIndex = -1;
            return;
        }
        if (selectedAffixIndex < 0 || selectedAffixIndex >= affixes.size()) {
            selectedAffixIndex = 0;
        }
    }

    private void cycleSelectedAffix(ItemStack stack) {
        List<ItemAffixData> affixes = ItemAffixService.getAffixes(stack);
        if (affixes.isEmpty()) {
            selectedAffixIndex = -1;
            return;
        }
        if (selectedAffixIndex < 0) {
            selectedAffixIndex = 0;
        } else {
            selectedAffixIndex = (selectedAffixIndex + 1) % affixes.size();
        }
    }

    private int getPlayerLevel(ServerPlayer serverPlayer) {
        PlayerProgressionStorage storage = serverPlayer.getData(ModAttachments.PLAYER_PROGRESSION);
        return storage == null ? 1 : storage.getLevel();
    }

    private static final class ArtificingSlot extends Slot {
        private ArtificingSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack != null && !stack.isEmpty() && ItemAffixService.canRollAffixes(stack);
        }
    }
}
