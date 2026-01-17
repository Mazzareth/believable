package app.masterwork.believable.race;

import net.minecraft.server.level.ServerPlayer;

public final class GoatRegenModifier implements RaceModifier {
    public static final String ID = "regeneration";
    public static final GoatRegenModifier INSTANCE = new GoatRegenModifier();
    private static final int REGEN_INTERVAL_TICKS = 40;

    private GoatRegenModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Regeneration";
    }

    @Override
    public String getDescription() {
        return "Regenerates a small amount of health over time.";
    }

    @Override
    public int getHpBonus() {
        return 0;
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        if (player.tickCount % REGEN_INTERVAL_TICKS != 0) {
            return;
        }
        if (player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F);
        }
    }
}
