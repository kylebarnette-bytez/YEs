package position;

/**
 * The {@code Position} class represents a square on the chess board
 * using row and column indices.
 *
 * <p>
 * Rows and columns are represented internally as integers in the range 0–7.
 * Row 0 corresponds to the top of the board (rank 8 in chess),
 * and row 7 corresponds to the bottom of the board (rank 1 in chess).
 * Column 0 corresponds to file 'A', and column 7 corresponds to file 'H'.
 * </p>
 */
public class Position {
    /** The row index (0–7). */
    private final int row;

    /** The column index (0–7). */
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /**
     * Returns whether this position is inside the 8x8 board.
     */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Converts algebraic notation (e.g., "E2") into a Position object.
     * @param algebraic notation string like "E2" (case-insensitive)
     */
    public static Position fromAlgebraic(String algebraic) {
        algebraic = algebraic.trim().toUpperCase();
        if (algebraic.length() != 2) {
            throw new IllegalArgumentException("Invalid position format: " + algebraic);
        }
        char file = algebraic.charAt(0); // 'A'..'H'
        char rank = algebraic.charAt(1); // '1'..'8'

        int col = file - 'A';
        int row = 8 - Character.getNumericValue(rank);

        if (col < 0 || col > 7 || row < 0 || row > 7) {
            throw new IllegalArgumentException("Out of bounds: " + algebraic);
        }

        return new Position(row, col);
    }

    /**
     * Converts this Position into algebraic notation (e.g., "E2").
     */
    public String toAlgebraic() {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    /**
     * Compares positions by row and column.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.row == other.row && this.col == other.col;
    }

    /**
     * Ensures Position can be used in sets, lists, maps.
     */
    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
