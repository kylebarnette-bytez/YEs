package gui;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class PieceIcons {

    private static Map<String, ImageIcon> icons = new HashMap<>();

    static {
        icons.put("WHITE_PAWN", new ImageIcon("resources/white_pawn.png"));
        icons.put("BLACK_PAWN", new ImageIcon("resources/black_pawn.png"));
        // Add others: rook, knight, bishop, queen, king...
    }

    public static ImageIcon getIcon(String key) {
        return icons.get(key);
    }
}
