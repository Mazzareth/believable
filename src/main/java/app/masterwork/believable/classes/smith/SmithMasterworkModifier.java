package app.masterwork.believable.classes.smith;

import app.masterwork.believable.character.ClassModifier;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.stats.PlayerStatStorage;
import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatValueCalculator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class SmithMasterworkModifier implements ClassModifier {
    public static final String ID = "smith_masterwork";
    public static final SmithMasterworkModifier INSTANCE = new SmithMasterworkModifier();
    private static final int REPAIR_INTERVAL_TICKS = 60;
    private static final int BASE_REPAIR = 1;
    private static final double REPAIR_PER_SMITHING = 0.5D;
    private static final int MAX_REPAIR = 6;

    private SmithMasterworkModifier() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Masterwork";
    }

    @Override
    public String getDescription() {
        return "Slowly repairs your most damaged gear.";
    }

    @Override
    public void onServerTick(ServerPlayer player) {
        if (player.tickCount % REPAIR_INTERVAL_TICKS != 0) {
            return;
        }
        int amount = getRepairAmount(player);
        if (amount <= 0) {
            return;
        }
        ItemStack target = findMostDamagedItem(player);
        if (target.isEmpty()) {
            return;
        }
        int damage = target.getDamageValue();
        if (damage <= 0) {
            return;
        }
        int newDamage = Math.max(0, damage - amount);
        if (newDamage != damage) {
            target.setDamageValue(newDamage);
        }
    }

    private int getRepairAmount(ServerPlayer player) {
        PlayerStatStorage stats = player.getData(ModAttachments.PLAYER_STATS);
        double smithing = StatValueCalculator.getEffectiveStatValue(StatDefinitions.SMITHING, stats, player);
        double base = StatDefinitions.SMITHING.getDefaultValue(player);
        double bonusLevels = Math.max(0.0D, smithing - base);
        int amount = BASE_REPAIR + (int) Math.floor(bonusLevels * REPAIR_PER_SMITHING);
        return Math.min(MAX_REPAIR, Math.max(0, amount));
    }

    private ItemStack findMostDamagedItem(ServerPlayer player) {
        ItemStack best = ItemStack.EMPTY;
        double bestRatio = 0.0D;
        best = pickMostDamaged(best, bestRatio, player.getInventory().items);
        if (!best.isEmpty()) {
            bestRatio = getDamageRatio(best);
        }
        best = pickMostDamaged(best, bestRatio, player.getInventory().armor);
        if (!best.isEmpty()) {
            bestRatio = getDamageRatio(best);
        }
        return pickMostDamaged(best, bestRatio, player.getInventory().offhand);
    }

    private ItemStack pickMostDamaged(ItemStack currentBest, double currentRatio, Iterable<ItemStack> stacks) {
        ItemStack best = currentBest;
        double bestRatio = currentRatio;
        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty() || !stack.isDamageableItem()) {
                continue;
            }
            int damage = stack.getDamageValue();
            if (damage <= 0) {
                continue;
            }
            double ratio = getDamageRatio(stack);
            if (ratio > bestRatio) {
                bestRatio = ratio;
                best = stack;
            }
        }
        return best;
    }

    private double getDamageRatio(ItemStack stack) {
        int max = stack.getMaxDamage();
        if (max <= 0) {
            return 0.0D;
        }
        return stack.getDamageValue() / (double) max;
    }
}
