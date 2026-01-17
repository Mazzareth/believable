package app.masterwork.believable.event;

import app.masterwork.believable.Believable;
import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.attachment.PlayerSalvageStorage;
import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.character.ClassModifier;
import app.masterwork.believable.classes.artificer.ArtificerService;
import app.masterwork.believable.item.GearDropService;
import app.masterwork.believable.item.ItemAffixService;
import app.masterwork.believable.item.ItemCategory;
import app.masterwork.believable.item.ItemRarity;
import app.masterwork.believable.classes.artificer.ArtificingMenu;
import app.masterwork.believable.menu.SmithingCraftingMenu;
import app.masterwork.believable.mob.MobStatStorage;
import app.masterwork.believable.mob.MobStatService;
import app.masterwork.believable.race.BasicRace;
import app.masterwork.believable.race.Race;
import app.masterwork.believable.race.RaceDefinitions;
import app.masterwork.believable.race.RaceModifier;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.classes.smith.SmithService;
import app.masterwork.believable.stats.LevelingRules;
import app.masterwork.believable.stats.PlayerDiscoveryStorage;
import app.masterwork.believable.stats.PlayerProgressionStorage;
import app.masterwork.believable.stats.PlayerSmithingStorage;
import app.masterwork.believable.stats.PlayerProgressionService;
import app.masterwork.believable.stats.PlayerVeinMinerStorage;
import app.masterwork.believable.classes.prospector.PlayerProspectorStorage;
import app.masterwork.believable.stats.SmithingProgressionService;
import app.masterwork.believable.stats.VeinMinerProgressionService;
import app.masterwork.believable.stats.XpSources;
import app.masterwork.believable.stats.PlayerStatStorage;
import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatApplier;
import app.masterwork.believable.stats.StatValueCalculator;
import app.masterwork.believable.network.BiomeDiscoveredPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.UUID;

public final class ModCommonEvents {
    private static final int HP_REGEN_INTERVAL_TICKS = 20;
    private static final int BIOME_CHECK_INTERVAL_TICKS = 40;
    private static final int STAT_APPLY_INTERVAL_TICKS = 20;

    private ModCommonEvents() {
    }

