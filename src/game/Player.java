package game;

import utils.Color;
import pieces.Piece;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Player} class represents a chess player.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Store player information (name and color)</li>
 *   <li>Track the list of pieces currently controlled by the player</li>
 *   <li>Allow adding and removing pieces when captured or placed</li>
 * </ul>
 *
 * <p>This class does not contain logic for moves — only state management.
 */
public class Player {

    /** The player's name (e.g., "Alice" or "Bob"). */
    private String name;

    /** The color controlled by this player: {@link utils.Color#WHITE} or {@link utils.Color#BLACK}. */
    private Color color;

    /** List of pieces this player currently has on the board. */
    private List<Piece> availablePieces;

    /**
     * Constructs a {@code Player} with the specified name and color.
     *
     * @param name  the name of the player
     * @param color the color (WHITE or BLACK) this player controls
     */
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.availablePieces = new ArrayList<>();
    }

    /**
     * Returns the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the color controlled by this player.
     *
     * @return the player's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the list of pieces currently owned by this player.
     *
     * @return list of pieces on the board for this player
     */
    public List<Piece> getAvailablePieces() {
        return availablePieces;
    }

    /**
     * Adds a piece to the player's collection.
     * <p>This should be called when the board initializes or a piece is promoted.
     *
     * @param piece the piece to add
     */
    public void addPiece(Piece piece) {
        availablePieces.add(piece);
    }

    /**
     * Removes a piece from the player's collection.
     * <p>This should be called when the piece is captured.
     *
     * @param piece the piece to remove
     */
    public void removePiece(Piece piece) {
        availablePieces.remove(piece);
    }

    /**
     * Returns a formatted string containing the player's name and color.
     *
     * @return string in the format "Name (Color)"
     */
    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}
