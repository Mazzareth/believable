package app.masterwork.believable.client.screen.components;

import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Draws an ornate info panel using the Unbelievable theme palette.
 */
public final class ThemedInfoPanel {
    private static final int EDGE_INSET = 3;
    private static final int HEADER_MIN_HEIGHT = 18;
    private static final int HEADER_PADDING = 8;

    public static int drawPanelWithHeader(GuiGraphics graphics,
                                          Font font,
                                          Component label,
                                          int x,
                                          int y,
                                          int width,
                                          int height) {
        if (width <= 0 || height <= 0) {
            return y;
        }
        int right = x + width;
        int bottom = y + height;

        graphics.fill(x, y, right, bottom, UnbelievableTheme.PANEL_SHADOW);
        graphics.fill(x + 1, y + 1, right - 1, bottom - 1, UnbelievableTheme.PANEL_OUTER);
        graphics.fill(x + 2, y + 2, right - 2, bottom - 2, UnbelievableTheme.PANEL_INNER);
        graphics.fill(x + EDGE_INSET, y + EDGE_INSET, right - EDGE_INSET, bottom - EDGE_INSET, UnbelievableTheme.BOX_FILL);

        int headerHeight = Math.min(26, Math.max(HEADER_MIN_HEIGHT, font.lineHeight + 10));
        int headerTop = y + EDGE_INSET;
        int headerBottom = Math.min(bottom - EDGE_INSET, headerTop + headerHeight);

        graphics.fillGradient(
            x + EDGE_INSET,
            headerTop,
            right - EDGE_INSET,
            headerBottom,
            UnbelievableTheme.TAB_SELECTED,
            UnbelievableTheme.TAB_BASE
        );
        graphics.fill(x + EDGE_INSET, headerBottom - 1, right - EDGE_INSET, headerBottom, UnbelievableTheme.GOLD_BRIGHT);
        graphics.fill(x + EDGE_INSET + 2, headerTop + 2, right - EDGE_INSET - 2, headerTop + 3, UnbelievableTheme.GOLD_FAINT);

        int textY = headerTop + (headerHeight - font.lineHeight) / 2;
        graphics.drawString(font, label, x + EDGE_INSET + HEADER_PADDING, textY, UnbelievableTheme.TEXT_PRIMARY, false);

        int cap = Math.min(12, width / 6);
        graphics.fill(x + EDGE_INSET + 2, headerTop + 2, x + EDGE_INSET + 2 + cap, headerTop + 4, UnbelievableTheme.GOLD_BRIGHT);
        graphics.fill(right - EDGE_INSET - 2 - cap, headerTop + 2, right - EDGE_INSET - 2, headerTop + 4, UnbelievableTheme.GOLD_BRIGHT);

        return headerBottom + HEADER_PADDING;
    }

    public static int drawSectionHeader(GuiGraphics graphics, Font font, Component label, int x, int y, int width) {
        graphics.drawString(font, label, x, y, UnbelievableTheme.TEXT_MUTED, false);
        int lineX = x + font.width(label) + 6;
        int lineY = y + font.lineHeight / 2;
        int right = x + width;
        if (lineX < right) {
            graphics.fill(lineX, lineY, right, lineY + 1, UnbelievableTheme.GOLD_DARK);
            graphics.fill(lineX, lineY + 1, right, lineY + 2, UnbelievableTheme.GOLD_FAINT);
        }
        return y + font.lineHeight + 4;
    }

    private ThemedInfoPanel() {
    }
}
