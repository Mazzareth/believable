package app.masterwork.believable.client.screen;

import app.masterwork.believable.attachment.PlayerAttachmentStorage;
import app.masterwork.believable.character.BasicClass;
import app.masterwork.believable.character.ClassDefinitions;
import app.masterwork.believable.character.ClassModifier;
import app.masterwork.believable.classes.cartographer.CartographerService;
import app.masterwork.believable.classes.cartographer.CartographyWaypoint;
import app.masterwork.believable.classes.cartographer.PlayerCartographyStorage;
import app.masterwork.believable.classes.cartographer.client.screen.CartographerSettingsSection;
import app.masterwork.believable.classes.smith.SmithService;
import app.masterwork.believable.client.screen.components.ThemedButton;
import app.masterwork.believable.client.screen.components.ThemedFrame;
import app.masterwork.believable.client.screen.components.ThemedInfoPanel;
import app.masterwork.believable.client.screen.layout.PanelLayout;
import app.masterwork.believable.client.screen.sections.ClassSelectionSection;
import app.masterwork.believable.client.screen.sections.MagicSection;
import app.masterwork.believable.client.screen.sections.RaceSection;
import app.masterwork.believable.client.screen.sections.SkillsSection;
import app.masterwork.believable.client.screen.stats.CharacterStatsLayout;
import app.masterwork.believable.client.screen.stats.StatCardLayout;
import app.masterwork.believable.client.screen.stats.StatRowLayout;
import app.masterwork.believable.client.screen.stats.StatsLayout;
import app.masterwork.believable.client.screen.stats.StatsLayoutBuilder;
import app.masterwork.believable.client.screen.tabs.TabButton;
import app.masterwork.believable.client.screen.tabs.TabNode;
import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import app.masterwork.believable.item.ItemRarity;
import app.masterwork.believable.magic.BasicMagicSchool;
import app.masterwork.believable.magic.MagicSchoolDefinitions;
import app.masterwork.believable.magic.Spell;
import app.masterwork.believable.menu.UnbelievableMenu;
import app.masterwork.believable.network.OpenSalvageMenuPayload;
import app.masterwork.believable.network.RenameWaypointPayload;
import app.masterwork.believable.race.BasicRace;
import app.masterwork.believable.race.Race;
import app.masterwork.believable.race.RaceDefinitions;
import app.masterwork.believable.race.RaceModifier;
import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.skills.Skill;
import app.masterwork.believable.skills.SkillCategory;
import app.masterwork.believable.skills.SkillDefinitions;
import app.masterwork.believable.stats.CharacterStat;
import app.masterwork.believable.stats.CharacterStatDefinitions;
import app.masterwork.believable.stats.CharacterStatRegistry;
import app.masterwork.believable.stats.CharacterStatEffect;
import app.masterwork.believable.stats.PlayerDiscoveryStorage;
import app.masterwork.believable.stats.PlayerProgressionStorage;
import app.masterwork.believable.stats.PlayerStatStorage;
import app.masterwork.believable.stats.Stat;
import app.masterwork.believable.stats.StatModifier;
import app.masterwork.believable.stats.StatRegistry;
import app.masterwork.believable.stats.StatValueCalculator;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Character menu UI that renders tabs for race, class, stats, and magic.
 */
