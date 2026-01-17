package app.masterwork.believable.client.screen.sections;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.character.ClassDefinitions;
import app.masterwork.believable.client.screen.UnbelievableScreen;
import app.masterwork.believable.client.screen.components.ThemedButton;
import app.masterwork.believable.client.screen.layout.ClassSelectionLayout;
import app.masterwork.believable.client.screen.layout.ClassSelectionLayoutBuilder;
import app.masterwork.believable.client.screen.layout.PanelLayout;
import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders and wires the class selection questionnaire + list panel.
 */
public final class ClassSelectionSection {
    public interface Host {
        Font font();

        PanelLayout panelLayout();

        int panelPadding();

        int panelGap();

        int buttonHeight();

        int buttonGap();

        int listButtonGap();

        void renderTabTitle(GuiGraphics graphics, int x, int y, Component label);

        void renderSectionHeader(GuiGraphics graphics, Component label, int x, int y);

        void renderInfoBox(GuiGraphics graphics, int x, int y, int width, int height);

        void renderDescriptionText(GuiGraphics graphics, int x, int y, int width, int height, String description);

        void addContentButton(Button button);

        void addTooltip(int x, int y, int width, int height, Component description);

        void rebuildTabs();

        PlayerAttachmentStorage playerStorage();

        boolean classSelectionActive();

        UnbelievableScreen.ClassSelectionCategory selectedCategory();

        void setSelectedCategory(UnbelievableScreen.ClassSelectionCategory category);

        BasicClass pendingClass();

        void setPendingClass(BasicClass pendingClass, String pendingClassKey);

        void clearPendingClass();

        void confirmPendingClass();

        String detailText();
    }

    public void renderQuestionnaire(Host host,
                                    GuiGraphics graphics,
                                    PanelLayout layout,
                                    int textX,
                                    String tabKey,
                                    String questionHeaderKey,
                                    String questionKey,
                                    String stepPathKey,
                                    String stepClassKey,
                                    String detailsHeaderKey,
                                    String allOptionsKey) {
        int titleY = layout.contentY() + 10;
        host.renderTabTitle(graphics, textX, titleY, Component.translatable(tabKey));
        ClassSelectionLayout selectionLayout = getLayout(host, layout);

        host.renderSectionHeader(graphics, Component.translatable(questionHeaderKey), selectionLayout.leftX(), selectionLayout.questionHeaderY());
        host.renderInfoBox(graphics, selectionLayout.leftX(), selectionLayout.questionBoxY(), selectionLayout.leftW(), selectionLayout.questionBoxH());
        host.renderDescriptionText(
            graphics,
            selectionLayout.leftX(),
            selectionLayout.questionBoxY(),
            selectionLayout.leftW(),
            selectionLayout.questionBoxH(),
            Component.translatable(questionKey).getString()
        );

        Component stepHeader = host.selectedCategory() == null
            ? Component.translatable(stepPathKey)
            : Component.translatable(stepClassKey);
        host.renderSectionHeader(graphics, stepHeader, selectionLayout.leftX(), selectionLayout.stepHeaderY());
        host.renderInfoBox(graphics, selectionLayout.leftX(), selectionLayout.stepBoxY(), selectionLayout.leftW(), selectionLayout.stepBoxH());

        host.renderSectionHeader(graphics, Component.translatable(detailsHeaderKey), selectionLayout.leftX(), selectionLayout.detailHeaderY());
        host.renderInfoBox(graphics, selectionLayout.leftX(), selectionLayout.detailBoxY(), selectionLayout.leftW(), selectionLayout.detailBoxH());
        host.renderDescriptionText(
            graphics,
            selectionLayout.leftX(),
            selectionLayout.detailBoxY(),
            selectionLayout.leftW(),
            selectionLayout.detailBoxH(),
            host.detailText()
        );

        host.renderSectionHeader(graphics, Component.translatable(allOptionsKey), selectionLayout.rightX(), selectionLayout.listHeaderY());
        host.renderInfoBox(graphics, selectionLayout.rightX(), selectionLayout.listBoxY(), selectionLayout.rightW(), selectionLayout.listBoxH());
        drawListLabels(host, graphics, selectionLayout);
    }

