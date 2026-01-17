package app.masterwork.believable.menu;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.character.ClassDefinitions;
import app.masterwork.believable.classes.cartographer.CartographerService;
import app.masterwork.believable.item.ItemRarity;
import app.masterwork.believable.magic.MagicSchoolDefinitions;
import app.masterwork.believable.race.RaceDefinitions;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.registry.ModMenus;
import app.masterwork.believable.skills.ActiveSkill;
import app.masterwork.believable.skills.PassiveSkill;
import app.masterwork.believable.skills.SkillDefinitions;
import app.masterwork.believable.stats.CharacterStat;
import app.masterwork.believable.stats.CharacterStatRegistry;
import app.masterwork.believable.stats.PlayerProgressionStorage;
import app.masterwork.believable.stats.PlayerStatStorage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * Server-side menu that applies character selections and stat adjustments.
 */
public class UnbelievableMenu extends AbstractContainerMenu {
    public static final int BUTTON_SELECT_GOAT = 0;
    public static final int BUTTON_SELECT_BUNNY = 1;
    public static final int BUTTON_SELECT_CAT = 2;
    public static final int BUTTON_SELECT_FENNEC = 16;
    public static final int BUTTON_SELECT_CANID = 17;
    public static final int BUTTON_SELECT_PROTOGEN = 18;
    public static final int BUTTON_SELECT_ARTIFICER = 19;
    public static final int BUTTON_SELECT_SMITH = 20;
    public static final int BUTTON_SELECT_MAGE = 21;
    public static final int BUTTON_SELECT_ALCHEMIST_CLASS = 22;
    public static final int BUTTON_SELECT_PROSPECTOR = 23;
    public static final int BUTTON_SELECT_ARCHITECT = 24;
    public static final int BUTTON_SELECT_WARDEN = 25;
    public static final int BUTTON_SELECT_CARTOGRAPHER = 26;
    public static final int BUTTON_SELECT_ELEMENTALISM = 6;
    public static final int BUTTON_SELECT_SPATIAL = 7;
    public static final int BUTTON_SELECT_TEMPORAL = 8;
    public static final int BUTTON_SELECT_ONEIROMANCY = 9;
    public static final int BUTTON_SELECT_ELDRITCH = 10;
    public static final int BUTTON_SELECT_AILMENT = 11;
    public static final int BUTTON_SELECT_NECROMANCY = 12;
    public static final int BUTTON_SELECT_BLESSING = 13;
    public static final int BUTTON_SELECT_WITCHERY = 14;
    public static final int BUTTON_SELECT_ALCHEMIST = 15;
    public static final int BUTTON_DROP_RARITY_DOWN = 3000;
    public static final int BUTTON_DROP_RARITY_UP = 3001;
    public static final int BUTTON_EQUIP_PASSIVE_BASE = 4000;
    public static final int BUTTON_EQUIP_ACTIVE_BASE = 5000;
    public static final int BUTTON_WAYPOINT_ADD = 6000;
    public static final int BUTTON_WAYPOINT_TOGGLE_BASE = 6100;
    public static final int BUTTON_WAYPOINT_MOVE_BASE = 6200;
    public static final int BUTTON_WAYPOINT_DELETE_BASE = 6300;
    public static final int BUTTON_STAT_INCREASE_BASE = 1000;
    public static final int BUTTON_STAT_DECREASE_BASE = 2000;

    public UnbelievableMenu(int containerId, Inventory inventory) {
        super(ModMenus.UNBELIEVABLE_MENU.get(), containerId);
    }

