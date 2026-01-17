package app.masterwork.believable.classes.cartographer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public final class PlayerCartographyStorage {
    public static final Codec<PlayerCartographyStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        CartographyWaypoint.CODEC.listOf().optionalFieldOf("waypoints", List.of())
            .forGetter(storage -> storage.getWaypoints())
    ).apply(instance, PlayerCartographyStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerCartographyStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerCartographyStorage decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<CartographyWaypoint> waypoints = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                waypoints.add(CartographyWaypoint.STREAM_CODEC.decode(buf));
            }
            return new PlayerCartographyStorage(waypoints);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerCartographyStorage storage) {
            List<CartographyWaypoint> waypoints = storage.getWaypoints();
            buf.writeVarInt(waypoints.size());
            for (CartographyWaypoint waypoint : waypoints) {
                CartographyWaypoint.STREAM_CODEC.encode(buf, waypoint);
            }
        }
    };

    private final List<CartographyWaypoint> waypoints = new ArrayList<>();

    public PlayerCartographyStorage() {
    }

    private PlayerCartographyStorage(List<CartographyWaypoint> waypoints) {
        setWaypoints(waypoints);
    }

    public List<CartographyWaypoint> getWaypoints() {
        return List.copyOf(waypoints);
    }

    public void setWaypoints(List<CartographyWaypoint> waypoints) {
        this.waypoints.clear();
        if (waypoints != null) {
            this.waypoints.addAll(waypoints);
        }
    }

    public void addWaypoint(CartographyWaypoint waypoint) {
        if (waypoint != null) {
            waypoints.add(waypoint);
        }
    }

    public boolean removeWaypoint(int index) {
        if (index < 0 || index >= waypoints.size()) {
            return false;
        }
        waypoints.remove(index);
        return true;
    }

    public boolean updateWaypoint(int index, CartographyWaypoint waypoint) {
        if (index < 0 || index >= waypoints.size() || waypoint == null) {
            return false;
        }
        waypoints.set(index, waypoint);
        return true;
    }
}
