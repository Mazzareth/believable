package app.masterwork.believable.client.screen.tabs;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree node for menu tab hierarchy.
 */
public final class TabNode {
    public final String translationKey;
    public final Component label;
    public final List<TabNode> children = new ArrayList<>();

    public TabNode(String translationKey) {
        this.translationKey = translationKey;
        this.label = Component.translatable(translationKey);
    }

    public void addChild(TabNode child) {
        this.children.add(child);
    }
}
