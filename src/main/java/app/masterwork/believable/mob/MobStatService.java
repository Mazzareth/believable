package app.masterwork.believable.mob;

import app.masterwork.believable.Believable;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.stats.PlayerProgressionStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.ChunkPos;

/**
 * Handles mob level scaling, stat rolling, and XP payout.
 */
public final class MobStatService {
    public static final double MAX_DAMAGE_BONUS = 12.0D;
    public static final double MAX_ARMOR_BONUS = 10.0D;
    public static final double MAX_HEALTH_BONUS = 40.0D;

    private static final double DAMAGE_PER_LEVEL = 0.25D;
    private static final double DAMAGE_VARIANCE_PER_LEVEL = 0.35D;
    private static final double ARMOR_PER_LEVEL = 0.22D;
    private static final double ARMOR_VARIANCE_PER_LEVEL = 0.30D;
    private static final double HEALTH_PER_LEVEL = 1.5D;
    private static final double HEALTH_VARIANCE_PER_LEVEL = 2.0D;

    private static final double HOSTILE_XP_PER_LEVEL = 6.0D;
    private static final double NEUTRAL_XP_PER_LEVEL = 4.0D;
    private static final double PASSIVE_XP_PER_LEVEL = 2.0D;
    private static final int CHUNK_LEVEL_RADIUS = 5;

    private static final ResourceLocation DAMAGE_MODIFIER_ID =
        ResourceLocation.fromNamespaceAndPath(Believable.MODID, "mob_level_damage");
    private static final ResourceLocation ARMOR_MODIFIER_ID =
        ResourceLocation.fromNamespaceAndPath(Believable.MODID, "mob_level_armor");
    private static final ResourceLocation HEALTH_MODIFIER_ID =
        ResourceLocation.fromNamespaceAndPath(Believable.MODID, "mob_level_health");

    private MobStatService() {
    }

    /**
     * Ensures mob stats are rolled and applied, syncing results to clients.
     */
    public static void ensureMobStats(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        MobStatStorage storage = entity.getData(ModAttachments.MOB_STATS);
        if (storage == null) {
            return;
        }
        boolean newlyRolled = false;
        if (!storage.isInitialized()) {
            int level = resolveLevelFromChunk(serverLevel, entity.chunkPosition());
            rollStats(storage, entity.getRandom(), level);
            storage.setInitialized(true);
            newlyRolled = true;
        }
        applyAttributeModifiers(entity, storage);
        if (newlyRolled) {
            entity.setHealth(entity.getMaxHealth());
        } else if (entity.getHealth() > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }
        entity.syncData(ModAttachments.MOB_STATS);
    }

    private static int resolveLevelFromChunk(ServerLevel level, ChunkPos chunkPos) {
        int total = 0;
        int count = 0;
        for (ServerPlayer player : level.players()) {
            ChunkPos playerChunk = player.chunkPosition();
            if (Math.abs(playerChunk.x - chunkPos.x) > CHUNK_LEVEL_RADIUS
                || Math.abs(playerChunk.z - chunkPos.z) > CHUNK_LEVEL_RADIUS) {
                continue;
            }
            PlayerProgressionStorage progression = player.getData(ModAttachments.PLAYER_PROGRESSION);
            int playerLevel = progression != null ? progression.getLevel() : 1;
            total += playerLevel;
            count++;
        }
        if (count == 0) {
            return 1;
        }
        return Math.max(1, Math.round((float) total / count));
    }

    private static void rollStats(MobStatStorage storage, RandomSource random, int level) {
        storage.setLevel(level);
        double damage = rollStat(random, level, DAMAGE_PER_LEVEL, DAMAGE_VARIANCE_PER_LEVEL, MAX_DAMAGE_BONUS);
        double armor = rollStat(random, level, ARMOR_PER_LEVEL, ARMOR_VARIANCE_PER_LEVEL, MAX_ARMOR_BONUS);
        double health = rollStat(random, level, HEALTH_PER_LEVEL, HEALTH_VARIANCE_PER_LEVEL, MAX_HEALTH_BONUS);

        storage.setDamageBonus(damage);
        storage.setArmorBonus(armor);
        storage.setHealthBonus(health);
        storage.setHighestStatLabel(resolveHighestStatLabel(damage, armor, health));
    }

    private static double rollStat(
        RandomSource random,
        int level,
        double basePerLevel,
        double variancePerLevel,
        double max
    ) {
        double base = level * basePerLevel;
        double variance = level * variancePerLevel;
        double value = base + random.nextDouble() * variance;
        return Mth.clamp(value, 0.0D, max);
    }

    private static String resolveHighestStatLabel(double damage, double armor, double health) {
        double damageScore = damage / MAX_DAMAGE_BONUS;
        double armorScore = armor / MAX_ARMOR_BONUS;
        double healthScore = health / MAX_HEALTH_BONUS;
        if (damageScore >= armorScore && damageScore >= healthScore) {
            return "Damaging";
        }
        if (armorScore >= healthScore) {
            return "Armored";
        }
        return "Healthy";
    }

    private static void applyAttributeModifiers(LivingEntity entity, MobStatStorage storage) {
        applyModifier(entity.getAttribute(Attributes.ATTACK_DAMAGE), DAMAGE_MODIFIER_ID, storage.getDamageBonus());
        applyModifier(entity.getAttribute(Attributes.ARMOR), ARMOR_MODIFIER_ID, storage.getArmorBonus());
        applyModifier(entity.getAttribute(Attributes.MAX_HEALTH), HEALTH_MODIFIER_ID, storage.getHealthBonus());
    }

    private static void applyModifier(AttributeInstance instance, ResourceLocation id, double amount) {
        if (instance == null) {
            return;
        }
        if (amount <= 0.0D) {
            instance.removeModifier(id);
            return;
        }
        AttributeModifier modifier = new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE);
        instance.addOrReplacePermanentModifier(modifier);
    }

    /**
     * Calculates XP based on mob level and hostility.
     */
    public static int getXpForKill(LivingEntity entity) {
        MobStatStorage storage = entity.getData(ModAttachments.MOB_STATS);
        int level = storage != null && storage.isInitialized() ? storage.getLevel() : 1;
        double perLevel = getXpPerLevel(entity);
        int xp = (int) Math.round(level * perLevel);
        return Math.max(1, xp);
    }

    private static double getXpPerLevel(LivingEntity entity) {
        if (entity instanceof Enemy) {
            return HOSTILE_XP_PER_LEVEL;
        }
        if (entity instanceof NeutralMob) {
            return NEUTRAL_XP_PER_LEVEL;
        }
        if (entity instanceof Animal) {
            return PASSIVE_XP_PER_LEVEL;
        }
        return NEUTRAL_XP_PER_LEVEL;
    }
}
