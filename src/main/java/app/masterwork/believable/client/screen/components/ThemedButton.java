package app.masterwork.believable.client.screen.components;

import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Button styled to match the Unbelievable menu theme.
 */
public class ThemedButton extends Button {
    public ThemedButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int border = this.active ? UnbelievableTheme.BUTTON_BORDER : UnbelievableTheme.BUTTON_BORDER_DISABLED;
        int fill = this.active
            ? (this.isHoveredOrFocused() ? UnbelievableTheme.BUTTON_FILL_HOVER : UnbelievableTheme.BUTTON_FILL)
            : UnbelievableTheme.BUTTON_FILL_DISABLED;
        int textColor = this.active ? UnbelievableTheme.BUTTON_TEXT : UnbelievableTheme.BUTTON_TEXT_DISABLED;

        int x = this.getX();
        int y = this.getY();
        graphics.fill(x, y, x + this.width, y + this.height, fill);
        graphics.fill(x, y, x + this.width, y + 1, border);
        graphics.fill(x, y + this.height - 1, x + this.width, y + this.height, border);
        graphics.fill(x, y, x + 1, y + this.height, border);
        graphics.fill(x + this.width - 1, y, x + this.width, y + this.height, border);
        if (this.height > 2 && this.width > 2) {
            int highlight = this.active ? UnbelievableTheme.GOLD_FAINT : UnbelievableTheme.PANEL_SHADOW;
            graphics.fill(x + 1, y + 1, x + this.width - 1, y + 2, highlight);
        }

        int textX = x + this.width / 2;
        int textY = y + (this.height - Minecraft.getInstance().font.lineHeight) / 2;
        graphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), textX, textY, textColor);
    }
}
