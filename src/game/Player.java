package game;

/**
 * The Player class represents a chess player.
 * Each player has a name and a color ("white" or "black").
 *
 * Phase 1: Simple data holder for player information.
 * Future phases: Could track captured pieces, score, or move history.
 */
public class Player {
    /** The player's name. */
    private String name;

    /** The player's color ("white" or "black"). */
    private String color;

    /**
     * Constructs a Player with the given name and color.
     * @param name the name of the player
     * @param color the color ("white" or "black") this player controls
     */
    public Player(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Returns the player's name.
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the player's color.
     * @return the color ("white" or "black")
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns a string representation of the player.
     * @return formatted string with name and color
     */
    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}
