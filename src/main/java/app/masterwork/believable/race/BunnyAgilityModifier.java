package app.masterwork.believable.race;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class BunnyAgilityModifier implements RaceModifier {
    public static final String ID = "agility";
    public static final BunnyAgilityModifier INSTANCE = new BunnyAgilityModifier();
    private static final int EFFECT_DURATION_TICKS = 220;
    private static final int REFRESH_THRESHOLD_TICKS = 40;

    private BunnyAgilityModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Agility";
    }

    @Override
    public String getDescription() {
        return "Grants speed and jump boost.";
    }

    @Override
    public int getHpBonus() {
        return 0;
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        refreshEffect(player, MobEffects.MOVEMENT_SPEED);
        refreshEffect(player, MobEffects.JUMP);
    }

    private void refreshEffect(ServerPlayer player, Holder<MobEffect> effect) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && current.getAmplifier() == 0 && current.getDuration() > REFRESH_THRESHOLD_TICKS) {
            return;
        }
        player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION_TICKS, 0, true, false, true));
    }
}
