package app.masterwork.believable.magic;

import net.minecraft.resources.ResourceLocation;

/**
 * Describes a spell entry in the magic menu.
 */
public interface Spell {
    /**
     * @return unique registry id for the spell
     */
    ResourceLocation id();

    /**
     * @return display name
     */
    String name();

    /**
     * @return short spell description
     */
    String description();

    /**
     * @return owning magic school id
     */
    String schoolId();

    /**
     * @return subsection tab key where the spell appears
     */
    String subsectionKey();

    /**
     * @return base mana cost before modifiers
     */
    int baseManaCost();

    /**
     * @return complexity budget required to equip or cast
     */
    int complexity();
}
