package app.masterwork.believable.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class PlayerVeinMinerStorage {
    public static final Codec<PlayerVeinMinerStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("level", 1).forGetter(PlayerVeinMinerStorage::getLevel),
        Codec.INT.optionalFieldOf("xp", 0).forGetter(PlayerVeinMinerStorage::getXp),
        Codec.BOOL.optionalFieldOf("enabled", false).forGetter(PlayerVeinMinerStorage::isEnabled)
    ).apply(instance, PlayerVeinMinerStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVeinMinerStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerVeinMinerStorage decode(RegistryFriendlyByteBuf buf) {
            int level = buf.readVarInt();
            int xp = buf.readVarInt();
            boolean enabled = buf.readBoolean();
            return new PlayerVeinMinerStorage(level, xp, enabled);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerVeinMinerStorage storage) {
            buf.writeVarInt(storage.getLevel());
            buf.writeVarInt(storage.getXp());
            buf.writeBoolean(storage.isEnabled());
        }
    };

    private int level;
    private int xp;
    private boolean enabled;
    private boolean veinMining;

    public PlayerVeinMinerStorage() {
        this(1, 0, false);
    }

    public PlayerVeinMinerStorage(int level, int xp, boolean enabled) {
        this.level = Math.max(1, level);
        this.xp = Math.max(0, xp);
        this.enabled = enabled;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVeinMining() {
        return veinMining;
    }

    public void setVeinMining(boolean veinMining) {
        this.veinMining = veinMining;
    }
}
