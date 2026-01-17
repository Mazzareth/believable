package app.masterwork.believable.client.screen.sections;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.race.BasicRace;
import app.masterwork.believable.race.Race;
import app.masterwork.believable.race.RaceDefinitions;
import app.masterwork.believable.stats.StatModifier;
import app.masterwork.believable.client.screen.components.ThemedButton;
import app.masterwork.believable.client.screen.components.ThemedInfoPanel;
import app.masterwork.believable.client.screen.layout.PanelLayout;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Renders and wires the race selection panel.
 */
public final class RaceSection {
    public interface Host {
        Font font();

        PanelLayout panelLayout();

        int panelPadding();

        int raceButtonHeight();

        int raceSectionGap();

        PlayerAttachmentStorage playerStorage();

        boolean characterTabSelected();

        boolean raceSelectionActive();

        boolean raceInfoTabSelected();

        String selectedRaceKey();

        void renderSectionHeader(GuiGraphics graphics, Component label, int x, int y);

        void renderInfoBox(GuiGraphics graphics, int x, int y, int width, int height);

        void renderDescriptionText(GuiGraphics graphics, int x, int y, int width, int height, String description);

        void renderStatModifierText(GuiGraphics graphics, int x, int y, int width, int height, List<StatModifier> modifiers, String emptyKey);

        void renderModifierText(GuiGraphics graphics, int x, int y, int width, int height, Race race);

        String statModifierLabel(List<StatModifier> modifiers, String emptyKey);

        String raceModifierLabel(Race race);

        void addContentButton(Button button);

        void selectRace(String raceKey, BasicRace race);
    }

    public void render(Host host,
                       GuiGraphics graphics,
                       PanelLayout layout,
                       String raceSelectedKey,
                       String raceDescriptionKey,
                       String raceStatsHeaderKey,
                       String raceModifiersHeaderKey,
                       String raceStatsKey,
                       String raceNoneKey) {
        PlayerAttachmentStorage storage = host.playerStorage();
        int textX = layout.contentX() + host.panelPadding();
        int textY = layout.contentY() + 24;
        if (!host.characterTabSelected() || storage == null) {
            return;
        }
        if (host.raceSelectionActive()) {
            String selectedRaceKey = host.selectedRaceKey();
            BasicRace selectedRace = RaceDefinitions.getByTabKey(selectedRaceKey);
            if (selectedRace == null) {
                return;
            }
            renderSelectionPanel(host, graphics, layout, textX, textY, selectedRace, raceDescriptionKey, raceStatsHeaderKey, raceModifiersHeaderKey, raceNoneKey);
            return;
        }
        Race race = storage.getRace();
        if (race == null || !host.raceInfoTabSelected()) {
            return;
        }
        drawRaceInfoPanel(
            host,
            graphics,
            layout,
            race,
            raceSelectedKey,
            raceDescriptionKey,
            raceStatsHeaderKey,
            raceModifiersHeaderKey,
            raceNoneKey
        );
    }

    public void rebuildButtons(Host host, String selectKey) {
        PlayerAttachmentStorage storage = host.playerStorage();
        if (storage == null || storage.getRace() != null) {
            return;
        }
        String selectedRaceKey = host.selectedRaceKey();
        BasicRace selectedRace = RaceDefinitions.getByTabKey(selectedRaceKey);
        if (selectedRace == null) {
            return;
        }
        PanelLayout layout = host.panelLayout();
        int buttonWidth = Math.min(180, layout.contentW() - host.panelPadding() * 2);
        int buttonX = layout.contentX() + host.panelPadding();
        int buttonY = layout.contentY() + layout.contentH() - host.panelPadding() - host.raceButtonHeight();
        host.addContentButton(new ThemedButton(
            buttonX,
            buttonY,
            buttonWidth,
            host.raceButtonHeight(),
            Component.translatable(selectKey),
            button -> host.selectRace(selectedRaceKey, selectedRace)
        ));
    }

