package app.masterwork.believable;

import app.masterwork.believable.config.Config;
import app.masterwork.believable.client.ModScreens;
import app.masterwork.believable.event.ModAttributeEvents;
import app.masterwork.believable.event.ModClientEvents;
import app.masterwork.believable.event.ModCommonEvents;
import app.masterwork.believable.event.ModCommonSetup;
import app.masterwork.believable.event.ModCreativeTabEvents;
import app.masterwork.believable.event.ModCommands;
import app.masterwork.believable.network.ModPayloads;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.registry.ModBlockEntities;
import app.masterwork.believable.registry.ModBlocks;
import app.masterwork.believable.registry.ModCreativeTabs;
import app.masterwork.believable.registry.ModItems;
import app.masterwork.believable.registry.ModKeyMappings;
import app.masterwork.believable.registry.ModMenus;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Believable.MODID)
public class Believable {
    public static final String MODID = "believable";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Believable(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ModCommonSetup::onCommonSetup);
        modEventBus.addListener(ModCreativeTabEvents::addCreative);
        modEventBus.addListener(ModAttributeEvents::onEntityAttributeModification);
        modEventBus.addListener(Config::onLoad);

        if (FMLLoader.getDist().isClient()) {
            modEventBus.addListener(ModClientEvents::onClientSetup);
            modEventBus.addListener(ModKeyMappings::onRegisterKeyMappings);
            modEventBus.addListener(ModScreens::onRegisterScreens);
            modEventBus.addListener(ModClientEvents::onRegisterGuiOverlays);
            NeoForge.EVENT_BUS.addListener(ModClientEvents::onClientTick);
            NeoForge.EVENT_BUS.addListener(ModClientEvents::onRenderGuiOverlay);
            NeoForge.EVENT_BUS.addListener(ModClientEvents::onRenderNameTag);
            NeoForge.EVENT_BUS.addListener(ModClientEvents::onRenderLivingPost);
            NeoForge.EVENT_BUS.addListener(ModClientEvents::onRenderLevelStage);
            NeoForge.EVENT_BUS.addListener(ModClientEvents::onItemTooltip);
            NeoForge.EVENT_BUS.addListener(ModClientEvents::onTooltipColor);
        }
        modEventBus.addListener(ModPayloads::onRegisterPayloadHandlers);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onServerStarting);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onEntityJoinLevel);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onLivingFall);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onPlayerXpChange);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onPlayerXpPickup);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onItemCrafted);
        NeoForge.EVENT_BUS.addListener(ModCommonEvents::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(ModCommands::onRegisterCommands);

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
