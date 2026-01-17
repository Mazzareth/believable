package app.masterwork.believable.client.screen.sections;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.client.screen.components.ThemedButton;
import app.masterwork.believable.client.screen.layout.PanelLayout;
import app.masterwork.believable.magic.BasicMagicSchool;
import app.masterwork.believable.magic.MagicSchoolDefinitions;
import app.masterwork.believable.magic.Spell;
import app.masterwork.believable.magic.SpellDefinitions;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Renders and wires the magic selection + subsection panels.
 */
public final class MagicSection {
    public interface Host {
        Font font();

        PanelLayout panelLayout();

        int panelPadding();

        int raceSectionGap();

        int magicSelectButtonHeight();

        float magicDescriptionHeaderScale();

        PlayerAttachmentStorage playerStorage();

        boolean magicTabSelected();

        boolean magicSelectionActive();

        String selectedMagicKey();

        String selectedMagicSubKey();

        void renderTabTitle(GuiGraphics graphics, int x, int y, Component label);

        void renderSectionHeader(GuiGraphics graphics, Component label, int x, int y);

        void renderGrandSectionHeader(GuiGraphics graphics, Component label, int x, int y);

        void renderInfoBox(GuiGraphics graphics, int x, int y, int width, int height);

        void renderDescriptionText(GuiGraphics graphics, int x, int y, int width, int height, String description);

        void renderSpellList(GuiGraphics graphics, int x, int y, int width, int height, List<Spell> spells, Component emptyLabel);

        void addContentButton(Button button);

        void selectMagicSchool(String magicKey, BasicMagicSchool school);
    }

    public void render(Host host,
                       GuiGraphics graphics,
                       PanelLayout layout,
                       String magicTabKey,
                       String magicSelectedKey,
                       String magicDescriptionKey,
                       String magicSpellsHeaderKey,
                       String magicSpellsEmptyKey) {
        PlayerAttachmentStorage storage = host.playerStorage();
        int textX = layout.contentX() + host.panelPadding();
        int titleY = layout.contentY() + 10;
        int titleHeight = (int) (host.font().lineHeight * 1.25f);
        int textY = titleY + titleHeight + 8;
        if (!host.magicTabSelected() || storage == null) {
            return;
        }
        host.renderTabTitle(graphics, textX, titleY, Component.translatable(magicTabKey));
        if (host.magicSelectionActive()) {
            String selectedMagicKey = host.selectedMagicKey();
            BasicMagicSchool selectedSchool = MagicSchoolDefinitions.getByTabKey(selectedMagicKey);
            if (selectedSchool == null) {
                return;
            }
            renderSelectionPanel(host, graphics, layout, textX, textY, selectedSchool, magicDescriptionKey);
            return;
        }
        BasicMagicSchool school = storage.getMagicSchool();
        String subKey = host.selectedMagicSubKey();
        if (school == null || subKey == null) {
            return;
        }
        renderSubsectionPanel(host, graphics, layout, textX, textY, school, subKey, magicSelectedKey, magicDescriptionKey, magicSpellsHeaderKey,
            magicSpellsEmptyKey);
    }

    public void rebuildButtons(Host host, String selectKey) {
        PlayerAttachmentStorage storage = host.playerStorage();
        if (storage == null || storage.getMagicSchool() != null) {
            return;
        }
        String selectedMagicKey = host.selectedMagicKey();
        BasicMagicSchool selectedSchool = MagicSchoolDefinitions.getByTabKey(selectedMagicKey);
        if (selectedSchool == null) {
            return;
        }
        PanelLayout layout = host.panelLayout();
        int buttonWidth = Math.max(0, layout.contentW() - host.panelPadding() * 2);
        int buttonX = layout.contentX() + host.panelPadding();
        int buttonY = layout.contentY() + layout.contentH() - host.panelPadding() - host.magicSelectButtonHeight();
        host.addContentButton(new ThemedButton(
            buttonX,
            buttonY,
            buttonWidth,
            host.magicSelectButtonHeight(),
            Component.translatable(selectKey),
            button -> host.selectMagicSchool(selectedMagicKey, selectedSchool)
        ));
    }

