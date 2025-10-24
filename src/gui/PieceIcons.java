package gui;

import java.util.HashMap;
import java.util.Map;

public class PieceIcons {

    private static final Map<String, String> icons = new HashMap<>();

    static {
        // White pieces (\u2654–\u2659)
        icons.put("WHITE_KING",   "\u2654");
        icons.put("WHITE_QUEEN",  "\u2655");
        icons.put("WHITE_ROOK",   "\u2656");
        icons.put("WHITE_BISHOP", "\u2657");
        icons.put("WHITE_KNIGHT", "\u2658");
        icons.put("WHITE_PAWN",   "\u2659");

        // Black pieces (\u265A–\u265F)
        icons.put("BLACK_KING",   "\u265A");
        icons.put("BLACK_QUEEN",  "\u265B");
        icons.put("BLACK_ROOK",   "\u265C");
        icons.put("BLACK_BISHOP", "\u265D");
        icons.put("BLACK_KNIGHT", "\u265E");
        icons.put("BLACK_PAWN",   "\u265F");
    }

    public static String getIcon(String key) {
        return icons.get(key);
    }
}
