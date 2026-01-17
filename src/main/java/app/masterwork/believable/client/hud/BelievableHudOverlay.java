package app.masterwork.believable.client.hud;

import app.masterwork.believable.registry.ModAttachments;
import app.masterwork.believable.classes.cartographer.client.hud.CartographerHudOverlay;
import app.masterwork.believable.stats.LevelingRules;
import app.masterwork.believable.stats.PlayerProgressionStorage;
import app.masterwork.believable.stats.PlayerStatStorage;
import app.masterwork.believable.stats.PlayerVeinMinerStorage;
import app.masterwork.believable.classes.prospector.PlayerProspectorStorage;
import app.masterwork.believable.classes.prospector.ProspectorService;
import app.masterwork.believable.stats.StatDefinitions;
import app.masterwork.believable.stats.StatValueCalculator;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Locale;

public final class BelievableHudOverlay {
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");
    private static final ResourceLocation ARMOR_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_half");
    private static final UiBar HEALTH_BAR = new UiBar(
        81,
        9,
        0xFF202020,
        0xFFC04040,
        0xFF000000,
        0xFFFFFFFF
    );
    private static final UiBar HUNGER_BAR = new UiBar(
        81,
        9,
        0xFF202020,
        0xFFF0C000,
        0xFF000000,
        0xFFFFFFFF
    );
    private static final UiBar OXYGEN_BAR = new UiBar(
        81,
        9,
        0xFF202020,
        0xFF4FA0FF,
        0xFF000000,
        0xFFFFFFFF
    );
    private static final UiBar MANA_BAR = new UiBar(
        81,
        9,
        0xFF202020,
        0xFFB14BFF,
        0xFF000000,
        0xFFFFFFFF
    );
    private static final UiBar XP_BAR = new UiBar(
        140,
        9,
        0xFF202020,
        0xFF4FA64F,
        0xFF000000,
        0xFFFFFFFF
    );

    private BelievableHudOverlay() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (minecraft.options.hideGui) {
            return;
        }
        Font font = minecraft.font;

        int margin = 8;
        int barGap = 6;
        int healthX = margin;
        int healthY = margin;
        int hungerX = margin;
        int hungerY = healthY + HEALTH_BAR.getHeight() + barGap;
        int oxygenX = margin;
        int oxygenY = hungerY + HUNGER_BAR.getHeight() + barGap;
        int manaX = margin;
        int manaY = oxygenY + OXYGEN_BAR.getHeight() + barGap;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int xpX = (screenWidth - 140) / 2;
        int xpY = margin;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int food = player.getFoodData().getFoodLevel();
        int air = player.getAirSupply();
        int maxAir = player.getMaxAirSupply();
        PlayerStatStorage statStorage = player.getData(ModAttachments.PLAYER_STATS);
        double maxMana = statStorage != null
            ? StatValueCalculator.getEffectiveStatValue(StatDefinitions.MAX_MANA, statStorage, player)
            : 0.0D;
        double currentMana = maxMana;
        if (statStorage != null && statStorage.hasValue(StatDefinitions.CURRENT_MANA)) {
            currentMana = statStorage.getValue(StatDefinitions.CURRENT_MANA, player);
        }
        PlayerProgressionStorage progression = player.getData(ModAttachments.PLAYER_PROGRESSION);
        int level = progression != null ? progression.getLevel() : 1;
        int xp = progression != null ? progression.getXp() : 0;
        int xpToNext = LevelingRules.getXpForNextLevel(level);
        PlayerVeinMinerStorage veinStorage = player.getData(ModAttachments.PLAYER_VEIN_MINER);
        int veinLevel = veinStorage != null ? veinStorage.getLevel() : 1;
        PlayerProspectorStorage prospectorStorage = player.getData(ModAttachments.PLAYER_PROSPECTOR);

