package app.masterwork.believable.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record StatModifier(ResourceLocation statId, double amount) {
    public static final Codec<StatModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("stat").forGetter(StatModifier::statId),
            Codec.DOUBLE.fieldOf("amount").forGetter(StatModifier::amount)
    ).apply(instance, StatModifier::new));

}
