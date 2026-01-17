package app.masterwork.believable.stats;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CharacterStatRegistry {
    private static final Map<ResourceLocation, CharacterStat> BY_ID = new LinkedHashMap<>();

    private CharacterStatRegistry() {
    }

    public static void register(CharacterStat stat) {
        CharacterStat existing = BY_ID.putIfAbsent(stat.id(), stat);
        if (existing != null) {
            throw new IllegalStateException("Duplicate stat id: " + stat.id());
        }
    }

    public static Collection<CharacterStat> getAll() {
        return Collections.unmodifiableCollection(BY_ID.values());
    }
}
