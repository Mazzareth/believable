package app.masterwork.believable.stats;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface Stat {
    ResourceLocation getId();

    String getName();

    String getDescription();

    double getDefaultValue(LivingEntity entity);

    double clamp(double value);

    void applyTo(LivingEntity entity, double value);

    default String formatValue(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.format("%.2f", value);
        }
    }
}
