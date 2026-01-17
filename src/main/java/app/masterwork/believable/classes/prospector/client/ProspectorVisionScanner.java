package app.masterwork.believable.classes.prospector.client;

import app.masterwork.believable.classes.prospector.ProspectorService;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Scans nearby blocks for ore positions when Prospector Vision is enabled.
 */
public final class ProspectorVisionScanner {
    private static final int SCAN_INTERVAL_TICKS = 20;

    private ProspectorVisionScanner() {
    }

    public static void tick(Minecraft minecraft) {
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        if (!ProspectorService.canUse(player)) {
            ProspectorClientState.setEnabled(false);
            return;
        }
        if (!ProspectorClientState.isEnabled()) {
            return;
        }
        int tick = player.tickCount;
        if (tick - ProspectorClientState.getLastScanTick() < SCAN_INTERVAL_TICKS) {
            return;
        }
        int range = ProspectorService.getVisionRange(player);
        if (range <= 0) {
            return;
        }
        List<BlockPos> ores = scanOres(player, range);
        ProspectorClientState.setCachedOres(ores, tick);
    }

    private static List<BlockPos> scanOres(Player player, int range) {
        Level level = player.level();
        BlockPos origin = player.blockPosition();
        int r2 = range * range;
        List<BlockPos> found = new ArrayList<>();
        int startX = origin.getX() - range;
        int startY = origin.getY() - range;
        int startZ = origin.getZ() - range;
        int endX = origin.getX() + range;
        int endY = origin.getY() + range;
        int endZ = origin.getZ() + range;

        for (int y = startY; y <= endY; y++) {
            int dy = y - origin.getY();
            for (int x = startX; x <= endX; x++) {
                int dx = x - origin.getX();
                for (int z = startZ; z <= endZ; z++) {
                    int dz = z - origin.getZ();
                    if (dx * dx + dy * dy + dz * dz > r2) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Tags.Blocks.ORES)) {
                        found.add(pos);
                    }
                }
            }
        }
        return found;
    }
}
