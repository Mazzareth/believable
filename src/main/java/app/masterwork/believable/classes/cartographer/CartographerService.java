package app.masterwork.believable.classes.cartographer;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.character.ClassDefinitions;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class CartographerService {
    public static final int MAX_WAYPOINT_NAME_LENGTH = 32;

    private CartographerService() {
    }

    public static boolean isCartographer(Player player) {
        if (player == null) {
            return false;
        }
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return false;
        }
        BasicClass playerClass = storage.getPlayerClass();
        return playerClass != null && ClassDefinitions.CARTOGRAPHER.name().equals(playerClass.name());
    }

    public static PlayerCartographyStorage getStorage(Player player) {
        if (player == null) {
            return null;
        }
        return player.getData(ModAttachments.PLAYER_CARTOGRAPHY);
    }

    public static List<CartographyWaypoint> getWaypoints(Player player) {
        PlayerCartographyStorage storage = getStorage(player);
        return storage == null ? List.of() : storage.getWaypoints();
    }

    public static void addWaypoint(ServerPlayer player) {
        if (player == null || !isCartographer(player)) {
            return;
        }
        PlayerCartographyStorage storage = player.getData(ModAttachments.PLAYER_CARTOGRAPHY);
        if (storage == null) {
            return;
        }
        String name = getDefaultName(storage);
        BlockPos position = player.blockPosition();
        ResourceLocation dimension = player.level().dimension().location();
        storage.addWaypoint(new CartographyWaypoint(name, position, dimension, true));
        player.syncData(ModAttachments.PLAYER_CARTOGRAPHY);
    }

    public static boolean toggleWaypoint(ServerPlayer player, int index) {
        PlayerCartographyStorage storage = getStorage(player);
        if (storage == null) {
            return false;
        }
        List<CartographyWaypoint> waypoints = storage.getWaypoints();
        if (index < 0 || index >= waypoints.size()) {
            return false;
        }
        CartographyWaypoint waypoint = waypoints.get(index);
        storage.updateWaypoint(index, waypoint.withEnabled(!waypoint.enabled()));
        player.syncData(ModAttachments.PLAYER_CARTOGRAPHY);
        return true;
    }

    public static boolean removeWaypoint(ServerPlayer player, int index) {
        PlayerCartographyStorage storage = getStorage(player);
        if (storage == null) {
            return false;
        }
        if (!storage.removeWaypoint(index)) {
            return false;
        }
        player.syncData(ModAttachments.PLAYER_CARTOGRAPHY);
        return true;
    }

    public static boolean moveWaypointToPlayer(ServerPlayer player, int index) {
        PlayerCartographyStorage storage = getStorage(player);
        if (storage == null) {
            return false;
        }
        List<CartographyWaypoint> waypoints = storage.getWaypoints();
        if (index < 0 || index >= waypoints.size()) {
            return false;
        }
        CartographyWaypoint waypoint = waypoints.get(index);
        BlockPos position = player.blockPosition();
        ResourceLocation dimension = player.level().dimension().location();
        storage.updateWaypoint(index, waypoint.withPosition(position, dimension));
        player.syncData(ModAttachments.PLAYER_CARTOGRAPHY);
        return true;
    }

    public static boolean renameWaypoint(ServerPlayer player, int index, String name) {
        PlayerCartographyStorage storage = getStorage(player);
        if (storage == null || name == null) {
            return false;
        }
        List<CartographyWaypoint> waypoints = storage.getWaypoints();
        if (index < 0 || index >= waypoints.size()) {
            return false;
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        if (trimmed.length() > MAX_WAYPOINT_NAME_LENGTH) {
            trimmed = trimmed.substring(0, MAX_WAYPOINT_NAME_LENGTH);
        }
        CartographyWaypoint waypoint = waypoints.get(index);
        storage.updateWaypoint(index, waypoint.withName(trimmed));
        player.syncData(ModAttachments.PLAYER_CARTOGRAPHY);
        return true;
    }

    private static String getDefaultName(PlayerCartographyStorage storage) {
        int nextIndex = storage == null ? 1 : storage.getWaypoints().size() + 1;
        return "Waypoint " + nextIndex;
    }
}
