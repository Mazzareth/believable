package app.masterwork.believable.network;

import app.masterwork.believable.Believable;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.stats.PlayerVeinMinerStorage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleVeinMinerPayload() implements CustomPacketPayload {
    public static final Type<ToggleVeinMinerPayload> TYPE =
        new Type<>(ResourceLocation.parse(Believable.MODID + ":toggle_vein_miner"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleVeinMinerPayload> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public ToggleVeinMinerPayload decode(RegistryFriendlyByteBuf buf) {
                return new ToggleVeinMinerPayload();
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, ToggleVeinMinerPayload payload) {
            }
        };

    public static void handle(ToggleVeinMinerPayload payload, IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PlayerVeinMinerStorage storage = serverPlayer.getData(ModAttachments.PLAYER_VEIN_MINER);
        if (storage == null) {
            return;
        }
        storage.setEnabled(!storage.isEnabled());
        serverPlayer.syncData(ModAttachments.PLAYER_VEIN_MINER);
    }

    @Override
    public Type<ToggleVeinMinerPayload> type() {
        return TYPE;
    }
}
