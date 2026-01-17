package app.masterwork.believable.classes.artificer;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.character.ClassDefinitions;
import app.masterwork.believable.item.ItemAffixData;
import app.masterwork.believable.item.ItemAffixService;
import app.masterwork.believable.item.ItemRarity;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.stats.PlayerStatStorage;
import app.masterwork.believable.stats.StatDefinitions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ArtificerService {
    private ArtificerService() {
    }

    public static boolean isArtificer(Player player) {
        if (player == null) {
            return false;
        }
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return false;
        }
        BasicClass playerClass = storage.getPlayerClass();
        return playerClass != null && ClassDefinitions.ARTIFICER.name().equals(playerClass.name());
    }

    public static int getComplexity(Player player) {
        if (player == null) {
            return 0;
        }
        PlayerStatStorage stats = player.getData(ModAttachments.PLAYER_STATS);
        if (stats == null) {
            return 0;
        }
        return (int) Math.floor(stats.getValue(StatDefinitions.MAX_COMPLEXITY, player));
    }

    public static int getSalvage(Player player) {
        if (player == null) {
            return 0;
        }
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        return storage == null ? 0 : storage.getSalvage();
    }

    public static void addSalvage(ServerPlayer player, int amount) {
        if (player == null || amount <= 0) {
            return;
        }
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return;
        }
        storage.setSalvage(storage.getSalvage() + amount);
        player.syncData(ModAttachments.PLAYER_STORAGE);
    }

    public static boolean spendSalvage(ServerPlayer player, int amount) {
        if (player == null || amount <= 0) {
            return false;
        }
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return false;
        }
        int current = storage.getSalvage();
        if (current < amount) {
            return false;
        }
        storage.setSalvage(current - amount);
        player.syncData(ModAttachments.PLAYER_STORAGE);
        return true;
    }

    public static int getSalvageValue(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !ItemAffixService.canRollAffixes(stack)) {
            return 0;
        }
        ItemRarity rarity = ItemAffixService.hasAffixes(stack) ? ItemAffixService.getRarity(stack) : ItemRarity.COMMON;
        int base = switch (rarity) {
            case COMMON -> 1;
            case UNCOMMON -> 2;
            case RARE -> 4;
            case LEGENDARY -> 7;
            case UNIQUE -> 10;
        };
        List<ItemAffixData> affixes = ItemAffixService.getAffixes(stack);
        return Math.max(1, base + Math.max(0, affixes.size() - 1));
    }
}
