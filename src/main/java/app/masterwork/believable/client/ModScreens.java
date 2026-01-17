package app.masterwork.believable.client;

import app.masterwork.believable.classes.artificer.ArtificingScreen;
import app.masterwork.believable.client.screen.SalvageScreen;
import app.masterwork.believable.client.screen.UnbelievableScreen;
import app.masterwork.believable.registry.ModMenus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ModScreens {
    private ModScreens() {
    }

    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.UNBELIEVABLE_MENU.get(), UnbelievableScreen::new);
        event.register(ModMenus.SALVAGE_MENU.get(), SalvageScreen::new);
        event.register(ModMenus.ARTIFICING_MENU.get(), ArtificingScreen::new);
    }
}
