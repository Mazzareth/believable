package app.masterwork.believable.client.screen.sections;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.client.screen.layout.PanelLayout;
import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import app.masterwork.believable.skills.ActiveSkill;
import app.masterwork.believable.skills.PassiveSkill;
import app.masterwork.believable.skills.Skill;
import app.masterwork.believable.skills.SkillDefinitions;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.masterwork.believable.skills.SkillCategory;

/**
 * Renders and wires the skills panel with slots and available lists.
 */
public final class SkillsSection {
    public interface Host {
        Font font();

        PanelLayout panelLayout();

        int panelPadding();

        int raceSectionGap();

        int raceBoxPadding();

        float mainContentTextScale();

        int mainContentLineGap();

        int skillEquipButtonWidth();

        int skillEquipButtonHeight();

        int skillEquipButtonGap();

        int skillsPassiveSlots();

        int skillsActiveSlots();

        boolean skillsTabSelected();

        boolean skillsPassiveTabSelected();

        boolean skillsActiveTabSelected();

        String selectedSkillsSubKey();

        PlayerAttachmentStorage playerStorage();

        void renderTabTitle(GuiGraphics graphics, int x, int y, Component label);

        void renderSectionHeader(GuiGraphics graphics, Component label, int x, int y);

        void renderInfoBox(GuiGraphics graphics, int x, int y, int width, int height);

        void addContentButton(Button button);

        void refreshContentButtons();

        Component skillNameComponent(String skillId);

        int drawWrappedLinesScaledHost(GuiGraphics graphics,
                List<FormattedCharSequence> lines,
                int x,
                int y,
                int maxLines,
                int color,
                int wrappedIndent,
                float scale);

        void addSkillTooltip(int x, int y, int width, int height, Component description);

        int skillsScroll();

        void setSkillsScroll(int scroll);

        int selectedSkillSlot();

        void setSelectedSkillSlot(int slotIndex);

        SkillCategory selectedSkillCategory();

        void setSelectedSkillCategory(SkillCategory category);

        void equipPassiveSkill(int slotIndex, int skillIndex);

        void equipActiveSkill(int slotIndex, int skillIndex);
    }

    private static final int SKILL_ENTRY_HEIGHT = 30;
    private static final int SKILL_ENTRY_GAP = 6;
    private static final int CATEGORY_TAB_HEIGHT = 20;
    private static final int CATEGORY_TAB_GAP = 4;
    private static final int SLOT_HEIGHT = 46;
    private static final int SLOT_GAP = 8;
    private static final int SLOT_SELECT_BUTTON_WIDTH = 72;
    private static final int SLOT_SELECT_BUTTON_HEIGHT = 18;

