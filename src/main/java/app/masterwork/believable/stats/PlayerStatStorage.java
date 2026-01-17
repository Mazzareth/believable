package app.masterwork.believable.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public final class PlayerStatStorage {
    public static final Codec<PlayerStatStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(ResourceLocation.CODEC, Codec.DOUBLE)
            .optionalFieldOf("values", Map.of())
            .forGetter(PlayerStatStorage::getValues)
    ).apply(instance, PlayerStatStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerStatStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerStatStorage decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            Map<ResourceLocation, Double> values = new HashMap<>();
            for (int i = 0; i < size; i++) {
                ResourceLocation id = buf.readResourceLocation();
                double value = buf.readDouble();
                values.put(id, value);
            }
            return new PlayerStatStorage(values);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerStatStorage storage) {
            Map<ResourceLocation, Double> values = storage.getValues();
            buf.writeVarInt(values.size());
            for (Map.Entry<ResourceLocation, Double> entry : values.entrySet()) {
                buf.writeResourceLocation(entry.getKey());
                buf.writeDouble(entry.getValue());
            }
        }
    };

    private final Map<ResourceLocation, Double> values;
    private boolean dirty = true;

    public PlayerStatStorage() {
        this(new HashMap<>());
    }

    public PlayerStatStorage(Map<ResourceLocation, Double> values) {
        this.values = new HashMap<>(values);
    }

    public Map<ResourceLocation, Double> getValues() {
        return values;
    }

    public double getValue(Stat stat, LivingEntity entity) {
        return values.getOrDefault(stat.getId(), stat.getDefaultValue(entity));
    }

    public double getValue(ResourceLocation id, double fallback) {
        return values.getOrDefault(id, fallback);
    }

    public boolean hasValue(Stat stat) {
        return values.containsKey(stat.getId());
    }

    public void setValue(Stat stat, double value) {
        double clamped = stat.clamp(value);
        Double previous = values.put(stat.getId(), clamped);
        if (previous == null || Double.compare(previous, clamped) != 0) {
            dirty = true;
        }
    }

    public void setValue(ResourceLocation id, double value) {
        Double previous = values.put(id, value);
        if (previous == null || Double.compare(previous, value) != 0) {
            dirty = true;
        }
    }

    public void clearValue(Stat stat) {
        if (values.remove(stat.getId()) != null) {
            dirty = true;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markClean() {
        dirty = false;
    }
}
