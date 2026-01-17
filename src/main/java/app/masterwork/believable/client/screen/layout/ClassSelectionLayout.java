package app.masterwork.believable.client.screen.layout;

/**
 * Immutable layout snapshot for class selection questionnaire + list panels.
 */
public record ClassSelectionLayout(
    int leftX,
    int leftW,
    int rightX,
    int rightW,
    int questionHeaderY,
    int questionBoxY,
    int questionBoxH,
    int stepHeaderY,
    int stepBoxY,
    int stepBoxH,
    int detailHeaderY,
    int detailBoxY,
    int detailBoxH,
    int listHeaderY,
    int listBoxY,
    int listBoxH,
    int footerY
) {
}
