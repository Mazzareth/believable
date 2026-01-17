package app.masterwork.believable.client.screen;

import app.masterwork.believable.client.screen.components.ThemedButton;
import app.masterwork.believable.client.screen.components.ThemedFrame;
import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import app.masterwork.believable.menu.SalvageMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SalvageScreen extends AbstractContainerScreen<SalvageMenu> {
    private static final int HEADER_HEIGHT = 18;
    private static final int SLOT_SIZE = 18;
    private final int rows;

    public SalvageScreen(SalvageMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.rows = menu.getRowCount();
        this.imageWidth = 176;
        this.imageHeight = 114 + this.rows * 18 + SalvageMenu.EXTRA_GAP;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        Component label = Component.translatable("screen.believable.salvage.toggle_mode");
        int buttonHeight = 14;
        int buttonWidth = Math.max(52, this.font.width(label) + 12);
        int x = this.leftPos + this.imageWidth - buttonWidth - 8;
        int y = this.topPos + 2;
        addRenderableWidget(new ThemedButton(x, y, buttonWidth, buttonHeight, label, button -> toggleMode()));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ThemedFrame.drawPanelWell(graphics, x, y, this.imageWidth, this.imageHeight);
        ThemedFrame.drawGildedFrame(graphics, x, y, this.imageWidth, this.imageHeight);
        graphics.fill(x + 2, y + 2, x + this.imageWidth - 2, y + HEADER_HEIGHT, UnbelievableTheme.CARD_HEADER);
        graphics.fill(x + 2, y + HEADER_HEIGHT - 1, x + this.imageWidth - 2, y + HEADER_HEIGHT, UnbelievableTheme.GOLD_DARK);

        int playerStartY = 103 + (this.rows - 4) * SLOT_SIZE + SalvageMenu.EXTRA_GAP;
        int topHeight = this.rows * SLOT_SIZE + HEADER_HEIGHT;
        int gapTop = y + topHeight;
        int gapBottom = y + playerStartY;
        graphics.fill(x + 2, gapTop, x + this.imageWidth - 2, gapBottom, UnbelievableTheme.BOX_FILL);
        graphics.fill(x + 2, gapTop, x + this.imageWidth - 2, gapTop + 1, UnbelievableTheme.BOX_BORDER);
        graphics.fill(x + 2, gapBottom - 1, x + this.imageWidth - 2, gapBottom, UnbelievableTheme.BOX_BORDER);

        ThemedFrame.drawSlotGrid(graphics, x + 8, y + 18, this.rows, 9, SLOT_SIZE);
        ThemedFrame.drawSlotGrid(graphics, x + 8, y + playerStartY, 3, 9, SLOT_SIZE);
        int hotbarY = playerStartY + 58;
        ThemedFrame.drawSlotGrid(graphics, x + 8, y + hotbarY, 1, 9, SLOT_SIZE);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, UnbelievableTheme.TEXT_TITLE, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, UnbelievableTheme.TEXT_SOFT, false);
        String modeKey = this.menu.isTrashMode()
            ? "screen.believable.salvage.mode.trash"
            : "screen.believable.salvage.mode.salvage";
        Component hint = Component.translatable("screen.believable.salvage.hint", Component.translatable(modeKey));
        int playerStartY = 103 + (this.rows - 4) * SLOT_SIZE + SalvageMenu.EXTRA_GAP;
        int gapTop = this.rows * SLOT_SIZE + HEADER_HEIGHT;
        int gapBottom = playerStartY;
        int maxWidth = this.imageWidth - 16;
        int availableHeight = Math.max(0, gapBottom - gapTop);
        var lines = this.font.split(hint, maxWidth);
        int lineHeight = this.font.lineHeight;
        int totalHeight = lines.size() * lineHeight;
        int hintY = gapTop + Math.max(0, (availableHeight - totalHeight) / 2);
        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(this.font, lines.get(i), 8, hintY + i * lineHeight, UnbelievableTheme.TEXT_MUTED, false);
        }
    }

    private void toggleMode() {
        Minecraft minecraft = this.minecraft;
        if (minecraft == null || minecraft.gameMode == null) {
            return;
        }
        minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, SalvageMenu.BUTTON_TOGGLE_TRASH);
    }

}
