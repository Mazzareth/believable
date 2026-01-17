package app.masterwork.believable.stats;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public record CharacterStat(ResourceLocation id, String name, String description, double defaultValue,
                            List<CharacterStatEffect> effects) {
    public CharacterStat(ResourceLocation id,
                         String name,
                         String description,
                         double defaultValue,
                         List<CharacterStatEffect> effects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.effects = List.copyOf(effects);
    }

    public double getDisplayValue(PlayerStatStorage storage, LivingEntity entity) {
        if (storage == null) {
            return defaultValue;
        }
        return storage.getValue(id, defaultValue);
    }
}
