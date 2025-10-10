package game;

import utils.Color;
import pieces.Piece;
import java.util.ArrayList;
import java.util.List;

/**
 * The Player class represents a chess player.
 * Each player has a name and a color (WHITE or BLACK).
 */
public class Player {

    /** The player's name. */
    private String name;

    /** The player's color. */
    private Color color;

    /** List of pieces this player currently has on the board. */
    private List<Piece> availablePieces;

    /**
     * Constructs a Player with the given name and color.
     * @param name  the name of the player
     * @param color the color (WHITE or BLACK) this player controls
     */
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.availablePieces = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public List<Piece> getAvailablePieces() {
        return availablePieces;
    }

    /** Add a piece to the player's collection */
    public void addPiece(Piece piece) {
        availablePieces.add(piece);
    }

    /** Remove a piece (e.g., when it's captured) */
    public void removePiece(Piece piece) {
        availablePieces.remove(piece);
    }

    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}
