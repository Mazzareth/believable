package app.masterwork.believable.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PlayerDiscoveryStorage {
    public static final Codec<PlayerDiscoveryStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.listOf().optionalFieldOf("biomes", List.of())
            .forGetter(storage -> List.copyOf(storage.discoveredBiomes))
    ).apply(instance, PlayerDiscoveryStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDiscoveryStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerDiscoveryStorage decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            Set<ResourceLocation> biomes = new HashSet<>();
            for (int i = 0; i < size; i++) {
                biomes.add(buf.readResourceLocation());
            }
            return new PlayerDiscoveryStorage(biomes);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerDiscoveryStorage storage) {
            Set<ResourceLocation> biomes = storage.getDiscoveredBiomes();
            buf.writeVarInt(biomes.size());
            for (ResourceLocation biomeId : biomes) {
                buf.writeResourceLocation(biomeId);
            }
        }
    };

    private final Set<ResourceLocation> discoveredBiomes;

    public PlayerDiscoveryStorage() {
        this(new HashSet<>());
    }

    private PlayerDiscoveryStorage(List<ResourceLocation> biomes) {
        this.discoveredBiomes = new HashSet<>(biomes);
    }

    private PlayerDiscoveryStorage(Set<ResourceLocation> biomes) {
        this.discoveredBiomes = new HashSet<>(biomes);
    }

    public boolean hasDiscovered(ResourceLocation biomeId) {
        return discoveredBiomes.contains(biomeId);
    }

    public boolean addDiscovered(ResourceLocation biomeId) {
        return discoveredBiomes.add(biomeId);
    }

    public Set<ResourceLocation> getDiscoveredBiomes() {
        return Collections.unmodifiableSet(discoveredBiomes);
    }

    public void setDiscoveredBiomes(Set<ResourceLocation> biomes) {
        discoveredBiomes.clear();
        if (biomes != null) {
            discoveredBiomes.addAll(biomes);
        }
    }
}
