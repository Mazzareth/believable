package app.masterwork.believable.client.screen.stats;

import java.util.List;

/**
 * Layout result for attribute card rendering.
 */
public record StatsLayout(List<StatCardLayout> cards, int cardsTop, int contentBottom, int maxScroll) {
}