    public void render(Host host,
            GuiGraphics graphics,
            PanelLayout layout,
            String tabKey,
            String slotsKey,
            String availableKey,
            String passiveSlotKey,
            String activeSlotKey,
            String emptyPassiveKey,
            String emptyActiveKey) {
        if (!host.skillsTabSelected()) {
            return;
        }

        int panelX = layout.contentX() + host.panelPadding();
        int panelY = layout.contentY() + 10;
        int panelW = Math.max(0, layout.contentW() - host.panelPadding() * 2);
        int panelH = Math.max(0, layout.contentH() - host.panelPadding() * 2 - 4);

        if (panelW <= 0 || panelH <= 0) {
            return;
        }

        Component header = Component.translatable(tabKey);
        int contentTop = app.masterwork.believable.client.screen.components.ThemedInfoPanel.drawPanelWithHeader(
                graphics,
                host.font(),
                header,
                panelX,
                panelY,
                panelW,
                panelH);

        String subKey = host.selectedSkillsSubKey();
        if (subKey == null) {
            return;
        }

        int innerX = panelX + host.panelPadding();
        int innerRight = panelX + panelW - host.panelPadding();
        int innerWidth = Math.max(0, innerRight - innerX);
        int innerBottom = panelY + panelH - host.panelPadding();
        int availableTotalHeight = Math.max(0, innerBottom - contentTop);

        if (innerWidth <= 0 || availableTotalHeight <= 0) {
            return;
        }

        SkillsPanelLayout skillsLayout = buildSkillsPanelLayout(host, innerX, innerWidth, contentTop,
                availableTotalHeight);

        app.masterwork.believable.client.screen.components.ThemedInfoPanel.drawSectionHeader(
                graphics,
                host.font(),
                Component.translatable(slotsKey),
                innerX,
                skillsLayout.slotsHeaderY(),
                innerWidth);
        host.renderInfoBox(graphics, innerX, skillsLayout.slotsBoxY(), skillsLayout.boxWidth(),
                skillsLayout.slotsBoxHeight());

        PlayerAttachmentStorage storage = host.playerStorage();
        String passiveOne = storage != null ? storage.getPassiveSkill(0) : null;
        String passiveTwo = storage != null ? storage.getPassiveSkill(1) : null;
        String activeOne = storage != null ? storage.getActiveSkill(0) : null;
        String activeTwo = storage != null ? storage.getActiveSkill(1) : null;

        if (host.skillsPassiveTabSelected()) {
            drawSkillSlots(host, graphics, innerX, skillsLayout.slotsBoxY(), skillsLayout.boxWidth(),
                    skillsLayout.slotsBoxHeight(),
                    passiveSlotKey, passiveOne, passiveTwo);
        } else if (host.skillsActiveTabSelected()) {
            drawSkillSlots(host, graphics, innerX, skillsLayout.slotsBoxY(), skillsLayout.boxWidth(),
                    skillsLayout.slotsBoxHeight(),
                    activeSlotKey, activeOne, activeTwo);
        }

        app.masterwork.believable.client.screen.components.ThemedInfoPanel.drawSectionHeader(
                graphics,
                host.font(),
                Component.translatable(availableKey),
                innerX,
                skillsLayout.availableHeaderY(),
                innerWidth);

        int categoryTabsY = skillsLayout.availableHeaderY() + host.font().lineHeight + 4;
        int categoryTabsHeight = drawCategoryTabs(
                host,
                graphics,
                innerX,
                categoryTabsY,
                innerWidth);

        int adjustedAvailableBoxY = skillsLayout.availableBoxY() + categoryTabsHeight;
        int adjustedAvailableBoxHeight = Math.max(0, skillsLayout.availableBoxHeight() - categoryTabsHeight);
        host.renderInfoBox(graphics, innerX, adjustedAvailableBoxY, skillsLayout.boxWidth(),
                adjustedAvailableBoxHeight);
        if (adjustedAvailableBoxHeight <= 0) {
            return;
        }

        int listHeight = Math.max(0, adjustedAvailableBoxHeight - host.raceBoxPadding() * 2);
        int maxScroll = getMaxSkillScroll(host, listHeight);
        int scroll = Math.max(0, Math.min(host.skillsScroll(), maxScroll));
        if (scroll != host.skillsScroll()) {
            host.setSkillsScroll(scroll);
        }

        if (host.skillsPassiveTabSelected()) {
            List<PassiveSkill> passives = filterSkillsByCategory(SkillDefinitions.getPassiveSkills(),
                    host.selectedSkillCategory());
            drawSkillList(host, graphics, innerX, adjustedAvailableBoxY, skillsLayout.boxWidth(),
                    adjustedAvailableBoxHeight,
                    passives, Component.translatable(emptyPassiveKey), scroll, host.skillsPassiveSlots());
        } else if (host.skillsActiveTabSelected()) {
            List<ActiveSkill> actives = filterSkillsByCategory(SkillDefinitions.getActiveSkills(),
                    host.selectedSkillCategory());
            drawSkillList(host, graphics, innerX, adjustedAvailableBoxY, skillsLayout.boxWidth(),
                    adjustedAvailableBoxHeight,
                    actives, Component.translatable(emptyActiveKey), scroll, host.skillsActiveSlots());
        }
    }

