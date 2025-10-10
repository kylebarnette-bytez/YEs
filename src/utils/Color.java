package utils;

/**
 * Represents the color of a chess piece or player.
 * Using an enum instead of strings avoids typos and allows safe comparisons.
 */
public enum Color {
    WHITE("White"),
    BLACK("Black");

    private final String displayName;

    Color(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns a human-readable name for this color.
     * Example: "White" or "Black".
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Returns the opposite color.
     * Example: WHITE.opposite() → BLACK
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
