package app.masterwork.believable.network;

import app.masterwork.believable.Believable;
import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.classes.smith.SmithService;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleSmithingSalvagePayload() implements CustomPacketPayload {
    public static final Type<ToggleSmithingSalvagePayload> TYPE =
        new Type<>(ResourceLocation.parse(Believable.MODID + ":toggle_smithing_salvage"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleSmithingSalvagePayload> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public ToggleSmithingSalvagePayload decode(RegistryFriendlyByteBuf buf) {
                return new ToggleSmithingSalvagePayload();
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, ToggleSmithingSalvagePayload payload) {
            }
        };

    public static void handle(ToggleSmithingSalvagePayload payload, IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PlayerAttachmentStorage storage = serverPlayer.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return;
        }
        if (!SmithService.canUse(serverPlayer)) {
            storage.setSmithingSalvageEnabled(false);
            serverPlayer.syncData(ModAttachments.PLAYER_STORAGE);
            return;
        }
        storage.setSmithingSalvageEnabled(!storage.isSmithingSalvageEnabled());
        serverPlayer.syncData(ModAttachments.PLAYER_STORAGE);
    }

    @Override
    public Type<ToggleSmithingSalvagePayload> type() {
        return TYPE;
    }
}
