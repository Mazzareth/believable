package app.masterwork.believable.stats;

import net.minecraft.server.level.ServerPlayer;

public final class StatApplier {
    private StatApplier() {
    }

    public static void apply(ServerPlayer player, PlayerStatStorage storage) {
        for (Stat stat : StatRegistry.getAll()) {
            double value = StatValueCalculator.getEffectiveStatValue(stat, storage, player);
            stat.applyTo(player, value);
        }
    }
}
