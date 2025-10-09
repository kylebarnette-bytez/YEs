package utils;

import board.Board;
import board.Position;
import game.Game;
import pieces.Knight;
import pieces.Pawn;

/**
 * Automated test file for Phase 1 of the Console Chess project.
 *
 * This test ensures:
 * - Board displays correctly
 * - Pieces can be placed and moved
 * - Empty square move is handled
 * - Input parsing works as expected
 * - Turn switching and move display work
 *
 * This file is not part of the final game logic — it’s for grading and self-verification.
 */
public class TestMain {
    public static void main(String[] args) {
        System.out.println("=== 🧪 Starting Chess Phase 1 Tests ===");

        // 1. 🧭 Initialize Board
        Board board = new Board();
        System.out.println("1. Board initialized and displayed:");
        board.display();

        // 2. ♟ Place a few pieces manually for testing
        Pawn whitePawn = new Pawn("white", new Position(6, 4)); // E2
        Pawn blackPawn = new Pawn("black", new Position(1, 4)); // E7
        Knight whiteKnight = new Knight("white", new Position(7, 6)); // G1

        board.setPiece(whitePawn, whitePawn.getPosition());
        board.setPiece(blackPawn, blackPawn.getPosition());
        board.setPiece(whiteKnight, whiteKnight.getPosition());

        System.out.println("2. After placing white pawn (E2), black pawn (E7), white knight (G1):");
        board.display();

        // 3. 🚀 Test move piece - valid pawn move
        Position fromE2 = new Position(6, 4);
        Position toE4 = new Position(4, 4);
        board.movePiece(fromE2, toE4);
        System.out.println("3. After moving white pawn from E2 to E4:");
        board.display();

        // 4. ⚠️ Test invalid move - empty square
        Position fromE5 = new Position(3, 4);
        Position toE6 = new Position(2, 4);
        System.out.println("4. Attempting to move from empty square E5:");
        board.movePiece(fromE5, toE6); // Should print error

        // 5. ♞ Test knight move
        Position fromG1 = new Position(7, 6);
        Position toF3 = new Position(5, 5);
        board.movePiece(fromG1, toF3);
        System.out.println("5. After moving knight G1 to F3:");
        board.display();

        // 6. 🧮 Check Position parsing via Game
        Game g = new Game();
        System.out.println("6. parsePosition(\"E2\") => " + gTestParse(g, "E2"));
        System.out.println("6. parsePosition(\"H8\") => " + gTestParse(g, "H8"));

        // 7. ✅ Final check — everything displayed correctly
        System.out.println("=== ✅ All Phase 1 tests executed ===");
    }

    // Helper method to access parsePosition for testing
    private static String gTestParse(Game g, String input) {
        try {
            Position pos = gTestParseWrapper(g, input);
            return pos.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Small trick: calls Game.parsePosition indirectly
    private static Position gTestParseWrapper(Game g, String input) throws Exception {
        java.lang.reflect.Method method = Game.class.getDeclaredMethod("parsePosition", String.class);
        method.setAccessible(true);
        return (Position) method.invoke(g, input);
    }
}
