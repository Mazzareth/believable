package app.masterwork.believable.magic;

import net.minecraft.resources.ResourceLocation;

/**
 * Immutable spell data holder used by spell definitions.
 */
public record BasicSpell(ResourceLocation id, String name, String description, String schoolId, String subsectionKey,
                         int baseManaCost, int complexity) implements Spell {
}
