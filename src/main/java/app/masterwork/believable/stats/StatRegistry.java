package app.masterwork.believable.stats;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class StatRegistry {
    private static final Map<ResourceLocation, Stat> BY_ID = new LinkedHashMap<>();

    private StatRegistry() {
    }

    public static void register(Stat stat) {
        Stat existing = BY_ID.putIfAbsent(stat.getId(), stat);
        if (existing != null) {
            throw new IllegalStateException("Duplicate stat id: " + stat.getId());
        }
    }

    public static Stat get(ResourceLocation id) {
        return BY_ID.get(id);
    }

    public static Collection<Stat> getAll() {
        return Collections.unmodifiableCollection(BY_ID.values());
    }
}
