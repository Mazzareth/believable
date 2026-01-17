package app.masterwork.believable.race;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.HashMap;
import java.util.Map;

public final class RaceModifierRegistry {
    private static final Map<String, RaceModifier> BY_ID = new HashMap<>();

    static {
        register(IncreasedHpModifier.INSTANCE);
        register(GoatRegenModifier.INSTANCE);
        register(GoatStepUpModifier.INSTANCE);
        register(BunnyAgilityModifier.INSTANCE);
        register(BunnyPartnerModifier.INSTANCE);
        register(CatGraceModifier.INSTANCE);
        register(CatProwlModifier.INSTANCE);
    }

    public static final Codec<RaceModifier> CODEC = Codec.STRING.flatXmap(
        id -> {
            RaceModifier modifier = BY_ID.get(id);
            if (modifier == null) {
                return DataResult.error(() -> "Unknown race modifier: " + id);
            }
            return DataResult.success(modifier);
        },
        modifier -> {
            String id = modifier.getId();
            RaceModifier registered = BY_ID.get(id);
            if (registered != modifier) {
                return DataResult.error(() -> "Unknown race modifier: " + modifier.getName());
            }
            return DataResult.success(id);
        }
    );

    private RaceModifierRegistry() {
    }

    private static void register(RaceModifier modifier) {
        String id = modifier.getId();
        RaceModifier existing = BY_ID.putIfAbsent(id, modifier);
        if (existing != null) {
            throw new IllegalStateException("Duplicate race modifier id: " + id);
        }
    }
}
