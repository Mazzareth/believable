package app.masterwork.believable.event;

import app.masterwork.believable.Believable;
import app.masterwork.believable.client.hud.BelievableHudOverlay;
import app.masterwork.believable.client.hud.MobNameplateRenderer;
import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import app.masterwork.believable.classes.cartographer.CartographerService;
import app.masterwork.believable.classes.cartographer.client.CartographerClientState;
import app.masterwork.believable.classes.prospector.ProspectorService;
import app.masterwork.believable.classes.prospector.client.ProspectorClientState;
import app.masterwork.believable.classes.prospector.client.ProspectorVisionRenderer;
import app.masterwork.believable.classes.prospector.client.ProspectorVisionScanner;
import app.masterwork.believable.classes.prospector.PlayerProspectorStorage;
import app.masterwork.believable.classes.smith.SmithService;
import app.masterwork.believable.item.ItemAffixService;
import app.masterwork.believable.network.OpenUnbelievableMenuPayload;
import app.masterwork.believable.network.ToggleProspectorVisionPayload;
import app.masterwork.believable.network.ToggleSmithingSalvagePayload;
import app.masterwork.believable.network.ToggleVeinMinerPayload;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.registry.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ModClientEvents {
    private ModClientEvents() {
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        Believable.LOGGER.info("HELLO FROM CLIENT SETUP");
        Believable.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        if (minecraft.screen != null) {
            return;
        }

        while (ModKeyMappings.UNBELIEVABLE_MENU.consumeClick()) {
            PacketDistributor.sendToServer(new OpenUnbelievableMenuPayload());
        }
        while (ModKeyMappings.VEIN_MINER.consumeClick()) {
            PacketDistributor.sendToServer(new ToggleVeinMinerPayload());
        }
        while (ModKeyMappings.CARTOGRAPHER_MINIMAP.consumeClick()) {
            if (!CartographerService.isCartographer(minecraft.player)) {
                continue;
            }
            boolean enabled = CartographerClientState.toggleMinimap();
            minecraft.player.displayClientMessage(
                Component.translatable(enabled
                    ? "message.believable.cartographer.minimap_on"
                    : "message.believable.cartographer.minimap_off"),
                true
            );
        }
        while (ModKeyMappings.PROSPECTOR_VISION.consumeClick()) {
            if (!ProspectorService.canUse(minecraft.player)) {
                continue;
            }
            PlayerProspectorStorage storage = minecraft.player.getData(ModAttachments.PLAYER_PROSPECTOR);
            boolean enabled = storage == null || !storage.isEnabled();
            PacketDistributor.sendToServer(new ToggleProspectorVisionPayload());
            ProspectorClientState.setEnabled(enabled);
            minecraft.player.displayClientMessage(
                Component.translatable(enabled
                    ? "message.believable.prospector.vision_on"
                    : "message.believable.prospector.vision_off"),
                true
            );
        }
        while (ModKeyMappings.SMITHING_SALVAGE.consumeClick()) {
            if (!SmithService.canUse(minecraft.player)) {
                continue;
            }
            PacketDistributor.sendToServer(new ToggleSmithingSalvagePayload());
            boolean enabled = !SmithService.isSalvageInfusionEnabled(minecraft.player);
            minecraft.player.displayClientMessage(
                Component.translatable(enabled
                    ? "message.believable.smithing.salvage_on"
                    : "message.believable.smithing.salvage_off"),
                true
            );
        }
        PlayerProspectorStorage storage = minecraft.player.getData(ModAttachments.PLAYER_PROSPECTOR);
        if (!ProspectorService.canUse(minecraft.player) || storage == null) {
            ProspectorClientState.setEnabled(false);
        } else if (ProspectorClientState.isEnabled() != storage.isEnabled()) {
            ProspectorClientState.setEnabled(storage.isEnabled());
        }
        ProspectorVisionScanner.tick(minecraft);
    }

    public static void onRegisterGuiOverlays(RegisterGuiLayersEvent event) {
        ResourceLocation id = ResourceLocation.parse(Believable.MODID + ":believable_bars");
        event.registerAboveAll(id, BelievableHudOverlay::render);
    }

    public static void onRenderGuiOverlay(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) {
            event.setCanceled(true);
            return;
        }
        if (event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) {
            event.setCanceled(true);
            return;
        }
        if (event.getName().equals(VanillaGuiLayers.AIR_LEVEL)) {
            event.setCanceled(true);
            return;
        }
        if (event.getName().equals(VanillaGuiLayers.ARMOR_LEVEL)) {
            event.setCanceled(true);
            return;
        }
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            event.setCanceled(true);
        }
    }

    public static void onRenderNameTag(RenderNameTagEvent event) {
        MobNameplateRenderer.onRenderNameTag(event);
    }

    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        MobNameplateRenderer.onRenderLivingPost(event);
    }

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        ProspectorVisionRenderer.render(event);
    }

    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemAffixService.appendTooltipLines(event.getItemStack(), event.getToolTip());
    }

    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        if (!ItemAffixService.shouldCustomizeTooltip(event.getItemStack())) {
            return;
        }
        event.setBackgroundStart(UnbelievableTheme.TOOLTIP_BG_START);
        event.setBackgroundEnd(UnbelievableTheme.TOOLTIP_BG_END);
        event.setBorderStart(UnbelievableTheme.TOOLTIP_BORDER_START);
        event.setBorderEnd(UnbelievableTheme.TOOLTIP_BORDER_END);
    }
}
