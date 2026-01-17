package app.masterwork.believable.race;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class CatProwlModifier implements RaceModifier {
    public static final String ID = "prowl";
    public static final CatProwlModifier INSTANCE = new CatProwlModifier();
    private static final int EFFECT_DURATION_TICKS = 600;
    private static final int REFRESH_THRESHOLD_TICKS = 200;
    private static final int DIM_LIGHT_LEVEL = 7;

    private CatProwlModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Prowl";
    }

    @Override
    public String getDescription() {
        return "Grants night vision and speed in dim light.";
    }

    @Override
    public int getHpBonus() {
        return 0;
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        int light = player.level().getMaxLocalRawBrightness(player.blockPosition());
        if (light > DIM_LIGHT_LEVEL) {
            return;
        }
        refreshEffect(player, MobEffects.NIGHT_VISION);
        refreshEffect(player, MobEffects.MOVEMENT_SPEED);
    }

    private void refreshEffect(ServerPlayer player, Holder<MobEffect> effect) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && current.getAmplifier() == 0 && current.getDuration() > REFRESH_THRESHOLD_TICKS) {
            return;
        }
        player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION_TICKS, 0, true, false, true));
    }
}
