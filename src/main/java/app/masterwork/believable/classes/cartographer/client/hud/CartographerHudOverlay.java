package app.masterwork.believable.classes.cartographer.client.hud;

import app.masterwork.believable.classes.cartographer.CartographerService;
import app.masterwork.believable.classes.cartographer.CartographyWaypoint;
import app.masterwork.believable.classes.cartographer.PlayerCartographyStorage;
import app.masterwork.believable.classes.cartographer.client.CartographerClientState;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public final class CartographerHudOverlay {
    private static final int MINIMAP_SIZE = 96;
    private static final int HUD_MARGIN = 8;
    private static final int BOX_PADDING = 6;
    private static final int BOX_BORDER = 1;
    private static final int BOX_BG = 0xB0000000;
    private static final int BOX_BORDER_COLOR = 0xFF4F4F4F;

    private CartographerHudOverlay() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }
        if (!CartographerService.isCartographer(player)) {
            return;
        }

        Font font = minecraft.font;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int x = screenWidth - HUD_MARGIN - MINIMAP_SIZE;
        int y = HUD_MARGIN;
        int cursorY = y;

        if (CartographerClientState.isMinimapVisible()) {
            renderMinimapBox(graphics, font, player, x, y, MINIMAP_SIZE, MINIMAP_SIZE);
            cursorY = y + MINIMAP_SIZE + 6;
        }

        renderWaypoints(graphics, font, player, x, cursorY, MINIMAP_SIZE);
    }

    private static void renderMinimapBox(GuiGraphics graphics, Font font, LocalPlayer player, int x, int y, int width, int height) {
        drawBox(graphics, x, y, width, height);
        ItemStack mapStack = getEquippedMap(player);
        if (mapStack.isEmpty()) {
            graphics.drawString(font, "No map equipped", x + BOX_PADDING, y + BOX_PADDING, 0xFFB8B8B8, false);
            return;
        }
        int mapSize = Math.max(0, Math.min(width - BOX_PADDING * 2, height - BOX_PADDING * 2));
        int mapX = x + (width - mapSize) / 2;
        int mapY = y + (height - mapSize) / 2;
        graphics.pose().pushPose();
        graphics.pose().translate(mapX, mapY, 0);
        float scale = mapSize / 16.0f;
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.renderItem(mapStack, 0, 0);
        graphics.pose().popPose();
    }

    private static void renderWaypoints(GuiGraphics graphics, Font font, LocalPlayer player, int x, int y, int width) {
        PlayerCartographyStorage storage = player.getData(ModAttachments.PLAYER_CARTOGRAPHY);
        if (storage == null) {
            return;
        }
        List<CartographyWaypoint> waypoints = storage.getWaypoints();
        if (waypoints.isEmpty()) {
            return;
        }
        ResourceLocation dimension = player.level().dimension().location();
        int lineHeight = font.lineHeight + 2;
        int cursorY = y;
        int maxLines = 6;
        int drawn = 0;
        BlockPos playerPos = player.blockPosition();

        for (CartographyWaypoint waypoint : waypoints) {
            if (!waypoint.enabled() || !dimension.equals(waypoint.dimension())) {
                continue;
            }
            int distance = (int) Math.round(Math.sqrt(waypoint.position().distSqr(playerPos)));
            String label = waypoint.name() + " (" + distance + "m)";
            graphics.drawString(font, label, x, cursorY, 0xFFE6E6E6, true);
            cursorY += lineHeight;
            drawn++;
            if (drawn >= maxLines) {
                break;
            }
        }
    }

    private static ItemStack getEquippedMap(LocalPlayer player) {
        ItemStack main = player.getMainHandItem();
        if (isMapItem(main)) {
            return main;
        }
        ItemStack offhand = player.getOffhandItem();
        return isMapItem(offhand) ? offhand : ItemStack.EMPTY;
    }

    private static boolean isMapItem(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(Items.FILLED_MAP) || stack.is(Items.MAP));
    }

    private static void drawBox(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, BOX_BG);
        graphics.fill(x, y, x + width, y + BOX_BORDER, BOX_BORDER_COLOR);
        graphics.fill(x, y + height - BOX_BORDER, x + width, y + height, BOX_BORDER_COLOR);
        graphics.fill(x, y, x + BOX_BORDER, y + height, BOX_BORDER_COLOR);
        graphics.fill(x + width - BOX_BORDER, y, x + width, y + height, BOX_BORDER_COLOR);
    }
}
