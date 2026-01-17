package app.masterwork.believable.network;

import app.masterwork.believable.Believable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BiomeDiscoveredPayload(ResourceLocation biomeId) implements CustomPacketPayload {
    public static final Type<BiomeDiscoveredPayload> TYPE =
        new Type<>(ResourceLocation.parse(Believable.MODID + ":biome_discovered"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BiomeDiscoveredPayload> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public BiomeDiscoveredPayload decode(RegistryFriendlyByteBuf buf) {
                return new BiomeDiscoveredPayload(buf.readResourceLocation());
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, BiomeDiscoveredPayload payload) {
                buf.writeResourceLocation(payload.biomeId());
            }
        };

    public static void handle(BiomeDiscoveredPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null) {
                return;
            }
            Component biomeName = Component.translatable(payload.biomeId().toLanguageKey("biome"));
            SystemToast.add(
                minecraft.getToasts(),
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                Component.translatable("toast.believable.biome_discovered"),
                biomeName
            );
        });
    }

    @Override
    public Type<BiomeDiscoveredPayload> type() {
        return TYPE;
    }
}
