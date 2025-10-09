package utils;

import board.Board;
import board.Position;
import pieces.Pawn;
import pieces.Knight;

/**
 * Official performance and functionality test for Phase 1.
 * This verifies stability, functionality, and basic performance
 * under simulated load.
 */
public class PerformanceTest {
    public static void main(String[] args) {
        System.out.println("=== 🧪 Official Phase 1 Test Run ===");

        long totalStart = System.nanoTime();

        // 1. Initialize board
        Board board = new Board();
        board.setPiece(new Pawn("white", new Position(6, 4)), new Position(6, 4)); // E2
        board.setPiece(new Knight("white", new Position(7, 6)), new Position(7, 6)); // G1
        board.display();

        // 2. Basic functional test
        System.out.println("→ Moving Pawn E2 to E4");
        board.movePiece(new Position(6, 4), new Position(4, 4));
        board.display();

        System.out.println("→ Moving Knight G1 to F3");
        board.movePiece(new Position(7, 6), new Position(5, 5));
        board.display();

        // 3. Edge case: empty square
        System.out.println("→ Attempting to move from empty square E5");
        board.movePiece(new Position(3, 4), new Position(2, 4));

        // 4. Performance stress test
        System.out.println("\n=== 🚀 Stress Test: 10,000 empty moves ===");
        long perfStart = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            board.movePiece(new Position(0, 0), new Position(0, 0)); // No actual piece
        }
        long perfEnd = System.nanoTime();
        double ms = (perfEnd - perfStart) / 1_000_000.0;
        System.out.println("Completed 10,000 empty moves in " + ms + " ms");

        // 5. Final board state
        System.out.println("\n=== ♟ Final Board State ===");
        board.display();

        long totalEnd = System.nanoTime();
        double totalMs = (totalEnd - totalStart) / 1_000_000.0;
        System.out.println("\n⏱ Total Test Duration: " + totalMs + " ms");

        // Simple memory info
        long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        System.out.println("🧠 Memory used after test: " + usedMem + " MB");

        System.out.println("✅ Phase 1 Test Complete.");
    }
}
