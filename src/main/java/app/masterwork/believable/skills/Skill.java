package app.masterwork.believable.skills;

/**
 * Describes a skill that can be equipped by the player.
 */
public interface Skill {
    /**
     * @return stable id for lookups and persistence
     */
    String id();

    /**
     * @return display name for UI
     */
    String name();

    /**
     * @return description for tooltips or lists
     */
    String description();

    /**
     * @return skill type classification
     */
    SkillType type();

    /**
     * @return skill category for filtering and organization
     */
    default SkillCategory category() {
        return SkillCategory.UTILITY;
    }
}
