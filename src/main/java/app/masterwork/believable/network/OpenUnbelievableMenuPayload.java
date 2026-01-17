package app.masterwork.believable.network;

import app.masterwork.believable.Believable;
import app.masterwork.believable.menu.UnbelievableMenu;
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

public record OpenUnbelievableMenuPayload() implements CustomPacketPayload {
    public static final Type<OpenUnbelievableMenuPayload> TYPE =
        new Type<>(ResourceLocation.parse(Believable.MODID + ":open_unbelievable_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenUnbelievableMenuPayload> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public OpenUnbelievableMenuPayload decode(RegistryFriendlyByteBuf buf) {
                return new OpenUnbelievableMenuPayload();
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, OpenUnbelievableMenuPayload payload) {
            }
        };

    public static void handle(OpenUnbelievableMenuPayload payload, IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        serverPlayer.openMenu(new MenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new UnbelievableMenu(containerId, inventory);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("screen.believable.unbelievable_menu");
            }
        });
    }

    @Override
    public Type<OpenUnbelievableMenuPayload> type() {
        return TYPE;
    }
}
