package app.masterwork.believable.registry;

import app.masterwork.believable.Believable;
import app.masterwork.believable.classes.artificer.ArtificingMenu;
import app.masterwork.believable.menu.SalvageMenu;
import app.masterwork.believable.menu.UnbelievableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(Registries.MENU, Believable.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<UnbelievableMenu>> UNBELIEVABLE_MENU =
        MENUS.register("unbelievable_menu", () -> new MenuType<>(UnbelievableMenu::new, FeatureFlags.VANILLA_SET));

    public static final DeferredHolder<MenuType<?>, MenuType<SalvageMenu>> SALVAGE_MENU =
        MENUS.register("salvage_menu", () -> new MenuType<>(SalvageMenu::new, FeatureFlags.VANILLA_SET));

    public static final DeferredHolder<MenuType<?>, MenuType<ArtificingMenu>> ARTIFICING_MENU =
        MENUS.register("artificing_menu", () -> new MenuType<>(ArtificingMenu::new, FeatureFlags.VANILLA_SET));

    private ModMenus() {
    }
}
