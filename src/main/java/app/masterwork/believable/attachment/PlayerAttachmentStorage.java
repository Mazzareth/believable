package app.masterwork.believable.attachment;

import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.character.ClassDefinitions;
import app.masterwork.believable.magic.BasicMagicSchool;
import app.masterwork.believable.magic.MagicSchoolDefinitions;
import app.masterwork.believable.race.BasicRace;
import app.masterwork.believable.race.RaceDefinitions;
import app.masterwork.believable.item.ItemRarity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public final class PlayerAttachmentStorage {
    public static final int PASSIVE_SKILL_SLOTS = 2;
    public static final int ACTIVE_SKILL_SLOTS = 2;

    public static final Codec<PlayerAttachmentStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("stored_value", 0).forGetter(PlayerAttachmentStorage::getStoredValue),
        BasicRace.CODEC.optionalFieldOf("race").forGetter(storage -> Optional.ofNullable(storage.race)),
        BasicClass.CODEC.optionalFieldOf("class").forGetter(storage -> Optional.ofNullable(storage.playerClass)),
        BasicMagicSchool.CODEC.optionalFieldOf("magic_school").forGetter(storage -> Optional.ofNullable(storage.magicSchool)),
        Codec.STRING.optionalFieldOf("passive_skill_one").forGetter(storage -> Optional.ofNullable(storage.passiveSkillOne)),
        Codec.STRING.optionalFieldOf("passive_skill_two").forGetter(storage -> Optional.ofNullable(storage.passiveSkillTwo)),
        Codec.STRING.optionalFieldOf("active_skill_one").forGetter(storage -> Optional.ofNullable(storage.activeSkillOne)),
        Codec.STRING.optionalFieldOf("active_skill_two").forGetter(storage -> Optional.ofNullable(storage.activeSkillTwo)),
        Codec.STRING.optionalFieldOf("partner").forGetter(storage -> Optional.ofNullable(storage.partnerId).map(UUID::toString)),
        Codec.STRING.optionalFieldOf("subjugator").forGetter(storage -> Optional.ofNullable(storage.subjugatorId).map(UUID::toString)),
        Codec.STRING.optionalFieldOf("subjugator_name").forGetter(storage -> Optional.ofNullable(storage.subjugatorName)),
        Codec.STRING.optionalFieldOf("subjugated_name").forGetter(storage -> Optional.ofNullable(storage.subjugatedName)),
        Codec.BOOL.optionalFieldOf("intro_message_sent", false).forGetter(PlayerAttachmentStorage::isIntroMessageSent),
        ItemRarity.CODEC.optionalFieldOf("min_drop_rarity", ItemRarity.RARE).forGetter(PlayerAttachmentStorage::getMinDropRarity),
        Codec.INT.optionalFieldOf("salvage", 0).forGetter(PlayerAttachmentStorage::getSalvage),
        Codec.BOOL.optionalFieldOf("smithing_salvage", false).forGetter(PlayerAttachmentStorage::isSmithingSalvageEnabled)
    ).apply(instance, (storedValue, race, playerClass, magicSchool, passiveOne, passiveTwo, activeOne, activeTwo,
                       partner, subjugator, subjugatorName, subjugatedName, introMessageSent, minDropRarity, salvage, smithingSalvage) -> new PlayerAttachmentStorage(
        storedValue,
        race.orElse(null),
        playerClass.orElse(null),
        magicSchool.orElse(null),
        passiveOne.orElse(null),
        passiveTwo.orElse(null),
        activeOne.orElse(null),
        activeTwo.orElse(null),
        partner.map(UUID::fromString).orElse(null),
        subjugator.map(UUID::fromString).orElse(null),
        subjugatorName.orElse(null),
        subjugatedName.orElse(null),
        introMessageSent,
        minDropRarity,
        salvage,
        smithingSalvage
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerAttachmentStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PlayerAttachmentStorage decode(RegistryFriendlyByteBuf buf) {
            int storedValue = buf.readVarInt();
            BasicRace race = null;
            if (buf.readBoolean()) {
                String raceKey = buf.readUtf();
                race = RaceDefinitions.getByTabKey(raceKey);
            }
            BasicClass playerClass = null;
            if (buf.readBoolean()) {
                String classKey = buf.readUtf();
                playerClass = ClassDefinitions.getByTabKey(classKey);
            }
            BasicMagicSchool magicSchool = null;
            if (buf.readBoolean()) {
                String magicKey = buf.readUtf();
                magicSchool = MagicSchoolDefinitions.getByTabKey(magicKey);
            }
            String passiveSkillOne = null;
            if (buf.readBoolean()) {
                passiveSkillOne = buf.readUtf();
            }
            String passiveSkillTwo = null;
            if (buf.readBoolean()) {
                passiveSkillTwo = buf.readUtf();
            }
            String activeSkillOne = null;
            if (buf.readBoolean()) {
                activeSkillOne = buf.readUtf();
            }
            String activeSkillTwo = null;
            if (buf.readBoolean()) {
                activeSkillTwo = buf.readUtf();
            }
            UUID partnerId = null;
            if (buf.readBoolean()) {
                partnerId = buf.readUUID();
            }
            UUID subjugatorId = null;
            if (buf.readBoolean()) {
                subjugatorId = buf.readUUID();
            }
            String subjugatorName = null;
            if (buf.readBoolean()) {
                subjugatorName = buf.readUtf();
            }
            String subjugatedName = null;
            if (buf.readBoolean()) {
                subjugatedName = buf.readUtf();
            }
            boolean introMessageSent = buf.readBoolean();
            ItemRarity minDropRarity = ItemRarity.fromId(buf.readUtf());
            int salvage = buf.readVarInt();
            boolean smithingSalvageEnabled = buf.readBoolean();
            return new PlayerAttachmentStorage(
                storedValue,
                race,
                playerClass,
                magicSchool,
                passiveSkillOne,
                passiveSkillTwo,
                activeSkillOne,
                activeSkillTwo,
                partnerId,
                subjugatorId,
                subjugatorName,
                subjugatedName,
                introMessageSent,
                minDropRarity,
                salvage,
                smithingSalvageEnabled
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerAttachmentStorage storage) {
            buf.writeVarInt(storage.getStoredValue());
            BasicRace race = storage.getRace();
            String raceKey = RaceDefinitions.getTabKey(race);
            if (raceKey == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeUtf(raceKey);
            }
            BasicClass playerClass = storage.getPlayerClass();
            String classKey = ClassDefinitions.getTabKey(playerClass);
            if (classKey == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeUtf(classKey);
            }
            BasicMagicSchool magicSchool = storage.getMagicSchool();
            String magicKey = MagicSchoolDefinitions.getTabKey(magicSchool);
            if (magicKey == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeUtf(magicKey);
            }
            writeOptionalString(buf, storage.passiveSkillOne);
            writeOptionalString(buf, storage.passiveSkillTwo);
            writeOptionalString(buf, storage.activeSkillOne);
            writeOptionalString(buf, storage.activeSkillTwo);
            UUID partnerId = storage.getPartnerId();
            if (partnerId == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeUUID(partnerId);
            }
            UUID subjugatorId = storage.getSubjugatorId();
            if (subjugatorId == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeUUID(subjugatorId);
            }
            String subjugatorName = storage.getSubjugatorName();
            if (subjugatorName == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeUtf(subjugatorName);
            }
            String subjugatedName = storage.getSubjugatedName();
            if (subjugatedName == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeUtf(subjugatedName);
            }
            buf.writeBoolean(storage.isIntroMessageSent());
            buf.writeUtf(storage.getMinDropRarity().getId());
            buf.writeVarInt(storage.getSalvage());
            buf.writeBoolean(storage.isSmithingSalvageEnabled());
        }
    };

    private int storedValue;
    private BasicRace race;
    private BasicClass playerClass;
    private BasicMagicSchool magicSchool;
    private String passiveSkillOne;
    private String passiveSkillTwo;
    private String activeSkillOne;
    private String activeSkillTwo;
    private UUID partnerId;
    private UUID subjugatorId;
    private String subjugatorName;
    private String subjugatedName;
    private boolean introMessageSent;
    private ItemRarity minDropRarity;
    private int salvage;
    private boolean smithingSalvageEnabled;

    public PlayerAttachmentStorage() {
        this(0, null, null, null, null, null, null, null, null, null, null, null, false, ItemRarity.RARE, 0, false);
    }

    public PlayerAttachmentStorage(int storedValue) {
        this(storedValue, null, null, null, null, null, null, null, null, null, null, null, false, ItemRarity.RARE, 0, false);
    }

    public PlayerAttachmentStorage(int storedValue, BasicRace race) {
        this(storedValue, race, null, null, null, null, null, null, null, null, null, null, false, ItemRarity.RARE, 0, false);
    }

    public PlayerAttachmentStorage(int storedValue, BasicRace race, BasicClass playerClass) {
        this(storedValue, race, playerClass, null, null, null, null, null, null, null, null, null, false, ItemRarity.RARE, 0, false);
    }

    public PlayerAttachmentStorage(int storedValue,
                                   BasicRace race,
                                   BasicClass playerClass,
                                   BasicMagicSchool magicSchool,
                                   String passiveSkillOne,
                                   String passiveSkillTwo,
                                   String activeSkillOne,
                                   String activeSkillTwo,
                                   UUID partnerId,
                                   UUID subjugatorId,
                                   String subjugatorName,
                                   String subjugatedName,
                                   boolean introMessageSent,
                                   ItemRarity minDropRarity,
                                   int salvage,
                                   boolean smithingSalvageEnabled) {
        this.storedValue = storedValue;
        this.race = race;
        this.playerClass = playerClass;
        this.magicSchool = magicSchool;
        this.passiveSkillOne = normalizeSkillId(passiveSkillOne);
        this.passiveSkillTwo = normalizeSkillId(passiveSkillTwo);
        this.activeSkillOne = normalizeSkillId(activeSkillOne);
        this.activeSkillTwo = normalizeSkillId(activeSkillTwo);
        this.partnerId = partnerId;
        this.subjugatorId = subjugatorId;
        this.subjugatorName = subjugatorName;
        this.subjugatedName = subjugatedName;
        this.introMessageSent = introMessageSent;
        this.minDropRarity = minDropRarity == null ? ItemRarity.RARE : minDropRarity;
        this.salvage = Math.max(0, salvage);
        this.smithingSalvageEnabled = smithingSalvageEnabled;
    }

    public int getStoredValue() {
        return storedValue;
    }

    public void setStoredValue(int storedValue) {
        this.storedValue = storedValue;
    }

    public BasicRace getRace() {
        return race;
    }

    public void setRace(BasicRace race) {
        this.race = race;
    }

    public boolean hasRace() {
        return race != null;
    }

    public BasicClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(BasicClass playerClass) {
        this.playerClass = playerClass;
    }

    public boolean hasClass() {
        return playerClass != null;
    }

    public BasicMagicSchool getMagicSchool() {
        return magicSchool;
    }

    public void setMagicSchool(BasicMagicSchool magicSchool) {
        this.magicSchool = magicSchool;
    }

    public boolean hasMagicSchool() {
        return magicSchool != null;
    }

    public String getPassiveSkill(int slot) {
        return switch (slot) {
            case 0 -> passiveSkillOne;
            case 1 -> passiveSkillTwo;
            default -> null;
        };
    }

    public void setPassiveSkill(int slot, String skillId) {
        String normalized = normalizeSkillId(skillId);
        switch (slot) {
            case 0 -> passiveSkillOne = normalized;
            case 1 -> passiveSkillTwo = normalized;
            default -> {
            }
        }
    }

    public String getActiveSkill(int slot) {
        return switch (slot) {
            case 0 -> activeSkillOne;
            case 1 -> activeSkillTwo;
            default -> null;
        };
    }

    public void setActiveSkill(int slot, String skillId) {
        String normalized = normalizeSkillId(skillId);
        switch (slot) {
            case 0 -> activeSkillOne = normalized;
            case 1 -> activeSkillTwo = normalized;
            default -> {
            }
        }
    }

    public UUID getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(UUID partnerId) {
        this.partnerId = partnerId;
    }

    public UUID getSubjugatorId() {
        return subjugatorId;
    }

    public void setSubjugatorId(UUID subjugatorId) {
        this.subjugatorId = subjugatorId;
    }

    public String getSubjugatorName() {
        return subjugatorName;
    }

    public void setSubjugatorName(String subjugatorName) {
        this.subjugatorName = subjugatorName;
    }

    public String getSubjugatedName() {
        return subjugatedName;
    }

    public void setSubjugatedName(String subjugatedName) {
        this.subjugatedName = subjugatedName;
    }

    public boolean isIntroMessageSent() {
        return introMessageSent;
    }

    public void setIntroMessageSent(boolean introMessageSent) {
        this.introMessageSent = introMessageSent;
    }

    public ItemRarity getMinDropRarity() {
        return minDropRarity == null ? ItemRarity.RARE : minDropRarity;
    }

    public void setMinDropRarity(ItemRarity minDropRarity) {
        this.minDropRarity = minDropRarity == null ? ItemRarity.RARE : minDropRarity;
    }

    public int getSalvage() {
        return Math.max(0, salvage);
    }

    public void setSalvage(int salvage) {
        this.salvage = Math.max(0, salvage);
    }

    public boolean isSmithingSalvageEnabled() {
        return smithingSalvageEnabled;
    }

    public void setSmithingSalvageEnabled(boolean smithingSalvageEnabled) {
        this.smithingSalvageEnabled = smithingSalvageEnabled;
    }

    private static String normalizeSkillId(String id) {
        return id == null || id.isBlank() ? null : id;
    }

    private static void writeOptionalString(RegistryFriendlyByteBuf buf, String value) {
        if (value == null || value.isBlank()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeUtf(value);
        }
    }
}
