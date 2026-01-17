package app.masterwork.believable.classes.cartographer.client.screen;

import app.masterwork.believable.classes.cartographer.CartographerService;
import app.masterwork.believable.classes.cartographer.CartographyWaypoint;
import app.masterwork.believable.classes.cartographer.PlayerCartographyStorage;
import app.masterwork.believable.client.screen.components.ThemedButton;
import app.masterwork.believable.client.screen.layout.PanelLayout;
import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class CartographerSettingsSection {
    private static final int LIST_PADDING = 8;
    private static final int TITLE_OFFSET = 10;
    private static final int HEADER_GAP = 8;
    private static final int HEADER_TO_BOX_GAP = 4;

    private int selectedIndex = -1;

    public interface Host {
        Font font();

        PanelLayout panelLayout();

        int panelPadding();

        int settingsButtonHeight();

        int settingsButtonGap();

        int settingsBoxGap();

        void renderTabTitle(GuiGraphics graphics, int x, int y, Component label);

        void renderSectionHeader(GuiGraphics graphics, Component label, int x, int y);

        void renderInfoBox(GuiGraphics graphics, int x, int y, int width, int height);

        void addContentButton(Button button);

        void addContentWidget(AbstractWidget widget);

        PlayerCartographyStorage cartographyStorage();

        LocalPlayer player();

        boolean isCartographer();

        void sendWaypointAdd();

        void sendWaypointToggle(int index);

        void sendWaypointMove(int index);

        void sendWaypointDelete(int index);

        void refreshContentButtons();

        void sendWaypointRename(int index, String name);
    }

    public int getFooterHeight(Host host) {
        int rows = 4;
        return host.settingsButtonHeight() * rows + host.settingsButtonGap() * (rows - 1) + host.settingsBoxGap();
    }

    public void render(Host host,
                       GuiGraphics graphics,
                       PanelLayout layout,
                       String settingsTabKey,
                       String waypointsTabKey,
                       String emptyKey,
                       String notCartographerKey) {
        int textX = layout.contentX() + host.panelPadding();
        int titleY = layout.contentY() + TITLE_OFFSET;
        host.renderTabTitle(graphics, textX, titleY, Component.translatable(settingsTabKey));
        int headerY = titleY + (int) (host.font().lineHeight * 1.25f) + HEADER_GAP;
        host.renderSectionHeader(graphics, Component.translatable(waypointsTabKey), textX, headerY);

        WaypointLayout waypointLayout = getWaypointLayout(host, layout, headerY);
        host.renderInfoBox(graphics, waypointLayout.listX, waypointLayout.listY, waypointLayout.listWidth, waypointLayout.listHeight);

        if (!host.isCartographer()) {
            graphics.drawString(host.font(), Component.translatable(notCartographerKey),
                waypointLayout.listX + LIST_PADDING, waypointLayout.listY + LIST_PADDING, UnbelievableTheme.TEXT_MUTED, false);
            return;
        }

        PlayerCartographyStorage storage = host.cartographyStorage();
        if (storage == null || storage.getWaypoints().isEmpty()) {
            graphics.drawString(host.font(), Component.translatable(emptyKey),
                waypointLayout.listX + LIST_PADDING, waypointLayout.listY + LIST_PADDING, UnbelievableTheme.TEXT_MUTED, false);
        }
    }

    public void rebuildButtons(Host host, String addKey, String toggleKey, String moveKey, String deleteKey, String renameKey) {
        if (!host.isCartographer()) {
            return;
        }
        PlayerCartographyStorage storage = host.cartographyStorage();
        if (storage == null) {
            return;
        }
        PanelLayout layout = host.panelLayout();
        int textX = layout.contentX() + host.panelPadding();
        int titleY = layout.contentY() + TITLE_OFFSET;
        int headerY = titleY + (int) (host.font().lineHeight * 1.25f) + HEADER_GAP;
        WaypointLayout waypointLayout = getWaypointLayout(host, layout, headerY);

        List<CartographyWaypoint> waypoints = storage.getWaypoints();
        if (selectedIndex >= waypoints.size()) {
            selectedIndex = -1;
        }
        int buttonHeight = host.settingsButtonHeight();
        int cursorY = waypointLayout.listY + LIST_PADDING;
        int maxY = waypointLayout.listY + waypointLayout.listHeight - LIST_PADDING;
        int listButtonWidth = Math.max(0, waypointLayout.listWidth - LIST_PADDING * 2);

        for (int i = 0; i < waypoints.size(); i++) {
            if (cursorY + buttonHeight > maxY) {
                break;
            }
            CartographyWaypoint waypoint = waypoints.get(i);
            String status = waypoint.enabled() ? "On" : "Off";
            boolean selected = i == selectedIndex;
            String labelText = (selected ? "> " : "") + waypoint.name() + " [" + status + "]";
            Component label = Component.literal(labelText);
            int index = i;
            ThemedButton button = new ThemedButton(
                waypointLayout.listX + LIST_PADDING,
                cursorY,
                listButtonWidth,
                buttonHeight,
                label,
                click -> {
                    selectedIndex = index;
                    host.refreshContentButtons();
                }
            );
            host.addContentButton(button);
            cursorY += buttonHeight + host.settingsButtonGap();
        }

        int actionX = waypointLayout.actionX;
        int actionY = waypointLayout.actionY;
        int actionWidth = waypointLayout.actionWidth;
        int gap = host.settingsButtonGap();
        int halfWidth = Math.max(0, (actionWidth - gap) / 2);

        host.addContentButton(new ThemedButton(
            actionX,
            actionY,
            halfWidth,
            buttonHeight,
            Component.translatable(addKey),
            button -> host.sendWaypointAdd()
        ));

        Button toggle = new ThemedButton(
            actionX + halfWidth + gap,
            actionY,
            halfWidth,
            buttonHeight,
            Component.translatable(toggleKey),
            button -> host.sendWaypointToggle(selectedIndex)
        );
        toggle.active = selectedIndex >= 0 && selectedIndex < waypoints.size();
        host.addContentButton(toggle);

        int rowTwoY = actionY + buttonHeight + gap;
        Button move = new ThemedButton(
            actionX,
            rowTwoY,
            halfWidth,
            buttonHeight,
            Component.translatable(moveKey),
            button -> host.sendWaypointMove(selectedIndex)
        );
        move.active = selectedIndex >= 0 && selectedIndex < waypoints.size();
        host.addContentButton(move);

        Button delete = new ThemedButton(
            actionX + halfWidth + gap,
            rowTwoY,
            halfWidth,
            buttonHeight,
            Component.translatable(deleteKey),
            button -> host.sendWaypointDelete(selectedIndex)
        );
        delete.active = selectedIndex >= 0 && selectedIndex < waypoints.size();
        host.addContentButton(delete);

        int rowThreeY = rowTwoY + buttonHeight + gap;
        int renameButtonWidth = Math.min(90, Math.max(0, actionWidth / 2));
        int renameInputWidth = Math.max(0, actionWidth - renameButtonWidth - gap);
        boolean hasSelection = selectedIndex >= 0 && selectedIndex < waypoints.size();
        EditBox renameBox = new EditBox(
            host.font(),
            actionX,
            rowThreeY,
            renameInputWidth,
            buttonHeight,
            Component.translatable(renameKey)
        );
        renameBox.setMaxLength(CartographerService.MAX_WAYPOINT_NAME_LENGTH);
        renameBox.setEditable(hasSelection);
        String selectedName = "";
        if (hasSelection) {
            String name = waypoints.get(selectedIndex).name();
            selectedName = name == null ? "" : name;
        }
        renameBox.setValue(selectedName);
        host.addContentWidget(renameBox);
        Button rename = new ThemedButton(
            actionX + renameInputWidth + gap,
            rowThreeY,
            renameButtonWidth,
            buttonHeight,
            Component.translatable(renameKey),
            button -> host.sendWaypointRename(selectedIndex, renameBox.getValue())
        );
        rename.active = hasSelection;
        host.addContentButton(rename);

        int rowFourY = rowThreeY + buttonHeight + gap;
        Button clearSelection = new ThemedButton(
            actionX,
            rowFourY,
            actionWidth,
            buttonHeight,
            Component.translatable("screen.believable.unbelievable_menu.settings.waypoints.clear_selection"),
            button -> {
                selectedIndex = -1;
                host.refreshContentButtons();
            }
        );
        clearSelection.active = selectedIndex >= 0 && selectedIndex < waypoints.size();
        host.addContentButton(clearSelection);
    }

    private WaypointLayout getWaypointLayout(Host host, PanelLayout layout, int headerY) {
        int textX = layout.contentX() + host.panelPadding();
        int contentRight = layout.contentX() + layout.contentW() - host.panelPadding();
        int contentBottom = layout.contentY() + layout.contentH() - host.panelPadding() - getFooterHeight(host);
        int listY = headerY + host.font().lineHeight + HEADER_TO_BOX_GAP;
        int listWidth = Math.max(0, contentRight - textX);
        int listHeight = Math.max(0, contentBottom - listY);

        int actionWidth = Math.min(220, layout.contentW() - host.panelPadding() * 2);
        int actionX = layout.contentX() + host.panelPadding();
        int actionY = layout.contentY() + layout.contentH() - host.panelPadding()
            - (host.settingsButtonHeight() * 4 + host.settingsButtonGap() * 3);

        return new WaypointLayout(textX, listY, listWidth, listHeight, actionX, actionY, actionWidth);
    }

    private record WaypointLayout(int listX, int listY, int listWidth, int listHeight, int actionX, int actionY, int actionWidth) {
    }
}
