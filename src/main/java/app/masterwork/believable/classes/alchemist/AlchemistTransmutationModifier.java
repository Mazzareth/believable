package app.masterwork.believable.classes.alchemist;

import app.masterwork.believable.character.ClassModifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class AlchemistTransmutationModifier implements ClassModifier {
    public static final String ID = "alchemist_transmutation";
    public static final AlchemistTransmutationModifier INSTANCE = new AlchemistTransmutationModifier();
    private static final int ALCHEMY_INTERVAL_TICKS = 200;
    private static final int REGEN_DURATION_TICKS = 100;
    private static final int ABSORPTION_DURATION_TICKS = 160;
    private static final int REGEN_AMPLIFIER = 1;
    private static final int ABSORPTION_AMPLIFIER = 0;
    private static final int FEED_AMOUNT = 1;
    private static final float FEED_SATURATION = 0.6F;
    private static final float DIRECT_HEAL = 2.0F;

    private AlchemistTransmutationModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Transmutation";
    }

    @Override
    public String getDescription() {
        return "Periodically restores health, hunger, and fortifies your body.";
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        if (player.tickCount % ALCHEMY_INTERVAL_TICKS != 0) {
            return;
        }
        if (player.getHealth() < player.getMaxHealth()) {
            player.heal(DIRECT_HEAL);
        }
        player.getFoodData().eat(FEED_AMOUNT, FEED_SATURATION);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_DURATION_TICKS, REGEN_AMPLIFIER, true, false));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ABSORPTION_DURATION_TICKS, ABSORPTION_AMPLIFIER, true, false));
    }
}
