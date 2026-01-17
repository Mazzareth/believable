package app.masterwork.believable.classes.prospector.client;

import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * Client-only state for Prospector Vision.
 */
public final class ProspectorClientState {
    private static boolean enabled;
    private static List<BlockPos> cachedOres = List.of();
    private static int lastScanTick;

    private ProspectorClientState() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean toggle() {
        enabled = !enabled;
        if (!enabled) {
            clearCache();
        }
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        if (!enabled) {
            clearCache();
        }
    }

    public static List<BlockPos> getCachedOres() {
        return cachedOres;
    }

    public static int getLastScanTick() {
        return lastScanTick;
    }

    public static void setCachedOres(List<BlockPos> ores, int tick) {
        cachedOres = List.copyOf(ores);
        lastScanTick = tick;
    }

    public static void clearCache() {
        cachedOres = List.of();
        lastScanTick = 0;
    }
}
