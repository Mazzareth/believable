package app.masterwork.believable.race;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.registry.ModAttachments;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.UUID;

public final class BunnyPartnerModifier implements RaceModifier {
    public static final String ID = "partner";
    public static final BunnyPartnerModifier INSTANCE = new BunnyPartnerModifier();
    private static final int EFFECT_DURATION_TICKS = 120;
    private static final int REFRESH_THRESHOLD_TICKS = 40;
    private static final double PARTNER_RANGE_SQR = 64.0;

    private BunnyPartnerModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Partner";
    }

    @Override
    public String getDescription() {
        return "Sneak right-click a player to bond. Use your Unpartner key to end it.";
    }

    @Override
    public int getHpBonus() {
        return 0;
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return;
        }
        UUID partnerId = storage.getPartnerId();
        if (partnerId == null || player.server == null) {
            return;
        }
        ServerPlayer partner = player.server.getPlayerList().getPlayer(partnerId);
        if (partner == null || partner == player) {
            return;
        }
        if (player.distanceToSqr(partner) > PARTNER_RANGE_SQR) {
            return;
        }
        refreshEffect(partner, MobEffects.MOVEMENT_SPEED);
        refreshEffect(partner, MobEffects.REGENERATION);
    }

    private void refreshEffect(ServerPlayer player, Holder<MobEffect> effect) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && current.getAmplifier() == 0 && current.getDuration() > REFRESH_THRESHOLD_TICKS) {
            return;
        }
        player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION_TICKS, 0, true, false, true));
    }
}
