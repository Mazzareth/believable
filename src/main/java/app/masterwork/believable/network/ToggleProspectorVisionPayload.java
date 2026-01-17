package app.masterwork.believable.network;

import app.masterwork.believable.Believable;
import app.masterwork.believable.classes.prospector.ProspectorService;
import app.masterwork.believable.classes.prospector.PlayerProspectorStorage;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleProspectorVisionPayload() implements CustomPacketPayload {
    public static final Type<ToggleProspectorVisionPayload> TYPE =
        new Type<>(ResourceLocation.parse(Believable.MODID + ":toggle_prospector_vision"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleProspectorVisionPayload> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public ToggleProspectorVisionPayload decode(RegistryFriendlyByteBuf buf) {
                return new ToggleProspectorVisionPayload();
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, ToggleProspectorVisionPayload payload) {
            }
        };

    public static void handle(ToggleProspectorVisionPayload payload, IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PlayerProspectorStorage storage = serverPlayer.getData(ModAttachments.PLAYER_PROSPECTOR);
        if (storage == null) {
            return;
        }
        if (!ProspectorService.canUse(serverPlayer)) {
            storage.setEnabled(false);
            serverPlayer.syncData(ModAttachments.PLAYER_PROSPECTOR);
            return;
        }
        storage.setEnabled(!storage.isEnabled());
        serverPlayer.syncData(ModAttachments.PLAYER_PROSPECTOR);
    }

    @Override
    public Type<ToggleProspectorVisionPayload> type() {
        return TYPE;
    }
}
