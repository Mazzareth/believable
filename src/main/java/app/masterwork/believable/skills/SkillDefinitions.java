package app.masterwork.believable.skills;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines built-in skills and provides lookup helpers.
 */
public final class SkillDefinitions {
    private static final List<PassiveSkill> PASSIVES = List.of(
        new PassiveSkill(
            "repair",
            "Repair",
            "All items in your inventory will be repaired, slowly."
        ),
        new PassiveSkill(
            "saturation",
            "Saturation",
            "You will regenerate hunger slowly over time."
        )
    );
    private static final List<ActiveSkill> ACTIVES = List.of();
    private static final List<Skill> ALL = buildAll();
    private static final Map<String, Skill> BY_ID = buildById();

    private SkillDefinitions() {
    }

    public static List<Skill> getAll() {
        return ALL;
    }

    public static List<PassiveSkill> getPassiveSkills() {
        return PASSIVES;
    }

    public static List<ActiveSkill> getActiveSkills() {
        return ACTIVES;
    }

    public static Skill getById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return BY_ID.get(id);
    }

    public static PassiveSkill getPassiveSkillByIndex(int index) {
        if (index < 0 || index >= PASSIVES.size()) {
            return null;
        }
        return PASSIVES.get(index);
    }

    public static ActiveSkill getActiveSkillByIndex(int index) {
        if (index < 0 || index >= ACTIVES.size()) {
            return null;
        }
        return ACTIVES.get(index);
    }

    public static int getPassiveSkillIndex(String id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < PASSIVES.size(); i++) {
            if (id.equals(PASSIVES.get(i).id())) {
                return i;
            }
        }
        return -1;
    }

    public static int getActiveSkillIndex(String id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < ACTIVES.size(); i++) {
            if (id.equals(ACTIVES.get(i).id())) {
                return i;
            }
        }
        return -1;
    }

    private static List<Skill> buildAll() {
        int total = PASSIVES.size() + ACTIVES.size();
        Skill[] skills = new Skill[total];
        int index = 0;
        for (PassiveSkill skill : PASSIVES) {
            skills[index++] = skill;
        }
        for (ActiveSkill skill : ACTIVES) {
            skills[index++] = skill;
        }
        return List.of(skills);
    }

    private static Map<String, Skill> buildById() {
        Map<String, Skill> map = new HashMap<>();
        for (Skill skill : ALL) {
            map.put(skill.id(), skill);
        }
        return Map.copyOf(map);
    }
}