    private void drawRaceInfoPanel(Host host,
                                   GuiGraphics graphics,
                                   PanelLayout layout,
                                   Race race,
                                   String raceSelectedKey,
                                   String raceDescriptionKey,
                                   String raceStatsHeaderKey,
                                   String raceModifiersHeaderKey,
                                   String raceNoneKey) {
        int panelX = layout.contentX() + host.panelPadding();
        int panelY = layout.contentY() + 10;
        int panelW = Math.max(0, layout.contentW() - host.panelPadding() * 2);
        int panelH = Math.max(0, layout.contentH() - host.panelPadding() * 2 - 4);
        if (panelW <= 0 || panelH <= 0) {
            return;
        }

        Component header = Component.translatable(raceSelectedKey, race.name());
        int contentTop = ThemedInfoPanel.drawPanelWithHeader(graphics, host.font(), header, panelX, panelY, panelW, panelH);
        int innerX = panelX + host.panelPadding();
        int innerRight = panelX + panelW - host.panelPadding();
        int innerWidth = Math.max(0, innerRight - innerX);
        int innerBottom = panelY + panelH - host.panelPadding();
        int availableHeight = Math.max(0, innerBottom - contentTop);
        if (innerWidth <= 0 || availableHeight <= 0) {
            return;
        }

        int gap = host.raceSectionGap();
        int sectionHeight = Math.max(0, availableHeight - gap * 2);
        int descriptionHeight = (int) (sectionHeight * 0.5f);
        int statsHeight = (int) (sectionHeight * 0.25f);
        int modifiersHeight = sectionHeight - descriptionHeight - statsHeight;
        int minSection = 44;
        if (statsHeight < minSection || modifiersHeight < minSection) {
            statsHeight = Math.min(minSection, sectionHeight / 3);
            modifiersHeight = statsHeight;
            descriptionHeight = Math.max(0, sectionHeight - statsHeight - modifiersHeight);
        }

        int descriptionY = contentTop;
        int statsY = descriptionY + descriptionHeight + gap;
        int modifiersY = statsY + statsHeight + gap;

        int descriptionTextY = ThemedInfoPanel.drawSectionHeader(
            graphics,
            host.font(),
            Component.translatable(raceDescriptionKey),
            innerX,
            descriptionY,
            innerWidth
        );
        host.renderDescriptionText(
            graphics,
            innerX,
            descriptionTextY,
            innerWidth,
            Math.max(0, descriptionHeight - (descriptionTextY - descriptionY)),
            race.description()
        );

        int statsTextY = ThemedInfoPanel.drawSectionHeader(
            graphics,
            host.font(),
            Component.translatable(raceStatsHeaderKey),
            innerX,
            statsY,
            innerWidth
        );
        host.renderStatModifierText(
            graphics,
            innerX,
            statsTextY,
            innerWidth,
            Math.max(0, statsHeight - (statsTextY - statsY)),
            race.statModifiers(),
            raceNoneKey
        );

        int modifiersTextY = ThemedInfoPanel.drawSectionHeader(
            graphics,
            host.font(),
            Component.translatable(raceModifiersHeaderKey),
            innerX,
            modifiersY,
            innerWidth
        );
        host.renderModifierText(
            graphics,
            innerX,
            modifiersTextY,
            innerWidth,
            Math.max(0, modifiersHeight - (modifiersTextY - modifiersY)),
            race
        );
    }

    private void renderSelectionPanel(Host host,
                                      GuiGraphics graphics,
                                      PanelLayout layout,
                                      int textX,
                                      int textY,
                                      Race race,
                                      String raceDescriptionKey,
                                      String raceStatsHeaderKey,
                                      String raceModifiersHeaderKey,
                                      String raceNoneKey) {
        int contentRight = layout.contentX() + layout.contentW() - host.panelPadding();
        int contentBottom = layout.contentY() + layout.contentH() - host.panelPadding();
        int buttonSpace = host.raceButtonHeight() + host.raceSectionGap();
        int panelBottom = contentBottom - buttonSpace;
        int lineHeight = host.font().lineHeight;
        int cursorY = textY + lineHeight + host.raceSectionGap();

        int availableHeight = Math.max(0, panelBottom - cursorY);
        if (availableHeight <= 0) {
            return;
        }
        int gapTotal = host.raceSectionGap() * 2;
        int sectionHeight = Math.max(0, availableHeight - gapTotal);
        int descriptionHeight = (int) (sectionHeight * 0.5f);
        int statsHeight = (int) (sectionHeight * 0.25f);
        int modifiersHeight = sectionHeight - descriptionHeight - statsHeight;
        int minSection = 44;
        if (statsHeight < minSection || modifiersHeight < minSection) {
            statsHeight = Math.min(minSection, sectionHeight / 3);
            modifiersHeight = statsHeight;
            descriptionHeight = Math.max(0, sectionHeight - statsHeight - modifiersHeight);
        }

        int descriptionY = cursorY;
        int statsY = descriptionY + descriptionHeight + host.raceSectionGap();
        int modifiersY = statsY + statsHeight + host.raceSectionGap();
        int boxWidth = Math.max(0, contentRight - textX);

        host.renderSectionHeader(graphics, Component.translatable(raceDescriptionKey), textX, descriptionY - lineHeight - 2);
        host.renderInfoBox(graphics, textX, descriptionY, boxWidth, descriptionHeight);
        host.renderDescriptionText(graphics, textX, descriptionY, boxWidth, descriptionHeight, race.description());

        host.renderSectionHeader(graphics, Component.translatable(raceStatsHeaderKey), textX, statsY - lineHeight - 2);
        host.renderInfoBox(graphics, textX, statsY, boxWidth, statsHeight);
        host.renderStatModifierText(graphics, textX, statsY, boxWidth, statsHeight, race.statModifiers(), raceNoneKey);

        host.renderSectionHeader(graphics, Component.translatable(raceModifiersHeaderKey), textX, modifiersY - lineHeight - 2);
        host.renderInfoBox(graphics, textX, modifiersY, boxWidth, modifiersHeight);
        host.renderModifierText(graphics, textX, modifiersY, boxWidth, modifiersHeight, race);
    }
}
