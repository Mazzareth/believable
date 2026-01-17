package app.masterwork.believable.classes.cartographer;

import app.masterwork.believable.character.ClassModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class CartographerPathfinderModifier implements ClassModifier {
    public static final String ID = "cartographer_pathfinder";
    public static final CartographerPathfinderModifier INSTANCE = new CartographerPathfinderModifier();
    private static final int BUFF_INTERVAL_TICKS = 40;
    private static final int SPEED_DURATION_TICKS = 80;
    private static final int NIGHT_VISION_DURATION_TICKS = 220;
    private static final int SPEED_AMPLIFIER = 1;
    private static final int NIGHT_VISION_AMPLIFIER = 0;

    private CartographerPathfinderModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Pathfinder";
    }

    @Override
    public String getDescription() {
        return "Your waypoints sharpen your senses and stride.";
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        if (player.tickCount % BUFF_INTERVAL_TICKS != 0) {
            return;
        }
        if (!hasActiveWaypoint(player)) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, SPEED_DURATION_TICKS, SPEED_AMPLIFIER, true, false));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION_TICKS, NIGHT_VISION_AMPLIFIER, true, false));
    }

    private boolean hasActiveWaypoint(ServerPlayer player) {
        ResourceLocation dimension = player.level().dimension().location();
        for (CartographyWaypoint waypoint : CartographerService.getWaypoints(player)) {
            if (waypoint.enabled() && dimension.equals(waypoint.dimension())) {
                return true;
            }
        }
        return false;
    }
}
