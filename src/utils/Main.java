package utils;

import game.Game;

/**
 * Entry point of the Chess Application.
 * Initializes and starts the game.
 */
public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();  // Initializes players and board
        game.play();   // Starts game loop
    }
}
