package app.masterwork.believable.classes.prospector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class PlayerProspectorStorage {
    public static final Codec<PlayerProspectorStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("enabled", false).forGetter(PlayerProspectorStorage::isEnabled)
    ).apply(instance, PlayerProspectorStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerProspectorStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerProspectorStorage decode(RegistryFriendlyByteBuf buf) {
            boolean enabled = buf.readBoolean();
            return new PlayerProspectorStorage(enabled);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerProspectorStorage storage) {
            buf.writeBoolean(storage.isEnabled());
        }
    };

    private boolean enabled;

    public PlayerProspectorStorage() {
        this(false);
    }

    public PlayerProspectorStorage(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
