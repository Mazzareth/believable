package app.masterwork.believable.stats;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public final class SimpleStat implements Stat {
    private final ResourceLocation id;
    private final String name;
    private final String description;
    private final double minValue;
    private final double maxValue;
    private final double fallbackDefault;

    public SimpleStat(ResourceLocation id,
                      String name,
                      String description,
                      double minValue,
                      double maxValue,
                      double fallbackDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.fallbackDefault = fallbackDefault;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getDefaultValue(LivingEntity entity) {
        return fallbackDefault;
    }

    @Override
    public double clamp(double value) {
        if (value < minValue) {
            return minValue;
        }
        if (value > maxValue) {
            return maxValue;
        }
        return value;
    }

    @Override
    public void applyTo(LivingEntity entity, double value) {
        // Stored stats have no direct attribute effect.
    }
}
