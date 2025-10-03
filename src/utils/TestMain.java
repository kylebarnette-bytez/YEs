package utils;

import game.Game;
import board.Position;

/**
 * The {@code TestMain} class is a simple utility for
 * testing parts of the chess project, such as parsePosition.
 * <p>
 * This class is not part of the final submission.
 * </p>
 */
public class TestMain {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();

        // Example test of parsePosition
        System.out.println("E2 -> " + gameTestParse(game, "E2"));
    }

    /**
     * Helper method to call Game.parsePosition for testing.
     * Uses reflection since parsePosition is private.
     *
     * @param game  the Game object
     * @param input the chess notation string (e.g., "E2")
     * @return the corresponding Position
     */
    private static Position gameTestParse(Game game, String input) {
        try {
            java.lang.reflect.Method method =
                    Game.class.getDeclaredMethod("parsePosition", String.class);
            method.setAccessible(true);
            return (Position) method.invoke(game, input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
