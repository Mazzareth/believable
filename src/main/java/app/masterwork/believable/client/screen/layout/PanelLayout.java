package app.masterwork.believable.client.screen.layout;

/**
 * Immutable layout snapshot used for positioning panel regions.
 */
public record PanelLayout(int panelX, int panelY, int panelW, int panelH, int sectionX, int sectionY, int sectionW,
                          int sectionH, int subsectionX, int subsectionY, int subsectionW, int subsectionH,
                          int contentX, int contentY, int contentW, int contentH) {
}
