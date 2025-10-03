package utils;

import game.Game;

/**
 * The entry point of the Chess program.
 * <p>
 * This class simply creates a Game object and runs it.
 * </p>
 */
public class Main {
    /**
     * Starts the program by creating and running a Game.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
        game.play();
    }
}
