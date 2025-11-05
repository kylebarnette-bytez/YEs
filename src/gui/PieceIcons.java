package gui;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides Unicode-based icons for chess pieces.
 * Each piece key (e.g., "WHITE_QUEEN") maps to its corresponding symbol.
 */
public class PieceIcons {

    private static final Map<String, String> icons = new HashMap<>();

    static {
        // White pieces
        icons.put("WHITE_KING",   "\u2654");
        icons.put("WHITE_QUEEN",  "\u2655");
        icons.put("WHITE_ROOK",   "\u2656");
        icons.put("WHITE_BISHOP", "\u2657");
        icons.put("WHITE_KNIGHT", "\u2658");
        icons.put("WHITE_PAWN",   "\u2659");

        // Black pieces
        icons.put("BLACK_KING",   "\u265A");
        icons.put("BLACK_QUEEN",  "\u265B");
        icons.put("BLACK_ROOK",   "\u265C");
        icons.put("BLACK_BISHOP", "\u265D");
        icons.put("BLACK_KNIGHT", "\u265E");
        icons.put("BLACK_PAWN",   "\u265F");
    }

    /**
     * Returns the Unicode icon for the given piece key.
     *
     * @param key the identifier of the chess piece
     * @return the Unicode symbol for the piece, or null if not found
     */
    public static String getIcon(String key) {
        return icons.get(key);
    }
}
