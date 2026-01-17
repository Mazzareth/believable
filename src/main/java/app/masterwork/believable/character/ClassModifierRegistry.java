package app.masterwork.believable.character;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import app.masterwork.believable.classes.alchemist.AlchemistTransmutationModifier;
import app.masterwork.believable.classes.artificer.ArtificerSalvageModifier;
import app.masterwork.believable.classes.cartographer.CartographerPathfinderModifier;
import app.masterwork.believable.classes.smith.SmithMasterworkModifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers and serializes class modifiers by stable id.
 */
public final class ClassModifierRegistry {
    private static final Map<String, ClassModifier> BY_ID = new HashMap<>();

    static {
        register(ArtificerSalvageModifier.INSTANCE);
        register(SmithMasterworkModifier.INSTANCE);
        register(AlchemistTransmutationModifier.INSTANCE);
        register(CartographerPathfinderModifier.INSTANCE);
    }

    public static final Codec<ClassModifier> CODEC = Codec.STRING.flatXmap(
        id -> {
            ClassModifier modifier = BY_ID.get(id);
            if (modifier == null) {
                return DataResult.error(() -> "Unknown class modifier: " + id);
            }
            return DataResult.success(modifier);
        },
        modifier -> {
            String id = modifier.getId();
            ClassModifier registered = BY_ID.get(id);
            if (registered != modifier) {
                return DataResult.error(() -> "Unknown class modifier: " + modifier.getName());
            }
            return DataResult.success(id);
        }
    );

    private ClassModifierRegistry() {
    }

    public static void init() {
        // Ensures static registration runs.
    }

    private static void register(ClassModifier modifier) {
        String id = modifier.getId();
        ClassModifier existing = BY_ID.putIfAbsent(id, modifier);
        if (existing != null) {
            throw new IllegalStateException("Duplicate class modifier id: " + id);
        }
    }
}