    public void rebuildButtons(Host host, String backKey, String confirmKey) {
        PlayerAttachmentStorage storage = host.playerStorage();
        if (storage == null || storage.getPlayerClass() != null || !host.classSelectionActive()) {
            return;
        }
        PanelLayout layout = host.panelLayout();
        ClassSelectionLayout selectionLayout = getLayout(host, layout);
        int buttonHeight = host.buttonHeight();
        int navY = selectionLayout.footerY();
        int navWidth = Math.max(0, selectionLayout.leftW());

        if (host.pendingClass() == null) {
            for (ClassSelectionEntry entry : buildStepEntries(host, selectionLayout)) {
                if (entry.type == ClassSelectionEntryType.CATEGORY_OPTION) {
                    host.addContentButton(new ThemedButton(
                        entry.x,
                        entry.y,
                        entry.width,
                        entry.height,
                        entry.label,
                        button -> {
                            host.setSelectedCategory(entry.category);
                            host.clearPendingClass();
                            host.rebuildTabs();
                        }
                    ));
                    host.addTooltip(entry.x, entry.y, entry.width, entry.height, Component.translatable(entry.category.descriptionKey()));
                    continue;
                }
                if (entry.type != ClassSelectionEntryType.CLASS_OPTION || entry.classOption == null) {
                    continue;
                }
                host.addContentButton(new ThemedButton(
                    entry.x,
                    entry.y,
                    entry.width,
                    entry.height,
                    entry.label,
                    button -> {
                        host.setPendingClass(entry.classOption, entry.classKey);
                        host.setSelectedCategory(entry.category);
                        host.rebuildTabs();
                    }
                ));
                host.addTooltip(entry.x, entry.y, entry.width, entry.height, Component.literal(entry.classOption.description()));
            }
        }

        for (ClassSelectionEntry entry : buildListEntries(host, selectionLayout)) {
            if (entry.type != ClassSelectionEntryType.CLASS_OPTION || entry.classOption == null) {
                continue;
            }
            host.addContentButton(new ThemedButton(
                entry.x,
                entry.y,
                entry.width,
                entry.height,
                entry.label,
                button -> {
                    host.setPendingClass(entry.classOption, entry.classKey);
                    host.setSelectedCategory(entry.category);
                    host.rebuildTabs();
                }
            ));
            host.addTooltip(entry.x, entry.y, entry.width, entry.height, Component.literal(entry.classOption.description()));
        }

        if (host.pendingClass() != null) {
            int confirmWidth = Math.min(140, navWidth);
            int backWidth = Math.min(100, navWidth - confirmWidth - host.buttonGap());
            if (backWidth < 80) {
                backWidth = Math.min(100, navWidth);
                confirmWidth = backWidth;
            }
            host.addContentButton(new ThemedButton(
                selectionLayout.leftX(),
                navY,
                backWidth,
                buttonHeight,
                Component.translatable(backKey),
                button -> {
                    host.clearPendingClass();
                    host.rebuildTabs();
                }
            ));
            host.addContentButton(new ThemedButton(
                selectionLayout.leftX() + navWidth - confirmWidth,
                navY,
                confirmWidth,
                buttonHeight,
                Component.translatable(confirmKey),
                button -> host.confirmPendingClass()
            ));
            return;
        }

        if (host.selectedCategory() != null) {
            host.addContentButton(new ThemedButton(
                selectionLayout.leftX(),
                navY,
                Math.min(120, navWidth),
                buttonHeight,
                Component.translatable(backKey),
                button -> {
                    host.setSelectedCategory(null);
                    host.clearPendingClass();
                    host.rebuildTabs();
                }
            ));
        }
    }

    private ClassSelectionLayout getLayout(Host host, PanelLayout layout) {
        int stepButtonCount = getStepButtonCount(host);
        return ClassSelectionLayoutBuilder.build(
            layout,
            host.font(),
            host.panelPadding(),
            host.panelGap(),
            host.buttonHeight(),
            host.buttonGap(),
            stepButtonCount
        );
    }

    private int getStepButtonCount(Host host) {
        if (host.selectedCategory() == null) {
            return UnbelievableScreen.ClassSelectionCategory.values().length;
        }
        return host.selectedCategory().classTabKeys().size();
    }

