package app.masterwork.believable.client.screen.stats;

import app.masterwork.believable.client.screen.layout.PanelLayout;
import app.masterwork.believable.stats.CharacterStat;
import app.masterwork.believable.stats.CharacterStatRegistry;
import app.masterwork.believable.stats.PlayerStatStorage;
import app.masterwork.believable.stats.Stat;
import app.masterwork.believable.stats.StatValueCalculator;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;

/**
 * Builds layout models for stat and attribute cards in the character screen.
 */
public final class StatsLayoutBuilder {
    private StatsLayoutBuilder() {
    }

    /**
     * Groups stats into themed categories for the attributes tab.
     */
    public static List<StatCategory> buildAttributeCategories(Iterable<Stat> stats) {
        StatCategory defense = new StatCategory("Defense", 0xFF4E7AA0);
        StatCategory offense = new StatCategory("Offense", 0xFF9C5A3A);
        StatCategory utility = new StatCategory("Utility", 0xFF3D7B6A);
        StatCategory exploration = new StatCategory("Exploration", 0xFF6B5AA8);
        StatCategory arcane = new StatCategory("Arcane", 0xFF8A57C4);
        StatCategory other = new StatCategory("Other", 0xFF6A6A6A);

        for (Stat stat : stats) {
            String idPath = stat.getId().getPath();
            switch (idPath) {
                case "max_health", "hp_regeneration", "armor", "armor_toughness", "fall_damage_multiplier" ->
                    defense.stats.add(stat);
                case "attack_damage", "attack_speed" -> offense.stats.add(stat);
                case "block_break_speed", "smithing" -> utility.stats.add(stat);
                case "step_height", "jump_strength" -> exploration.stats.add(stat);
                case "max_mana", "current_mana", "mana_regeneration", "spell_power", "max_complexity" ->
                    arcane.stats.add(stat);
                default -> other.stats.add(stat);
            }
        }

        List<StatCategory> categories = new ArrayList<>();
        if (!defense.stats.isEmpty()) {
            categories.add(defense);
        }
        if (!offense.stats.isEmpty()) {
            categories.add(offense);
        }
        if (!utility.stats.isEmpty()) {
            categories.add(utility);
        }
        if (!exploration.stats.isEmpty()) {
            categories.add(exploration);
        }
        if (!arcane.stats.isEmpty()) {
            categories.add(arcane);
        }
        if (!other.stats.isEmpty()) {
            categories.add(other);
        }
        return categories;
    }

    /**
     * Computes card layout positions and scroll bounds for attribute cards.
     */
    public static StatsLayout buildAttributesLayout(PanelLayout layout,
                                                    Iterable<Stat> stats,
                                                    PlayerStatStorage statStorage,
                                                    LivingEntity player,
                                                    Font font,
                                                    int textX,
                                                    int titleY,
                                                    int statCardTwoColumnMinWidth,
                                                    int statCardGap,
                                                    int statCardPadding,
                                                    int statCardHeaderHeight,
                                                    int statValueGap,
                                                    int panelPadding,
                                                    DoubleFunction<String> valueFormatter) {
        List<StatCategory> categories = buildAttributeCategories(stats);
        int titleHeight = (int) (font.lineHeight * 1.25f);
        int cardsTop = titleY + titleHeight + 10;
        int contentRight = layout.contentX() + layout.contentW() - panelPadding;
        int contentBottom = layout.contentY() + layout.contentH() - panelPadding;
        int availableWidth = Math.max(0, contentRight - textX);
        int columns = availableWidth >= statCardTwoColumnMinWidth ? 2 : 1;
        int cardWidth = Math.max(0, (availableWidth - statCardGap * (columns - 1)) / columns);
        int[] columnY = new int[columns];
        for (int i = 0; i < columns; i++) {
            columnY[i] = cardsTop;
        }
        int rowWidth = Math.max(0, cardWidth - statCardPadding * 2);
        int lineHeight = font.lineHeight + 3;
        int maxColumnY = cardsTop;
        List<StatCardLayout> cards = new ArrayList<>();

        for (StatCategory category : categories) {
            List<StatRowLayout> rows = new ArrayList<>();
            int bodyHeight = statCardPadding * 2;
            for (Stat stat : category.stats) {
                StatRowLayout rowLayout = buildStatRowLayout(
                    stat,
                    statStorage,
                    player,
                    font,
                    rowWidth,
                    statValueGap,
                    valueFormatter
                );
                rows.add(rowLayout);
                bodyHeight += rowLayout.totalLines() * lineHeight;
            }
            int cardHeight = statCardHeaderHeight + bodyHeight;
            int columnIndex = 0;
            for (int i = 1; i < columns; i++) {
                if (columnY[i] < columnY[columnIndex]) {
                    columnIndex = i;
                }
            }
            int cardX = textX + columnIndex * (cardWidth + statCardGap);
            int cardY = columnY[columnIndex];
            cards.add(new StatCardLayout(category, rows, cardX, cardY, cardWidth, cardHeight));
            columnY[columnIndex] = cardY + cardHeight + statCardGap;
            maxColumnY = Math.max(maxColumnY, columnY[columnIndex]);
        }
        int availableHeight = Math.max(0, contentBottom - cardsTop);
        int totalHeight = Math.max(0, maxColumnY - cardsTop - statCardGap);
        int maxScroll = Math.max(0, totalHeight - availableHeight);
        return new StatsLayout(cards, cardsTop, contentBottom, maxScroll);
    }