    public void rebuildButtons(Host host, PanelLayout layout) {
        List<? extends Skill> skills;
        SkillEquipHandler equipHandler;
        int slotCount;
        if (host.skillsPassiveTabSelected()) {
            skills = SkillDefinitions.getPassiveSkills();
            equipHandler = host::equipPassiveSkill;
            slotCount = host.skillsPassiveSlots();
        } else if (host.skillsActiveTabSelected()) {
            skills = SkillDefinitions.getActiveSkills();
            equipHandler = host::equipActiveSkill;
            slotCount = host.skillsActiveSlots();
        } else {
            return;
        }
        if (skills.isEmpty()) {
            return;
        }

        int panelX = layout.contentX() + host.panelPadding();
        int panelY = layout.contentY() + 10;
        int panelW = Math.max(0, layout.contentW() - host.panelPadding() * 2);
        int panelH = Math.max(0, layout.contentH() - host.panelPadding() * 2 - 4);

        int headerHeight = Math.min(26, Math.max(18, host.font().lineHeight + 10));
        int edgeInset = 3;
        int headerPadding = 8;
        int headerBottom = Math.min(panelY + panelH - edgeInset, panelY + edgeInset + headerHeight);
        int contentTop = headerBottom + headerPadding;

        int innerX = panelX + host.panelPadding();
        int innerRight = panelX + panelW - host.panelPadding();
        int innerWidth = Math.max(0, innerRight - innerX);
        int innerBottom = panelY + panelH - host.panelPadding();
        int availableTotalHeight = Math.max(0, innerBottom - contentTop);

        if (innerWidth <= 0 || availableTotalHeight <= 0) {
            return;
        }

        SkillsPanelLayout skillsLayout = buildSkillsPanelLayout(host, innerX, innerWidth, contentTop,
                availableTotalHeight);
        
        addCategoryButtons(host, innerX, skillsLayout.availableHeaderY() + host.font().lineHeight + 4, innerWidth);

        int categoryTabsHeight = getCategoryTabsHeight(host);
        int availableBoxY = skillsLayout.availableBoxY() + categoryTabsHeight;
        int availableBoxHeight = Math.max(0, skillsLayout.availableBoxHeight() - categoryTabsHeight);

        int listTop = availableBoxY + host.raceBoxPadding();
        int listBottom = availableBoxY + availableBoxHeight - host.raceBoxPadding();
        int listWidth = Math.max(0, skillsLayout.boxWidth() - host.raceBoxPadding() * 2);

        int selectedSlot = clampSelectedSlot(host, slotCount);
        int scroll = host.skillsScroll();
        int step = SKILL_ENTRY_HEIGHT + SKILL_ENTRY_GAP;
        int listBaseY = listTop - (scroll % step);
        int firstIndex = Math.max(0, scroll / step);
        int buttonRight = innerX + host.raceBoxPadding() + listWidth - 8;

        int slotContentX = innerX + host.raceBoxPadding();
        int slotContentY = skillsLayout.slotsBoxY() + host.raceBoxPadding();
        int slotContentWidth = Math.max(0, skillsLayout.boxWidth() - host.raceBoxPadding() * 2);

        for (int i = 0; i < slotCount; i++) {
            int slotY = slotContentY + i * (SLOT_HEIGHT + SLOT_GAP);
            if (slotY + SLOT_HEIGHT > skillsLayout.slotsBoxY() + skillsLayout.slotsBoxHeight()) {
                break;
            }
            boolean selected = i == selectedSlot;
            Component label = Component.literal(selected ? "Selected" : "Select");
            int finalI = i;
            Button slotButton = new app.masterwork.believable.client.screen.components.ThemedButton(
                    slotContentX + slotContentWidth - SLOT_SELECT_BUTTON_WIDTH - 6,
                    slotY + (SLOT_HEIGHT - SLOT_SELECT_BUTTON_HEIGHT) / 2,
                    SLOT_SELECT_BUTTON_WIDTH,
                    SLOT_SELECT_BUTTON_HEIGHT,
                    label,
                    button -> {
                        host.setSelectedSkillSlot(finalI);
                        host.refreshContentButtons();
                    });
            slotButton.active = !selected;
            host.addContentButton(slotButton);
        }

        for (int i = firstIndex; i < skills.size(); i++) {
            int cardY = listBaseY + (i - firstIndex) * step;
            if (cardY + SKILL_ENTRY_HEIGHT < listTop) {
                continue;
            }
            if (cardY > listBottom) {
                break;
            }

            int buttonY = cardY + (SKILL_ENTRY_HEIGHT - host.skillEquipButtonHeight()) / 2;
            final int skillIndex = i;
            int slotIndex = selectedSlot;

            String label = slotCount <= 1 ? "Equip" : "Equip " + slotLabel(slotIndex);
            host.addContentButton(new app.masterwork.believable.client.screen.components.ThemedButton(
                    buttonRight - host.skillEquipButtonWidth(),
                    buttonY,
                    host.skillEquipButtonWidth(),
                    host.skillEquipButtonHeight(),
                    Component.literal(label),
                    button -> equipHandler.equip(slotIndex, skillIndex)));
        }
    }

