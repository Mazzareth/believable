package app.masterwork.believable.network;

import app.masterwork.believable.Believable;
import app.masterwork.believable.classes.cartographer.CartographerService;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RenameWaypointPayload(int index, String name) implements CustomPacketPayload {
    public static final Type<RenameWaypointPayload> TYPE =
        new Type<>(ResourceLocation.parse(Believable.MODID + ":rename_waypoint"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RenameWaypointPayload> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public RenameWaypointPayload decode(RegistryFriendlyByteBuf buf) {
                int index = buf.readVarInt();
                String name = buf.readUtf(CartographerService.MAX_WAYPOINT_NAME_LENGTH);
                return new RenameWaypointPayload(index, name);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, RenameWaypointPayload payload) {
                buf.writeVarInt(payload.index());
                buf.writeUtf(payload.name(), CartographerService.MAX_WAYPOINT_NAME_LENGTH);
            }
        };

    public static void handle(RenameWaypointPayload payload, IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        CartographerService.renameWaypoint(serverPlayer, payload.index(), payload.name());
    }

    @Override
    public Type<RenameWaypointPayload> type() {
        return TYPE;
    }
}