public class UnbelievableScreen extends AbstractContainerScreen<UnbelievableMenu>
        implements ClassSelectionSection.Host, RaceSection.Host, MagicSection.Host, SkillsSection.Host,
        CartographerSettingsSection.Host {
    private static final int OUTER_INSET = 8;
    private static final int INNER_INSET = 8;
    private static final int HEADER_HEIGHT = 32;
    private static final int FOOTER_HEIGHT = 52;
    private static final int PANEL_PADDING = 8;
    private static final int TAB_HEIGHT = 22;
    private static final int TAB_GAP = 6;
    private static final int SECTION_WIDTH = 96;
    private static final int SUBSECTION_WIDTH = 128;
    private static final int COLUMN_GAP = 8;
    private static final int RACE_BUTTON_HEIGHT = 20;
    private static final int RACE_SECTION_GAP = 8;
    private static final int RACE_BOX_PADDING = 6;
    private static final int RACE_BOX_BORDER = 2;
    private static final int CHARACTER_INFO_COLUMN_GAP = 12;
    private static final int CHARACTER_INFO_MIN_COLUMN_WIDTH = 220;
    private static final int TAB_COLUMN_MIN_WIDTH = 72;
    private static final int STAT_CARD_GAP = 10;
    private static final int STAT_CARD_PADDING = 8;
    private static final int STAT_CARD_HEADER_HEIGHT = 16;
    private static final int STAT_CARD_TWO_COLUMN_MIN_WIDTH = 360;
    private static final int STAT_VALUE_GAP = 6;
    private static final int STAT_ROW_HEIGHT = 26;
    private static final int STAT_ROW_GAP = 6;
    private static final int STAT_BUTTON_SIZE = 18;
    private static final int STAT_BUTTON_GAP = 4;
    private static final int STAT_LEVEL_BOX_MIN_WIDTH = 32;
    private static final int STAT_HEADER_HEIGHT = 36;
    private static final int MAGIC_SELECT_BUTTON_HEIGHT = 32;
    private static final int SETTINGS_BUTTON_HEIGHT = 22;
    private static final int SETTINGS_BUTTON_GAP = 8;
    private static final int SETTINGS_BOX_GAP = 8;
    private static final int CLASS_SELECTION_PANEL_GAP = 12;
    private static final int CLASS_SELECTION_BUTTON_GAP = 6;
    private static final int CLASS_LIST_BUTTON_GAP = 4;
    private static final int SKILL_EQUIP_BUTTON_WIDTH = 78;
    private static final int SKILL_EQUIP_BUTTON_HEIGHT = 18;
    private static final int SKILL_EQUIP_BUTTON_GAP = 6;
    private static final float MAGIC_DESCRIPTION_HEADER_SCALE = 1.35f;
    private static final float MAIN_CONTENT_TEXT_SCALE = 1.12f;
    private static final int MAIN_CONTENT_LINE_GAP = 1;
    private static final String RACE_DESCRIPTION_KEY = "screen.believable.unbelievable_menu.race.description";
    private static final String RACE_STATS_HEADER_KEY = "screen.believable.unbelievable_menu.race.stats_header";
    private static final String RACE_MODIFIERS_HEADER_KEY = "screen.believable.unbelievable_menu.race.modifiers_header";
    private static final String RACE_STATS_KEY = "screen.believable.unbelievable_menu.race.stats";
    private static final String CHARACTER_TAB_KEY = "screen.believable.unbelievable_menu.tab.character";
    private static final String STATS_TAB_KEY = "screen.believable.unbelievable_menu.tab.stats";
    private static final String ATTRIBUTES_TAB_KEY = "screen.believable.unbelievable_menu.tab.attributes";
    private static final String CLASS_SELECTION_TAB_KEY = "screen.believable.unbelievable_menu.tab.class_selection";
    private static final String INFO_TAB_KEY = "screen.believable.unbelievable_menu.tab.info";
    private static final String CLASS_DESCRIPTION_KEY = "screen.believable.unbelievable_menu.class.description";
    private static final String CLASS_STATS_HEADER_KEY = "screen.believable.unbelievable_menu.class.stats_header";
    private static final String CLASS_FEATURES_HEADER_KEY = "screen.believable.unbelievable_menu.class.features_header";
    private static final String CLASS_FEATURES_KEY = "screen.believable.unbelievable_menu.class.features";
    private static final String CLASS_STATS_KEY = "screen.believable.unbelievable_menu.class.stats";
    private static final String CLASS_NONE_KEY = "screen.believable.unbelievable_menu.class.none";
    private static final String CLASS_QUESTION_HEADER_KEY = "screen.believable.unbelievable_menu.class.question_header";
    private static final String CLASS_QUESTION_KEY = "screen.believable.unbelievable_menu.class.question";
    private static final String CLASS_QUESTION_HINT_KEY = "screen.believable.unbelievable_menu.class.question_hint";
    private static final String CLASS_STEP_PATH_KEY = "screen.believable.unbelievable_menu.class.step.path";
    private static final String CLASS_STEP_CLASS_KEY = "screen.believable.unbelievable_menu.class.step.class";
    private static final String CLASS_ALL_OPTIONS_KEY = "screen.believable.unbelievable_menu.class.all_options";
    private static final String CLASS_DETAILS_HEADER_KEY = "screen.believable.unbelievable_menu.class.details_header";
    private static final String CLASS_BACK_KEY = "screen.believable.unbelievable_menu.class.back";
    private static final String CLASS_CONFIRM_KEY = "screen.believable.unbelievable_menu.class.confirm";
    private static final String CLASS_PATH_UTILITY_KEY = "screen.believable.unbelievable_menu.class.path.utility";
    private static final String CLASS_PATH_MAGIC_KEY = "screen.believable.unbelievable_menu.class.path.magic";
    private static final String CLASS_PATH_INVENTION_KEY = "screen.believable.unbelievable_menu.class.path.invention";
    private static final String CLASS_PATH_UTILITY_DESC_KEY = "screen.believable.unbelievable_menu.class.path.utility.desc";
    private static final String CLASS_PATH_MAGIC_DESC_KEY = "screen.believable.unbelievable_menu.class.path.magic.desc";
    private static final String CLASS_PATH_INVENTION_DESC_KEY = "screen.believable.unbelievable_menu.class.path.invention.desc";
    private static final String STATS_EMPTY_KEY = "screen.believable.unbelievable_menu.stats.empty";
    private static final String ATTRIBUTES_EMPTY_KEY = "screen.believable.unbelievable_menu.attributes.empty";
    private static final String MAGIC_TAB_KEY = "screen.believable.unbelievable_menu.tab.magic";
    private static final String MAGIC_DESCRIPTION_KEY = "screen.believable.unbelievable_menu.magic.description";
    private static final String MAGIC_SPELLS_HEADER_KEY = "screen.believable.unbelievable_menu.magic.spells_header";
    private static final String MAGIC_SPELLS_EMPTY_KEY = "screen.believable.unbelievable_menu.magic.spells.empty";
    private static final String SKILLS_TAB_KEY = "screen.believable.unbelievable_menu.tab.skills";
    private static final String SKILLS_PASSIVE_TAB_KEY = "screen.believable.unbelievable_menu.tab.skills.passive";
    private static final String SKILLS_ACTIVE_TAB_KEY = "screen.believable.unbelievable_menu.tab.skills.active";
    private static final String SKILLS_SLOTS_KEY = "screen.believable.unbelievable_menu.skills.slots";
    private static final String SKILLS_AVAILABLE_KEY = "screen.believable.unbelievable_menu.skills.available";
    private static final String SKILLS_PASSIVE_SLOT_KEY = "screen.believable.unbelievable_menu.skills.passive_slot";
    private static final String SKILLS_ACTIVE_SLOT_KEY = "screen.believable.unbelievable_menu.skills.active_slot";
    private static final String SKILLS_EMPTY_PASSIVE_KEY = "screen.believable.unbelievable_menu.skills.empty.passive";
    private static final String SKILLS_EMPTY_ACTIVE_KEY = "screen.believable.unbelievable_menu.skills.empty.active";
    private static final String SKILLS_NONE_KEY = "screen.believable.unbelievable_menu.skills.none";
    private static final String INDEX_TAB_KEY = "screen.believable.unbelievable_menu.tab.index";
    private static final String INDEX_MOB_TAB_KEY = "screen.believable.unbelievable_menu.tab.index.mob";
    private static final String INDEX_BIOME_TAB_KEY = "screen.believable.unbelievable_menu.tab.index.biome";
    private static final String INDEX_MOB_EMPTY_KEY = "screen.believable.unbelievable_menu.index.mob.empty";
    private static final String INDEX_BIOME_EMPTY_KEY = "screen.believable.unbelievable_menu.index.biome.empty";
    private static final String SETTINGS_TAB_KEY = "screen.believable.unbelievable_menu.tab.settings";
    private static final String SETTINGS_SALVAGE_TAB_KEY = "screen.believable.unbelievable_menu.tab.settings.salvage";
    private static final String SETTINGS_WAYPOINTS_TAB_KEY = "screen.believable.unbelievable_menu.tab.settings.waypoints";
    private static final String SETTINGS_WAYPOINTS_EMPTY_KEY = "screen.believable.unbelievable_menu.settings.waypoints.empty";
    private static final String SETTINGS_WAYPOINTS_NOT_CARTOGRAPHER_KEY = "screen.believable.unbelievable_menu.settings.waypoints.not_cartographer";
    private static final String SETTINGS_MIN_RARITY_KEY = "screen.believable.unbelievable_menu.settings.minimum_rarity";
    private static final String SETTINGS_MIN_RARITY_VALUE_KEY = "screen.believable.unbelievable_menu.settings.minimum_rarity_value";
    private static final int CLASS_TOOLTIP_WIDTH = 240;
    private static final int SKILL_TOOLTIP_WIDTH = 260;

    private final List<TabNode> rootTabs = new ArrayList<>();
    private final List<TabNode> selectedPath = new ArrayList<>();
    private final List<Button> tabButtons = new ArrayList<>();
    private final List<GuiEventListener> contentButtons = new ArrayList<>();
    private final List<CharacterStatRowHitbox> statRowHitboxes = new ArrayList<>();
    private final List<ClassSelectionTooltip> classSelectionTooltips = new ArrayList<>();
    private final List<SkillTooltip> skillTooltips = new ArrayList<>();
    private final ClassSelectionSection classSelectionSection = new ClassSelectionSection();
    private final RaceSection raceSection = new RaceSection();
    private final MagicSection magicSection = new MagicSection();
    private final SkillsSection skillsSection = new SkillsSection();
    private final CartographerSettingsSection cartographerSettingsSection = new CartographerSettingsSection();
    private int sectionScroll;
    private int subsectionScroll;
    private int statsScroll;
    private int indexScroll;
    private int skillsScroll;
    private int layoutVersion;
    private int cachedLayoutVersion = -1;
    private int cachedLayoutWidth = -1;
    private int cachedLayoutHeight = -1;
    private PanelLayout cachedPanelLayout;
    private AttributesLayoutKey cachedAttributesLayoutKey;
    private StatsLayout cachedAttributesLayout;
    private CharacterStatsLayoutKey cachedCharacterStatsLayoutKey;
    private CharacterStatsLayout cachedCharacterStatsLayout;
    private boolean cachedManaUnlocked;
    private int cachedWaypointHash;
    private ClassSelectionCategory selectedClassCategory;
    private BasicClass pendingClass;
    private String pendingClassKey;
    private int passiveSkillSlotSelection;
    private int activeSkillSlotSelection;
    private SkillCategory passiveSkillCategorySelection = SkillCategory.ALL;
    private SkillCategory activeSkillCategorySelection = SkillCategory.ALL;

    public UnbelievableScreen(UnbelievableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth = this.width;
        this.imageHeight = this.height;
        super.init();
        invalidateLayoutCaches();
        this.cachedManaUnlocked = hasManaUnlocked();
        refreshTabs(null, null);
        int buttonWidth = 120;
        int buttonHeight = 20;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height - 32;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose())
                .bounds(x, y, buttonWidth, buttonHeight)
                .build());
    }

    @Override
    public void containerTick() {
        super.containerTick();
        boolean manaUnlocked = hasManaUnlocked();
        if (manaUnlocked != this.cachedManaUnlocked) {
            this.cachedManaUnlocked = manaUnlocked;
            refreshTabs(null, null);
        }
        if (isSettingsWaypointsTabSelected()) {
            PlayerCartographyStorage storage = cartographyStorage();
            int waypointHash = getWaypointHash(storage);
            if (waypointHash != this.cachedWaypointHash) {
                this.cachedWaypointHash = waypointHash;
                rebuildContentButtons();
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = 0;
        int y = 0;
        int w = this.width;
        int h = this.height;

        graphics.fillGradient(x, y, x + w, y + h, UnbelievableTheme.BACKDROP_TOP, UnbelievableTheme.BACKDROP_BOTTOM);
        graphics.fillGradient(
                x + OUTER_INSET,
                y + OUTER_INSET,
                x + w - OUTER_INSET,
                y + h - OUTER_INSET,
                UnbelievableTheme.INNER_TOP,
                UnbelievableTheme.INNER_BOTTOM);
        ThemedFrame.drawOrnateGildedFrame(graphics, x + OUTER_INSET, y + OUTER_INSET, w - OUTER_INSET * 2, h - OUTER_INSET * 2);

        float t = (Util.getMillis() % 3000L) / 3000.0f;
        float pulse = (Mth.sin(t * Mth.TWO_PI) + 1.0f) * 0.5f;
        int glow = 40 + (int) (90 * pulse);
        int glowColor = (glow << 24) | (UnbelievableTheme.GOLD & 0x00FFFFFF);
        graphics.fill(
                x + OUTER_INSET + 6,
                y + OUTER_INSET + 30,
                x + w - OUTER_INSET - 6,
                y + OUTER_INSET + 32,
                glowColor);
        for (int i = 0; i < 5; i++) {
            int lineY = y + OUTER_INSET + 60 + i * 38;
            if (lineY > y + h - OUTER_INSET - 24) {
                break;
            }
            graphics.fill(
                    x + OUTER_INSET + 20,
                    lineY,
                    x + w - OUTER_INSET - 20,
                    lineY + 1,
                    UnbelievableTheme.GOLD_FAINT);
        }

        PanelLayout layout = getPanelLayout();
        graphics.fill(layout.panelX(), layout.panelY(), layout.panelX() + layout.panelW(),
                layout.panelY() + layout.panelH(), UnbelievableTheme.PANEL_OUTER);
        graphics.fill(
                layout.panelX() + 1,
                layout.panelY() + 1,
                layout.panelX() + layout.panelW() - 1,
                layout.panelY() + layout.panelH() - 1,
                UnbelievableTheme.PANEL_INNER);
        ThemedFrame.drawPanelWell(graphics, layout.sectionX() - 3, layout.sectionY() - 3, layout.sectionW() + 6,
                layout.sectionH() + 6);
        ThemedFrame.drawPanelWell(graphics, layout.subsectionX() - 3, layout.subsectionY() - 3, layout.subsectionW() + 6,
                layout.subsectionH() + 6);
        ThemedFrame.drawPanelWell(graphics, layout.contentX() - 3, layout.contentY() - 3, layout.contentW() + 6,
                layout.contentH() + 6);
        graphics.fill(
                layout.sectionX() + layout.sectionW() + COLUMN_GAP / 2,
                layout.panelY() + 6,
                layout.sectionX() + layout.sectionW() + COLUMN_GAP / 2 + 1,
                layout.panelY() + layout.panelH() - 6,
                UnbelievableTheme.GOLD_DARK);
        graphics.fill(
                layout.subsectionX() + layout.subsectionW() + COLUMN_GAP / 2,
                layout.panelY() + 6,
                layout.subsectionX() + layout.subsectionW() + COLUMN_GAP / 2 + 1,
                layout.panelY() + layout.panelH() - 6,
                UnbelievableTheme.GOLD_DARK);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int titleX = this.width / 2;
        graphics.drawCenteredString(this.font, this.title, titleX, 10, UnbelievableTheme.TEXT_TITLE);

        PanelLayout layout = getPanelLayout();
        graphics.drawString(
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.section"),
                layout.sectionX(),
                layout.sectionY() - 12,
                UnbelievableTheme.TEXT_MUTED,
                false);
        graphics.drawString(
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.subsection"),
                layout.subsectionX(),
                layout.subsectionY() - 12,
                UnbelievableTheme.TEXT_MUTED,
                false);
        graphics.drawString(
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.main_content"),
                layout.contentX(),
                layout.contentY() - 12,
                UnbelievableTheme.TEXT_MUTED,
                false);
        magicSection.render(
                this,
                graphics,
                layout,
                MAGIC_TAB_KEY,
                "screen.believable.unbelievable_menu.magic.selected",
                MAGIC_DESCRIPTION_KEY,
                MAGIC_SPELLS_HEADER_KEY,
                MAGIC_SPELLS_EMPTY_KEY);
        renderCharacterInfo(graphics, layout);
        renderClassSelectionTooltip(graphics, mouseX, mouseY);
        renderAttributesInfo(graphics, layout);
        renderStatsInfo(graphics, layout, mouseX, mouseY);
        this.skillTooltips.clear();
        skillsSection.render(
                this,
                graphics,
                layout,
                SKILLS_TAB_KEY,
                SKILLS_SLOTS_KEY,
                SKILLS_AVAILABLE_KEY,
                SKILLS_PASSIVE_SLOT_KEY,
                SKILLS_ACTIVE_SLOT_KEY,
                SKILLS_EMPTY_PASSIVE_KEY,
                SKILLS_EMPTY_ACTIVE_KEY);
        renderSkillTooltip(graphics, mouseX, mouseY);
        renderSettingsInfo(graphics, layout);
        renderIndexInfo(graphics, layout);
    }

    private void refreshTabs(String preferredRootKey, String preferredSubKey) {
        String selectedRootKey = preferredRootKey;
        if (selectedRootKey == null && !this.selectedPath.isEmpty()) {
            selectedRootKey = this.selectedPath.get(0).translationKey;
        }
        String selectedSubKey = preferredSubKey;
        if (selectedSubKey == null && this.selectedPath.size() > 1) {
            selectedSubKey = this.selectedPath.get(1).translationKey;
        }

        this.rootTabs.clear();
        TabNode example = new TabNode("screen.believable.unbelievable_menu.tab.example_section");
        example.addChild(new TabNode("screen.believable.unbelievable_menu.tab.example_sub_one"));
        example.addChild(new TabNode("screen.believable.unbelievable_menu.tab.example_sub_two"));
        TabNode magic = null;
        if (hasManaUnlocked()) {
            magic = new TabNode(MAGIC_TAB_KEY);
            if (!hasSelectedMagicSchool()) {
                for (String tabKey : MagicSchoolDefinitions.getSchoolTabKeys()) {
                    magic.addChild(new TabNode(tabKey));
                }
            } else {
                BasicMagicSchool school = getSelectedMagicSchool();
                if (school != null) {
                    for (String subKey : school.subsectionTabKeys()) {
                        magic.addChild(new TabNode(subKey));
                    }
                }
            }
        }
        TabNode character = new TabNode(CHARACTER_TAB_KEY);
        if (!hasSelectedRace()) {
            character.addChild(new TabNode(RaceDefinitions.TAB_GOAT_KEY));
            character.addChild(new TabNode(RaceDefinitions.TAB_CAT_KEY));
            character.addChild(new TabNode(RaceDefinitions.TAB_FENNEC_KEY));
            character.addChild(new TabNode(RaceDefinitions.TAB_CANID_KEY));
            character.addChild(new TabNode(RaceDefinitions.TAB_PROTOGEN_KEY));
        } else if (!hasSelectedClass()) {
            character.addChild(new TabNode(CLASS_SELECTION_TAB_KEY));
        } else {
            character.addChild(new TabNode(STATS_TAB_KEY));
            character.addChild(new TabNode(ATTRIBUTES_TAB_KEY));
            character.addChild(new TabNode(INFO_TAB_KEY));
        }
        TabNode index = new TabNode(INDEX_TAB_KEY);
        index.addChild(new TabNode(INDEX_MOB_TAB_KEY));
        index.addChild(new TabNode(INDEX_BIOME_TAB_KEY));
        TabNode settings = new TabNode(SETTINGS_TAB_KEY);
        settings.addChild(new TabNode(SETTINGS_SALVAGE_TAB_KEY));
        settings.addChild(new TabNode(SETTINGS_WAYPOINTS_TAB_KEY));
        TabNode skills = new TabNode(SKILLS_TAB_KEY);
        skills.addChild(new TabNode(SKILLS_PASSIVE_TAB_KEY));
        skills.addChild(new TabNode(SKILLS_ACTIVE_TAB_KEY));
        this.rootTabs.add(character);
        if (magic != null) {
            this.rootTabs.add(magic);
        }
        this.rootTabs.add(skills);
        this.rootTabs.add(index);
        this.rootTabs.add(settings);
        this.rootTabs.add(example);

        if (this.rootTabs.isEmpty()) {
            return;
        }
        TabNode root = findTabByKey(this.rootTabs, selectedRootKey);
        if (root == null) {
            root = this.rootTabs.get(0);
        }
        selectTab(root, 0);
        if (selectedSubKey != null && !root.children.isEmpty()) {
            TabNode sub = findTabByKey(root.children, selectedSubKey);
            if (sub != null) {
                selectTab(sub, 1);
            }
        }
    }

    private void selectTab(TabNode tab, int depth) {
        while (this.selectedPath.size() > depth) {
            this.selectedPath.remove(this.selectedPath.size() - 1);
        }
        if (this.selectedPath.size() == depth) {
            this.selectedPath.add(tab);
        } else {
            this.selectedPath.set(depth, tab);
        }
        TabNode current = tab;
        while (!current.children.isEmpty()) {
            current = current.children.get(0);
            this.selectedPath.add(current);
        }
        this.subsectionScroll = 0;
        this.statsScroll = 0;
        this.indexScroll = 0;
        this.skillsScroll = 0;
        invalidateLayoutCaches();
        rebuildTabButtons();
    }

    private void rebuildTabButtons() {
        clearTabButtons();
        clearContentButtons();
        if (this.rootTabs.isEmpty()) {
            return;
        }
        PanelLayout layout = getPanelLayout();
        int sectionMaxScroll = getMaxScroll(this.rootTabs.size(), layout.sectionH());
        this.sectionScroll = Mth.clamp(this.sectionScroll, 0, sectionMaxScroll);
        int baseY = layout.sectionY() + PANEL_PADDING;
        int maxVisibleY = layout.sectionY() + layout.sectionH() - PANEL_PADDING;
        for (int i = 0; i < this.rootTabs.size(); i++) {
            TabNode tab = this.rootTabs.get(i);
            int buttonY = baseY + i * (TAB_HEIGHT + TAB_GAP) - this.sectionScroll;
            if (buttonY + TAB_HEIGHT < baseY || buttonY > maxVisibleY) {
                continue;
            }
            boolean selected = !this.selectedPath.isEmpty() && this.selectedPath.get(0) == tab;
            addTabButton(new TabButton(layout.sectionX(), buttonY, layout.sectionW(), TAB_HEIGHT, tab.label,
                    button -> selectTab(tab, 0), selected, 0));
        }

        if (!this.selectedPath.isEmpty()) {
            TabNode root = this.selectedPath.get(0);
            int subsectionMaxScroll = getMaxScroll(root.children.size(), layout.subsectionH());
            this.subsectionScroll = Mth.clamp(this.subsectionScroll, 0, subsectionMaxScroll);
            int subBaseY = layout.subsectionY() + PANEL_PADDING;
            int subMaxVisibleY = layout.subsectionY() + layout.subsectionH() - PANEL_PADDING;
            for (int i = 0; i < root.children.size(); i++) {
                TabNode tab = root.children.get(i);
                int buttonY = subBaseY + i * (TAB_HEIGHT + TAB_GAP) - this.subsectionScroll;
                if (buttonY + TAB_HEIGHT < subBaseY || buttonY > subMaxVisibleY) {
                    continue;
                }
                boolean selected = this.selectedPath.size() > 1 && this.selectedPath.get(1) == tab;
                addTabButton(new TabButton(layout.subsectionX(), buttonY, layout.subsectionW(), TAB_HEIGHT, tab.label,
                        button -> selectTab(tab, 1), selected, 1));
            }
        }

        int rowY = layout.contentY() + 24;
        for (int depth = 2; depth < this.selectedPath.size(); depth++) {
            final int depthIndex = depth;
            TabNode parent = this.selectedPath.get(depth - 1);
            if (parent.children.isEmpty()) {
                continue;
            }
            int cursorX = layout.contentX() + PANEL_PADDING;
            int cursorY = rowY;
            int rowHeight = TAB_HEIGHT + TAB_GAP;
            int maxRowY = cursorY;
            for (TabNode child : parent.children) {
                int textWidth = this.font.width(child.label);
                int width = Mth.clamp(textWidth + 18, 74, 170);
                if (cursorX + width > layout.contentX() + layout.contentW() - PANEL_PADDING) {
                    cursorX = layout.contentX() + PANEL_PADDING;
                    cursorY += rowHeight;
                }
                boolean selected = this.selectedPath.size() > depth && this.selectedPath.get(depth) == child;
                addTabButton(new TabButton(cursorX, cursorY, width, TAB_HEIGHT, child.label,
                        button -> selectTab(child, depthIndex), selected, depthIndex));
                cursorX += width + TAB_GAP;
                maxRowY = Math.max(maxRowY, cursorY);
            }
            rowY = maxRowY + TAB_HEIGHT + TAB_GAP + 4;
        }
        rebuildContentButtons();
    }

    private void addTabButton(TabButton button) {
        this.tabButtons.add(button);
        this.addRenderableWidget(button);
    }

    private void clearTabButtons() {
        for (GuiEventListener widget : this.tabButtons) {
            this.removeWidget(widget);
        }
        this.tabButtons.clear();
    }

    @Override
    public void addContentButton(Button button) {
        this.contentButtons.add(button);
        this.addRenderableWidget(button);
    }

    @Override
    public void addContentWidget(AbstractWidget widget) {
        this.contentButtons.add(widget);
        this.addRenderableWidget(widget);
    }

    private void clearContentButtons() {
        for (GuiEventListener widget : this.contentButtons) {
            this.removeWidget(widget);
        }
        this.contentButtons.clear();
    }

    @Override
    public void refreshContentButtons() {
        clearContentButtons();
        rebuildContentButtons();
    }

    private void rebuildContentButtons() {
        clearContentButtons();
        this.classSelectionTooltips.clear();
        this.skillTooltips.clear();
        if (isMagicSelectionActive()) {
            magicSection.rebuildButtons(this, "screen.believable.unbelievable_menu.magic.select");
        }
        if (isRaceSelectionActive()) {
            raceSection.rebuildButtons(this, "screen.believable.unbelievable_menu.race.select");
        }
        if (isClassSelectionActive()) {
            rebuildClassContentButtons();
        }
        if (isSkillsPassiveTabSelected() || isSkillsActiveTabSelected()) {
            skillsSection.rebuildButtons(this, getPanelLayout());
        }
        if (isSettingsSalvageTabSelected()) {
            rebuildSettingsContentButtons();
        }
        if (isSettingsWaypointsTabSelected()) {
            cartographerSettingsSection.rebuildButtons(
                    this,
                    "screen.believable.unbelievable_menu.settings.waypoints.add",
                    "screen.believable.unbelievable_menu.settings.waypoints.toggle",
                    "screen.believable.unbelievable_menu.settings.waypoints.move_here",
                    "screen.believable.unbelievable_menu.settings.waypoints.delete",
                    "screen.believable.unbelievable_menu.settings.waypoints.rename");
        }
    }

    private void rebuildClassContentButtons() {
        classSelectionSection.rebuildButtons(this, CLASS_BACK_KEY, CLASS_CONFIRM_KEY);
    }

    private void rebuildSettingsContentButtons() {
        PlayerAttachmentStorage storage = getPlayerStorage();
        if (storage == null) {
            return;
        }
        PanelLayout layout = getPanelLayout();
        int buttonWidth = 80;
        int buttonHeight = SETTINGS_BUTTON_HEIGHT;
        int buttonY = layout.contentY() + layout.contentH() - PANEL_PADDING - buttonHeight;
        int buttonX = layout.contentX() + PANEL_PADDING;
        int actionWidth = Math.min(200, layout.contentW() - PANEL_PADDING * 2);
        int actionY = buttonY - buttonHeight - SETTINGS_BUTTON_GAP;
        addContentButton(new ThemedButton(
                buttonX,
                actionY,
                actionWidth,
                buttonHeight,
                Component.translatable("screen.believable.unbelievable_menu.settings.open_salvage"),
                button -> PacketDistributor.sendToServer(new OpenSalvageMenuPayload())));
        ItemRarity rarity = storage.getMinDropRarity();
        Button lower = new ThemedButton(
                buttonX,
                buttonY,
                buttonWidth,
                buttonHeight,
                Component.translatable("screen.believable.unbelievable_menu.settings.lower"),
                button -> sendDropRarityChangeToServer(-1));
        lower.active = rarity.ordinal() > 0;
        addContentButton(lower);
        Button higher = new ThemedButton(
                buttonX + buttonWidth + 8,
                buttonY,
                buttonWidth,
                buttonHeight,
                Component.translatable("screen.believable.unbelievable_menu.settings.higher"),
                button -> sendDropRarityChangeToServer(1));
        higher.active = rarity.ordinal() < ItemRarity.values().length - 1;
        addContentButton(higher);
    }

    private String getBreadcrumb() {
        if (this.selectedPath.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.selectedPath.size(); i++) {
            if (i > 0) {
                builder.append(" > ");
            }
            builder.append(this.selectedPath.get(i).label.getString());
        }
        return builder.toString();
    }

    private void renderCharacterInfo(GuiGraphics graphics, PanelLayout layout) {
        PlayerAttachmentStorage storage = getPlayerStorage();
        if (!isCharacterTabSelected() || storage == null) {
            return;
        }
        if (isRaceSelectionActive()) {
            raceSection.render(
                    this,
                    graphics,
                    layout,
                    "screen.believable.unbelievable_menu.race.selected",
                    RACE_DESCRIPTION_KEY,
                    RACE_STATS_HEADER_KEY,
                    RACE_MODIFIERS_HEADER_KEY,
                    RACE_STATS_KEY,
                    "screen.believable.unbelievable_menu.race.none");
            return;
        }
        if (isClassSelectionActive()) {
            int textX = layout.contentX() + PANEL_PADDING;
            renderClassSelectionQuestionnaire(graphics, layout, textX);
            return;
        }
        if (!isInfoTabSelected()) {
            return;
        }
        BasicClass playerClass = storage.getPlayerClass();
        Race race = storage.getRace();
        if (playerClass == null || race == null) {
            return;
        }
        renderCombinedCharacterInfo(graphics, layout, race, playerClass);
    }

    private void renderCombinedCharacterInfo(GuiGraphics graphics, PanelLayout layout, Race race,
            BasicClass playerClass) {
        int panelX = layout.contentX() + PANEL_PADDING;
        int panelY = layout.contentY() + 10;
        int panelW = Math.max(0, layout.contentW() - PANEL_PADDING * 2);
        int panelH = Math.max(0, layout.contentH() - PANEL_PADDING * 2 - 4);
        if (panelW <= 0 || panelH <= 0) {
            return;
        }

        Component header = Component.translatable(INFO_TAB_KEY);
        int contentTop = ThemedInfoPanel.drawPanelWithHeader(graphics, this.font, header, panelX, panelY, panelW,
                panelH);
        int innerX = panelX + PANEL_PADDING;
        int innerRight = panelX + panelW - PANEL_PADDING;
        int innerWidth = Math.max(0, innerRight - innerX);
        int innerBottom = panelY + panelH - PANEL_PADDING;
        int availableHeight = Math.max(0, innerBottom - contentTop);
        if (innerWidth <= 0 || availableHeight <= 0) {
            return;
        }

        if (innerWidth >= CHARACTER_INFO_MIN_COLUMN_WIDTH * 2 + CHARACTER_INFO_COLUMN_GAP) {
            int columnWidth = (innerWidth - CHARACTER_INFO_COLUMN_GAP) / 2;
            int leftX = innerX;
            int rightX = innerX + columnWidth + CHARACTER_INFO_COLUMN_GAP;
            renderRaceInfoColumn(graphics, leftX, contentTop, columnWidth, availableHeight, race);
            renderClassInfoColumn(graphics, rightX, contentTop, columnWidth, availableHeight, playerClass);
        } else {
            int gap = RACE_SECTION_GAP;
            int raceHeight = (availableHeight - gap) / 2;
            int classHeight = Math.max(0, availableHeight - raceHeight - gap);
            renderRaceInfoColumn(graphics, innerX, contentTop, innerWidth, raceHeight, race);
            renderClassInfoColumn(graphics, innerX, contentTop + raceHeight + gap, innerWidth, classHeight,
                    playerClass);
        }
    }

    private void renderRaceInfoColumn(GuiGraphics graphics, int x, int y, int width, int height, Race race) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int cursorY = ThemedInfoPanel.drawSectionHeader(
                graphics,
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.race.selected", race.name()),
                x,
                y,
                width);
        int availableHeight = Math.max(0, height - (cursorY - y));
        renderDetailBlocks(
                graphics,
                x,
                cursorY,
                width,
                availableHeight,
                Component.translatable(RACE_DESCRIPTION_KEY),
                race.description(),
                Component.translatable(RACE_STATS_HEADER_KEY),
                null,
                Component.translatable(RACE_MODIFIERS_HEADER_KEY),
                race,
                null);
    }

    private void renderClassInfoColumn(GuiGraphics graphics, int x, int y, int width, int height,
            BasicClass playerClass) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int cursorY = ThemedInfoPanel.drawSectionHeader(
                graphics,
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.class.selected", playerClass.name()),
                x,
                y,
                width);
        int availableHeight = Math.max(0, height - (cursorY - y));
        renderDetailBlocks(
                graphics,
                x,
                cursorY,
                width,
                availableHeight,
                Component.translatable(CLASS_DESCRIPTION_KEY),
                playerClass.description(),
                Component.translatable(CLASS_STATS_HEADER_KEY),
                playerClass,
                Component.translatable(CLASS_FEATURES_HEADER_KEY),
                null,
                playerClass);
    }

    private void renderDetailBlocks(GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            Component descriptionHeader,
            String description,
            Component statsHeader,
            BasicClass playerClass,
            Component modifiersHeader,
            Race race,
            BasicClass modifiersClass) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int gap = RACE_SECTION_GAP;
        int sectionHeight = Math.max(0, height - gap * 2);
        int descriptionHeight = (int) (sectionHeight * 0.5f);
        int statsHeight = (int) (sectionHeight * 0.25f);
        int modifiersHeight = sectionHeight - descriptionHeight - statsHeight;
        int minSection = 36;
        if (statsHeight < minSection || modifiersHeight < minSection) {
            statsHeight = Math.min(minSection, sectionHeight / 3);
            modifiersHeight = statsHeight;
            descriptionHeight = Math.max(0, sectionHeight - statsHeight - modifiersHeight);
        }

        int descriptionY = y;
        int statsY = descriptionY + descriptionHeight + gap;
        int modifiersY = statsY + statsHeight + gap;

        int descriptionTextY = ThemedInfoPanel.drawSectionHeader(
                graphics,
                this.font,
                descriptionHeader,
                x,
                descriptionY,
                width);
        drawDescriptionText(
                graphics,
                x,
                descriptionTextY,
                width,
                Math.max(0, descriptionHeight - (descriptionTextY - descriptionY)),
                description);

        int statsTextY = ThemedInfoPanel.drawSectionHeader(
                graphics,
                this.font,
                statsHeader,
                x,
                statsY,
                width);
        int statsHeightLeft = Math.max(0, statsHeight - (statsTextY - statsY));
        if (race != null) {
            drawRaceStatsText(graphics, x, statsTextY, width, statsHeightLeft, race);
        } else if (playerClass != null) {
            drawStatModifierText(graphics, x, statsTextY, width, statsHeightLeft, playerClass.statModifiers(),
                    CLASS_NONE_KEY);
        }

        int modifiersTextY = ThemedInfoPanel.drawSectionHeader(
                graphics,
                this.font,
                modifiersHeader,
                x,
                modifiersY,
                width);
        int modifiersHeightLeft = Math.max(0, modifiersHeight - (modifiersTextY - modifiersY));
        if (modifiersClass != null) {
            drawClassModifierText(graphics, x, modifiersTextY, width, modifiersHeightLeft, modifiersClass);
        } else if (race != null) {
            drawModifierText(graphics, x, modifiersTextY, width, modifiersHeightLeft, race);
        }
    }

    private boolean isCharacterTabSelected() {
        return !this.selectedPath.isEmpty() && CHARACTER_TAB_KEY.equals(this.selectedPath.get(0).translationKey);
    }

    private boolean isMagicTabSelected() {
        return !this.selectedPath.isEmpty() && MAGIC_TAB_KEY.equals(this.selectedPath.get(0).translationKey);
    }

    private boolean isMagicSelectionActive() {
        return isMagicTabSelected() && !hasSelectedMagicSchool();
    }

    private boolean isRaceSelectionActive() {
        return isCharacterTabSelected() && !hasSelectedRace();
    }

    private boolean isClassSelectionActive() {
        return isCharacterTabSelected()
                && hasSelectedRace()
                && !hasSelectedClass()
                && isClassSelectionTabSelected();
    }

    private boolean isStatsTabSelected() {
        return isCharacterTabSelected()
                && hasSelectedRace()
                && hasSelectedClass()
                && this.selectedPath.size() > 1
                && STATS_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isAttributesTabSelected() {
        return isCharacterTabSelected()
                && hasSelectedRace()
                && hasSelectedClass()
                && this.selectedPath.size() > 1
                && ATTRIBUTES_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isClassSelectionTabSelected() {
        return isCharacterTabSelected()
                && this.selectedPath.size() > 1
                && CLASS_SELECTION_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isInfoTabSelected() {
        return isCharacterTabSelected()
                && hasSelectedRace()
                && hasSelectedClass()
                && this.selectedPath.size() > 1
                && INFO_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isIndexTabSelected() {
        return !this.selectedPath.isEmpty() && INDEX_TAB_KEY.equals(this.selectedPath.get(0).translationKey);
    }

    private boolean isIndexMobTabSelected() {
        return isIndexTabSelected()
                && this.selectedPath.size() > 1
                && INDEX_MOB_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isIndexBiomeTabSelected() {
        return isIndexTabSelected()
                && this.selectedPath.size() > 1
                && INDEX_BIOME_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isSettingsTabSelected() {
        return !this.selectedPath.isEmpty() && SETTINGS_TAB_KEY.equals(this.selectedPath.get(0).translationKey);
    }

    private boolean isSettingsSalvageTabSelected() {
        return isSettingsTabSelected()
                && this.selectedPath.size() > 1
                && SETTINGS_SALVAGE_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isSettingsWaypointsTabSelected() {
        return isSettingsTabSelected()
                && this.selectedPath.size() > 1
                && SETTINGS_WAYPOINTS_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isSkillsTabSelected() {
        return !this.selectedPath.isEmpty() && SKILLS_TAB_KEY.equals(this.selectedPath.get(0).translationKey);
    }

    private boolean isSkillsPassiveTabSelected() {
        return isSkillsTabSelected()
                && this.selectedPath.size() > 1
                && SKILLS_PASSIVE_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private boolean isSkillsActiveTabSelected() {
        return isSkillsTabSelected()
                && this.selectedPath.size() > 1
                && SKILLS_ACTIVE_TAB_KEY.equals(this.selectedPath.get(1).translationKey);
    }

    private String getSelectedMagicKey() {
        if (!isMagicSelectionActive() || this.selectedPath.size() < 2) {
            return null;
        }
        return this.selectedPath.get(1).translationKey;
    }

    private String getSelectedRaceKey() {
        if (!isRaceSelectionActive() || this.selectedPath.size() < 2) {
            return null;
        }
        return this.selectedPath.get(1).translationKey;
    }

    private void renderAttributesInfo(GuiGraphics graphics, PanelLayout layout) {
        if (!isAttributesTabSelected()) {
            return;
        }
        int textX = layout.contentX() + PANEL_PADDING;
        int titleY = layout.contentY() + 10;
        drawTabTitle(graphics, textX, titleY, Component.translatable(ATTRIBUTES_TAB_KEY));
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }
        PlayerStatStorage statStorage = this.minecraft.player.getData(ModAttachments.PLAYER_STATS);
        if (statStorage == null || StatRegistry.getAll().isEmpty()) {
            int emptyY = titleY + (int) (this.font.lineHeight * 1.25f) + 12;
            drawStatsEmptyPanel(graphics, layout, textX, emptyY, Component.translatable(ATTRIBUTES_EMPTY_KEY));
            return;
        }
        StatsLayout statsLayout = getAttributesLayout(layout, statStorage, textX, titleY);
        this.statsScroll = Mth.clamp(this.statsScroll, 0, statsLayout.maxScroll());
        graphics.enableScissor(
                layout.contentX(),
                statsLayout.cardsTop(),
                layout.contentX() + layout.contentW(),
                statsLayout.contentBottom());
        for (StatCardLayout card : statsLayout.cards()) {
            int cardY = card.y() - this.statsScroll;
            if (cardY + card.height() < statsLayout.cardsTop() || cardY > statsLayout.contentBottom()) {
                continue;
            }
            drawStatCard(graphics, card, cardY);
        }
        graphics.disableScissor();
    }

    private void renderStatsInfo(GuiGraphics graphics, PanelLayout layout, int mouseX, int mouseY) {
        if (!isStatsTabSelected()) {
            return;
        }
        int textX = layout.contentX() + PANEL_PADDING;
        int titleY = layout.contentY() + 10;
        drawTabTitle(graphics, textX, titleY, Component.translatable(STATS_TAB_KEY));
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }
        PlayerStatStorage statStorage = this.minecraft.player.getData(ModAttachments.PLAYER_STATS);
        if (statStorage == null || CharacterStatRegistry.getAll().isEmpty()) {
            int emptyY = titleY + (int) (this.font.lineHeight * 1.25f) + 12;
            drawStatsEmptyPanel(graphics, layout, textX, emptyY, Component.translatable(STATS_EMPTY_KEY));
            return;
        }

        CharacterStatsLayout statsLayout = getCharacterStatsLayout(layout, textX, titleY);
        this.statsScroll = Mth.clamp(this.statsScroll, 0, statsLayout.maxScroll());

        drawInfoBox(graphics, textX, statsLayout.headerY(), statsLayout.rowWidth(), statsLayout.headerHeight());
        renderStatsHeader(graphics, textX, statsLayout.headerY(), statsLayout.rowWidth());

        this.statRowHitboxes.clear();
        graphics.enableScissor(
                layout.contentX(),
                statsLayout.listTop(),
                layout.contentX() + layout.contentW(),
                statsLayout.contentBottom());
        CharacterStatRowHitbox hovered = null;
        int index = 0;
        for (CharacterStat stat : CharacterStatRegistry.getAll()) {
            int rowY = statsLayout.listTop() + index * (STAT_ROW_HEIGHT + STAT_ROW_GAP) - this.statsScroll;
            index++;
            if (rowY + STAT_ROW_HEIGHT < statsLayout.listTop() || rowY > statsLayout.contentBottom()) {
                continue;
            }
            CharacterStatRowHitbox hitbox = drawCharacterStatRow(
                    graphics,
                    stat,
                    statStorage,
                    textX,
                    rowY,
                    statsLayout.rowWidth(),
                    mouseX,
                    mouseY);
            this.statRowHitboxes.add(hitbox);
            if (hitbox.isRowHovered(mouseX, mouseY)) {
                hovered = hitbox;
            }
        }
        graphics.disableScissor();

        if (hovered != null) {
            renderStatTooltip(graphics, hovered.stat, mouseX, mouseY);
        }
    }

    private void renderIndexInfo(GuiGraphics graphics, PanelLayout layout) {
        if (!isIndexTabSelected()) {
            return;
        }
        int textX = layout.contentX() + PANEL_PADDING;
        int titleY = layout.contentY() + 10;
        drawTabTitle(graphics, textX, titleY, Component.translatable(INDEX_TAB_KEY));
        if (this.selectedPath.size() < 2) {
            return;
        }
        String subKey = this.selectedPath.get(1).translationKey;
        int headerY = titleY + (int) (this.font.lineHeight * 1.25f) + 8;
        drawSectionHeader(graphics, Component.translatable(subKey), textX, headerY);
        int listY = headerY + this.font.lineHeight + 4;
        if (INDEX_BIOME_TAB_KEY.equals(subKey)) {
            renderIndexBiomePanel(graphics, layout, textX, listY);
        } else if (INDEX_MOB_TAB_KEY.equals(subKey)) {
            renderIndexMobPanel(graphics, layout, textX, listY);
        }
    }

    private void renderSettingsInfo(GuiGraphics graphics, PanelLayout layout) {
        if (!isSettingsTabSelected()) {
            return;
        }
        PlayerAttachmentStorage storage = getPlayerStorage();
        if (storage == null) {
            return;
        }
        int textX = layout.contentX() + PANEL_PADDING;
        int titleY = layout.contentY() + 10;
        drawTabTitle(graphics, textX, titleY, Component.translatable(SETTINGS_TAB_KEY));
        if (this.selectedPath.size() < 2) {
            return;
        }
        String subKey = this.selectedPath.get(1).translationKey;
        if (SETTINGS_WAYPOINTS_TAB_KEY.equals(subKey)) {
            cartographerSettingsSection.render(
                    this,
                    graphics,
                    layout,
                    SETTINGS_TAB_KEY,
                    SETTINGS_WAYPOINTS_TAB_KEY,
                    SETTINGS_WAYPOINTS_EMPTY_KEY,
                    SETTINGS_WAYPOINTS_NOT_CARTOGRAPHER_KEY);
            return;
        }
        int headerY = titleY + (int) (this.font.lineHeight * 1.25f) + 8;
        drawSectionHeader(graphics, Component.translatable(subKey), textX, headerY);

        int contentRight = layout.contentX() + layout.contentW() - PANEL_PADDING;
        int contentBottom = layout.contentY() + layout.contentH() - PANEL_PADDING - getSettingsFooterHeight();
        int boxWidth = Math.max(0, contentRight - textX);
        int boxY = headerY + this.font.lineHeight + 4;
        int boxHeight = Math.max(0, contentBottom - boxY);
        drawInfoBox(graphics, textX, boxY, boxWidth, boxHeight);

        ItemRarity rarity = storage.getMinDropRarity();
        Component label = Component.translatable(SETTINGS_MIN_RARITY_KEY);
        Component value = Component.translatable(
                SETTINGS_MIN_RARITY_VALUE_KEY,
                rarity.getDisplayName());
        graphics.drawString(this.font, label, textX + 8, boxY + 10, UnbelievableTheme.TEXT_PRIMARY, false);
        graphics.drawString(
                this.font,
                value,
                textX + 8,
                boxY + 10 + this.font.lineHeight + 4,
                UnbelievableTheme.TEXT_MUTED,
                false);
    }

    private int getSettingsFooterHeight() {
        if (isSettingsWaypointsTabSelected()) {
            return cartographerSettingsSection.getFooterHeight(this);
        }
        return SETTINGS_BUTTON_HEIGHT * 2 + SETTINGS_BUTTON_GAP + SETTINGS_BOX_GAP;
    }

    private void renderIndexBiomePanel(GuiGraphics graphics, PanelLayout layout, int textX, int listY) {
        int contentRight = layout.contentX() + layout.contentW() - PANEL_PADDING;
        int contentBottom = layout.contentY() + layout.contentH() - PANEL_PADDING;
        int boxWidth = Math.max(0, contentRight - textX);
        int boxHeight = Math.max(0, contentBottom - listY);
        drawInfoBox(graphics, textX, listY, boxWidth, boxHeight);

        PlayerDiscoveryStorage discovery = getPlayerDiscovery();
        if (discovery == null || discovery.getDiscoveredBiomes().isEmpty()) {
            graphics.drawString(
                    this.font,
                    Component.translatable(INDEX_BIOME_EMPTY_KEY),
                    textX + 8,
                    listY + 10,
                    UnbelievableTheme.TEXT_MUTED,
                    false);
            return;
        }

        List<ResourceLocation> biomes = new ArrayList<>(discovery.getDiscoveredBiomes());
        biomes.sort(Comparator.comparing(ResourceLocation::toString));

        int lineStep = this.font.lineHeight + 2;
        int maxScroll = getIndexBiomeMaxScroll(boxHeight, lineStep, biomes.size());
        this.indexScroll = Mth.clamp(this.indexScroll, 0, maxScroll);

        int listTop = listY + RACE_BOX_PADDING;
        int listBottom = listY + boxHeight - RACE_BOX_PADDING;
        int drawX = textX + RACE_BOX_PADDING;

        graphics.enableScissor(
                layout.contentX(),
                listY,
                layout.contentX() + layout.contentW(),
                listY + boxHeight);
        for (int i = 0; i < biomes.size(); i++) {
            int drawY = listTop + i * lineStep - this.indexScroll;
            if (drawY + this.font.lineHeight < listY || drawY > listBottom) {
                continue;
            }
            Component label = Component.literal("- ").append(getBiomeLabel(biomes.get(i)));
            graphics.drawString(this.font, label, drawX, drawY, UnbelievableTheme.TEXT_MUTED, false);
        }
        graphics.disableScissor();
    }

    private void renderIndexMobPanel(GuiGraphics graphics, PanelLayout layout, int textX, int listY) {
        int contentRight = layout.contentX() + layout.contentW() - PANEL_PADDING;
        int contentBottom = layout.contentY() + layout.contentH() - PANEL_PADDING;
        int boxWidth = Math.max(0, contentRight - textX);
        int boxHeight = Math.max(0, contentBottom - listY);
        drawInfoBox(graphics, textX, listY, boxWidth, boxHeight);
        graphics.drawString(
                this.font,
                Component.translatable(INDEX_MOB_EMPTY_KEY),
                textX + 8,
                listY + 10,
                UnbelievableTheme.TEXT_MUTED,
                false);
    }

    private int getIndexBiomeMaxScroll(int boxHeight, int lineStep, int itemCount) {
        int availableHeight = Math.max(0, boxHeight - RACE_BOX_PADDING * 2);
        int contentHeight = itemCount * lineStep;
        return Math.max(0, contentHeight - availableHeight);
    }

    private int getIndexListTopY(PanelLayout layout) {
        int titleY = layout.contentY() + 10;
        int headerY = titleY + (int) (this.font.lineHeight * 1.25f) + 8;
        return headerY + this.font.lineHeight + 4;
    }

    private int getIndexBiomeMaxScroll(PanelLayout layout) {
        if (!isIndexBiomeTabSelected()) {
            return 0;
        }
        PlayerDiscoveryStorage discovery = getPlayerDiscovery();
        if (discovery == null || discovery.getDiscoveredBiomes().isEmpty()) {
            return 0;
        }
        int listY = getIndexListTopY(layout);
        int contentBottom = layout.contentY() + layout.contentH() - PANEL_PADDING;
        int boxHeight = Math.max(0, contentBottom - listY);
        int lineStep = this.font.lineHeight + 2;
        return getIndexBiomeMaxScroll(boxHeight, lineStep, discovery.getDiscoveredBiomes().size());
    }

    private String formatStatValue(double value) {
        if (Math.abs(value - Math.round(value)) < 0.01D) {
            return String.valueOf((int) Math.round(value));
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private String formatSignedValue(double value) {
        String prefix = value >= 0.0D ? "+ " : "- ";
        return prefix + formatStatValue(Math.abs(value));
    }

    private void drawClassDetails(GuiGraphics graphics, int textX, int textY, BasicClass playerClass) {
        graphics.drawString(
                this.font,
                Component.literal(playerClass.description()),
                textX,
                textY,
                UnbelievableTheme.TEXT_MUTED,
                false);
        graphics.drawString(
                this.font,
                Component.translatable(CLASS_STATS_KEY,
                        getStatModifierLabel(playerClass.statModifiers(), CLASS_NONE_KEY)),
                textX,
                textY + 12,
                UnbelievableTheme.TEXT_MUTED,
                false);
        graphics.drawString(
                this.font,
                Component.translatable(CLASS_FEATURES_KEY, getClassModifierLabel(playerClass)),
                textX,
                textY + 24,
                UnbelievableTheme.TEXT_MUTED,
                false);
    }

    private void renderClassSelectionQuestionnaire(GuiGraphics graphics, PanelLayout layout, int textX) {
        classSelectionSection.renderQuestionnaire(
                this,
                graphics,
                layout,
                textX,
                CLASS_SELECTION_TAB_KEY,
                CLASS_QUESTION_HEADER_KEY,
                CLASS_QUESTION_KEY,
                CLASS_STEP_PATH_KEY,
                CLASS_STEP_CLASS_KEY,
                CLASS_DETAILS_HEADER_KEY,
                CLASS_ALL_OPTIONS_KEY);
    }

    private void drawSectionHeader(GuiGraphics graphics, Component label, int x, int y) {
        graphics.drawString(this.font, label, x, y, UnbelievableTheme.TEXT_MUTED, false);
    }

    private void drawGrandSectionHeader(GuiGraphics graphics, Component label, int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(MAGIC_DESCRIPTION_HEADER_SCALE, MAGIC_DESCRIPTION_HEADER_SCALE, 1.0f);
        graphics.drawString(this.font, label, 0, 0, UnbelievableTheme.TEXT_PRIMARY, false);
        graphics.pose().popPose();
    }

    private void drawInfoBox(GuiGraphics graphics, int x, int y, int width, int height) {
        int innerX = x - RACE_BOX_BORDER;
        int innerY = y - RACE_BOX_BORDER;
        int innerW = width + RACE_BOX_BORDER * 2;
        int innerH = height + RACE_BOX_BORDER * 2;
        graphics.fill(innerX, innerY, innerX + innerW, innerY + innerH, UnbelievableTheme.PANEL_SHADOW);
        graphics.fill(innerX + 1, innerY + 1, innerX + innerW - 1, innerY + innerH - 1, UnbelievableTheme.BOX_BORDER);
        graphics.fill(x, y, x + width, y + height, UnbelievableTheme.BOX_INNER);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, UnbelievableTheme.BOX_FILL);
        graphics.fill(x, y, x + width, y + 1, UnbelievableTheme.GOLD_DARK);
    }

    private void drawTabTitle(GuiGraphics graphics, int x, int y, Component label) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(1.25f, 1.25f, 1.0f);
        graphics.drawString(this.font, label, 0, 0, UnbelievableTheme.TEXT_PRIMARY, false);
        graphics.pose().popPose();
    }

    private void drawStatsEmptyPanel(GuiGraphics graphics, PanelLayout layout, int x, int y, Component label) {
        int width = Math.max(0, layout.contentW() - PANEL_PADDING * 2);
        int height = Math.min(48, Math.max(28, layout.contentH() - PANEL_PADDING * 2));
        drawInfoBox(graphics, x, y, width, height);
        graphics.drawString(this.font, label, x + 8, y + 10, UnbelievableTheme.TEXT_MUTED, false);
    }

    private void drawStatCard(GuiGraphics graphics, StatCardLayout card, int y) {
        int x = card.x();
        int width = card.width();
        int height = card.height();
        if (width <= 0 || height <= 0) {
            return;
        }
        int borderColor = UnbelievableTheme.CARD_BORDER;
        graphics.fill(x, y, x + width, y + height, UnbelievableTheme.CARD_FILL);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, UnbelievableTheme.CARD_INNER);
        graphics.fill(x, y, x + width, y + 1, borderColor);
        graphics.fill(x, y + height - 1, x + width, y + height, borderColor);
        graphics.fill(x, y, x + 2, y + height, borderColor);
        graphics.fill(x + width - 2, y, x + width, y + height, borderColor);
        graphics.fill(x, y, x + width, y + STAT_CARD_HEADER_HEIGHT, UnbelievableTheme.CARD_HEADER);
        graphics.fill(x, y + STAT_CARD_HEADER_HEIGHT - 1, x + width, y + STAT_CARD_HEADER_HEIGHT,
                card.category().accentColor);
        graphics.fill(x + 2, y + 2, x + width - 2, y + 3, UnbelievableTheme.GOLD_FAINT);

        graphics.pose().pushPose();
        graphics.pose().translate(x + 8, y + 3, 0);
        graphics.pose().scale(1.05f, 1.05f, 1.0f);
        graphics.drawString(this.font, Component.literal(card.category().title), 0, 0, UnbelievableTheme.TEXT_PRIMARY,
                false);
        graphics.pose().popPose();

        int rowX = x + STAT_CARD_PADDING;
        int rowY = y + STAT_CARD_HEADER_HEIGHT + STAT_CARD_PADDING;
        int lineHeight = this.font.lineHeight + 3;
        int valueRightX = x + width - STAT_CARD_PADDING;
        int rowWidth = Math.max(0, width - STAT_CARD_PADDING * 2);
        for (StatRowLayout row : card.rows()) {
            for (int i = 0; i < row.nameLines().size(); i++) {
                graphics.drawString(this.font, row.nameLines().get(i), rowX, rowY + i * lineHeight,
                        UnbelievableTheme.TEXT_SOFT, false);
            }
            int valueLineIndex = row.valueOnFirstLine() ? 0 : row.nameLines().size();
            int valueY = rowY + valueLineIndex * lineHeight;
            int valueX = valueRightX - row.valueWidth();
            if (rowWidth > 0) {
                graphics.drawString(this.font, Component.literal(row.valueText()), valueX, valueY,
                        UnbelievableTheme.TEXT_SELECTED, false);
            }
            rowY += row.totalLines() * lineHeight;
        }
    }

    private void renderStatsHeader(GuiGraphics graphics, int x, int y, int width) {
        PlayerProgressionStorage progression = getPlayerProgression();
        int level = progression != null ? progression.getLevel() : 1;
        int skillPoints = progression != null ? progression.getSkillPoints() : 0;
        int xp = progression != null ? progression.getXp() : 0;
        double xpGain = progression != null ? progression.getXpGain() : 1.0D;

        int lineHeight = this.font.lineHeight + 2;
        int columnWidth = Math.max(0, width / 2);
        int leftX = x + 8;
        int rightX = x + columnWidth + 8;
        int lineY = y + 6;
        int skillColor = skillPoints > 0 ? UnbelievableTheme.TEXT_SELECTED : UnbelievableTheme.TEXT_SOFT;

        graphics.drawString(
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.stats.level", level),
                leftX,
                lineY,
                UnbelievableTheme.TEXT_SOFT,
                false);
        graphics.drawString(
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.stats.skill_points", skillPoints),
                rightX,
                lineY,
                skillColor,
                false);
        graphics.drawString(
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.stats.xp", xp),
                leftX,
                lineY + lineHeight,
                UnbelievableTheme.TEXT_MUTED,
                false);
        graphics.drawString(
                this.font,
                Component.translatable("screen.believable.unbelievable_menu.stats.xp_gain", formatStatValue(xpGain)),
                rightX,
                lineY + lineHeight,
                UnbelievableTheme.TEXT_MUTED,
                false);
    }

    private CharacterStatRowHitbox drawCharacterStatRow(GuiGraphics graphics,
            CharacterStat stat,
            PlayerStatStorage statStorage,
            int x,
            int y,
            int width,
            int mouseX,
            int mouseY) {
        int rowHeight = STAT_ROW_HEIGHT;
        boolean hoveredRow = isWithin(mouseX, mouseY, x, y, width, rowHeight);
        int rowBorder = hoveredRow ? UnbelievableTheme.ROW_BORDER_HOVER : UnbelievableTheme.ROW_BORDER;
        int rowFill = hoveredRow ? UnbelievableTheme.ROW_FILL_HOVER : UnbelievableTheme.ROW_FILL;
        int rowInner = hoveredRow ? UnbelievableTheme.ROW_INNER_HOVER : UnbelievableTheme.ROW_INNER;

        graphics.fill(x, y, x + width, y + rowHeight, rowBorder);
        graphics.fill(x + 1, y + 1, x + width - 1, y + rowHeight - 1, rowFill);
        graphics.fill(x + 2, y + 2, x + width - 2, y + rowHeight - 2, rowInner);
        if (hoveredRow) {
            graphics.fill(x + 2, y + 2, x + width - 2, y + 3, UnbelievableTheme.GOLD_FAINT);
        }

        double levelValue = StatValueCalculator.getCharacterStatLevel(stat, statStorage, this.minecraft.player);
        int level = (int) Math.round(levelValue);
        int baseLevel = (int) Math.round(stat.defaultValue());
        PlayerProgressionStorage progression = getPlayerProgression();
        int skillPoints = progression != null ? progression.getSkillPoints() : 0;
        boolean canIncrease = skillPoints > 0;
        boolean canDecrease = level > baseLevel;

        String levelText = String.valueOf(level);
        int levelBoxWidth = Math.max(STAT_LEVEL_BOX_MIN_WIDTH, this.font.width(levelText) + 12);
        int right = x + width - 8;
        int buttonY = y + (rowHeight - STAT_BUTTON_SIZE) / 2;
        int plusX = right - STAT_BUTTON_SIZE;
        int levelX = plusX - STAT_BUTTON_GAP - levelBoxWidth;
        int minusX = levelX - STAT_BUTTON_GAP - STAT_BUTTON_SIZE;

        int nameY = y + (rowHeight - this.font.lineHeight) / 2;
        graphics.drawString(this.font, Component.literal(stat.name()), x + 10, nameY, UnbelievableTheme.TEXT_SOFT,
                false);

        boolean minusHover = isWithin(mouseX, mouseY, minusX, buttonY, STAT_BUTTON_SIZE, STAT_BUTTON_SIZE);
        boolean plusHover = isWithin(mouseX, mouseY, plusX, buttonY, STAT_BUTTON_SIZE, STAT_BUTTON_SIZE);
        drawStatButton(graphics, minusX, buttonY, "-", canDecrease, minusHover);
        drawStatButton(graphics, plusX, buttonY, "+", canIncrease, plusHover);

        int levelBorder = UnbelievableTheme.BUTTON_BORDER;
        graphics.fill(levelX, buttonY, levelX + levelBoxWidth, buttonY + STAT_BUTTON_SIZE,
                UnbelievableTheme.BUTTON_FILL_DISABLED);
        graphics.fill(levelX, buttonY, levelX + levelBoxWidth, buttonY + 1, levelBorder);
        graphics.fill(levelX, buttonY + STAT_BUTTON_SIZE - 1, levelX + levelBoxWidth, buttonY + STAT_BUTTON_SIZE,
                levelBorder);
        graphics.fill(levelX, buttonY, levelX + 1, buttonY + STAT_BUTTON_SIZE, levelBorder);
        graphics.fill(levelX + levelBoxWidth - 1, buttonY, levelX + levelBoxWidth, buttonY + STAT_BUTTON_SIZE,
                levelBorder);

        int levelTextX = levelX + (levelBoxWidth - this.font.width(levelText)) / 2;
        int levelTextY = buttonY + (STAT_BUTTON_SIZE - this.font.lineHeight) / 2;
        graphics.drawString(this.font, Component.literal(levelText), levelTextX, levelTextY,
                UnbelievableTheme.TEXT_SELECTED, false);

        return new CharacterStatRowHitbox(
                stat,
                x,
                y,
                width,
                rowHeight,
                minusX,
                plusX,
                buttonY,
                STAT_BUTTON_SIZE,
                canDecrease,
                canIncrease);
    }

    private void drawStatButton(GuiGraphics graphics, int x, int y, String symbol, boolean enabled, boolean hovered) {
        int border = enabled ? UnbelievableTheme.BUTTON_BORDER : UnbelievableTheme.BUTTON_BORDER_DISABLED;
        int fill = enabled ? (hovered ? UnbelievableTheme.BUTTON_FILL_HOVER : UnbelievableTheme.BUTTON_FILL)
                : UnbelievableTheme.BUTTON_FILL_DISABLED;
        int textColor = enabled ? UnbelievableTheme.BUTTON_TEXT : UnbelievableTheme.BUTTON_TEXT_DISABLED;
        graphics.fill(x, y, x + STAT_BUTTON_SIZE, y + STAT_BUTTON_SIZE, fill);
        graphics.fill(x, y, x + STAT_BUTTON_SIZE, y + 1, border);
        graphics.fill(x, y + STAT_BUTTON_SIZE - 1, x + STAT_BUTTON_SIZE, y + STAT_BUTTON_SIZE, border);
        graphics.fill(x, y, x + 1, y + STAT_BUTTON_SIZE, border);
        graphics.fill(x + STAT_BUTTON_SIZE - 1, y, x + STAT_BUTTON_SIZE, y + STAT_BUTTON_SIZE, border);
        graphics.fill(x + 1, y + 1, x + STAT_BUTTON_SIZE - 1, y + 2,
                enabled ? UnbelievableTheme.GOLD_FAINT : UnbelievableTheme.PANEL_SHADOW);
        int textX = x + (STAT_BUTTON_SIZE - this.font.width(symbol)) / 2;
        int textY = y + (STAT_BUTTON_SIZE - this.font.lineHeight) / 2;
        graphics.drawString(this.font, Component.literal(symbol), textX, textY, textColor, false);
    }

    private void renderStatTooltip(GuiGraphics graphics, CharacterStat stat, int mouseX, int mouseY) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(stat.name()).getVisualOrderText());
        tooltip.add(Component.literal(stat.description()).getVisualOrderText());
        if (!stat.effects().isEmpty()) {
            tooltip.add(
                    Component.translatable("screen.believable.unbelievable_menu.stats.per_level").getVisualOrderText());
            for (CharacterStatEffect effect : stat.effects()) {
                Stat target = StatRegistry.get(effect.statId());
                String statName = target != null ? target.getName() : effect.statId().getPath();
                String delta = formatSignedValue(effect.perLevel());
                tooltip.add(Component.literal(delta + " " + statName).getVisualOrderText());
            }
        }
        if (this.minecraft != null && this.minecraft.player != null && SmithService.canUse(this.minecraft.player)) {
            PlayerStatStorage statStorage = this.minecraft.player.getData(ModAttachments.PLAYER_STATS);
            if (stat == CharacterStatDefinitions.STRENGTH) {
                double perPoint = SmithService.getDurabilityPerStrength() * 100.0D;
                double total = SmithService.getStrengthDurabilityBonus(statStorage, this.minecraft.player) * 100.0D;
                tooltip.add(Component.literal("Smith Bonus").getVisualOrderText());
                tooltip.add(Component.literal(
                        "+ " + formatStatValue(perPoint) + "% crafted durability per Strength point")
                        .getVisualOrderText());
                tooltip.add(Component.literal("Current: + " + formatStatValue(total) + "% durability")
                        .getVisualOrderText());
            } else if (stat == CharacterStatDefinitions.DEXTERITY) {
                double perPoint = SmithService.getQualityLevelsPerDexterity();
                int total = SmithService.getDexterityQualityBonus(statStorage, this.minecraft.player);
                tooltip.add(Component.literal("Smith Bonus").getVisualOrderText());
                tooltip.add(Component.literal(
                        "+ " + formatStatValue(perPoint) + " quality levels per Dexterity point")
                        .getVisualOrderText());
                tooltip.add(Component.literal("Current: + " + total + " quality levels").getVisualOrderText());
            }
        }
        graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
    }

    private void drawDescriptionText(GuiGraphics graphics, int x, int y, int width, int height, String description) {
        int textX = x + RACE_BOX_PADDING;
        int textY = y + RACE_BOX_PADDING;
        float scale = MAIN_CONTENT_TEXT_SCALE;
        int textWidth = Math.max(0, (int) ((width - RACE_BOX_PADDING * 2) / scale));
        int maxLines = Math.max(0, (int) ((height - RACE_BOX_PADDING * 2) / (this.font.lineHeight * scale)));
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(
                Component.literal(description),
                textWidth);
        drawWrappedLinesScaled(graphics, lines, textX, textY, maxLines, UnbelievableTheme.TEXT_MUTED, 0, scale);
    }

    @Override
    public net.minecraft.client.gui.Font font() {
        return this.font;
    }

    @Override
    public PanelLayout panelLayout() {
        return getPanelLayout();
    }

    @Override
    public int panelPadding() {
        return PANEL_PADDING;
    }

    @Override
    public int panelGap() {
        return CLASS_SELECTION_PANEL_GAP;
    }

    @Override
    public int buttonHeight() {
        return RACE_BUTTON_HEIGHT;
    }

    @Override
    public int buttonGap() {
        return CLASS_SELECTION_BUTTON_GAP;
    }

    @Override
    public int listButtonGap() {
        return CLASS_LIST_BUTTON_GAP;
    }

    @Override
    public void renderTabTitle(GuiGraphics graphics, int x, int y, Component label) {
        drawTabTitle(graphics, x, y, label);
    }

    @Override
    public void renderSectionHeader(GuiGraphics graphics, Component label, int x, int y) {
        drawSectionHeader(graphics, label, x, y);
    }

    @Override
    public void renderInfoBox(GuiGraphics graphics, int x, int y, int width, int height) {
        drawInfoBox(graphics, x, y, width, height);
    }

    @Override
    public int settingsButtonHeight() {
        return SETTINGS_BUTTON_HEIGHT;
    }

    @Override
    public int settingsButtonGap() {
        return SETTINGS_BUTTON_GAP;
    }

    @Override
    public int settingsBoxGap() {
        return SETTINGS_BOX_GAP;
    }

    @Override
    public PlayerCartographyStorage cartographyStorage() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return null;
        }
        return this.minecraft.player.getData(ModAttachments.PLAYER_CARTOGRAPHY);
    }

    @Override
    public LocalPlayer player() {
        return this.minecraft == null ? null : this.minecraft.player;
    }

    @Override
    public boolean isCartographer() {
        PlayerAttachmentStorage storage = getPlayerStorage();
        if (storage == null || storage.getPlayerClass() == null) {
            return false;
        }
        return ClassDefinitions.CARTOGRAPHER.name().equals(storage.getPlayerClass().name());
    }

    @Override
    public void sendWaypointAdd() {
        sendWaypointButton(UnbelievableMenu.BUTTON_WAYPOINT_ADD);
    }

    @Override
    public void sendWaypointToggle(int index) {
        sendWaypointButton(UnbelievableMenu.BUTTON_WAYPOINT_TOGGLE_BASE + index);
    }

    @Override
    public void sendWaypointMove(int index) {
        sendWaypointButton(UnbelievableMenu.BUTTON_WAYPOINT_MOVE_BASE + index);
    }

    @Override
    public void sendWaypointDelete(int index) {
        sendWaypointButton(UnbelievableMenu.BUTTON_WAYPOINT_DELETE_BASE + index);
    }

    @Override
    public void sendWaypointRename(int index, String name) {
        if (index < 0 || name == null) {
            return;
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        if (trimmed.length() > CartographerService.MAX_WAYPOINT_NAME_LENGTH) {
            trimmed = trimmed.substring(0, CartographerService.MAX_WAYPOINT_NAME_LENGTH);
        }
        PacketDistributor.sendToServer(new RenameWaypointPayload(index, trimmed));
    }

    @Override
    public void renderDescriptionText(GuiGraphics graphics, int x, int y, int width, int height, String description) {
        drawDescriptionText(graphics, x, y, width, height, description);
    }

    @Override
    public void addTooltip(int x, int y, int width, int height, Component description) {
        addClassSelectionTooltip(x, y, width, height, description);
    }

    @Override
    public void rebuildTabs() {
        rebuildTabButtons();
    }

    @Override
    public PlayerAttachmentStorage playerStorage() {
        return getPlayerStorage();
    }

    @Override
    public boolean classSelectionActive() {
        return isClassSelectionActive();
    }

    @Override
    public ClassSelectionCategory selectedCategory() {
        return selectedClassCategory;
    }

    @Override
    public void setSelectedCategory(ClassSelectionCategory category) {
        selectedClassCategory = category;
    }

    @Override
    public BasicClass pendingClass() {
        return pendingClass;
    }

    @Override
    public void setPendingClass(BasicClass pendingClass, String pendingClassKey) {
        this.pendingClass = pendingClass;
        this.pendingClassKey = pendingClassKey;
    }

    @Override
    public void clearPendingClass() {
        this.pendingClass = null;
        this.pendingClassKey = null;
    }

    @Override
    public String detailText() {
        return getClassSelectionDetailText();
    }

    @Override
    public int raceButtonHeight() {
        return RACE_BUTTON_HEIGHT;
    }

    @Override
    public int raceSectionGap() {
        return RACE_SECTION_GAP;
    }

    @Override
    public boolean characterTabSelected() {
        return isCharacterTabSelected();
    }

    @Override
    public boolean raceSelectionActive() {
        return isRaceSelectionActive();
    }

    @Override
    public boolean raceInfoTabSelected() {
        return false;
    }

    @Override
    public String selectedRaceKey() {
        return getSelectedRaceKey();
    }

    @Override
    public void renderStatModifierText(GuiGraphics graphics, int x, int y, int width, int height,
            List<StatModifier> modifiers, String emptyKey) {
        drawStatModifierText(graphics, x, y, width, height, modifiers, emptyKey);
    }

    @Override
    public void renderModifierText(GuiGraphics graphics, int x, int y, int width, int height, Race race) {
        drawModifierText(graphics, x, y, width, height, race);
    }

    @Override
    public String statModifierLabel(List<StatModifier> modifiers, String emptyKey) {
        return getStatModifierLabel(modifiers, emptyKey);
    }

    @Override
    public String raceModifierLabel(Race race) {
        return getModifierLabel(race);
    }

    @Override
    public void selectRace(String raceKey, BasicRace race) {
        applyRaceSelection(raceKey, race);
    }

    @Override
    public int magicSelectButtonHeight() {
        return MAGIC_SELECT_BUTTON_HEIGHT;
    }

    @Override
    public float magicDescriptionHeaderScale() {
        return MAGIC_DESCRIPTION_HEADER_SCALE;
    }

    @Override
    public boolean magicTabSelected() {
        return isMagicTabSelected();
    }

    @Override
    public boolean magicSelectionActive() {
        return isMagicSelectionActive();
    }

    @Override
    public String selectedMagicKey() {
        return getSelectedMagicKey();
    }

    @Override
    public String selectedMagicSubKey() {
        if (!isMagicTabSelected() || this.selectedPath.size() < 2) {
            return null;
        }
        return this.selectedPath.get(1).translationKey;
    }

    @Override
    public void renderGrandSectionHeader(GuiGraphics graphics, Component label, int x, int y) {
        drawGrandSectionHeader(graphics, label, x, y);
    }

    @Override
    public void renderSpellList(GuiGraphics graphics, int x, int y, int width, int height, List<Spell> spells,
            Component emptyLabel) {
        drawSpellList(graphics, x, y, width, height, spells, emptyLabel);
    }

    @Override
    public void selectMagicSchool(String magicKey, BasicMagicSchool school) {
        applyMagicSelection(magicKey, school);
    }

    @Override
    public int raceBoxPadding() {
        return RACE_BOX_PADDING;
    }

    @Override
    public float mainContentTextScale() {
        return MAIN_CONTENT_TEXT_SCALE;
    }

    @Override
    public int mainContentLineGap() {
        return MAIN_CONTENT_LINE_GAP;
    }

    @Override
    public int skillEquipButtonWidth() {
        return SKILL_EQUIP_BUTTON_WIDTH;
    }

    @Override
    public int skillEquipButtonHeight() {
        return SKILL_EQUIP_BUTTON_HEIGHT;
    }

    @Override
    public int skillEquipButtonGap() {
        return SKILL_EQUIP_BUTTON_GAP;
    }

    @Override
    public int skillsPassiveSlots() {
        return PlayerAttachmentStorage.PASSIVE_SKILL_SLOTS;
    }

    @Override
    public int skillsActiveSlots() {
        return PlayerAttachmentStorage.ACTIVE_SKILL_SLOTS;
    }

    @Override
    public boolean skillsTabSelected() {
        return isSkillsTabSelected();
    }

    @Override
    public boolean skillsPassiveTabSelected() {
        return isSkillsPassiveTabSelected();
    }

    @Override
    public boolean skillsActiveTabSelected() {
        return isSkillsActiveTabSelected();
    }

    @Override
    public String selectedSkillsSubKey() {
        if (!isSkillsTabSelected() || this.selectedPath.size() < 2) {
            return null;
        }
        return this.selectedPath.get(1).translationKey;
    }

    @Override
    public Component skillNameComponent(String skillId) {
        return getSkillNameComponent(skillId);
    }

    @Override
    public int drawWrappedLinesScaledHost(GuiGraphics graphics,
            List<FormattedCharSequence> lines,
            int x,
            int y,
            int maxLines,
            int color,
            int wrappedIndent,
            float scale) {
        return drawWrappedLinesScaled(graphics, lines, x, y, maxLines, color, wrappedIndent, scale);
    }

    @Override
    public void equipPassiveSkill(int slotIndex, int skillIndex) {
        sendPassiveSkillEquipToServer(slotIndex, skillIndex);
    }

    @Override
    public void equipActiveSkill(int slotIndex, int skillIndex) {
        sendActiveSkillEquipToServer(slotIndex, skillIndex);
    }

    @Override
    public void addSkillTooltip(int x, int y, int width, int height, Component description) {
        addSkillTooltipInternal(x, y, width, height, description);
    }

    @Override
    public int skillsScroll() {
        return skillsScroll;
    }

    @Override
    public void setSkillsScroll(int scroll) {
        this.skillsScroll = scroll;
    }

    @Override
    public int selectedSkillSlot() {
        return isSkillsActiveTabSelected() ? activeSkillSlotSelection : passiveSkillSlotSelection;
    }

    @Override
    public void setSelectedSkillSlot(int slotIndex) {
        if (isSkillsActiveTabSelected()) {
            activeSkillSlotSelection = slotIndex;
        } else {
            passiveSkillSlotSelection = slotIndex;
        }
    }

    @Override
    public SkillCategory selectedSkillCategory() {
        return isSkillsActiveTabSelected() ? activeSkillCategorySelection : passiveSkillCategorySelection;
    }

    @Override
    public void setSelectedSkillCategory(SkillCategory category) {
        if (isSkillsActiveTabSelected()) {
            activeSkillCategorySelection = category;
        } else {
            passiveSkillCategorySelection = category;
        }
    }

    private void drawSpellList(GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            List<Spell> spells,
            Component emptyLabel) {
        int textX = x + RACE_BOX_PADDING;
        int textY = y + RACE_BOX_PADDING;
        float scale = MAIN_CONTENT_TEXT_SCALE;
        int textWidth = Math.max(0, (int) ((width - RACE_BOX_PADDING * 2) / scale));
        int maxLines = Math.max(0, (int) ((height - RACE_BOX_PADDING * 2) / (this.font.lineHeight * scale)));
        int linesLeft = maxLines;

        if (spells.isEmpty()) {
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(emptyLabel, textWidth);
            drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED, 0, scale);
            return;
        }

        for (Spell spell : spells) {
            if (linesLeft <= 0) {
                return;
            }
            String line = "- " + spell.name() + ": " + spell.description();
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(
                    Component.literal(line),
                    textWidth);
            int drawn = drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED,
                    10, scale);
            linesLeft -= drawn;
            textY += Math.round(drawn * (this.font.lineHeight + MAIN_CONTENT_LINE_GAP) * scale);
        }
    }

    private void drawModifierText(GuiGraphics graphics, int x, int y, int width, int height, Race race) {
        int textX = x + RACE_BOX_PADDING;
        int textY = y + RACE_BOX_PADDING;
        float scale = MAIN_CONTENT_TEXT_SCALE;
        int textWidth = Math.max(0, (int) ((width - RACE_BOX_PADDING * 2) / scale));
        int maxLines = Math.max(0, (int) ((height - RACE_BOX_PADDING * 2) / (this.font.lineHeight * scale)));
        int linesLeft = maxLines;

        if (race.modifiers().isEmpty()) {
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(
                    Component.translatable("screen.believable.unbelievable_menu.race.none"),
                    textWidth);
            drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED, 0, scale);
            return;
        }

        for (RaceModifier modifier : race.modifiers()) {
            if (linesLeft <= 0) {
                return;
            }
            String line = "- " + modifier.getName() + ": " + modifier.getDescription();
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(
                    Component.literal(line),
                    textWidth);
            int drawn = drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED,
                    10, scale);
            linesLeft -= drawn;
            textY += Math.round(drawn * (this.font.lineHeight + MAIN_CONTENT_LINE_GAP) * scale);
        }
    }

    private void drawStatModifierText(GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            List<StatModifier> modifiers,
            String emptyKey) {
        int textX = x + RACE_BOX_PADDING;
        int textY = y + RACE_BOX_PADDING;
        float scale = MAIN_CONTENT_TEXT_SCALE;
        int textWidth = Math.max(0, (int) ((width - RACE_BOX_PADDING * 2) / scale));
        int maxLines = Math.max(0, (int) ((height - RACE_BOX_PADDING * 2) / (this.font.lineHeight * scale)));
        int linesLeft = maxLines;

        if (modifiers.isEmpty()) {
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(
                    Component.translatable(emptyKey),
                    textWidth);
            drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED, 0, scale);
            return;
        }

        for (StatModifier modifier : modifiers) {
            if (linesLeft <= 0) {
                return;
            }
            String line = "- " + getStatModifierName(modifier) + ": " + formatSignedValue(modifier.amount());
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(
                    Component.literal(line),
                    textWidth);
            int drawn = drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED,
                    10, scale);
            linesLeft -= drawn;
            textY += Math.round(drawn * (this.font.lineHeight + MAIN_CONTENT_LINE_GAP) * scale);
        }
    }

    private void drawRaceStatsText(GuiGraphics graphics, int x, int y, int width, int height, Race race) {
        int textX = x + RACE_BOX_PADDING;
        int textY = y + RACE_BOX_PADDING;
        float scale = MAIN_CONTENT_TEXT_SCALE;
        int textWidth = Math.max(0, (int) ((width - RACE_BOX_PADDING * 2) / scale));
        int maxLines = Math.max(0, (int) ((height - RACE_BOX_PADDING * 2) / (this.font.lineHeight * scale)));
        int linesLeft = maxLines;

        String hpLine = "- "
                + Component.translatable("screen.believable.unbelievable_menu.race.hp", race.hp()).getString();
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(Component.literal(hpLine), textWidth);
        int drawn = drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED, 10,
                scale);
        linesLeft -= drawn;
        textY += Math.round(drawn * (this.font.lineHeight + MAIN_CONTENT_LINE_GAP) * scale);

        for (StatModifier modifier : race.statModifiers()) {
            if (linesLeft <= 0) {
                return;
            }
            String line = "- " + getStatModifierName(modifier) + ": " + formatSignedValue(modifier.amount());
            List<net.minecraft.util.FormattedCharSequence> modifierLines = this.font.split(
                    Component.literal(line),
                    textWidth);
            int modifierDrawn = drawWrappedLinesScaled(graphics, modifierLines, textX, textY, linesLeft,
                    UnbelievableTheme.TEXT_MUTED, 10, scale);
            linesLeft -= modifierDrawn;
            textY += Math.round(modifierDrawn * (this.font.lineHeight + MAIN_CONTENT_LINE_GAP) * scale);
        }
    }

    private void drawClassModifierText(GuiGraphics graphics, int x, int y, int width, int height,
            BasicClass playerClass) {
        int textX = x + RACE_BOX_PADDING;
        int textY = y + RACE_BOX_PADDING;
        float scale = MAIN_CONTENT_TEXT_SCALE;
        int textWidth = Math.max(0, (int) ((width - RACE_BOX_PADDING * 2) / scale));
        int maxLines = Math.max(0, (int) ((height - RACE_BOX_PADDING * 2) / (this.font.lineHeight * scale)));
        int linesLeft = maxLines;

        if (playerClass.modifiers().isEmpty()) {
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(
                    Component.translatable(CLASS_NONE_KEY),
                    textWidth);
            drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED, 0, scale);
            return;
        }

        for (ClassModifier modifier : playerClass.modifiers()) {
            if (linesLeft <= 0) {
                return;
            }
            String line = "- " + modifier.getName() + ": " + modifier.getDescription();
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(
                    Component.literal(line),
                    textWidth);
            int drawn = drawWrappedLinesScaled(graphics, lines, textX, textY, linesLeft, UnbelievableTheme.TEXT_MUTED,
                    10, scale);
            linesLeft -= drawn;
            textY += Math.round(drawn * (this.font.lineHeight + MAIN_CONTENT_LINE_GAP) * scale);
        }
    }

    private void renderClassSelectionTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isClassSelectionActive() || this.classSelectionTooltips.isEmpty()) {
            return;
        }
        for (ClassSelectionTooltip tooltip : this.classSelectionTooltips) {
            if (tooltip.isHovered(mouseX, mouseY)) {
                graphics.renderTooltip(this.font, tooltip.lines(), mouseX, mouseY);
                break;
            }
        }
    }

    private void renderSkillTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isSkillsTabSelected() || this.skillTooltips.isEmpty()) {
            return;
        }
        for (SkillTooltip tooltip : this.skillTooltips) {
            if (tooltip.isHovered(mouseX, mouseY)) {
                graphics.renderTooltip(this.font, tooltip.lines(), mouseX, mouseY);
                break;
            }
        }
    }

    private void addClassSelectionTooltip(int x, int y, int width, int height, Component description) {
        if (description == null) {
            return;
        }
        List<FormattedCharSequence> lines = this.font.split(description, CLASS_TOOLTIP_WIDTH);
        if (lines.isEmpty()) {
            return;
        }
        this.classSelectionTooltips.add(new ClassSelectionTooltip(x, y, width, height, lines));
    }

    private void addSkillTooltipInternal(int x, int y, int width, int height, Component description) {
        if (description == null) {
            return;
        }
        List<FormattedCharSequence> lines = this.font.split(description, SKILL_TOOLTIP_WIDTH);
        if (lines.isEmpty()) {
            return;
        }
        this.skillTooltips.add(new SkillTooltip(x, y, width, height, lines));
    }

    private String getClassSelectionDetailText() {
        if (pendingClass != null) {
            return pendingClass.name() + ": " + pendingClass.description();
        }
        if (selectedClassCategory != null) {
            String name = Component.translatable(selectedClassCategory.labelKey()).getString();
            String description = Component.translatable(selectedClassCategory.descriptionKey()).getString();
            return name + ": " + description;
        }
        return Component.translatable(CLASS_QUESTION_HINT_KEY).getString();
    }

    @Override
    public void confirmPendingClass() {
        if (pendingClass == null || pendingClassKey == null) {
            return;
        }
        selectClass(pendingClassKey, pendingClass);
    }

    private int drawWrappedLines(GuiGraphics graphics, List<net.minecraft.util.FormattedCharSequence> lines, int x,
            int y, int maxLines, int color, int wrappedIndent) {
        int lineHeight = this.font.lineHeight;
        int limit = Math.min(maxLines, lines.size());
        for (int i = 0; i < limit; i++) {
            int drawX = x + (i == 0 ? 0 : wrappedIndent);
            graphics.drawString(this.font, lines.get(i), drawX, y + i * lineHeight, color, false);
        }
        return limit;
    }

    private int drawWrappedLinesScaled(GuiGraphics graphics,
            List<net.minecraft.util.FormattedCharSequence> lines,
            int x,
            int y,
            int maxLines,
            int color,
            int wrappedIndent,
            float scale) {
        int lineHeight = this.font.lineHeight + MAIN_CONTENT_LINE_GAP;
        int limit = Math.min(maxLines, lines.size());
        int indent = Math.round(wrappedIndent / scale);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        for (int i = 0; i < limit; i++) {
            int drawX = i == 0 ? 0 : indent;
            graphics.drawString(this.font, lines.get(i), drawX, i * lineHeight, color, false);
        }
        graphics.pose().popPose();
        return limit;
    }

    private void applyRaceSelection(String raceKey, BasicRace race) {
        PlayerAttachmentStorage storage = getPlayerStorage();
        if (storage != null) {
            storage.setRace(race);
        }
        sendRaceSelectionToServer(raceKey);
        refreshTabs(CHARACTER_TAB_KEY, null);
    }

    private void selectClass(String classKey, BasicClass playerClass) {
        PlayerAttachmentStorage storage = getPlayerStorage();
        if (storage != null) {
            storage.setPlayerClass(playerClass);
        }
        sendClassSelectionToServer(classKey);
        selectedClassCategory = null;
        pendingClass = null;
        pendingClassKey = null;
        refreshTabs(CHARACTER_TAB_KEY, STATS_TAB_KEY);
    }

    private void applyMagicSelection(String magicKey, BasicMagicSchool school) {
        PlayerAttachmentStorage storage = getPlayerStorage();
        if (storage != null) {
            storage.setMagicSchool(school);
        }
        sendMagicSelectionToServer(magicKey);
        refreshTabs(MAGIC_TAB_KEY, null);
    }

    private void sendRaceSelectionToServer(String raceKey) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        int buttonId = switch (raceKey) {
            case RaceDefinitions.TAB_GOAT_KEY -> UnbelievableMenu.BUTTON_SELECT_GOAT;
            case RaceDefinitions.TAB_CAT_KEY -> UnbelievableMenu.BUTTON_SELECT_CAT;
            case RaceDefinitions.TAB_FENNEC_KEY -> UnbelievableMenu.BUTTON_SELECT_FENNEC;
            case RaceDefinitions.TAB_CANID_KEY -> UnbelievableMenu.BUTTON_SELECT_CANID;
            case RaceDefinitions.TAB_PROTOGEN_KEY -> UnbelievableMenu.BUTTON_SELECT_PROTOGEN;
            default -> -1;
        };
        if (buttonId >= 0) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    private void sendClassSelectionToServer(String classKey) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        int buttonId = switch (classKey) {
            case ClassDefinitions.TAB_ARTIFICER_KEY -> UnbelievableMenu.BUTTON_SELECT_ARTIFICER;
            case ClassDefinitions.TAB_SMITH_KEY -> UnbelievableMenu.BUTTON_SELECT_SMITH;
            case ClassDefinitions.TAB_MAGE_KEY -> UnbelievableMenu.BUTTON_SELECT_MAGE;
            case ClassDefinitions.TAB_ALCHEMIST_KEY -> UnbelievableMenu.BUTTON_SELECT_ALCHEMIST_CLASS;
            case ClassDefinitions.TAB_PROSPECTOR_KEY -> UnbelievableMenu.BUTTON_SELECT_PROSPECTOR;
            case ClassDefinitions.TAB_ARCHITECT_KEY -> UnbelievableMenu.BUTTON_SELECT_ARCHITECT;
            case ClassDefinitions.TAB_WARDEN_KEY -> UnbelievableMenu.BUTTON_SELECT_WARDEN;
            case ClassDefinitions.TAB_CARTOGRAPHER_KEY -> UnbelievableMenu.BUTTON_SELECT_CARTOGRAPHER;
            default -> -1;
        };
        if (buttonId >= 0) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    private void sendMagicSelectionToServer(String magicKey) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        int buttonId = switch (magicKey) {
            case MagicSchoolDefinitions.TAB_ELEMENTALISM_KEY -> UnbelievableMenu.BUTTON_SELECT_ELEMENTALISM;
            case MagicSchoolDefinitions.TAB_SPATIAL_KEY -> UnbelievableMenu.BUTTON_SELECT_SPATIAL;
            case MagicSchoolDefinitions.TAB_TEMPORAL_KEY -> UnbelievableMenu.BUTTON_SELECT_TEMPORAL;
            case MagicSchoolDefinitions.TAB_ONEIROMANCY_KEY -> UnbelievableMenu.BUTTON_SELECT_ONEIROMANCY;
            case MagicSchoolDefinitions.TAB_ELDRITCH_KEY -> UnbelievableMenu.BUTTON_SELECT_ELDRITCH;
            case MagicSchoolDefinitions.TAB_AILMENT_KEY -> UnbelievableMenu.BUTTON_SELECT_AILMENT;
            case MagicSchoolDefinitions.TAB_NECROMANCY_KEY -> UnbelievableMenu.BUTTON_SELECT_NECROMANCY;
            case MagicSchoolDefinitions.TAB_BLESSING_KEY -> UnbelievableMenu.BUTTON_SELECT_BLESSING;
            case MagicSchoolDefinitions.TAB_WITCHERY_KEY -> UnbelievableMenu.BUTTON_SELECT_WITCHERY;
            case MagicSchoolDefinitions.TAB_ALCHEMIST_KEY -> UnbelievableMenu.BUTTON_SELECT_ALCHEMIST;
            default -> -1;
        };
        if (buttonId >= 0) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    private void sendPassiveSkillEquipToServer(int slotIndex, int skillIndex) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        int passiveCount = SkillDefinitions.getPassiveSkills().size();
        if (passiveCount <= 0
                || slotIndex < 0
                || slotIndex >= PlayerAttachmentStorage.PASSIVE_SKILL_SLOTS
                || skillIndex < 0
                || skillIndex >= passiveCount) {
            return;
        }
        int buttonId = UnbelievableMenu.BUTTON_EQUIP_PASSIVE_BASE + slotIndex * passiveCount + skillIndex;
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    private void sendActiveSkillEquipToServer(int slotIndex, int skillIndex) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        int activeCount = SkillDefinitions.getActiveSkills().size();
        if (activeCount <= 0
                || slotIndex < 0
                || slotIndex >= PlayerAttachmentStorage.ACTIVE_SKILL_SLOTS
                || skillIndex < 0
                || skillIndex >= activeCount) {
            return;
        }
        int buttonId = UnbelievableMenu.BUTTON_EQUIP_ACTIVE_BASE + slotIndex * activeCount + skillIndex;
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    private void sendWaypointButton(int buttonId) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    private void sendDropRarityChangeToServer(int delta) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        int buttonId = delta < 0
                ? UnbelievableMenu.BUTTON_DROP_RARITY_DOWN
                : UnbelievableMenu.BUTTON_DROP_RARITY_UP;
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    private void sendStatAdjustment(CharacterStat stat, int delta) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        int index = getCharacterStatIndex(stat);
        if (index < 0) {
            return;
        }
        int buttonId = delta > 0
                ? UnbelievableMenu.BUTTON_STAT_INCREASE_BASE + index
                : UnbelievableMenu.BUTTON_STAT_DECREASE_BASE + index;
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    private int getCharacterStatIndex(CharacterStat stat) {
        int current = 0;
        for (CharacterStat entry : CharacterStatRegistry.getAll()) {
            if (entry == stat) {
                return current;
            }
            current++;
        }
        return -1;
    }

    private boolean hasSelectedRace() {
        PlayerAttachmentStorage storage = getPlayerStorage();
        return storage != null && storage.getRace() != null;
    }

    private BasicMagicSchool getSelectedMagicSchool() {
        PlayerAttachmentStorage storage = getPlayerStorage();
        return storage != null ? storage.getMagicSchool() : null;
    }

    private boolean hasSelectedMagicSchool() {
        PlayerAttachmentStorage storage = getPlayerStorage();
        return storage != null && storage.getMagicSchool() != null;
    }

    private boolean hasSelectedClass() {
        PlayerAttachmentStorage storage = getPlayerStorage();
        return storage != null && storage.getPlayerClass() != null;
    }

    private TabNode findTabByKey(List<TabNode> tabs, String key) {
        if (key == null) {
            return null;
        }
        for (TabNode tab : tabs) {
            if (key.equals(tab.translationKey)) {
                return tab;
            }
        }
        return null;
    }

    private String getModifierLabel(Race race) {
        if (race.modifiers().isEmpty()) {
            return Component.translatable("screen.believable.unbelievable_menu.race.none").getString();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < race.modifiers().size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(race.modifiers().get(i).getName());
        }
        return builder.toString();
    }

    private String getClassModifierLabel(BasicClass playerClass) {
        if (playerClass.modifiers().isEmpty()) {
            return Component.translatable(CLASS_NONE_KEY).getString();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < playerClass.modifiers().size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(playerClass.modifiers().get(i).getName());
        }
        return builder.toString();
    }

    private String getStatModifierLabel(List<StatModifier> modifiers, String emptyKey) {
        if (modifiers.isEmpty()) {
            return Component.translatable(emptyKey).getString();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < modifiers.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            StatModifier modifier = modifiers.get(i);
            builder.append(getStatModifierName(modifier))
                    .append(" ")
                    .append(formatSignedValue(modifier.amount()));
        }
        return builder.toString();
    }

    private String getStatModifierName(StatModifier modifier) {
        Stat stat = StatRegistry.get(modifier.statId());
        return stat != null ? stat.getName() : modifier.statId().toString();
    }

    private Component getSkillNameComponent(String skillId) {
        Skill skill = SkillDefinitions.getById(skillId);
        if (skill == null) {
            return Component.translatable(SKILLS_NONE_KEY);
        }
        return Component.literal(skill.name());
    }

    private PlayerAttachmentStorage getPlayerStorage() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return null;
        }
        return this.minecraft.player.getData(ModAttachments.PLAYER_STORAGE);
    }

    private PlayerProgressionStorage getPlayerProgression() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return null;
        }
        return this.minecraft.player.getData(ModAttachments.PLAYER_PROGRESSION);
    }

    private boolean hasManaUnlocked() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return false;
        }
        PlayerStatStorage statStorage = this.minecraft.player.getData(ModAttachments.PLAYER_STATS);
        if (statStorage == null) {
            return false;
        }
        CharacterStat attunement = CharacterStatDefinitions.ATTUNEMENT;
        double level = StatValueCalculator.getCharacterStatLevel(attunement, statStorage, this.minecraft.player);
        return level > attunement.defaultValue();
    }

    private PlayerDiscoveryStorage getPlayerDiscovery() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return null;
        }
        return this.minecraft.player.getData(ModAttachments.PLAYER_DISCOVERY);
    }

    private int getWaypointHash(PlayerCartographyStorage storage) {
        if (storage == null) {
            return 0;
        }
        int hash = 1;
        for (CartographyWaypoint waypoint : storage.getWaypoints()) {
            hash = 31 * hash + (waypoint.name() == null ? 0 : waypoint.name().hashCode());
            hash = 31 * hash + waypoint.dimension().hashCode();
            hash = 31 * hash + Long.hashCode(waypoint.position().asLong());
            hash = 31 * hash + Boolean.hashCode(waypoint.enabled());
        }
        return hash;
    }

    private Component getBiomeLabel(ResourceLocation biomeId) {
        return Component.translatable(biomeId.toLanguageKey("biome"));
    }

    private void invalidateLayoutCaches() {
        layoutVersion++;
        cachedPanelLayout = null;
        cachedAttributesLayoutKey = null;
        cachedAttributesLayout = null;
        cachedCharacterStatsLayoutKey = null;
        cachedCharacterStatsLayout = null;
    }

    private StatsLayout getAttributesLayout(PanelLayout layout, PlayerStatStorage statStorage, int textX, int titleY) {
        AttributesLayoutKey key = new AttributesLayoutKey(this.width, this.height, layoutVersion,
                StatRegistry.getAll().size());
        if (cachedAttributesLayout != null && key.equals(cachedAttributesLayoutKey)) {
            return cachedAttributesLayout;
        }
        StatsLayout statsLayout = StatsLayoutBuilder.buildAttributesLayout(
                layout,
                StatRegistry.getAll(),
                statStorage,
                this.minecraft.player,
                this.font,
                textX,
                titleY,
                STAT_CARD_TWO_COLUMN_MIN_WIDTH,
                STAT_CARD_GAP,
                STAT_CARD_PADDING,
                STAT_CARD_HEADER_HEIGHT,
                STAT_VALUE_GAP,
                PANEL_PADDING,
                this::formatStatValue);
        cachedAttributesLayoutKey = key;
        cachedAttributesLayout = statsLayout;
        return statsLayout;
    }

    private CharacterStatsLayout getCharacterStatsLayout(PanelLayout layout, int textX, int titleY) {
        CharacterStatsLayoutKey key = new CharacterStatsLayoutKey(
                this.width,
                this.height,
                layoutVersion,
                CharacterStatRegistry.getAll().size());
        if (cachedCharacterStatsLayout != null && key.equals(cachedCharacterStatsLayoutKey)) {
            return cachedCharacterStatsLayout;
        }
        CharacterStatsLayout statsLayout = StatsLayoutBuilder.buildCharacterStatsLayout(
                layout,
                this.font,
                textX,
                titleY,
                STAT_HEADER_HEIGHT,
                STAT_ROW_HEIGHT,
                STAT_ROW_GAP,
                PANEL_PADDING);
        cachedCharacterStatsLayoutKey = key;
        cachedCharacterStatsLayout = statsLayout;
        return statsLayout;
    }

    private PanelLayout getPanelLayout() {
        if (cachedPanelLayout != null
                && cachedLayoutWidth == this.width
                && cachedLayoutHeight == this.height
                && cachedLayoutVersion == layoutVersion) {
            return cachedPanelLayout;
        }
        int panelX = OUTER_INSET + INNER_INSET;
        int panelY = OUTER_INSET + HEADER_HEIGHT;
        int panelW = this.width - (OUTER_INSET + INNER_INSET) * 2;
        int panelH = this.height - OUTER_INSET * 2 - HEADER_HEIGHT - FOOTER_HEIGHT;
        int columnY = panelY + PANEL_PADDING;
        int columnH = Math.max(0, panelH - PANEL_PADDING * 2);
        int sectionX = panelX + PANEL_PADDING;
        int sectionY = columnY;
        int sectionW = SECTION_WIDTH;
        int sectionH = columnH;
        int subsectionX = sectionX + sectionW + COLUMN_GAP;
        int subsectionY = columnY;
        int subsectionW = SUBSECTION_WIDTH;
        if (!this.selectedPath.isEmpty()) {
            TabNode root = this.selectedPath.get(0);
            subsectionW = getTabColumnWidth(root.children, SUBSECTION_WIDTH);
        }
        int subsectionH = columnH;
        int contentX = subsectionX + subsectionW + COLUMN_GAP;
        int contentY = columnY;
        int contentW = Math.max(0, panelX + panelW - PANEL_PADDING - contentX);
        int contentH = columnH;
        PanelLayout layout = new PanelLayout(
                panelX,
                panelY,
                panelW,
                panelH,
                sectionX,
                sectionY,
                sectionW,
                sectionH,
                subsectionX,
                subsectionY,
                subsectionW,
                subsectionH,
                contentX,
                contentY,
                contentW,
                contentH);
        cachedPanelLayout = layout;
        cachedLayoutWidth = this.width;
        cachedLayoutHeight = this.height;
        cachedLayoutVersion = layoutVersion;
        return layout;
    }

    private int getMaxScroll(int items, int availableHeight) {
        int contentHeight = items * (TAB_HEIGHT + TAB_GAP) - TAB_GAP;
        int max = contentHeight - (availableHeight - PANEL_PADDING * 2);
        return Math.max(0, max);
    }

    private int getTabColumnWidth(List<TabNode> tabs, int maxWidth) {
        if (tabs.isEmpty()) {
            return maxWidth;
        }
        int widest = 0;
        for (TabNode tab : tabs) {
            widest = Math.max(widest, this.font.width(tab.label));
        }
        return Mth.clamp(widest + 18, TAB_COLUMN_MIN_WIDTH, maxWidth);
    }

    private int getAttributesMaxScroll(PanelLayout layout) {
        if (!isAttributesTabSelected() || this.minecraft == null || this.minecraft.player == null) {
            return 0;
        }
        PlayerStatStorage statStorage = this.minecraft.player.getData(ModAttachments.PLAYER_STATS);
        if (statStorage == null || StatRegistry.getAll().isEmpty()) {
            return 0;
        }
        int textX = layout.contentX() + PANEL_PADDING;
        int titleY = layout.contentY() + 10;
        StatsLayout statsLayout = getAttributesLayout(layout, statStorage, textX, titleY);
        return statsLayout.maxScroll();
    }

    private int getStatsMaxScroll(PanelLayout layout) {
        if (!isStatsTabSelected() || this.minecraft == null || this.minecraft.player == null) {
            return 0;
        }
        if (CharacterStatRegistry.getAll().isEmpty()) {
            return 0;
        }
        int textX = layout.contentX() + PANEL_PADDING;
        int titleY = layout.contentY() + 10;
        CharacterStatsLayout statsLayout = getCharacterStatsLayout(layout, textX, titleY);
        return statsLayout.maxScroll();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isStatsTabSelected()) {
            for (CharacterStatRowHitbox hitbox : this.statRowHitboxes) {
                if (hitbox.isMinusHovered(mouseX, mouseY) && hitbox.canDecrease) {
                    sendStatAdjustment(hitbox.stat, -1);
                    return true;
                }
                if (hitbox.isPlusHovered(mouseX, mouseY) && hitbox.canIncrease) {
                    sendStatAdjustment(hitbox.stat, 1);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getFocused() instanceof EditBox editBox && editBox.canConsumeInput()) {
            if (editBox.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (this.minecraft != null && this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.getFocused() instanceof EditBox editBox && editBox.canConsumeInput()) {
            if (editBox.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    private record CharacterStatRowHitbox(CharacterStat stat, int rowX, int rowY, int rowW, int rowH, int minusX,
            int plusX, int buttonY, int buttonSize, boolean canDecrease,
            boolean canIncrease) {

        private boolean isRowHovered(double mouseX, double mouseY) {
            return mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= rowY && mouseY <= rowY + rowH;
        }

        private boolean isMinusHovered(double mouseX, double mouseY) {
            return mouseX >= minusX && mouseX <= minusX + buttonSize
                    && mouseY >= buttonY && mouseY <= buttonY + buttonSize;
        }

        private boolean isPlusHovered(double mouseX, double mouseY) {
            return mouseX >= plusX && mouseX <= plusX + buttonSize
                    && mouseY >= buttonY && mouseY <= buttonY + buttonSize;
        }
    }

    private record AttributesLayoutKey(int width, int height, int layoutVersion, int statCount) {
    }

    private record CharacterStatsLayoutKey(int width, int height, int layoutVersion, int statCount) {
    }

    public enum ClassSelectionCategory {
        UTILITY(
                CLASS_PATH_UTILITY_KEY,
                CLASS_PATH_UTILITY_DESC_KEY,
                List.of(
                        ClassDefinitions.TAB_PROSPECTOR_KEY,
                        ClassDefinitions.TAB_CARTOGRAPHER_KEY,
                        ClassDefinitions.TAB_ARCHITECT_KEY)),
        MAGIC(
                CLASS_PATH_MAGIC_KEY,
                CLASS_PATH_MAGIC_DESC_KEY,
                List.of(
                        ClassDefinitions.TAB_MAGE_KEY,
                        ClassDefinitions.TAB_WARDEN_KEY)),
        INVENTION(
                CLASS_PATH_INVENTION_KEY,
                CLASS_PATH_INVENTION_DESC_KEY,
                List.of(
                        ClassDefinitions.TAB_ARTIFICER_KEY,
                        ClassDefinitions.TAB_SMITH_KEY,
                        ClassDefinitions.TAB_ALCHEMIST_KEY));

        private final String labelKey;
        private final String descriptionKey;
        private final List<String> classTabKeys;

        ClassSelectionCategory(String labelKey, String descriptionKey, List<String> classTabKeys) {
            this.labelKey = labelKey;
            this.descriptionKey = descriptionKey;
            this.classTabKeys = classTabKeys;
        }

        public String labelKey() {
            return labelKey;
        }

        public String descriptionKey() {
            return descriptionKey;
        }

        public List<String> classTabKeys() {
            return classTabKeys;
        }
    }

    private record ClassSelectionTooltip(int x, int y, int width, int height, List<FormattedCharSequence> lines) {
        private boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }

    private record SkillTooltip(int x, int y, int width, int height, List<FormattedCharSequence> lines) {
        private boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        PanelLayout layout = getPanelLayout();
        if (isWithin(mouseX, mouseY, layout.sectionX(), layout.sectionY(), layout.sectionW(), layout.sectionH())) {
            int maxScroll = getMaxScroll(this.rootTabs.size(), layout.sectionH());
            this.sectionScroll = Mth.clamp(this.sectionScroll - (int) (deltaY * (TAB_HEIGHT + TAB_GAP)), 0, maxScroll);
            rebuildTabButtons();
            return true;
        }
        if (isWithin(mouseX, mouseY, layout.subsectionX(), layout.subsectionY(), layout.subsectionW(),
                layout.subsectionH()) && !this.selectedPath.isEmpty()) {
            TabNode root = this.selectedPath.get(0);
            int maxScroll = getMaxScroll(root.children.size(), layout.subsectionH());
            this.subsectionScroll = Mth.clamp(this.subsectionScroll - (int) (deltaY * (TAB_HEIGHT + TAB_GAP)), 0,
                    maxScroll);
            rebuildTabButtons();
            return true;
        }
        if (isIndexBiomeTabSelected()
                && isWithin(mouseX, mouseY, layout.contentX(), layout.contentY(), layout.contentW(),
                        layout.contentH())) {
            int maxScroll = getIndexBiomeMaxScroll(layout);
            if (maxScroll > 0) {
                int step = this.font.lineHeight + 6;
                this.indexScroll = Mth.clamp(this.indexScroll - (int) (deltaY * step), 0, maxScroll);
                return true;
            }
        }
        if (isSkillsPassiveTabSelected() || isSkillsActiveTabSelected()) {
            SkillsSection.SkillListBounds bounds = skillsSection.getSkillListBounds(this, layout);
            if (bounds != null
                    && isWithin(mouseX, mouseY, bounds.boxX(), bounds.boxY(), bounds.boxWidth(), bounds.boxHeight())) {
                int maxScroll = bounds.maxScroll();
                if (maxScroll > 0) {
                    int step = bounds.scrollStep();
                    int nextScroll = Mth.clamp(this.skillsScroll - (int) (deltaY * step), 0, maxScroll);
                    if (nextScroll != this.skillsScroll) {
                        this.skillsScroll = nextScroll;
                        rebuildContentButtons();
                    }
                    return true;
                }
            }
        }
        if ((isStatsTabSelected() || isAttributesTabSelected())
                && isWithin(mouseX, mouseY, layout.contentX(), layout.contentY(), layout.contentW(),
                        layout.contentH())) {
            int maxScroll = isStatsTabSelected() ? getStatsMaxScroll(layout) : getAttributesMaxScroll(layout);
            if (maxScroll > 0) {
                int step = this.font.lineHeight + 6;
                this.statsScroll = Mth.clamp(this.statsScroll - (int) (deltaY * step), 0, maxScroll);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    private boolean isWithin(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