    public SkillListBounds getSkillListBounds(Host host, PanelLayout layout) {
        if (!host.skillsTabSelected()) {
            return null;
        }
        int panelX = layout.contentX() + host.panelPadding();
        int panelY = layout.contentY() + 10;
        int panelW = Math.max(0, layout.contentW() - host.panelPadding() * 2);
        int panelH = Math.max(0, layout.contentH() - host.panelPadding() * 2 - 4);
        if (panelW <= 0 || panelH <= 0) {
            return null;
        }

        int headerHeight = Math.min(26, Math.max(18, host.font().lineHeight + 10));
        int edgeInset = 3;
        int headerPadding = 8;
        int headerBottom = Math.min(panelY + panelH - edgeInset, panelY + edgeInset + headerHeight);
        int contentTop = headerBottom + headerPadding;

        int innerX = panelX + host.panelPadding();
        int innerRight = panelX + panelW - host.panelPadding();
        int innerWidth = Math.max(0, innerRight - innerX);
        int innerBottom = panelY + panelH - host.panelPadding();
        int availableTotalHeight = Math.max(0, innerBottom - contentTop);
        if (innerWidth <= 0 || availableTotalHeight <= 0) {
            return null;
        }

        SkillsPanelLayout skillsLayout = buildSkillsPanelLayout(host, innerX, innerWidth, contentTop,
                availableTotalHeight);
        int listHeight = Math.max(0, skillsLayout.availableBoxHeight() - host.raceBoxPadding() * 2);
        int maxScroll = getMaxSkillScroll(host, listHeight);
        return new SkillListBounds(innerX, skillsLayout.availableBoxY(), skillsLayout.boxWidth(),
                skillsLayout.availableBoxHeight(), maxScroll, SKILL_ENTRY_HEIGHT + SKILL_ENTRY_GAP);
    }

    private SkillsPanelLayout buildSkillsPanelLayout(Host host, int x, int width, int startY, int totalHeight) {
        int slotsHeaderY = startY;
        int slotsBoxY = slotsHeaderY + host.font().lineHeight + 4;

        // Calculate needed height for 2 slots + padding
        int neededSlotHeight = host.raceBoxPadding() * 2 + SLOT_HEIGHT * 2 + SLOT_GAP;
        int slotsBoxHeight = Math.min(160, Math.max(80, neededSlotHeight));

        int availableHeaderY = slotsBoxY + slotsBoxHeight + host.raceSectionGap();
        int availableBoxY = availableHeaderY + host.font().lineHeight + 4;

        int usedHeight = availableBoxY - startY;
        int availableBoxHeight = Math.max(0, totalHeight - usedHeight);

        return new SkillsPanelLayout(
                startY,
                slotsHeaderY,
                slotsBoxY,
                slotsBoxHeight,
                availableHeaderY,
                availableBoxY,
                availableBoxHeight,
                width);
    }

