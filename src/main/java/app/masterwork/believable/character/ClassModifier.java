package app.masterwork.believable.character;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

/**
 * Gameplay hook for class-specific perks that can react to server ticks or events.
 */
public interface ClassModifier {
    /**
     * @return stable registry id for serialization
     */
    String getId();

    /**
     * @return display name for UI
     */
    String getName();

    /**
     * @return short description shown in the class UI
     */
    String getDescription();

    /**
     * Called on the server tick for players with this modifier.
     */
    default void onServerTick(ServerPlayer player) {
    }

    /**
     * Called when a player with this modifier triggers a fall event.
     */
    default void onLivingFall(LivingFallEvent event) {
    }
}
