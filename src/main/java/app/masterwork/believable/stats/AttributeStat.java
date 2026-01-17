package app.masterwork.believable.stats;

import app.masterwork.believable.Believable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class AttributeStat implements Stat {
    private final ResourceLocation id;
    private final String name;
    private final String description;
    private final ResourceLocation attributeId;
    private final ResourceLocation modifierId;
    private final double minValue;
    private final double maxValue;
    private final double fallbackDefault;

    public AttributeStat(ResourceLocation id,
                         String name,
                         String description,
                         ResourceLocation attributeId,
                         double minValue,
                         double maxValue,
                         double fallbackDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.attributeId = attributeId;
        this.modifierId = ResourceLocation.parse(Believable.MODID + ":stat_" + id.getPath());
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.fallbackDefault = fallbackDefault;
    }

    public Holder<Attribute> getAttribute() {
        if (attributeId == null) {
            return null;
        }
        if (!BuiltInRegistries.ATTRIBUTE.containsKey(attributeId)) {
            return null;
        }
        return BuiltInRegistries.ATTRIBUTE.getHolder(attributeId).orElse(null);
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
        Holder<Attribute> attribute = getAttribute();
        if (attribute == null) {
            return fallbackDefault;
        }
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return fallbackDefault;
        }
        return instance.getBaseValue();
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
        Holder<Attribute> attribute = getAttribute();
        if (attribute == null) {
            return;
        }
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(modifierId);
        double desired = clamp(value);
        double delta = desired - instance.getBaseValue();
        if (Math.abs(delta) <= 1.0E-6D) {
            return;
        }
        instance.addTransientModifier(new AttributeModifier(
            modifierId,
            delta,
            AttributeModifier.Operation.ADD_VALUE
        ));
    }
}