    private void drawSkillList(Host host,
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            List<? extends Skill> skills,
            Component emptyLabel,
            int scroll,
            int slotCount) {
        int listX = x + host.raceBoxPadding();
        int listY = y + host.raceBoxPadding();
        int listWidth = Math.max(0, width - host.raceBoxPadding() * 2);
        int listBottom = y + height - host.raceBoxPadding();
        int buttonAreaWidth = host.skillEquipButtonWidth() + 14;
        int accentWidth = 3;
        int step = SKILL_ENTRY_HEIGHT + SKILL_ENTRY_GAP;
        int firstIndex = Math.max(0, scroll / step);
        int offset = scroll % step;
        int baseY = listY - offset;

        if (skills.isEmpty()) {
            graphics.drawString(host.font(), emptyLabel, listX, listY, UnbelievableTheme.TEXT_MUTED, false);
            return;
        }

        graphics.enableScissor(x, y, x + width, y + height);
        for (int i = firstIndex; i < skills.size(); i++) {
            int cardY = baseY + (i - firstIndex) * step;
            if (cardY + SKILL_ENTRY_HEIGHT > listBottom) {
                break;
            }

            Skill skill = skills.get(i);

            // Draw Card Background
            int right = listX + listWidth;
            int bottom = cardY + SKILL_ENTRY_HEIGHT;

            graphics.fill(listX, cardY, right, bottom, UnbelievableTheme.CARD_HEADER);
            graphics.fill(listX, cardY, right, cardY + 1, UnbelievableTheme.CARD_BORDER);
            graphics.fill(listX, bottom - 1, right, bottom, UnbelievableTheme.CARD_BORDER);
            graphics.fill(listX, cardY, listX + 1, bottom, UnbelievableTheme.CARD_BORDER);
            graphics.fill(right - 1, cardY, right, bottom, UnbelievableTheme.CARD_BORDER);
            graphics.fill(listX, cardY + 1, right, cardY + 2, UnbelievableTheme.GOLD_FAINT);
            graphics.fill(listX, cardY, listX + accentWidth, bottom, UnbelievableTheme.GOLD_FAINT);

            int dividerX = right - buttonAreaWidth;
            graphics.fill(dividerX, cardY + 2, dividerX + 1, bottom - 2, UnbelievableTheme.ROW_BORDER);

            // Skill Name (centered vertically in the card)
            int titleY = cardY + (SKILL_ENTRY_HEIGHT - host.font().lineHeight) / 2;
            int nameX = listX + accentWidth + 8;
            graphics.drawString(host.font(), skill.name(), nameX, titleY, UnbelievableTheme.TEXT_PRIMARY, false);

            if (cardY + SKILL_ENTRY_HEIGHT > y && cardY < y + height) {
                Component tooltip = Component.literal(skill.name() + "\n" + skill.description());
                host.addSkillTooltip(nameX, titleY, host.font().width(skill.name()), host.font().lineHeight, tooltip);
            }
        }
        graphics.disableScissor();
    }