        String healthText = String.format(Locale.ROOT, "%d / %d", Mth.ceil(health), Mth.ceil(maxHealth));
        String hungerText = String.format(Locale.ROOT, "%d / 20", food);
        String oxygenText = String.format(Locale.ROOT, "%d / %d", air, maxAir);
        String manaText = String.format(Locale.ROOT, "%d / %d", Mth.ceil(currentMana), Mth.ceil(maxMana));
        String xpText = String.format(Locale.ROOT, "XP %d / %d (Lv %d)", xp, xpToNext, level);
        String veinText = Component.translatable("hud.believable.vein_miner_level", veinLevel).getString();
        String visionText = Component.translatable(
            "hud.believable.prospector_vision",
            ProspectorService.getVisionRange(player)
        ).getString();
        int armorValue = player.getArmorValue();
        int armorToughness = (int) Math.round(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));

        HEALTH_BAR.render(graphics, font, healthX, healthY, health, maxHealth, healthText);
        HUNGER_BAR.render(graphics, font, hungerX, hungerY, food, 20.0D, hungerText);
        OXYGEN_BAR.render(graphics, font, oxygenX, oxygenY, air, maxAir, oxygenText);
        int nextLeftY = oxygenY + OXYGEN_BAR.getHeight() + barGap;
        if (maxMana > 0.0D) {
            MANA_BAR.render(graphics, font, manaX, manaY, currentMana, maxMana, manaText);
            nextLeftY = manaY + MANA_BAR.getHeight() + barGap;
        }
        renderArmorTooltip(graphics, font, margin, nextLeftY, armorValue, armorToughness);
        XP_BAR.render(graphics, font, xpX, xpY, xp, xpToNext, xpText);
        int statusY = xpY + XP_BAR.getHeight() + 4;
        if (veinStorage != null && veinStorage.isEnabled()) {
            graphics.drawString(font, veinText, xpX, statusY, 0xFFE6E6E6, true);
            statusY += font.lineHeight + 2;
        }
        if (prospectorStorage != null && prospectorStorage.isEnabled() && ProspectorService.canUse(player)) {
            graphics.drawString(font, visionText, xpX, statusY, 0xFFE6E6E6, true);
        }
        CartographerHudOverlay.render(graphics, partialTick);
    }

    private static void renderArmorTooltip(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int armorValue,
        int armorToughness
    ) {
        boolean showArmor = armorValue > 0;
        boolean showToughness = armorToughness > 0;
        if (!showArmor && !showToughness) {
            return;
        }

        int iconSize = 9;
        int padding = 4;
        int segmentGap = 10;
        int textPadding = 4;

        int armorTextWidth = showArmor ? font.width(String.valueOf(armorValue)) : 0;
        int toughnessTextWidth = showToughness ? font.width(String.valueOf(armorToughness)) : 0;

        int armorSegmentWidth = showArmor ? iconSize + textPadding + armorTextWidth : 0;
        int toughnessSegmentWidth = showToughness ? iconSize + textPadding + toughnessTextWidth : 0;
        int segments = (showArmor ? 1 : 0) + (showToughness ? 1 : 0);
        int totalWidth = armorSegmentWidth + toughnessSegmentWidth + (segments > 1 ? segmentGap : 0);
        int boxWidth = totalWidth + padding * 2;
        int boxHeight = iconSize + padding * 2;

        graphics.fill(x, y, x + boxWidth, y + boxHeight, 0xB0000000);
        graphics.fill(x, y, x + boxWidth, y + 1, 0xFF5A5A5A);
        graphics.fill(x, y + boxHeight - 1, x + boxWidth, y + boxHeight, 0xFF5A5A5A);
        graphics.fill(x, y, x + 1, y + boxHeight, 0xFF5A5A5A);
        graphics.fill(x + boxWidth - 1, y, x + boxWidth, y + boxHeight, 0xFF5A5A5A);

        int cursorX = x + padding;
        int iconY = y + padding;
        int textY = y + padding + (iconSize - font.lineHeight) / 2;
        if (showArmor) {
            graphics.blitSprite(ARMOR_FULL_SPRITE, cursorX, iconY, iconSize, iconSize);
            graphics.drawString(font, String.valueOf(armorValue), cursorX + iconSize + textPadding, textY, 0xFFE6E6E6, true);
            cursorX += armorSegmentWidth + (showToughness ? segmentGap : 0);
        }
        if (showToughness) {
            graphics.blitSprite(ARMOR_HALF_SPRITE, cursorX, iconY, iconSize, iconSize);
            graphics.drawString(font, String.valueOf(armorToughness), cursorX + iconSize + textPadding, textY, 0xFFE6E6E6, true);
        }
    }
}
