package app.masterwork.believable.character;

import java.util.List;
import app.masterwork.believable.stats.StatModifier;

/**
 * Defines the shared data required to describe a playable class.
 */
public interface CharacterClass {
    /**
     * @return display name used in UI and tooltips
     */
    String name();

    /**
     * @return short description of the class fantasy
     */
    String description();

    /**
     * @return stat modifiers granted by this class
     */
    List<StatModifier> statModifiers();

    /**
     * @return additional modifiers with gameplay hooks
     */
    List<ClassModifier> modifiers();
}
