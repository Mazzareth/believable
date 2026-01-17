package app.masterwork.believable.race;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public interface RaceModifier {
    String getId();

    String getName();

    String getDescription();

    int getHpBonus();

    default void onServerTick(ServerPlayer player) {
    }

    default void onLivingFall(LivingFallEvent event) {
    }
}
