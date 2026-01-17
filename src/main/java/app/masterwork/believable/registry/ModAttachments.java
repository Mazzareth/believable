package app.masterwork.believable.registry;

import app.masterwork.believable.Believable;
import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.classes.cartographer.PlayerCartographyStorage;
import app.masterwork.believable.classes.prospector.PlayerProspectorStorage;
import app.masterwork.believable.attachment.PlayerSalvageStorage;
import app.masterwork.believable.mob.MobStatStorage;
import app.masterwork.believable.stats.PlayerDiscoveryStorage;
import app.masterwork.believable.stats.PlayerProgressionStorage;
import app.masterwork.believable.stats.PlayerSmithingStorage;
import app.masterwork.believable.stats.PlayerStatStorage;
import app.masterwork.believable.stats.PlayerVeinMinerStorage;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Believable.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerAttachmentStorage>> PLAYER_STORAGE =
        ATTACHMENT_TYPES.register(
            "player_storage",
            () -> AttachmentType.builder(() -> new PlayerAttachmentStorage())
                .serialize(PlayerAttachmentStorage.CODEC)
                .sync(PlayerAttachmentStorage.STREAM_CODEC)
                .build()
        );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerStatStorage>> PLAYER_STATS =
        ATTACHMENT_TYPES.register(
            "player_stats",
            () -> AttachmentType.builder(() -> new PlayerStatStorage())
                .serialize(PlayerStatStorage.CODEC)
                .sync(PlayerStatStorage.STREAM_CODEC)
                .build()
        );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerProgressionStorage>> PLAYER_PROGRESSION =
        ATTACHMENT_TYPES.register(
            "player_progression",
            () -> AttachmentType.builder(() -> new PlayerProgressionStorage())
                .serialize(PlayerProgressionStorage.CODEC)
                .sync(PlayerProgressionStorage.STREAM_CODEC)
                .build()
        );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerDiscoveryStorage>> PLAYER_DISCOVERY =
        ATTACHMENT_TYPES.register(
            "player_discovery",
            () -> AttachmentType.builder(() -> new PlayerDiscoveryStorage())
                .serialize(PlayerDiscoveryStorage.CODEC)
                .sync(PlayerDiscoveryStorage.STREAM_CODEC)
                .build()
        );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerVeinMinerStorage>> PLAYER_VEIN_MINER =
        ATTACHMENT_TYPES.register(
            "player_vein_miner",
            () -> AttachmentType.builder(() -> new PlayerVeinMinerStorage())
                .serialize(PlayerVeinMinerStorage.CODEC)
                .sync(PlayerVeinMinerStorage.STREAM_CODEC)
                .build()
        );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerProspectorStorage>> PLAYER_PROSPECTOR =
        ATTACHMENT_TYPES.register(
            "player_prospector",
            () -> AttachmentType.builder(() -> new PlayerProspectorStorage())
                .serialize(PlayerProspectorStorage.CODEC)
                .sync(PlayerProspectorStorage.STREAM_CODEC)
                .build()
        );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerSmithingStorage>> PLAYER_SMITHING =
        ATTACHMENT_TYPES.register(
            "player_smithing",
            () -> AttachmentType.builder(() -> new PlayerSmithingStorage())
                .serialize(PlayerSmithingStorage.CODEC)
                .sync(PlayerSmithingStorage.STREAM_CODEC)
                .build()
        );


    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerCartographyStorage>> PLAYER_CARTOGRAPHY =
        ATTACHMENT_TYPES.register(
            "player_cartography",
            () -> AttachmentType.builder(() -> new PlayerCartographyStorage())
                .serialize(PlayerCartographyStorage.CODEC)
                .sync(PlayerCartographyStorage.STREAM_CODEC)
                .build()
        );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerSalvageStorage>> PLAYER_SALVAGE =
        ATTACHMENT_TYPES.register(
            "player_salvage",
            () -> AttachmentType.builder(() -> new PlayerSalvageStorage())
                .serialize(PlayerSalvageStorage.CODEC)
                .build()
        );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MobStatStorage>> MOB_STATS =
        ATTACHMENT_TYPES.register(
            "mob_stats",
            () -> AttachmentType.builder(() -> new MobStatStorage())
                .serialize(MobStatStorage.CODEC)
                .sync(MobStatStorage.STREAM_CODEC)
                .build()
        );

    private ModAttachments() {
    }
}
