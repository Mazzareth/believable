package app.masterwork.believable.client.screen.tabs;

import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Custom button that renders a tab with depth-aware styling.
 */
public class TabButton extends Button {
    private final boolean selected;
    private final int depth;

    public TabButton(int x, int y, int width, int height, Component message, OnPress onPress, boolean selected, int depth) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.selected = selected;
        this.depth = depth;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int base = this.selected ? UnbelievableTheme.TAB_SELECTED : UnbelievableTheme.TAB_BASE;
        int hover = this.isHoveredOrFocused() ? UnbelievableTheme.TAB_HOVER : base;
        int depthTint = Math.max(0, 20 - this.depth * 6);
        int tint = 0xFF000000 | (depthTint << 16) | (depthTint << 8) | depthTint;
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, hover);
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, UnbelievableTheme.TAB_EDGE);
        graphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, tint);
        if (this.selected || this.isHoveredOrFocused()) {
            graphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + 2, UnbelievableTheme.GOLD_BRIGHT);
        }
        int textColor = this.selected ? UnbelievableTheme.TEXT_SELECTED : UnbelievableTheme.TEXT_TAB;
        graphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), this.getX() + this.width / 2, this.getY() + 7, textColor);
    }
}