    /**
     * Handles button clicks coming from the client screen.
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        boolean updated = switch (id) {
            case BUTTON_SELECT_GOAT -> {
                storage.setRace(RaceDefinitions.GOAT);
                yield true;
            }
            case BUTTON_SELECT_CAT -> {
                storage.setRace(RaceDefinitions.CAT);
                yield true;
            }
            case BUTTON_SELECT_FENNEC -> {
                storage.setRace(RaceDefinitions.FENNEC);
                yield true;
            }
            case BUTTON_SELECT_CANID -> {
                storage.setRace(RaceDefinitions.CANID);
                yield true;
            }
            case BUTTON_SELECT_PROTOGEN -> {
                storage.setRace(RaceDefinitions.PROTOGEN);
                yield true;
            }
            case BUTTON_SELECT_ARTIFICER -> {
                storage.setPlayerClass(ClassDefinitions.ARTIFICER);
                yield true;
            }
            case BUTTON_SELECT_SMITH -> {
                storage.setPlayerClass(ClassDefinitions.SMITH);
                yield true;
            }
            case BUTTON_SELECT_MAGE -> {
                storage.setPlayerClass(ClassDefinitions.MAGE);
                yield true;
            }
            case BUTTON_SELECT_ALCHEMIST_CLASS -> {
                storage.setPlayerClass(ClassDefinitions.ALCHEMIST);
                yield true;
            }
            case BUTTON_SELECT_PROSPECTOR -> {
                storage.setPlayerClass(ClassDefinitions.PROSPECTOR);
                yield true;
            }
            case BUTTON_SELECT_ARCHITECT -> {
                storage.setPlayerClass(ClassDefinitions.ARCHITECT);
                yield true;
            }
            case BUTTON_SELECT_WARDEN -> {
                storage.setPlayerClass(ClassDefinitions.WARDEN);
                yield true;
            }
            case BUTTON_SELECT_CARTOGRAPHER -> {
                storage.setPlayerClass(ClassDefinitions.CARTOGRAPHER);
                yield true;
            }
            case BUTTON_SELECT_ELEMENTALISM -> {
                storage.setMagicSchool(MagicSchoolDefinitions.ELEMENTALISM);
                yield true;
            }
            case BUTTON_SELECT_SPATIAL -> {
                storage.setMagicSchool(MagicSchoolDefinitions.SPATIAL);
                yield true;
            }
            case BUTTON_SELECT_TEMPORAL -> {
                storage.setMagicSchool(MagicSchoolDefinitions.TEMPORAL);
                yield true;
            }
            case BUTTON_SELECT_ONEIROMANCY -> {
                storage.setMagicSchool(MagicSchoolDefinitions.ONEIROMANCY);
                yield true;
            }
            case BUTTON_SELECT_ELDRITCH -> {
                storage.setMagicSchool(MagicSchoolDefinitions.ELDRITCH);
                yield true;
            }
            case BUTTON_SELECT_AILMENT -> {
                storage.setMagicSchool(MagicSchoolDefinitions.AILMENT);
                yield true;
            }
            case BUTTON_SELECT_NECROMANCY -> {
                storage.setMagicSchool(MagicSchoolDefinitions.NECROMANCY);
                yield true;
            }
            case BUTTON_SELECT_BLESSING -> {
                storage.setMagicSchool(MagicSchoolDefinitions.BLESSING);
                yield true;
            }
            case BUTTON_SELECT_WITCHERY -> {
                storage.setMagicSchool(MagicSchoolDefinitions.WITCHERY);
                yield true;
            }
            case BUTTON_SELECT_ALCHEMIST -> {
                storage.setMagicSchool(MagicSchoolDefinitions.ALCHEMIST);
                yield true;
            }
            case BUTTON_DROP_RARITY_DOWN -> adjustDropRarity(storage, -1);
            case BUTTON_DROP_RARITY_UP -> adjustDropRarity(storage, 1);
            case BUTTON_WAYPOINT_ADD -> handleWaypointAdd(player);
            default -> handleWaypointButton(player, id) || handleSkillButton(player, id) || handleStatButton(player, id);
        };
        if (updated && (id < BUTTON_STAT_INCREASE_BASE
            || id == BUTTON_DROP_RARITY_DOWN
            || id == BUTTON_DROP_RARITY_UP)) {
            player.syncData(ModAttachments.PLAYER_STORAGE);
        }
        return updated;
    }

    private boolean handleSkillButton(Player player, int id) {
        int passiveCount = SkillDefinitions.getPassiveSkills().size();
        int max = passiveCount * PlayerAttachmentStorage.PASSIVE_SKILL_SLOTS;
        if (passiveCount > 0 && id >= BUTTON_EQUIP_PASSIVE_BASE && id < BUTTON_EQUIP_PASSIVE_BASE + max) {
            int offset = id - BUTTON_EQUIP_PASSIVE_BASE;
            int slot = offset / passiveCount;
            int skillIndex = offset % passiveCount;
            if (slot < 0 || slot >= PlayerAttachmentStorage.PASSIVE_SKILL_SLOTS) {
                return false;
            }
            PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
            if (storage == null) {
                return false;
            }
            PassiveSkill skill = SkillDefinitions.getPassiveSkillByIndex(skillIndex);
            if (skill == null) {
                return false;
            }
            storage.setPassiveSkill(slot, skill.id());
            player.syncData(ModAttachments.PLAYER_STORAGE);
            return true;
        }

        int activeCount = SkillDefinitions.getActiveSkills().size();
        int activeMax = activeCount * PlayerAttachmentStorage.ACTIVE_SKILL_SLOTS;
        if (activeCount == 0 || id < BUTTON_EQUIP_ACTIVE_BASE || id >= BUTTON_EQUIP_ACTIVE_BASE + activeMax) {
            return false;
        }
        int offset = id - BUTTON_EQUIP_ACTIVE_BASE;
        int slot = offset / activeCount;
        int skillIndex = offset % activeCount;
        if (slot < 0 || slot >= PlayerAttachmentStorage.ACTIVE_SKILL_SLOTS) {
            return false;
        }
        PlayerAttachmentStorage storage = player.getData(ModAttachments.PLAYER_STORAGE);
        if (storage == null) {
            return false;
        }
        ActiveSkill skill = SkillDefinitions.getActiveSkillByIndex(skillIndex);
        if (skill == null) {
            return false;
        }
        storage.setActiveSkill(slot, skill.id());
        player.syncData(ModAttachments.PLAYER_STORAGE);
        return true;
    }

    private boolean adjustDropRarity(PlayerAttachmentStorage storage, int delta) {
        if (storage == null || delta == 0) {
            return false;
        }
        ItemRarity current = storage.getMinDropRarity();
        ItemRarity[] values = ItemRarity.values();
        int index = current.ordinal() + delta;
        if (index < 0 || index >= values.length) {
            return false;
        }
        storage.setMinDropRarity(values[index]);
        return true;
    }

    private boolean handleWaypointAdd(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        CartographerService.addWaypoint(serverPlayer);
        return true;
    }

    private boolean handleWaypointButton(Player player, int id) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        if (id >= BUTTON_WAYPOINT_TOGGLE_BASE && id < BUTTON_WAYPOINT_MOVE_BASE) {
            int index = id - BUTTON_WAYPOINT_TOGGLE_BASE;
            return CartographerService.toggleWaypoint(serverPlayer, index);
        }
        if (id >= BUTTON_WAYPOINT_MOVE_BASE && id < BUTTON_WAYPOINT_DELETE_BASE) {
            int index = id - BUTTON_WAYPOINT_MOVE_BASE;
            return CartographerService.moveWaypointToPlayer(serverPlayer, index);
        }
        if (id >= BUTTON_WAYPOINT_DELETE_BASE && id < BUTTON_WAYPOINT_DELETE_BASE + 1000) {
            int index = id - BUTTON_WAYPOINT_DELETE_BASE;
            return CartographerService.removeWaypoint(serverPlayer, index);
        }
        return false;
    }

    private boolean handleStatButton(Player player, int id) {
        int statCount = CharacterStatRegistry.getAll().size();
        if (id >= BUTTON_STAT_INCREASE_BASE && id < BUTTON_STAT_INCREASE_BASE + statCount) {
            return adjustStat(player, id - BUTTON_STAT_INCREASE_BASE, 1);
        }
        if (id >= BUTTON_STAT_DECREASE_BASE && id < BUTTON_STAT_DECREASE_BASE + statCount) {
            return adjustStat(player, id - BUTTON_STAT_DECREASE_BASE, -1);
        }
        return false;
    }

    private boolean adjustStat(Player player, int index, int delta) {
        CharacterStat stat = getCharacterStatByIndex(index);
        if (stat == null) {
            return false;
        }
        PlayerStatStorage statStorage = player.getData(ModAttachments.PLAYER_STATS);
        PlayerProgressionStorage progression = player.getData(ModAttachments.PLAYER_PROGRESSION);
        if (statStorage == null || progression == null) {
            return false;
        }
        int baseLevel = (int) Math.round(stat.defaultValue());
        int currentLevel = (int) Math.round(statStorage.getValue(stat.id(), stat.defaultValue()));
        if (delta > 0 && progression.getSkillPoints() <= 0) {
            return false;
        }
        if (delta < 0 && currentLevel <= baseLevel) {
            return false;
        }
        int newLevel = currentLevel + delta;
        if (newLevel < baseLevel) {
            newLevel = baseLevel;
        }
        statStorage.setValue(stat.id(), newLevel);
        progression.setSkillPoints(progression.getSkillPoints() - delta);
        player.syncData(ModAttachments.PLAYER_STATS);
        player.syncData(ModAttachments.PLAYER_PROGRESSION);
        return true;
    }

    private CharacterStat getCharacterStatByIndex(int index) {
        int current = 0;
        for (CharacterStat stat : CharacterStatRegistry.getAll()) {
            if (current == index) {
                return stat;
            }
            current++;
        }
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
