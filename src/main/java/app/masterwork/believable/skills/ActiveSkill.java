package app.masterwork.believable.skills;

/**
 * Active skill definition that can be triggered, tied to a magic school.
 */
public record ActiveSkill(String id, String name, String description, String schoolId, SkillCategory category)
        implements Skill {
    public ActiveSkill(String id, String name, String description, String schoolId) {
        this(id, name, description, schoolId, SkillCategory.COMBAT);
    }

    @Override
    public SkillType type() {
        return SkillType.ACTIVE;
    }

    @Override
    public SkillCategory category() {
        return category;
    }
}
