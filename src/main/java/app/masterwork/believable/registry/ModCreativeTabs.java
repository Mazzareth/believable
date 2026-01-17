package app.masterwork.believable.registry;

import app.masterwork.believable.Believable;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Believable.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register(
        "example_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.believable"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> Items.SPYGLASS.getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(ModItems.EXAMPLE_ITEM.get()))
            .build()
    );

    private ModCreativeTabs() {
    }
}

