package app.masterwork.believable.attachment;

import app.masterwork.believable.classes.artificer.ArtificerService;
import app.masterwork.believable.item.ItemAffixService;
import app.masterwork.believable.item.ItemRarity;
import app.masterwork.believable.stats.SmithingProgressionService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class PlayerSalvageStorage implements Container {
    public static final int SLOT_COUNT = 27;

    public static final Codec<PlayerSalvageStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(ItemStack.CODEC)
            .optionalFieldOf("items", List.of())
            .forGetter(PlayerSalvageStorage::getItems)
    ).apply(instance, PlayerSalvageStorage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSalvageStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerSalvageStorage decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
            for (int i = 0; i < size && i < SLOT_COUNT; i++) {
                items.set(i, ItemStack.STREAM_CODEC.decode(buf));
            }
            return new PlayerSalvageStorage(items);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerSalvageStorage storage) {
            List<ItemStack> items = storage.items;
            buf.writeVarInt(items.size());
            for (ItemStack stack : items) {
                ItemStack.STREAM_CODEC.encode(buf, stack);
            }
        }
    };

    private final NonNullList<ItemStack> items;

    public PlayerSalvageStorage() {
        this(NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY));
    }

    public PlayerSalvageStorage(List<ItemStack> items) {
        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        int limit = Math.min(items.size(), SLOT_COUNT);
        for (int i = 0; i < limit; i++) {
            ItemStack stack = items.get(i);
            this.items.set(i, stack == null ? ItemStack.EMPTY : stack);
        }
    }

    public List<ItemStack> getItems() {
        return List.copyOf(items);
    }

    public void salvageAll(ServerPlayer player) {
        int salvageGained = 0;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (ItemAffixService.canRollAffixes(stack)) {
                ItemRarity rarity = ItemAffixService.getRarity(stack);
                SmithingProgressionService.awardSalvageXp(player, stack, rarity);
                salvageGained += ArtificerService.getSalvageValue(stack);
            }
            items.set(i, ItemStack.EMPTY);
        }
        if (salvageGained > 0) {
            ArtificerService.addSalvage(player, salvageGained);
        }
        setChanged();
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < items.size() ? items.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (index < 0 || index >= items.size() || count <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = items.get(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack split = stack.split(count);
        if (stack.isEmpty()) {
            items.set(index, ItemStack.EMPTY);
        }
        setChanged();
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = items.get(index);
        items.set(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index < 0 || index >= items.size()) {
            return;
        }
        items.set(index, stack == null ? ItemStack.EMPTY : stack);
        if (stack != null && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
    }
}
