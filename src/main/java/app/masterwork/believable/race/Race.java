package app.masterwork.believable.race;

import java.util.List;
import app.masterwork.believable.stats.StatModifier;

public interface Race {
    String name();

    String description();

    int hp();

    List<StatModifier> statModifiers();

    List<RaceModifier> modifiers();
}