    /**
     * Computes layout bounds for the character stats list.
     */
    public static CharacterStatsLayout buildCharacterStatsLayout(PanelLayout layout,
                                                                 Font font,
                                                                 int textX,
                                                                 int titleY,
                                                                 int statHeaderHeight,
                                                                 int statRowHeight,
                                                                 int statRowGap,
                                                                 int panelPadding) {
        int titleHeight = (int) (font.lineHeight * 1.25f);
        int headerY = titleY + titleHeight + 8;
        int contentRight = layout.contentX() + layout.contentW() - panelPadding;
        int contentBottom = layout.contentY() + layout.contentH() - panelPadding;
        int rowWidth = Math.max(0, contentRight - textX);
        int listTop = headerY + statHeaderHeight + 10;
        int statCount = CharacterStatRegistry.getAll().size();
        int totalHeight = statCount * statRowHeight + Math.max(0, statCount - 1) * statRowGap;
        int availableHeight = Math.max(0, contentBottom - listTop);
        int maxScroll = Math.max(0, totalHeight - availableHeight);
        return new CharacterStatsLayout(
            headerY,
            statHeaderHeight,
            listTop,
            contentBottom,
            rowWidth,
            maxScroll
        );
    }

    /**
     * Builds a row layout for attribute stats with wrapped name text.
     */
    public static StatRowLayout buildStatRowLayout(Stat stat,
                                                   PlayerStatStorage statStorage,
                                                   LivingEntity player,
                                                   Font font,
                                                   int rowWidth,
                                                   int statValueGap,
                                                   DoubleFunction<String> valueFormatter) {
        double value = StatValueCalculator.getEffectiveStatValue(stat, statStorage, player);
        String valueText = valueFormatter.apply(value);
        int valueWidth = font.width(valueText);
        List<FormattedCharSequence> nameLines = rowWidth > 0
            ? font.split(net.minecraft.network.chat.Component.literal(stat.getName()), rowWidth)
            : List.of(FormattedCharSequence.EMPTY);
        if (nameLines.isEmpty()) {
            nameLines = List.of(FormattedCharSequence.EMPTY);
        }
        int firstLineWidth = font.width(nameLines.get(0));
        boolean valueOnFirstLine = rowWidth > 0 && firstLineWidth + valueWidth + statValueGap <= rowWidth;
        int totalLines = nameLines.size() + (valueOnFirstLine ? 0 : 1);
        return new StatRowLayout(nameLines, valueText, valueWidth, valueOnFirstLine, Math.max(1, totalLines));
    }

    /**
     * Builds a row layout for character stats with wrapped name text.
     */
    public static StatRowLayout buildCharacterStatRowLayout(CharacterStat stat,
                                                            PlayerStatStorage statStorage,
                                                            LivingEntity player,
                                                            Font font,
                                                            int rowWidth,
                                                            int statValueGap,
                                                            DoubleFunction<String> valueFormatter) {
        double value = stat.getDisplayValue(statStorage, player);
        String valueText = valueFormatter.apply(value);
        int valueWidth = font.width(valueText);
        List<FormattedCharSequence> nameLines = rowWidth > 0
            ? font.split(net.minecraft.network.chat.Component.literal(stat.name()), rowWidth)
            : List.of(FormattedCharSequence.EMPTY);
        if (nameLines.isEmpty()) {
            nameLines = List.of(FormattedCharSequence.EMPTY);
        }
        int firstLineWidth = font.width(nameLines.get(0));
        boolean valueOnFirstLine = rowWidth > 0 && firstLineWidth + valueWidth + statValueGap <= rowWidth;
        int totalLines = nameLines.size() + (valueOnFirstLine ? 0 : 1);
        return new StatRowLayout(nameLines, valueText, valueWidth, valueOnFirstLine, Math.max(1, totalLines));
    }

}
