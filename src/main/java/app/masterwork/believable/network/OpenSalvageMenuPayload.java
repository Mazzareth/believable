package app.masterwork.believable.network;

import app.masterwork.believable.Believable;
import app.masterwork.believable.attachment.PlayerSalvageStorage;
import app.masterwork.believable.menu.SalvageMenu;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenSalvageMenuPayload() implements CustomPacketPayload {
    public static final Type<OpenSalvageMenuPayload> TYPE =
        new Type<>(ResourceLocation.parse(Believable.MODID + ":open_salvage_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSalvageMenuPayload> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public OpenSalvageMenuPayload decode(RegistryFriendlyByteBuf buf) {
                return new OpenSalvageMenuPayload();
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, OpenSalvageMenuPayload payload) {
            }
        };

    public static void handle(OpenSalvageMenuPayload payload, IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PlayerSalvageStorage storage = serverPlayer.getData(ModAttachments.PLAYER_SALVAGE);
        if (storage == null) {
            return;
        }
        serverPlayer.openMenu(new MenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new SalvageMenu(containerId, inventory, storage);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("screen.believable.salvage_chest");
            }
        });
    }

    @Override
    public Type<OpenSalvageMenuPayload> type() {
        return TYPE;
    }
}
