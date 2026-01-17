package app.masterwork.believable.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.block.Blocks;

public class SmithingCraftingMenu extends CraftingMenu {
    private final ContainerLevelAccess smithingAccess;

    public SmithingCraftingMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public SmithingCraftingMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(containerId, inventory, access);
        this.smithingAccess = access;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.smithingAccess, player, Blocks.SMITHING_TABLE);
    }
}
