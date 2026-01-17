package app.masterwork.believable.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class PlayerSmithingStorage {
    public static final Codec<PlayerSmithingStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("level", 1).forGetter(PlayerSmithingStorage::getLevel),
        Codec.INT.optionalFieldOf("xp", 0).forGetter(PlayerSmithingStorage::getXp)
    ).apply(instance, PlayerSmithingStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSmithingStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerSmithingStorage decode(RegistryFriendlyByteBuf buf) {
            int level = buf.readVarInt();
            int xp = buf.readVarInt();
            return new PlayerSmithingStorage(level, xp);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerSmithingStorage storage) {
            buf.writeVarInt(storage.getLevel());
            buf.writeVarInt(storage.getXp());
        }
    };

    private int level;
    private int xp;

    public PlayerSmithingStorage() {
        this(1, 0);
    }

    public PlayerSmithingStorage(int level, int xp) {
        this.level = Math.max(1, level);
        this.xp = Math.max(0, xp);
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
}