    private List<ClassSelectionEntry> buildStepEntries(Host host, ClassSelectionLayout layout) {
        List<ClassSelectionEntry> entries = new ArrayList<>();
        int padding = 8;
        int cursorY = layout.stepBoxY() + padding;
        int boxBottom = layout.stepBoxY() + layout.stepBoxH();
        int x = layout.leftX() + padding;
        int width = Math.max(0, layout.leftW() - padding * 2);

        if (host.selectedCategory() == null) {
            for (UnbelievableScreen.ClassSelectionCategory category : UnbelievableScreen.ClassSelectionCategory.values()) {
                if (cursorY + host.buttonHeight() > boxBottom) {
                    break;
                }
                entries.add(ClassSelectionEntry.categoryOption(
                    x,
                    cursorY,
                    width,
                    host.buttonHeight(),
                    Component.translatable(category.labelKey()),
                    category
                ));
                cursorY += host.buttonHeight() + host.buttonGap();
            }
            return entries;
        }

        for (String classKey : host.selectedCategory().classTabKeys()) {
            BasicClass option = ClassDefinitions.getByTabKey(classKey);
            if (option == null) {
                continue;
            }
            if (cursorY + host.buttonHeight() > boxBottom) {
                break;
            }
            entries.add(ClassSelectionEntry.classOption(
                x,
                cursorY,
                width,
                host.buttonHeight(),
                Component.translatable(classKey),
                option,
                classKey,
                host.selectedCategory()
            ));
            cursorY += host.buttonHeight() + host.buttonGap();
        }
        return entries;
    }

    private List<ClassSelectionEntry> buildListEntries(Host host, ClassSelectionLayout layout) {
        List<ClassSelectionEntry> entries = new ArrayList<>();
        int padding = 8;
        int cursorY = layout.listBoxY() + padding;
        int boxBottom = layout.listBoxY() + layout.listBoxH();
        int x = layout.rightX() + padding;
        int width = Math.max(0, layout.rightW() - padding * 2);
        int headerGap = 4;

        for (UnbelievableScreen.ClassSelectionCategory category : UnbelievableScreen.ClassSelectionCategory.values()) {
            if (cursorY + host.font().lineHeight > boxBottom) {
                break;
            }
            entries.add(ClassSelectionEntry.header(
                x,
                cursorY,
                width,
                host.font().lineHeight,
                Component.translatable(category.labelKey())
            ));
            cursorY += host.font().lineHeight + headerGap;
            for (String classKey : category.classTabKeys()) {
                BasicClass option = ClassDefinitions.getByTabKey(classKey);
                if (option == null) {
                    continue;
                }
                if (cursorY + host.buttonHeight() > boxBottom) {
                    break;
                }
                entries.add(ClassSelectionEntry.classOption(
                    x,
                    cursorY,
                    width,
                    host.buttonHeight(),
                    Component.translatable(classKey),
                    option,
                    classKey,
                    category
                ));
                cursorY += host.buttonHeight() + host.listButtonGap();
            }
            cursorY += headerGap;
        }
        return entries;
    }

    private void drawListLabels(Host host, GuiGraphics graphics, ClassSelectionLayout layout) {
        for (ClassSelectionEntry entry : buildListEntries(host, layout)) {
            if (entry.type != ClassSelectionEntryType.HEADER) {
                continue;
            }
            graphics.drawString(host.font(), entry.label, entry.x, entry.y, UnbelievableTheme.TEXT_MUTED, false);
        }
    }

    private enum ClassSelectionEntryType {
        HEADER,
        CATEGORY_OPTION,
        CLASS_OPTION
    }

    private static final class ClassSelectionEntry {
        private final ClassSelectionEntryType type;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final Component label;
        private final BasicClass classOption;
        private final String classKey;
        private final UnbelievableScreen.ClassSelectionCategory category;

        private ClassSelectionEntry(ClassSelectionEntryType type,
                                    int x,
                                    int y,
                                    int width,
                                    int height,
                                    Component label,
                                    BasicClass classOption,
                                    String classKey,
                                    UnbelievableScreen.ClassSelectionCategory category) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.label = label;
            this.classOption = classOption;
            this.classKey = classKey;
            this.category = category;
        }

        private static ClassSelectionEntry header(int x, int y, int width, int height, Component label) {
            return new ClassSelectionEntry(ClassSelectionEntryType.HEADER, x, y, width, height, label, null, null, null);
        }

        private static ClassSelectionEntry categoryOption(int x, int y, int width, int height, Component label,
                                                          UnbelievableScreen.ClassSelectionCategory category) {
            return new ClassSelectionEntry(ClassSelectionEntryType.CATEGORY_OPTION, x, y, width, height, label, null, null, category);
        }

        private static ClassSelectionEntry classOption(int x, int y, int width, int height, Component label, BasicClass option,
                                                       String classKey, UnbelievableScreen.ClassSelectionCategory category) {
            return new ClassSelectionEntry(ClassSelectionEntryType.CLASS_OPTION, x, y, width, height, label, option, classKey, category);
        }
    }
}
