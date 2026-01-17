package app.masterwork.believable.client.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public final class UiBar {
    private final int width;
    private final int height;
    private final int backgroundColor;
    private final int fillColor;
    private final int borderColor;
    private final int textColor;

    public UiBar(int width, int height, int backgroundColor, int fillColor, int borderColor, int textColor) {
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.textColor = textColor;
    }

    public int getHeight() {
        return height;
    }

    public void render(GuiGraphics graphics, Font font, int x, int y, double value, double maxValue, String text) {
        float percent = maxValue <= 0.0D ? 0.0F : (float) (value / maxValue);
        percent = Mth.clamp(percent, 0.0F, 1.0F);
        int filledWidth = Math.round(width * percent);

        graphics.fill(x, y, x + width, y + height, backgroundColor);
        if (filledWidth > 0) {
            graphics.fill(x, y, x + filledWidth, y + height, fillColor);
        }
        drawBorder(graphics, x, y);

        int textWidth = font.width(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - font.lineHeight) / 2;
        graphics.drawString(font, text, textX, textY, textColor, true);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + width, y + 1, borderColor);
        graphics.fill(x, y + height - 1, x + width, y + height, borderColor);
        graphics.fill(x, y, x + 1, y + height, borderColor);
        graphics.fill(x + width - 1, y, x + width, y + height, borderColor);
    }
}
