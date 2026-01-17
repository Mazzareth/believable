package app.masterwork.believable.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class PlayerProgressionStorage {
    public static final Codec<PlayerProgressionStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("level", 1).forGetter(PlayerProgressionStorage::getLevel),
        Codec.INT.optionalFieldOf("skill_points", 0).forGetter(PlayerProgressionStorage::getSkillPoints),
        Codec.INT.optionalFieldOf("xp", 0).forGetter(PlayerProgressionStorage::getXp),
        Codec.DOUBLE.optionalFieldOf("xp_gain", 1.0D).forGetter(PlayerProgressionStorage::getXpGain)
    ).apply(instance, PlayerProgressionStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerProgressionStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerProgressionStorage decode(RegistryFriendlyByteBuf buf) {
            int level = buf.readVarInt();
            int skillPoints = buf.readVarInt();
            int xp = buf.readVarInt();
            double xpGain = buf.readDouble();
            return new PlayerProgressionStorage(level, skillPoints, xp, xpGain);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerProgressionStorage storage) {
            buf.writeVarInt(storage.getLevel());
            buf.writeVarInt(storage.getSkillPoints());
            buf.writeVarInt(storage.getXp());
            buf.writeDouble(storage.getXpGain());
        }
    };

    private int level;
    private int skillPoints;
    private int xp;
    private double xpGain;

    public PlayerProgressionStorage() {
        this(1, 0, 0, 1.0D);
    }

    public PlayerProgressionStorage(int level, int skillPoints, int xp, double xpGain) {
        this.level = Math.max(1, level);
        this.skillPoints = Math.max(0, skillPoints);
        this.xp = Math.max(0, xp);
        this.xpGain = Math.max(0.0D, xpGain);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = Math.max(0, skillPoints);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    public double getXpGain() {
        return xpGain;
    }

    public void setXpGain(double xpGain) {
        this.xpGain = Math.max(0.0D, xpGain);
    }
}
