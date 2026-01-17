package app.masterwork.believable.event;

import app.masterwork.believable.Believable;
import app.masterwork.believable.config.Config;
import app.masterwork.believable.character.ClassModifierRegistry;
import app.masterwork.believable.stats.CharacterStatDefinitions;
import app.masterwork.believable.stats.StatDefinitions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public final class ModCommonSetup {
    private ModCommonSetup() {
    }

    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        Believable.LOGGER.info("HELLO FROM COMMON SETUP");
        StatDefinitions.init();
        CharacterStatDefinitions.init();
        ClassModifierRegistry.init();

        if (Config.logDirtBlock) {
            Believable.LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        Believable.LOGGER.info("{}{}", Config.magicNumberIntroduction, Config.magicNumber);

        Config.items.forEach(item -> Believable.LOGGER.info("ITEM >> {}", item.toString()));
    }
}
