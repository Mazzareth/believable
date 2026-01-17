package app.masterwork.believable.classes.cartographer.client;

public final class CartographerClientState {
    private static boolean minimapVisible;

    private CartographerClientState() {
    }

    public static boolean isMinimapVisible() {
        return minimapVisible;
    }

    public static boolean toggleMinimap() {
        minimapVisible = !minimapVisible;
        return minimapVisible;
    }

    public static void setMinimapVisible(boolean visible) {
        minimapVisible = visible;
    }
}
