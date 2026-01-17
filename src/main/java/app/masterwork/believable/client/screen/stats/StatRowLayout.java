package app.masterwork.believable.client.screen.stats;

import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * Layout result for a stat row including wrapped name lines.
 */
public record StatRowLayout(List<FormattedCharSequence> nameLines, String valueText, int valueWidth,
                            boolean valueOnFirstLine, int totalLines) {
}
