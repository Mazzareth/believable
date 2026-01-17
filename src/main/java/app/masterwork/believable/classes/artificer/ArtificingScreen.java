package app.masterwork.believable.classes.artificer;

import app.masterwork.believable.client.screen.components.ThemedButton;
import app.masterwork.believable.client.screen.components.ThemedFrame;
import app.masterwork.believable.client.screen.theme.UnbelievableTheme;
import app.masterwork.believable.item.ItemAffixService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ArtificingScreen extends AbstractContainerScreen<ArtificingMenu> {
    private static final int HEADER_HEIGHT = 18;
    private static final int BUTTON_WIDTH = 132;
    private static final int BUTTON_HEIGHT = 16;
    private static final int BUTTON_GAP = 4;
    private static final int BUTTON_ROWS = 6;
    private static final int BUTTON_START_Y = ArtificingMenu.SLOT_Y + ArtificingMenu.SLOT_SIZE + 8;
    private static final int PLAYER_INV_START_Y = ArtificingMenu.PLAYER_INV_START_Y;
    private static final int BOTTOM_PADDING = 8;
    private final ThemedButton[] optionButtons = new ThemedButton[BUTTON_ROWS];

    public ArtificingScreen(ArtificingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 188;
        this.imageHeight = ArtificingMenu.HOTBAR_START_Y + ArtificingMenu.SLOT_SIZE + BOTTOM_PADDING;
        this.inventoryLabelY = PLAYER_INV_START_Y - 8;
    }

    @Override
    protected void init() {
        super.init();
        addOptionButton(0, ArtificingMenu.BUTTON_REROLL_RANDOM);
        addOptionButton(1, ArtificingMenu.BUTTON_REROLL_SAME_RARITY);
        addOptionButton(2, ArtificingMenu.BUTTON_REROLL_VALUES);
        addOptionButton(3, ArtificingMenu.BUTTON_ADD_AFFIX);
        addOptionButton(4, ArtificingMenu.BUTTON_SELECT_AFFIX);
        addOptionButton(5, ArtificingMenu.BUTTON_REROLL_SELECTED);
    }

    private void addOptionButton(int row, int buttonId) {
        int x = this.leftPos + 22;
        int y = this.topPos + BUTTON_START_Y + row * (BUTTON_HEIGHT + BUTTON_GAP);
        Component label = buildButtonLabel(buttonId);
        ThemedButton button = new ThemedButton(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, label, b -> pressOption(buttonId));
        button.active = canUseOption(buttonId);
        addRenderableWidget(button);
        if (row >= 0 && row < optionButtons.length) {
            optionButtons[row] = button;
        }
    }

    private Component buildButtonLabel(int buttonId) {
        int cost = ArtificingMenu.getCostForButton(buttonId);
        String key = switch (buttonId) {
            case ArtificingMenu.BUTTON_REROLL_RANDOM -> "screen.believable.artificing.option.reroll_random";
            case ArtificingMenu.BUTTON_REROLL_SAME_RARITY -> "screen.believable.artificing.option.reroll_same_rarity";
            case ArtificingMenu.BUTTON_REROLL_VALUES -> "screen.believable.artificing.option.reroll_values";
            case ArtificingMenu.BUTTON_ADD_AFFIX -> "screen.believable.artificing.option.add_affix";
            case ArtificingMenu.BUTTON_SELECT_AFFIX -> "screen.believable.artificing.option.select_affix";
            case ArtificingMenu.BUTTON_REROLL_SELECTED -> "screen.believable.artificing.option.reroll_selected";
            default -> "screen.believable.artificing.option.reroll_random";
        };
        if (cost <= 0) {
            return Component.translatable(key);
        }
        return Component.translatable(
            "screen.believable.artificing.option.with_cost",
            Component.translatable(key),
            cost
        );
    }

    private boolean canUseOption(int buttonId) {
        int complexity = menu.getSyncedComplexity();
        int required = ArtificingMenu.getRequiredComplexity(buttonId);
        int salvage = menu.getSyncedSalvage();
        int cost = ArtificingMenu.getCostForButton(buttonId);
        if (buttonId == ArtificingMenu.BUTTON_SELECT_AFFIX) {
            return complexity >= required && salvage >= cost && hasAnyAffix();
        }
        if (buttonId == ArtificingMenu.BUTTON_REROLL_SELECTED) {
            return complexity >= required && salvage >= cost && hasSelectableAffix();
        }
        return complexity >= required && salvage >= cost;
    }

    private void pressOption(int buttonId) {
        Minecraft minecraft = this.minecraft;
        if (minecraft == null || minecraft.gameMode == null) {
            return;
        }
        minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (int i = 0; i < optionButtons.length; i++) {
            ThemedButton button = optionButtons[i];
            if (button == null) {
                continue;
            }
            int buttonId = switch (i) {
                case 0 -> ArtificingMenu.BUTTON_REROLL_RANDOM;
                case 1 -> ArtificingMenu.BUTTON_REROLL_SAME_RARITY;
                case 2 -> ArtificingMenu.BUTTON_REROLL_VALUES;
                case 3 -> ArtificingMenu.BUTTON_ADD_AFFIX;
                case 4 -> ArtificingMenu.BUTTON_SELECT_AFFIX;
                case 5 -> ArtificingMenu.BUTTON_REROLL_SELECTED;
                default -> -1;
            };
            if (buttonId >= 0) {
                button.active = canUseOption(buttonId);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ThemedFrame.drawPanelWell(graphics, x, y, this.imageWidth, this.imageHeight);
        ThemedFrame.drawGildedFrame(graphics, x, y, this.imageWidth, this.imageHeight);
        graphics.fill(x + 2, y + 2, x + this.imageWidth - 2, y + HEADER_HEIGHT, UnbelievableTheme.CARD_HEADER);
        graphics.fill(x + 2, y + HEADER_HEIGHT - 1, x + this.imageWidth - 2, y + HEADER_HEIGHT, UnbelievableTheme.GOLD_DARK);
        ThemedFrame.drawSlot(graphics, x + ArtificingMenu.SLOT_X, y + ArtificingMenu.SLOT_Y, ArtificingMenu.SLOT_SIZE);
        ThemedFrame.drawSlotGrid(graphics, x + 8, y + PLAYER_INV_START_Y, 3, 9, ArtificingMenu.SLOT_SIZE);
        ThemedFrame.drawSlotGrid(graphics, x + 8, y + PLAYER_INV_START_Y + 58, 1, 9, ArtificingMenu.SLOT_SIZE);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, UnbelievableTheme.TEXT_TITLE, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, UnbelievableTheme.TEXT_SOFT, false);

        Component salvage = Component.translatable(
            "screen.believable.artificing.salvage",
            Integer.toString(menu.getSyncedSalvage())
        );
        Component complexity = Component.translatable(
            "screen.believable.artificing.complexity",
            Integer.toString(menu.getSyncedComplexity())
        );
        graphics.drawString(this.font, salvage, 8, 22, UnbelievableTheme.TEXT_MUTED, false);
        graphics.drawString(this.font, complexity, 8, 34, UnbelievableTheme.TEXT_MUTED, false);
        graphics.drawString(this.font, buildSelectedAffixLabel(), 8, 46, UnbelievableTheme.TEXT_MUTED, false);
    }

    private Component buildSelectedAffixLabel() {
        ItemStack stack = menu.getSlot(0).getItem();
        if (stack.isEmpty() || !ItemAffixService.hasAffixes(stack)) {
            return Component.translatable(
                "screen.believable.artificing.selected_affix",
                Component.translatable("screen.believable.artificing.selected_affix.none")
            );
        }
        var affixes = ItemAffixService.getAffixes(stack);
        if (affixes.isEmpty()) {
            return Component.translatable(
                "screen.believable.artificing.selected_affix",
                Component.translatable("screen.believable.artificing.selected_affix.none")
            );
        }
        int index = menu.getSelectedAffixIndex();
        if (index < 0 || index >= affixes.size()) {
            index = 0;
        }
        String name = ItemAffixService.getAffixDisplayName(affixes.get(index).id());
        return Component.translatable("screen.believable.artificing.selected_affix", Component.literal(name));
    }

    private boolean hasSelectableAffix() {
        ItemStack stack = menu.getSlot(0).getItem();
        if (stack.isEmpty() || !ItemAffixService.hasAffixes(stack)) {
            return false;
        }
        var affixes = ItemAffixService.getAffixes(stack);
        if (affixes.isEmpty()) {
            return false;
        }
        int index = menu.getSelectedAffixIndex();
        return index >= 0 && index < affixes.size();
    }

    private boolean hasAnyAffix() {
        ItemStack stack = menu.getSlot(0).getItem();
        return !stack.isEmpty() && ItemAffixService.hasAffixes(stack) && !ItemAffixService.getAffixes(stack).isEmpty();
    }

}
