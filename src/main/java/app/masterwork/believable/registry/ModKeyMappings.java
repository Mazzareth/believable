package app.masterwork.believable.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class ModKeyMappings {
    private static final String CATEGORY = "key.categories.believable";

    public static final KeyMapping UNBELIEVABLE_MENU = new KeyMapping(
            "key.believable.unbelievable_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            CATEGORY
    );
    public static final KeyMapping VEIN_MINER = new KeyMapping(
            "key.believable.vein_miner",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            CATEGORY
    );
    public static final KeyMapping SKILL_ONE = new KeyMapping(
            "key.believable.skill_one",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            CATEGORY
    );
    public static final KeyMapping CARTOGRAPHER_MINIMAP = new KeyMapping(
            "key.believable.cartographer.minimap",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            CATEGORY
    );
    public static final KeyMapping PROSPECTOR_VISION = new KeyMapping(
            "key.believable.prospector.vision",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );
    public static final KeyMapping SMITHING_SALVAGE = new KeyMapping(
            "key.believable.smithing.salvage",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );

    public static final KeyMapping SKILL_TWO = new KeyMapping(
            "key.believable.skill_two",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            CATEGORY
    );

    private ModKeyMappings() {
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(UNBELIEVABLE_MENU);
        event.register(VEIN_MINER);
        event.register(SKILL_ONE);
        event.register(SKILL_TWO);
        event.register(CARTOGRAPHER_MINIMAP);
        event.register(PROSPECTOR_VISION);
        event.register(SMITHING_SALVAGE);
    }
}
