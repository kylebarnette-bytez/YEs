package utils;

/**
 * The {@code Color} enum represents the two possible sides in a chess game:
 * {@link #WHITE White} and {@link #BLACK Black}.
 *
 * <p>Using an enum for piece and player colors provides:
 * <ul>
 *   <li>Type safety (avoids fragile string comparisons)</li>
 *   <li>Easy switching and logic handling</li>
 *   <li>Improved readability in code and debug output</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 *     Color c = Color.WHITE;
 *     System.out.println(c.opposite()); // Prints "Black"
 * </pre>
 */
public enum Color {

    /** White side — moves first in the game. */
    WHITE("White"),

    /** Black side — moves second in the game. */
    BLACK("Black");

    /** A human-readable display name for the color. */
    private final String displayName;

    /**
     * Constructs a {@code Color} enum constant with the given display name.
     *
     * @param displayName the human-readable name for this color
     */
    Color(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name for this color.
     * <p>Example:
     * <ul>
     *   <li>{@code Color.WHITE.toString()} → "White"</li>
     *   <li>{@code Color.BLACK.toString()} → "Black"</li>
     * </ul>
     *
     * @return the color name as a {@link String}
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Returns the opposite color.
     * <p>Example:
     * <ul>
     *   <li>{@code Color.WHITE.opposite()} → {@code Color.BLACK}</li>
     *   <li>{@code Color.BLACK.opposite()} → {@code Color.WHITE}</li>
     * </ul>
     *
     * @return the opposite {@code Color} value
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
