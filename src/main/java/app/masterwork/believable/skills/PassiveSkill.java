package app.masterwork.believable.skills;

/**
 * Passive skill definition that applies continuously when equipped.
 */
public record PassiveSkill(String id, String name, String description, SkillCategory category) implements Skill {
    public PassiveSkill(String id, String name, String description) {
        this(id, name, description, SkillCategory.UTILITY);
    }

    @Override
    public SkillType type() {
        return SkillType.PASSIVE;
    }

    @Override
    public SkillCategory category() {
        return category;
    }
}