    private void renderSelectionPanel(Host host,
                                      GuiGraphics graphics,
                                      PanelLayout layout,
                                      int textX,
                                      int textY,
                                      BasicMagicSchool school,
                                      String magicDescriptionKey) {
        int contentRight = layout.contentX() + layout.contentW() - host.panelPadding();
        int contentBottom = layout.contentY() + layout.contentH() - host.panelPadding();
        int buttonSpace = host.magicSelectButtonHeight() + host.raceSectionGap();
        int panelBottom = contentBottom - buttonSpace;
        int cursorY = textY + host.raceSectionGap();
        int headerHeight = (int) (host.font().lineHeight * host.magicDescriptionHeaderScale());

        host.renderGrandSectionHeader(graphics, Component.translatable(magicDescriptionKey), textX, cursorY);
        int descriptionY = cursorY + headerHeight + 6;
        int availableHeight = Math.max(0, panelBottom - descriptionY);
        if (availableHeight <= 0) {
            return;
        }
        int descriptionHeight = Math.max(0, availableHeight);
        int boxWidth = Math.max(0, contentRight - textX);

        host.renderInfoBox(graphics, textX, descriptionY, boxWidth, descriptionHeight);
        host.renderDescriptionText(graphics, textX, descriptionY, boxWidth, descriptionHeight, school.description());
    }

    private void renderSubsectionPanel(Host host,
                                       GuiGraphics graphics,
                                       PanelLayout layout,
                                       int textX,
                                       int textY,
                                       BasicMagicSchool school,
                                       String subKey,
                                       String magicSelectedKey,
                                       String magicDescriptionKey,
                                       String magicSpellsHeaderKey,
                                       String magicSpellsEmptyKey) {
        int lineHeight = host.font().lineHeight;
        graphics.drawString(
            host.font(),
            Component.translatable(magicSelectedKey, school.name()),
            textX,
            textY,
            0xFFDFC98C,
            false
        );

        int contentRight = layout.contentX() + layout.contentW() - host.panelPadding();
        int contentBottom = layout.contentY() + layout.contentH() - host.panelPadding();
        int headerY = textY + lineHeight + host.raceSectionGap();
        int descriptionY = headerY + lineHeight + 4;
        int boxWidth = Math.max(0, contentRight - textX);
        int availableHeight = Math.max(0, contentBottom - descriptionY);

        host.renderSectionHeader(graphics, Component.translatable(magicDescriptionKey), textX, headerY);
        int sectionHeight = Math.max(0, availableHeight - host.raceSectionGap());
        int descriptionHeight = (int) (sectionHeight * 0.45f);
        int spellsHeight = sectionHeight - descriptionHeight;
        int minSection = 44;
        if (descriptionHeight < minSection || spellsHeight < minSection) {
            descriptionHeight = Math.min(minSection, sectionHeight / 2);
            spellsHeight = Math.max(0, sectionHeight - descriptionHeight);
        }
        host.renderInfoBox(graphics, textX, descriptionY, boxWidth, descriptionHeight);

        String description = MagicSchoolDefinitions.getSubsectionDescription(subKey);
        if (description == null) {
            description = school.description();
        }
        host.renderDescriptionText(graphics, textX, descriptionY, boxWidth, descriptionHeight, description);

        int spellsY = descriptionY + descriptionHeight + host.raceSectionGap();
        spellsHeight = Math.max(0, contentBottom - spellsY);
        if (spellsHeight <= 0) {
            return;
        }
        host.renderSectionHeader(graphics, Component.translatable(magicSpellsHeaderKey), textX, spellsY - lineHeight - 2);
        host.renderInfoBox(graphics, textX, spellsY, boxWidth, spellsHeight);

        List<Spell> spells = SpellDefinitions.getBySchoolAndSubsection(school.id(), subKey);
        if (spells.isEmpty()
            && (MagicSchoolDefinitions.TAB_MAGIC_SPELLS_KEY.equals(subKey)
            || MagicSchoolDefinitions.TAB_MAGIC_OVERVIEW_KEY.equals(subKey))) {
            spells = SpellDefinitions.getBySchoolId(school.id());
        }
        host.renderSpellList(graphics, textX, spellsY, boxWidth, spellsHeight, spells, Component.translatable(magicSpellsEmptyKey));
    }
}