    public static void onServerStarting(ServerStartingEvent event) {
        Believable.LOGGER.info("HELLO from server starting");
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PlayerStatStorage statStorage = serverPlayer.getData(ModAttachments.PLAYER_STATS);
        if (statStorage != null) {
            if (statStorage.isDirty() || player.tickCount % STAT_APPLY_INTERVAL_TICKS == 0) {
                StatApplier.apply(serverPlayer, statStorage);
                statStorage.markClean();
            }
        }
        if (player.tickCount % STAT_APPLY_INTERVAL_TICKS == 0) {
            SmithingProgressionService.ensureSmithingStat(serverPlayer);
            ensureEquippedAffixes(serverPlayer);
        }
        applyHealthRegen(serverPlayer, statStorage);
        Race race = getRace(serverPlayer);
        if (race != null) {
            for (RaceModifier modifier : race.modifiers()) {
                modifier.onServerTick(serverPlayer);
            }
        }
        BasicClass playerClass = getPlayerClass(serverPlayer);
        if (playerClass != null) {
            for (ClassModifier modifier : playerClass.modifiers()) {
                modifier.onServerTick(serverPlayer);
            }
        }
        checkBiomeDiscovery(serverPlayer);
    }

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity living)) {
            return;
        }
        if (living instanceof Player) {
            return;
        }
        MobStatService.ensureMobStats(living);
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity living)) {
            return;
        }
        if (living instanceof Player) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        int playerLevel = getPlayerLevel(serverPlayer);
        int xp = MobStatService.getXpForKill(living);
        if (xp <= 0) {
            return;
        }
        PlayerProgressionService.awardXp(serverPlayer, xp, XpSources.MOB_KILL);

        MobStatStorage storage = living.getData(ModAttachments.MOB_STATS);
        int level = storage != null && storage.isInitialized() ? storage.getLevel() : 1;
        if (!shouldDropGear(living, level)) {
            return;
        }
        var drop = GearDropService.createRandomDrop(living, playerLevel);
        if (!drop.isEmpty()) {
            ItemRarity rarity = ItemAffixService.getRarity(drop);
            ItemRarity minRarity = getMinDropRarity(serverPlayer);
            if (rarity.ordinal() < minRarity.ordinal()) {
                SmithingProgressionService.awardSalvageXp(serverPlayer, drop, rarity);
                return;
            }
            living.spawnAtLocation(drop);
        }
    }

    private static void applyHealthRegen(ServerPlayer player, PlayerStatStorage statStorage) {
        if (player.tickCount % HP_REGEN_INTERVAL_TICKS != 0) {
            return;
        }
        if (player.getHealth() >= player.getMaxHealth()) {
            return;
        }
        double regen = StatValueCalculator.getEffectiveStatValue(
            StatDefinitions.HP_REGENERATION,
            statStorage,
            player
        );
        if (regen <= 0.0D) {
            return;
        }
        player.heal((float) regen);
    }

    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        Race race = getRace(serverPlayer);
        if (race == null) {
            return;
        }
        for (RaceModifier modifier : race.modifiers()) {
            modifier.onLivingFall(event);
        }
        BasicClass playerClass = getPlayerClass(serverPlayer);
        if (playerClass == null) {
            return;
        }
        for (ClassModifier modifier : playerClass.modifiers()) {
            modifier.onLivingFall(event);
        }
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!(event.getTarget() instanceof ServerPlayer target)) {
            return;
        }
        if (serverPlayer == target) {
            return;
        }
        if (!serverPlayer.isCrouching()) {
            return;
        }
        if (tryHandleSubjugation(serverPlayer, target)) {
            return;
        }
        PlayerAttachmentStorage storage = serverPlayer.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return;
        }
        BasicRace race = storage.getRace();
        if (race == null) {
            return;
        }
        if (storage.getPartnerId() != null) {
            serverPlayer.displayClientMessage(
                Component.literal("You already have a partner. Use your Unpartner key to end it."),
                true
            );
            return;
        }
        storage.setPartnerId(target.getUUID());
        serverPlayer.syncData(ModAttachments.PLAYER_STORAGE);
        sendPartnerMessages(serverPlayer, target, true);
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            syncDoubleDoor(serverPlayer, event);
        }
        if (event.getLevel().getBlockState(event.getPos()).is(Blocks.SMITHING_TABLE)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            if (!SmithService.canUse(serverPlayer)) {
                serverPlayer.displayClientMessage(
                    Component.translatable("message.believable.smithing.not_smith"),
                    true
                );
                return;
            }
            serverPlayer.openMenu(new SimpleMenuProvider(
                (containerId, inventory, player) -> new SmithingCraftingMenu(
                    containerId,
                    inventory,
                    ContainerLevelAccess.create(event.getLevel(), event.getPos())
                ),
                Component.translatable("screen.believable.smithing_table")
            ));
            return;
        }
        if (!event.getLevel().getBlockState(event.getPos()).is(Blocks.ENCHANTING_TABLE)) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!ArtificerService.isArtificer(serverPlayer)) {
            serverPlayer.displayClientMessage(
                Component.translatable("message.believable.artificing.not_artificer"),
                true
            );
            return;
        }
        serverPlayer.openMenu(new SimpleMenuProvider(
            (containerId, inventory, player) -> new ArtificingMenu(containerId, inventory),
            Component.translatable("screen.believable.artificing_table")
        ));
    }

    private static void syncDoubleDoor(ServerPlayer player, PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock doorBlock)) {
            return;
        }
        if (!doorBlock.type().canOpenByHand()) {
            return;
        }
        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
        DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
        Direction offsetDir = hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
        BlockPos otherPos = pos.relative(offsetDir);
        BlockState otherState = level.getBlockState(otherPos);
        if (!(otherState.getBlock() instanceof DoorBlock otherDoor)) {
            return;
        }
        if (otherState.getValue(DoorBlock.FACING) != facing) {
            return;
        }
        if (otherState.getValue(DoorBlock.HINGE) == hinge) {
            return;
        }
        if (otherState.getValue(DoorBlock.HALF) != half) {
            return;
        }
        boolean newOpen = !state.getValue(DoorBlock.OPEN);
        otherDoor.setOpen(player, level, otherState, otherPos, newOpen);
    }

    private static boolean tryHandleSubjugation(ServerPlayer subjugator, ServerPlayer target) {
        ItemStack nameTag = findNamedNameTag(subjugator);
        if (nameTag.isEmpty()) {
            return false;
        }
        PlayerAttachmentStorage storage = target.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return false;
        }
        String newName = nameTag.getHoverName().getString();
        if (newName == null || newName.isBlank()) {
            return false;
        }
        UUID existingSubjugator = storage.getSubjugatorId();
        UUID subjugatorId = subjugator.getUUID();
        if (existingSubjugator != null && !existingSubjugator.equals(subjugatorId)) {
            subjugator.displayClientMessage(
                Component.literal(target.getGameProfile().getName() + " is already subjugated."),
                true
            );
            return true;
        }
        storage.setSubjugatorId(subjugatorId);
        storage.setSubjugatorName(subjugator.getGameProfile().getName());
        storage.setSubjugatedName(newName);
        target.syncData(ModAttachments.PLAYER_STORAGE);
        if (!subjugator.isCreative()) {
            nameTag.shrink(1);
        }
        subjugator.displayClientMessage(
            Component.literal("You subjugated " + target.getGameProfile().getName() + " as " + newName + "."),
            true
        );
        target.displayClientMessage(
            Component.literal("You have been subjugated by " + subjugator.getGameProfile().getName() + ". New name: " + newName + "."),
            true
        );
        return true;
    }

    private static ItemStack findNamedNameTag(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        if (isNamedNameTag(mainHand)) {
            return mainHand;
        }
        ItemStack offHand = player.getOffhandItem();
        if (isNamedNameTag(offHand)) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isNamedNameTag(ItemStack stack) {
        return !stack.isEmpty() && stack.is(Items.NAME_TAG) && stack.has(DataComponents.CUSTOM_NAME);
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getOriginal() instanceof ServerPlayer original)) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        PlayerAttachmentStorage originalStorage = original.getData(ModAttachments.PLAYER_STORAGE);
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage != null && originalStorage != null) {
            storage.setStoredValue(originalStorage.getStoredValue());
            storage.setRace(originalStorage.getRace());
            storage.setPlayerClass(originalStorage.getPlayerClass());
            storage.setMagicSchool(originalStorage.getMagicSchool());
            storage.setPartnerId(originalStorage.getPartnerId());
            storage.setSubjugatorId(originalStorage.getSubjugatorId());
            storage.setSubjugatorName(originalStorage.getSubjugatorName());
            storage.setSubjugatedName(originalStorage.getSubjugatedName());
            storage.setIntroMessageSent(originalStorage.isIntroMessageSent());
            storage.setSalvage(originalStorage.getSalvage());
            storage.setSmithingSalvageEnabled(originalStorage.isSmithingSalvageEnabled());
            player.syncData(ModAttachments.PLAYER_STORAGE);
        }

        PlayerStatStorage originalStats = original.getData(ModAttachments.PLAYER_STATS);
        PlayerStatStorage statStorage = player.getData(ModAttachments.PLAYER_STATS);
        if (statStorage != null && originalStats != null) {
            statStorage.getValues().clear();
            statStorage.getValues().putAll(originalStats.getValues());
            player.syncData(ModAttachments.PLAYER_STATS);
        }

        PlayerProgressionStorage originalProgression = original.getData(ModAttachments.PLAYER_PROGRESSION);
        PlayerProgressionStorage progression = player.getData(ModAttachments.PLAYER_PROGRESSION);
        if (progression != null && originalProgression != null) {
            progression.setLevel(originalProgression.getLevel());
            progression.setSkillPoints(originalProgression.getSkillPoints());
            progression.setXp(originalProgression.getXp());
            progression.setXpGain(originalProgression.getXpGain());
            player.syncData(ModAttachments.PLAYER_PROGRESSION);
        }

        PlayerDiscoveryStorage originalDiscovery = original.getData(ModAttachments.PLAYER_DISCOVERY);
        PlayerDiscoveryStorage discovery = player.getData(ModAttachments.PLAYER_DISCOVERY);
        if (discovery != null && originalDiscovery != null) {
            discovery.setDiscoveredBiomes(originalDiscovery.getDiscoveredBiomes());
            player.syncData(ModAttachments.PLAYER_DISCOVERY);
        }

        PlayerVeinMinerStorage originalVeinMiner = original.getData(ModAttachments.PLAYER_VEIN_MINER);
        PlayerVeinMinerStorage veinMiner = player.getData(ModAttachments.PLAYER_VEIN_MINER);
        if (veinMiner != null && originalVeinMiner != null) {
            veinMiner.setLevel(originalVeinMiner.getLevel());
            veinMiner.setXp(originalVeinMiner.getXp());
            veinMiner.setEnabled(originalVeinMiner.isEnabled());
            veinMiner.setVeinMining(false);
            player.syncData(ModAttachments.PLAYER_VEIN_MINER);
        }

        PlayerProspectorStorage originalProspector = original.getData(ModAttachments.PLAYER_PROSPECTOR);
        PlayerProspectorStorage prospector = player.getData(ModAttachments.PLAYER_PROSPECTOR);
        if (prospector != null && originalProspector != null) {
            prospector.setEnabled(originalProspector.isEnabled());
            player.syncData(ModAttachments.PLAYER_PROSPECTOR);
        }

        PlayerSmithingStorage originalSmithing = original.getData(ModAttachments.PLAYER_SMITHING);
        PlayerSmithingStorage smithing = player.getData(ModAttachments.PLAYER_SMITHING);
        if (smithing != null && originalSmithing != null) {
            smithing.setLevel(originalSmithing.getLevel());
            smithing.setXp(originalSmithing.getXp());
            player.syncData(ModAttachments.PLAYER_SMITHING);
        }

        PlayerSalvageStorage originalSalvage = original.getData(ModAttachments.PLAYER_SALVAGE);
        PlayerSalvageStorage salvage = player.getData(ModAttachments.PLAYER_SALVAGE);
        if (salvage != null && originalSalvage != null) {
            salvage.clearContent();
            int size = salvage.getContainerSize();
            for (int i = 0; i < size; i++) {
                salvage.setItem(i, originalSalvage.getItem(i).copy());
            }
        }
    }

    public static void onPlayerXpChange(PlayerXpEvent.XpChange event) {
        if (event.getAmount() <= 0) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PlayerProgressionService.awardXp(serverPlayer, event.getAmount(), XpSources.VANILLA);
        event.setAmount(0);
    }

    public static void onPlayerXpPickup(PlayerXpEvent.PickupXp event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        int orbValue = event.getOrb().value;
        if (orbValue <= 0) {
            return;
        }
        PlayerProgressionService.awardXp(serverPlayer, orbValue, XpSources.VANILLA);
        event.getOrb().value = 0;
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (serverPlayer.level().isClientSide()) {
            return;
        }
        PlayerAttachmentStorage storage = serverPlayer.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null || storage.isIntroMessageSent()) {
            return;
        }
        serverPlayer.displayClientMessage(
            Component.literal("Press U to open the Character Menu and choose a Race and Class"),
            false
        );
        storage.setIntroMessageSent(true);
        serverPlayer.syncData(ModAttachments.PLAYER_STORAGE);
    }

    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (serverPlayer.level().isClientSide()) {
            return;
        }
        ItemStack crafted = event.getCrafting();
        if (crafted.isEmpty()) {
            return;
        }
        ItemStack craftedSnapshot = crafted.copy();
        int playerLevel = getPlayerLevel(serverPlayer);
        boolean rolledAffixes = false;
        PlayerStatStorage statStorage = serverPlayer.getData(ModAttachments.PLAYER_STATS);
        boolean smithingCrafting = serverPlayer.containerMenu instanceof SmithingCraftingMenu;
        int rollLevel = smithingCrafting
            ? SmithService.getQualityRollLevel(serverPlayer, statStorage, playerLevel)
            : playerLevel;
        if (smithingCrafting && SmithService.canUse(serverPlayer) && ItemAffixService.canRollAffixes(crafted)) {
            rollLevel += SmithService.consumeSalvageForCraft(serverPlayer);
        }
        if (ItemAffixService.canRollAffixes(crafted) && !ItemAffixService.hasAffixes(crafted)) {
            ItemCategory category = ItemAffixService.getCategoryForAffixes(crafted);
            if (category != null) {
                ItemAffixService.rollAffixes(crafted, category, serverPlayer.getRandom(), rollLevel);
                rolledAffixes = ItemAffixService.hasAffixes(crafted);
            }
        }
        if (rolledAffixes) {
            applyCraftedAffixes(serverPlayer, craftedSnapshot, crafted);
        }
        if (smithingCrafting && SmithService.canUse(serverPlayer)) {
            applyCraftedSmithingBonuses(serverPlayer, craftedSnapshot, crafted);
        }
        SmithingProgressionService.awardCraftXp(serverPlayer, crafted, event.getInventory());
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) {
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        BlockState state = event.getState();
        PlayerVeinMinerStorage veinStorage = serverPlayer.getData(ModAttachments.PLAYER_VEIN_MINER);
        if (veinStorage != null) {
            VeinMinerProgressionService.awardXp(serverPlayer, LevelingRules.VEIN_MINER_BLOCK_XP);
        }
        if (veinStorage != null && veinStorage.isEnabled() && !veinStorage.isVeinMining()) {
            if (serverPlayer.getFoodData().getFoodLevel() <= 0) {
                return;
            }
            serverPlayer.getFoodData().setFoodLevel(serverPlayer.getFoodData().getFoodLevel() - 1);
            performVeinMining(serverPlayer, event.getPos(), state, veinStorage.getLevel());
        }
        if (!state.is(BlockTags.STONE_ORE_REPLACEABLES)) {
            return;
        }
        PlayerProgressionService.awardXp(serverPlayer, LevelingRules.ORE_MINING_XP, XpSources.ORE_MINING);
    }

    private static void performVeinMining(ServerPlayer player, BlockPos origin, BlockState state, int extraBlocks) {
        if (extraBlocks <= 0) {
            return;
        }
        ServerLevel level = player.serverLevel();
        PlayerVeinMinerStorage storage = player.getData(ModAttachments.PLAYER_VEIN_MINER);
        if (storage == null) {
            return;
        }
        storage.setVeinMining(true);
        try {
            var queue = new java.util.ArrayDeque<BlockPos>();
            var visited = new java.util.HashSet<BlockPos>();
            queue.add(origin);
            visited.add(origin);

            int broken = 0;
            while (!queue.isEmpty() && broken < extraBlocks) {
                BlockPos current = queue.poll();
                for (Direction direction : Direction.values()) {
                    BlockPos next = current.relative(direction);
                    if (!visited.add(next)) {
                        continue;
                    }
                    if (!level.isLoaded(next)) {
                        continue;
                    }
                    BlockState nextState = level.getBlockState(next);
                    if (nextState.getBlock() != state.getBlock()) {
                        continue;
                    }
                    if (!player.hasCorrectToolForDrops(nextState)) {
                        continue;
                    }
                    level.destroyBlock(next, true, player);
                    broken++;
                    if (broken >= extraBlocks) {
                        break;
                    }
                    queue.add(next);
                }
            }
        } finally {
            storage.setVeinMining(false);
        }
    }

    private static void checkBiomeDiscovery(ServerPlayer player) {
        if (player.tickCount % BIOME_CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        PlayerDiscoveryStorage discovery = player.getData(ModAttachments.PLAYER_DISCOVERY);
        if (discovery == null) {
            return;
        }
        ResourceKey<Biome> biomeKey = player.level()
            .getBiome(player.blockPosition())
            .unwrapKey()
            .orElse(null);
        if (biomeKey == null) {
            return;
        }
        ResourceLocation biomeId = biomeKey.location();
        if (!discovery.addDiscovered(biomeId)) {
            return;
        }
        player.syncData(ModAttachments.PLAYER_DISCOVERY);
        PacketDistributor.sendToPlayer(player, new BiomeDiscoveredPayload(biomeId));
        PlayerProgressionService.awardXp(player, LevelingRules.BIOME_DISCOVERY_XP, XpSources.BIOME_DISCOVERY);
    }

    private static Race getRace(ServerPlayer player) {
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        return storage == null ? null : storage.getRace();
    }

    private static BasicClass getPlayerClass(ServerPlayer player) {
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        return storage == null ? null : storage.getPlayerClass();
    }

    private static int getPlayerLevel(ServerPlayer player) {
        PlayerProgressionStorage storage = player.getData(ModAttachments.PLAYER_PROGRESSION);
        return storage == null ? 1 : storage.getLevel();
    }

    private static void ensureEquippedAffixes(ServerPlayer player) {
        int playerLevel = getPlayerLevel(player);
        var random = player.getRandom();
        ensureStackAffixes(player.getMainHandItem(), random, playerLevel);
        ensureStackAffixes(player.getOffhandItem(), random, playerLevel);
        for (ItemStack stack : player.getInventory().items) {
            ensureStackAffixes(stack, random, playerLevel);
        }
        for (ItemStack stack : player.getInventory().armor) {
            ensureStackAffixes(stack, random, playerLevel);
        }
    }

    private static void ensureStackAffixes(ItemStack stack, net.minecraft.util.RandomSource random, int level) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (!ItemAffixService.canRollAffixes(stack)) {
            return;
        }
        if (!ItemAffixService.hasAffixes(stack)) {
            ItemCategory category = ItemAffixService.getCategoryForAffixes(stack);
            if (category != null) {
                ItemAffixService.rollAffixes(stack, category, random, level);
            }
            return;
        }
        ItemAffixService.ensureImplicitAffixes(stack, random);
    }

    private static ItemRarity getMinDropRarity(ServerPlayer player) {
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        return storage == null ? ItemRarity.RARE : storage.getMinDropRarity();
    }

    private static boolean shouldDropGear(LivingEntity living, int level) {
        if (living instanceof Animal) {
            return false;
        }
        double chance = 0.30D + Math.min(0.15D, level * 0.003D);
        return living.getRandom().nextDouble() < chance;
    }

    private static void applyCraftedAffixes(ServerPlayer player, ItemStack baseStack, ItemStack affixedStack) {
        AbstractContainerMenu menu = player.containerMenu;
        ItemStack carried = menu.getCarried();
        if (!carried.isEmpty()
            && ItemStack.isSameItemSameComponents(carried, baseStack)
            && ItemAffixService.copyAffixes(affixedStack, carried)) {
            menu.broadcastChanges();
            return;
        }
        Slot target = findCraftedItemSlot(menu, baseStack);
        if (target == null) {
            return;
        }
        ItemStack targetStack = target.getItem();
        if (ItemAffixService.copyAffixes(affixedStack, targetStack)) {
            target.setChanged();
            menu.broadcastChanges();
        }
    }

    private static void applyCraftedSmithingBonuses(ServerPlayer player, ItemStack baseStack, ItemStack craftedStack) {
        boolean applied = SmithService.applyDurabilityBonus(player, craftedStack);
        if (!applied) {
            return;
        }
        AbstractContainerMenu menu = player.containerMenu;
        ItemStack carried = menu.getCarried();
        if (!carried.isEmpty()
            && ItemStack.isSameItemSameComponents(carried, baseStack)
            && SmithService.applyDurabilityBonus(player, carried)) {
            menu.broadcastChanges();
            return;
        }
        Slot target = findCraftedItemSlot(menu, baseStack);
        if (target == null) {
            return;
        }
        ItemStack targetStack = target.getItem();
        if (SmithService.applyDurabilityBonus(player, targetStack)) {
            target.setChanged();
            menu.broadcastChanges();
        }
    }

    private static Slot findCraftedItemSlot(AbstractContainerMenu menu, ItemStack baseStack) {
        int start;
        int end;
        if (menu instanceof CraftingMenu) {
            start = 10;
            end = 46;
        } else if (menu instanceof InventoryMenu) {
            start = InventoryMenu.INV_SLOT_START;
            end = InventoryMenu.USE_ROW_SLOT_END;
        } else {
            return null;
        }
        for (int i = end - 1; i >= start; i--) {
            Slot slot = menu.getSlot(i);
            ItemStack candidate = slot.getItem();
            if (candidate.isEmpty()) {
                continue;
            }
            if (!ItemStack.isSameItemSameComponents(candidate, baseStack)) {
                continue;
            }
            if (ItemAffixService.hasAffixes(candidate)) {
                continue;
            }
            return slot;
        }
        return null;
    }

    public static void sendPartnerMessages(ServerPlayer bunny, ServerPlayer partner, boolean partnered) {
        String bunnyName = bunny.getGameProfile().getName();
        String partnerName = partner.getGameProfile().getName();
        if (partnered) {
            bunny.displayClientMessage(Component.literal("You partnered with " + partnerName + "."), true);
            partner.displayClientMessage(Component.literal(bunnyName + " chose you as their partner."), true);
            if (bunny.server != null) {
                bunny.server.getPlayerList().broadcastSystemMessage(
                    Component.literal(bunnyName + " and " + partnerName + " are now partners."),
                    false
                );
            }
            return;
        }
        bunny.displayClientMessage(Component.literal("You are no longer partnered with " + partnerName + "."), true);
        partner.displayClientMessage(Component.literal(bunnyName + " ended the partnership."), true);
        if (bunny.server != null) {
            bunny.server.getPlayerList().broadcastSystemMessage(
                Component.literal(bunnyName + " and " + partnerName + " are no longer partners."),
                false
            );
        }
    }
}
