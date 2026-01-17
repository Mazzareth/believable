package app.masterwork.believable.client.screen.stats;

import java.util.List;

/**
 * Layout result for a single stat category card.
 */
public record StatCardLayout(StatCategory category, List<StatRowLayout> rows, int x, int y, int width, int height) {
}
