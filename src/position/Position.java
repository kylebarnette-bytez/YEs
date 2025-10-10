package position;

/**
 * The {@code Position} class represents a single square on the chess board
 * using zero-based row and column indices.
 *
 * <p>Mapping between board coordinates and chess notation:
 * <ul>
 *   <li>Rows are indexed 0–7, where row 0 corresponds to rank 8, and row 7 to rank 1.</li>
 *   <li>Columns are indexed 0–7, where column 0 corresponds to file 'A', and column 7 to file 'H'.</li>
 * </ul>
 *
 * <p>Example:
 * <pre>
 *   new Position(6, 4) → "E2"
 *   new Position(0, 0) → "A8"
 * </pre>
 *
 * This class is immutable.
 */
public class Position {

    /** The row index (0–7). */
    private final int row;

    /** The column index (0–7). */
    private final int col;

    /**
     * Constructs a {@code Position} with the given row and column.
     *
     * @param row the row index (0–7)
     * @param col the column index (0–7)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row index of this position.
     *
     * @return the row index (0–7)
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column index of this position.
     *
     * @return the column index (0–7)
     */
    public int getCol() {
        return col;
    }

    /**
     * Returns whether this position is inside the valid 8x8 chess board.
     *
     * @return {@code true} if the position is within the board boundaries, {@code false} otherwise
     */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Parses algebraic chess notation (e.g., "E2") into a {@code Position}.
     *
     * @param algebraic the algebraic notation string (case-insensitive)
     * @return a {@code Position} object representing the square
     * @throws IllegalArgumentException if the notation is invalid or out of bounds
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
     * Converts this position into standard algebraic notation (e.g., "E2").
     *
     * @return algebraic notation string representing this position
     */
    public String toAlgebraic() {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    /**
     * Checks whether this position is equal to another object.
     * Positions are equal if they have the same row and column.
     *
     * @param obj the object to compare
     * @return {@code true} if the positions are the same, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.row == other.row && this.col == other.col;
    }

    /**
     * Returns a hash code for this position based on its row and column.
     * This allows {@code Position} to be used in hash-based collections.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    /**
     * Returns a string representation of this position in internal (row, col) format.
     * <p>Example: {@code (6, 4)}
     *
     * @return string in the format "(row, col)"
     */
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
