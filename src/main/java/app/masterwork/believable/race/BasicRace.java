package app.masterwork.believable.race;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import app.masterwork.believable.stats.StatModifier;

import java.util.List;

public record BasicRace(String name, String description, int hp, List<StatModifier> statModifiers,
                        List<RaceModifier> modifiers) implements Race {
    public static final Codec<BasicRace> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(BasicRace::name),
            Codec.STRING.fieldOf("description").forGetter(BasicRace::description),
            Codec.INT.fieldOf("hp").forGetter(BasicRace::hp),
            StatModifier.CODEC.listOf().optionalFieldOf("stat_modifiers", List.of()).forGetter(BasicRace::statModifiers),
            RaceModifierRegistry.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(BasicRace::modifiers)
    ).apply(instance, BasicRace::new));

    public BasicRace(String name,
                     String description,
                     int hp,
                     List<StatModifier> statModifiers,
                     List<RaceModifier> modifiers) {
        this.name = name;
        this.description = description;
        this.hp = hp;
        this.statModifiers = statModifiers == null ? List.of() : List.copyOf(statModifiers);
        this.modifiers = modifiers == null ? List.of() : List.copyOf(modifiers);
    }
}
