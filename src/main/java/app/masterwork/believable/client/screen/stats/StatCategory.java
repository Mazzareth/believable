package app.masterwork.believable.client.screen.stats;

import app.masterwork.believable.stats.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * Groups stats into a titled, colored section.
 */
public final class StatCategory {
    public final String title;
    public final int accentColor;
    public final List<Stat> stats = new ArrayList<>();

    public StatCategory(String title, int accentColor) {
        this.title = title;
        this.accentColor = accentColor;
    }
}