    private void drawSkillSlots(Host host,
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            String slotKey,
            String slotOne,
            String slotTwo) {
        int contentX = x + host.raceBoxPadding();
        int contentY = y + host.raceBoxPadding();
        int contentWidth = Math.max(0, width - host.raceBoxPadding() * 2);

        String[] slotValues = new String[] { slotOne, slotTwo };
        int slotCount = host.skillsPassiveTabSelected() ? host.skillsPassiveSlots() : host.skillsActiveSlots();
        int maxSlots = Math.min(slotCount, slotValues.length);
        int selectedSlot = clampSelectedSlot(host, slotCount);
        int maxValueWidth = Math.max(0, contentWidth - SLOT_SELECT_BUTTON_WIDTH - 18);

        for (int i = 0; i < maxSlots; i++) {
            int slotY = contentY + i * (SLOT_HEIGHT + SLOT_GAP);
            if (slotY + SLOT_HEIGHT > y + height) {
                break;
            }

            String skillId = slotValues[i];
            boolean filled = skillId != null;
            boolean selected = i == selectedSlot;
            int borderColor = selected ? UnbelievableTheme.ROW_BORDER_HOVER : UnbelievableTheme.ROW_BORDER;
            int fillColor = selected ? UnbelievableTheme.ROW_FILL_HOVER : UnbelievableTheme.ROW_FILL;

            // Draw Slot Box
            int right = contentX + contentWidth;
            int bottom = slotY + SLOT_HEIGHT;

            graphics.fill(contentX, slotY, right, bottom, fillColor);
            graphics.fill(contentX, slotY, right, slotY + 1, borderColor);
            graphics.fill(contentX, bottom - 1, right, bottom, borderColor);
            graphics.fill(contentX, slotY, contentX + 1, bottom, borderColor);
            graphics.fill(right - 1, slotY, right, bottom, borderColor);

            if (selected) {
                graphics.fill(contentX, slotY, contentX + 2, bottom, UnbelievableTheme.GOLD_FAINT);
                graphics.fill(contentX + 2, slotY + 2, right - 2, slotY + 3, UnbelievableTheme.GOLD_FAINT);
            }

            // Draw Label (Top)
            Component label = Component.translatable(slotKey, i + 1);
            int labelY = slotY + 6;
            graphics.drawString(host.font(), label, contentX + 10, labelY, UnbelievableTheme.TEXT_SOFT, false);

            // Draw Value (Lower)
            Component value = host.skillNameComponent(skillId);
            String valueText = value.getString();
            if (host.font().width(valueText) > maxValueWidth) {
                valueText = host.font().plainSubstrByWidth(valueText, Math.max(0, maxValueWidth - 6)) + "...";
            }
            int valueY = labelY + host.font().lineHeight + 2;
            graphics.drawString(host.font(), valueText, contentX + 10, valueY,
                    filled ? UnbelievableTheme.TEXT_SELECTED : UnbelievableTheme.TEXT_MUTED, false);

            if (filled) {
                Skill skill = SkillDefinitions.getById(skillId);
                if (skill != null) {
                    Component tooltip = Component.literal(skill.name() + "\n" + skill.description());
                    host.addSkillTooltip(contentX + 10, valueY, host.font().width(valueText), host.font().lineHeight,
                            tooltip);
                }
            }
        }
    }

    private record SkillsPanelLayout(int headerY,
            int slotsHeaderY,
            int slotsBoxY,
            int slotsBoxHeight,
            int availableHeaderY,
            int availableBoxY,
            int availableBoxHeight,
            int boxWidth) {
    }

    public record SkillListBounds(int boxX, int boxY, int boxWidth, int boxHeight, int maxScroll, int scrollStep) {
    }

    private interface SkillEquipHandler {
        void equip(int slotIndex, int skillIndex);
    }

    private int getMaxSkillScroll(Host host, int listHeight) {
        int count = getSkillListCount(host);
        if (count <= 0 || listHeight <= 0) {
            return 0;
        }
        int contentHeight = count * (SKILL_ENTRY_HEIGHT + SKILL_ENTRY_GAP) - SKILL_ENTRY_GAP;
        return Math.max(0, contentHeight - listHeight);
    }

    private int getSkillListCount(Host host) {
        if (host.skillsPassiveTabSelected()) {
            return SkillDefinitions.getPassiveSkills().size();
        }
        if (host.skillsActiveTabSelected()) {
            return SkillDefinitions.getActiveSkills().size();
        }
        return 0;
    }

    private int clampSelectedSlot(Host host, int slotCount) {
        if (slotCount <= 0) {
            return 0;
        }
        int selected = host.selectedSkillSlot();
        if (selected < 0 || selected >= slotCount) {
            return 0;
        }
        return selected;
    }

    private String slotLabel(int slotIndex) {
        return switch (slotIndex) {
            case 0 -> "I";
            case 1 -> "II";
            case 2 -> "III";
            default -> "IV";
        };
    }

