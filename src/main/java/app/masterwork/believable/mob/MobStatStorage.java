package app.masterwork.believable.mob;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Stores per-mob level scaling data for damage, armor, and health bonuses.
 */
public final class MobStatStorage {
    public static final Codec<MobStatStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("initialized", false).forGetter(MobStatStorage::isInitialized),
        Codec.INT.optionalFieldOf("level", 1).forGetter(MobStatStorage::getLevel),
        Codec.DOUBLE.optionalFieldOf("health_bonus", 0.0D).forGetter(MobStatStorage::getHealthBonus),
        Codec.DOUBLE.optionalFieldOf("damage_bonus", 0.0D).forGetter(MobStatStorage::getDamageBonus),
        Codec.DOUBLE.optionalFieldOf("armor_bonus", 0.0D).forGetter(MobStatStorage::getArmorBonus),
        Codec.STRING.optionalFieldOf("highest_stat", "Balanced").forGetter(MobStatStorage::getHighestStatLabel)
    ).apply(instance, MobStatStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MobStatStorage> STREAM_CODEC = new StreamCodec<>() {
        private static final int LABEL_MAX_LENGTH = 32;

        @Override
        public MobStatStorage decode(RegistryFriendlyByteBuf buf) {
            boolean initialized = buf.readBoolean();
            int level = buf.readVarInt();
            double healthBonus = buf.readDouble();
            double damageBonus = buf.readDouble();
            double armorBonus = buf.readDouble();
            String highestStatLabel = buf.readUtf(LABEL_MAX_LENGTH);
            return new MobStatStorage(initialized, level, healthBonus, damageBonus, armorBonus, highestStatLabel);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MobStatStorage storage) {
            buf.writeBoolean(storage.isInitialized());
            buf.writeVarInt(storage.getLevel());
            buf.writeDouble(storage.getHealthBonus());
            buf.writeDouble(storage.getDamageBonus());
            buf.writeDouble(storage.getArmorBonus());
            buf.writeUtf(storage.getHighestStatLabel(), LABEL_MAX_LENGTH);
        }
    };

    private boolean initialized;
    private int level;
    private double healthBonus;
    private double damageBonus;
    private double armorBonus;
    private String highestStatLabel;

    public MobStatStorage() {
        this(false, 1, 0.0D, 0.0D, 0.0D, "Balanced");
    }

    public MobStatStorage(
        boolean initialized,
        int level,
        double healthBonus,
        double damageBonus,
        double armorBonus,
        String highestStatLabel
    ) {
        this.initialized = initialized;
        this.level = Math.max(1, level);
        this.healthBonus = Math.max(0.0D, healthBonus);
        this.damageBonus = Math.max(0.0D, damageBonus);
        this.armorBonus = Math.max(0.0D, armorBonus);
        this.highestStatLabel = highestStatLabel == null || highestStatLabel.isBlank() ? "Balanced" : highestStatLabel;
    }

    /**
     * @return true once the mob stats have been rolled and stored
     */
    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public double getHealthBonus() {
        return healthBonus;
    }

    public void setHealthBonus(double healthBonus) {
        this.healthBonus = Math.max(0.0D, healthBonus);
    }

    public double getDamageBonus() {
        return damageBonus;
    }

    public void setDamageBonus(double damageBonus) {
        this.damageBonus = Math.max(0.0D, damageBonus);
    }

    public double getArmorBonus() {
        return armorBonus;
    }

    public void setArmorBonus(double armorBonus) {
        this.armorBonus = Math.max(0.0D, armorBonus);
    }

    public String getHighestStatLabel() {
        return highestStatLabel;
    }

    public void setHighestStatLabel(String highestStatLabel) {
        if (highestStatLabel == null || highestStatLabel.isBlank()) {
            this.highestStatLabel = "Balanced";
            return;
        }
        this.highestStatLabel = highestStatLabel;
    }
}
