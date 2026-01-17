package app.masterwork.believable.skills;

/**
 * Categories for grouping and filtering skills in the UI.
 */
public enum SkillCategory {
    ALL("screen.believable.unbelievable_menu.skills.category.all", "All Skills"),
    COMBAT("screen.believable.unbelievable_menu.skills.category.combat", "Combat"),
    UTILITY("screen.believable.unbelievable_menu.skills.category.utility", "Utility"),
    MOVEMENT("screen.believable.unbelievable_menu.skills.category.movement", "Movement"),
    CRAFTING("screen.believable.unbelievable_menu.skills.category.crafting", "Crafting");

    private final String translationKey;
    private final String defaultName;

    SkillCategory(String translationKey, String defaultName) {
        this.translationKey = translationKey;
        this.defaultName = defaultName;
    }

    public String translationKey() {
        return translationKey;
    }

    public String defaultName() {
        return defaultName;
    }
}