    private <T extends Skill> List<T> filterSkillsByCategory(List<T> skills, SkillCategory category) {
        if (category == null || category == SkillCategory.ALL) {
            return skills;
        }
        return skills.stream()
                .filter(skill -> skill.category() == category)
                .collect(Collectors.toList());
    }

    private int drawCategoryTabs(Host host, GuiGraphics graphics, int x, int y, int width) {
        SkillCategory selectedCategory = host.selectedSkillCategory();
        if (selectedCategory == null) {
            return 0;
        }

        List<SkillCategory> categories = getAvailableCategories(host);
        if (categories.size() <= 1) {
            return 0; // No need to show tabs if there's only ALL or nothing
        }

        int tabX = x;
        int tabY = y + 4;
        int tabWidth = Math.min(72, (width - (categories.size() - 1) * CATEGORY_TAB_GAP) / categories.size());

        for (SkillCategory category : categories) {
            boolean selected = category == selectedCategory;
            int tabFill = selected ? UnbelievableTheme.TAB_SELECTED : UnbelievableTheme.TAB_BASE;
            int textColor = selected ? UnbelievableTheme.TEXT_PRIMARY : UnbelievableTheme.TEXT_MUTED;

            graphics.fill(tabX, tabY, tabX + tabWidth, tabY + CATEGORY_TAB_HEIGHT, tabFill);
            graphics.fill(tabX, tabY, tabX + tabWidth, tabY + 1, UnbelievableTheme.ROW_BORDER);
            graphics.fill(tabX, tabY + CATEGORY_TAB_HEIGHT - 1, tabX + tabWidth, tabY + CATEGORY_TAB_HEIGHT,
                    UnbelievableTheme.ROW_BORDER);

            if (selected) {
                graphics.fill(tabX, tabY + 1, tabX + tabWidth, tabY + 2, UnbelievableTheme.GOLD_FAINT);
            }

            String categoryName = Component.translatable(category.translationKey()).getString();
            int textX = tabX + (tabWidth - host.font().width(categoryName)) / 2;
            int textY = tabY + (CATEGORY_TAB_HEIGHT - host.font().lineHeight) / 2;
            graphics.drawString(host.font(), categoryName, textX, textY, textColor, false);

            tabX += tabWidth + CATEGORY_TAB_GAP;
        }

        return CATEGORY_TAB_HEIGHT + 8;
    }

    private List<SkillCategory> getAvailableCategories(Host host) {
        List<SkillCategory> categories = new ArrayList<>();
        categories.add(SkillCategory.ALL);

        List<? extends Skill> skills;
        if (host.skillsPassiveTabSelected()) {
            skills = SkillDefinitions.getPassiveSkills();
        } else if (host.skillsActiveTabSelected()) {
            skills = SkillDefinitions.getActiveSkills();
        } else {
            return categories;
        }

        for (Skill skill : skills) {
            SkillCategory category = skill.category();
            if (category != SkillCategory.ALL && !categories.contains(category)) {
                categories.add(category);
            }
        }

        return categories;
    }

    private int getCategoryTabsHeight(Host host) {
        List<SkillCategory> categories = getAvailableCategories(host);
        if (categories.size() <= 1) {
            return 0;
        }
        return CATEGORY_TAB_HEIGHT + 8;
    }

    private void addCategoryButtons(Host host, int x, int y, int width) {
        List<SkillCategory> categories = getAvailableCategories(host);
        if (categories.size() <= 1) {
            return;
        }

        int tabX = x;
        int tabY = y + 4;
        int tabWidth = Math.min(72, (width - (categories.size() - 1) * CATEGORY_TAB_GAP) / categories.size());

        for (SkillCategory category : categories) {
            host.addContentButton(new InvisibleButton(tabX, tabY, tabWidth, CATEGORY_TAB_HEIGHT, button -> {
                host.setSelectedSkillCategory(category);
                host.refreshContentButtons();
            }));
            tabX += tabWidth + CATEGORY_TAB_GAP;
        }
    }

    private static class InvisibleButton extends Button {
        public InvisibleButton(int x, int y, int width, int height, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Invisible
        }
    }
}
