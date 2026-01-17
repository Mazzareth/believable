package app.masterwork.believable.magic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * Immutable implementation of a magic school backed by a codec.
 */
public record BasicMagicSchool(String id, String name, String description,
                               List<String> subsectionTabKeys) implements MagicSchool {
    public static final Codec<BasicMagicSchool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(BasicMagicSchool::id),
            Codec.STRING.fieldOf("name").forGetter(BasicMagicSchool::name),
            Codec.STRING.fieldOf("description").forGetter(BasicMagicSchool::description),
            Codec.STRING.listOf().optionalFieldOf("subsections", List.of()).forGetter(BasicMagicSchool::subsectionTabKeys)
    ).apply(instance, BasicMagicSchool::new));

    public BasicMagicSchool(String id, String name, String description, List<String> subsectionTabKeys) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subsectionTabKeys = subsectionTabKeys == null ? List.of() : List.copyOf(subsectionTabKeys);
    }
}
