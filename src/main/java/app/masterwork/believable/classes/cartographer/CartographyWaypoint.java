package app.masterwork.believable.classes.cartographer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record CartographyWaypoint(String name, BlockPos position, ResourceLocation dimension, boolean enabled) {
    public static final Codec<CartographyWaypoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(CartographyWaypoint::name),
        BlockPos.CODEC.fieldOf("position").forGetter(CartographyWaypoint::position),
        ResourceLocation.CODEC.fieldOf("dimension").forGetter(CartographyWaypoint::dimension),
        Codec.BOOL.optionalFieldOf("enabled", true).forGetter(CartographyWaypoint::enabled)
    ).apply(instance, CartographyWaypoint::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CartographyWaypoint> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CartographyWaypoint decode(RegistryFriendlyByteBuf buf) {
            String name = buf.readUtf(256);
            BlockPos position = buf.readBlockPos();
            ResourceLocation dimension = buf.readResourceLocation();
            boolean enabled = buf.readBoolean();
            return new CartographyWaypoint(name, position, dimension, enabled);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CartographyWaypoint waypoint) {
            buf.writeUtf(waypoint.name(), 256);
            buf.writeBlockPos(waypoint.position());
            buf.writeResourceLocation(waypoint.dimension());
            buf.writeBoolean(waypoint.enabled());
        }
    };

    public CartographyWaypoint(String name, BlockPos position, ResourceLocation dimension, boolean enabled) {
        this.name = name == null ? "Waypoint" : name;
        this.position = position == null ? BlockPos.ZERO : position;
        this.dimension = dimension == null ? ResourceLocation.withDefaultNamespace("overworld") : dimension;
        this.enabled = enabled;
    }

    public CartographyWaypoint withEnabled(boolean enabled) {
        return new CartographyWaypoint(name, position, dimension, enabled);
    }

    public CartographyWaypoint withPosition(BlockPos position, ResourceLocation dimension) {
        return new CartographyWaypoint(name, position, dimension, enabled);
    }

    public CartographyWaypoint withName(String name) {
        return new CartographyWaypoint(name, position, dimension, enabled);
    }
}
