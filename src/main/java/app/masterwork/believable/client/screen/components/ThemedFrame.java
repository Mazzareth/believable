package app.masterwork.believable.client.screen.components;

import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Shared frame + slot drawing helpers for themed screens.
 */
public final class ThemedFrame {
    private ThemedFrame() {
    }

    public static void drawPanelWell(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, UnbelievableTheme.PANEL_SHADOW);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, UnbelievableTheme.PANEL_OUTER);
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, UnbelievableTheme.PANEL_INNER);
    }

    public static void drawGildedFrame(GuiGraphics graphics, int x, int y, int width, int height) {
        int right = x + width;
        int bottom = y + height;
        graphics.fill(x, y, right, y + 1, UnbelievableTheme.GOLD);
        graphics.fill(x, bottom - 1, right, bottom, UnbelievableTheme.GOLD);
        graphics.fill(x, y, x + 1, bottom, UnbelievableTheme.GOLD);
        graphics.fill(right - 1, y, right, bottom, UnbelievableTheme.GOLD);
        graphics.fill(x + 1, y + 1, right - 1, y + 2, UnbelievableTheme.GOLD_BRIGHT);
        graphics.fill(x + 1, bottom - 2, right - 1, bottom - 1, UnbelievableTheme.GOLD_DARK);
    }

    public static void drawOrnateGildedFrame(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGildedFrame(graphics, x, y, width, height);
        int right = x + width;
        int bottom = y + height;
        int cap = 14;
        graphics.fill(x + 2, y + 2, x + 2 + cap, y + 4, UnbelievableTheme.GOLD_BRIGHT);
        graphics.fill(right - cap - 2, y + 2, right - 2, y + 4, UnbelievableTheme.GOLD_BRIGHT);
        graphics.fill(x + 2, bottom - 4, x + 2 + cap, bottom - 2, UnbelievableTheme.GOLD_DARK);
        graphics.fill(right - cap - 2, bottom - 4, right - 2, bottom - 2, UnbelievableTheme.GOLD_DARK);
    }

    public static void drawSlotGrid(GuiGraphics graphics, int startX, int startY, int rows, int columns,
                                    int slotSize) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = startX + col * slotSize;
                int y = startY + row * slotSize;
                drawSlot(graphics, x, y, slotSize);
            }
        }
    }

    public static void drawSlot(GuiGraphics graphics, int x, int y, int size) {
        int right = x + size;
        int bottom = y + size;
        graphics.fill(x, y, right, bottom, UnbelievableTheme.BOX_FILL);
        graphics.fill(x, y, right, y + 1, UnbelievableTheme.ROW_BORDER);
        graphics.fill(x, bottom - 1, right, bottom, UnbelievableTheme.ROW_BORDER);
        graphics.fill(x, y, x + 1, bottom, UnbelievableTheme.ROW_BORDER);
        graphics.fill(right - 1, y, right, bottom, UnbelievableTheme.ROW_BORDER);
        graphics.fill(x + 1, y + 1, right - 1, y + 2, UnbelievableTheme.GOLD_FAINT);
    }
}
