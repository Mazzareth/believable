package app.masterwork.believable.classes.artificer;

import app.masterwork.believable.character.ClassModifier;
import net.minecraft.server.level.ServerPlayer;

public final class ArtificerSalvageModifier implements ClassModifier {
    public static final String ID = "artificer_salvage";
    public static final ArtificerSalvageModifier INSTANCE = new ArtificerSalvageModifier();
    private static final int SALVAGE_INTERVAL_TICKS = 100;
    private static final int COMPLEXITY_PER_SALVAGE = 4;
    private static final int MAX_SALVAGE_PER_TICK = 4;

    private ArtificerSalvageModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Salvage Flow";
    }

    @Override
    public String getDescription() {
        return "Generates salvage over time based on your complexity.";
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        if (player.tickCount % SALVAGE_INTERVAL_TICKS != 0) {
            return;
        }
        int complexity = ArtificerService.getComplexity(player);
        int salvage = Math.max(1, complexity / COMPLEXITY_PER_SALVAGE);
        salvage = Math.min(MAX_SALVAGE_PER_TICK, salvage);
        ArtificerService.addSalvage(player, salvage);
    }
}
