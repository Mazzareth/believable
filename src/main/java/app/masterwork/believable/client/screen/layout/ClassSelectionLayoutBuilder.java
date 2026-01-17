package app.masterwork.believable.client.screen.layout;

import net.minecraft.client.gui.Font;

/**
 * Computes panel layout for the class selection questionnaire + class list.
 */
public final class ClassSelectionLayoutBuilder {
    private ClassSelectionLayoutBuilder() {
    }

    public static ClassSelectionLayout build(PanelLayout layout,
                                             Font font,
                                             int panelPadding,
                                             int panelGap,
                                             int buttonHeight,
                                             int buttonGap,
                                             int stepButtonCount) {
        int lineHeight = font.lineHeight;
        int titleY = layout.contentY() + 10;
        int headerY = titleY + (int) (lineHeight * 1.25f) + 8;
        int contentX = layout.contentX() + panelPadding;
        int contentRight = layout.contentX() + layout.contentW() - panelPadding;
        int contentBottom = layout.contentY() + layout.contentH() - panelPadding;
        int availableW = Math.max(0, contentRight - contentX);
        int rightW = Math.min(220, Math.max(160, availableW / 3));
        int leftW = Math.max(0, availableW - panelGap - rightW);
        int leftX = contentX;
        int rightX = leftX + leftW + panelGap;

        int questionHeaderY = headerY;
        int questionBoxY = questionHeaderY + lineHeight + 2;
        int questionBoxH = Math.min(72, Math.max(52, (int) (layout.contentH() * 0.22f)));

        int stepHeaderY = questionBoxY + questionBoxH + panelGap;
        int stepBoxY = stepHeaderY + lineHeight + 2;
        int stepBoxH = stepButtonCount > 0
            ? stepButtonCount * buttonHeight + Math.max(0, stepButtonCount - 1) * buttonGap + 12
            : 0;

        int footerY = contentBottom - buttonHeight;
        int detailHeaderY = stepBoxY + stepBoxH + panelGap;
        int detailBoxY = detailHeaderY + lineHeight + 2;
        int detailBoxH = Math.max(0, footerY - panelGap - detailBoxY);
        int minDetail = 44;
        if (detailBoxH < minDetail) {
            int deficit = minDetail - detailBoxH;
            stepBoxH = Math.max(48, stepBoxH - deficit);
            detailHeaderY = stepBoxY + stepBoxH + panelGap;
            detailBoxY = detailHeaderY + lineHeight + 2;
            detailBoxH = Math.max(0, footerY - panelGap - detailBoxY);
        }

        int listHeaderY = headerY;
        int listBoxY = listHeaderY + lineHeight + 2;
        int listBoxH = Math.max(0, contentBottom - listBoxY);

        return new ClassSelectionLayout(
            leftX,
            leftW,
            rightX,
            rightW,
            questionHeaderY,
            questionBoxY,
            questionBoxH,
            stepHeaderY,
            stepBoxY,
            stepBoxH,
            detailHeaderY,
            detailBoxY,
            detailBoxH,
            listHeaderY,
            listBoxY,
            listBoxH,
            footerY
        );
    }
}
