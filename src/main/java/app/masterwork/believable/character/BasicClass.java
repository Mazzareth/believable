package app.masterwork.believable.character;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import app.masterwork.believable.stats.StatModifier;

import java.util.List;

/**
 * Immutable data holder for a playable class, including descriptive text and stat modifiers.
 */
public record BasicClass(String name, String description, List<StatModifier> statModifiers,
                         List<ClassModifier> modifiers) implements CharacterClass {
    public static final Codec<BasicClass> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(BasicClass::name),
            Codec.STRING.fieldOf("description").forGetter(BasicClass::description),
            StatModifier.CODEC.listOf().optionalFieldOf("stat_modifiers", List.of()).forGetter(BasicClass::statModifiers),
            ClassModifierRegistry.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(BasicClass::modifiers)
    ).apply(instance, BasicClass::new));

    public BasicClass(String name,
                      String description,
                      List<StatModifier> statModifiers,
                      List<ClassModifier> modifiers) {
        this.name = name;
        this.description = description;
        this.statModifiers = statModifiers == null ? List.of() : List.copyOf(statModifiers);
        this.modifiers = modifiers == null ? List.of() : List.copyOf(modifiers);
    }
}
