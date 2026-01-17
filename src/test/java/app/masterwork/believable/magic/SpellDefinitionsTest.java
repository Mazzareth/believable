package app.masterwork.believable.magic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpellDefinitionsTest {
    @Test
    void getBySchoolIdReturnsSpells() {
        List<Spell> spells = SpellDefinitions.getBySchoolId(MagicSchoolDefinitions.ELEMENTALISM.id());
        assertFalse(spells.isEmpty());
        assertTrue(spells.stream().allMatch(spell -> MagicSchoolDefinitions.ELEMENTALISM.id().equals(spell.schoolId())));
    }

    @Test
    void getBySchoolAndSubsectionFilters() {
        List<Spell> spells = SpellDefinitions.getBySchoolAndSubsection(
            MagicSchoolDefinitions.AILMENT.id(),
            MagicSchoolDefinitions.TAB_AILMENT_POISON_KEY
        );
        assertFalse(spells.isEmpty());
        assertTrue(spells.stream().allMatch(spell -> MagicSchoolDefinitions.TAB_AILMENT_POISON_KEY.equals(spell.subsectionKey())));
    }

    @Test
    void nullSubsectionReturnsSchoolList() {
        List<Spell> bySchool = SpellDefinitions.getBySchoolId(MagicSchoolDefinitions.NECROMANCY.id());
        List<Spell> bySchoolAndNull = SpellDefinitions.getBySchoolAndSubsection(MagicSchoolDefinitions.NECROMANCY.id(), null);
        assertEquals(bySchool, bySchoolAndNull);
    }
}
