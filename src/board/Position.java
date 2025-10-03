package board;

/**
 * The {@code Position} class represents a square on the chess board
 * using row and column indices.
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

    /**
     * Constructs a {@code Position} with the given row and column.
     *
     * @param row the row index (0–7), where 0 is the top of the board (rank 8)
     * @param col the column index (0–7), where 0 is file 'A'
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row index for this position.
     *
     * @return the row index (0–7)
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column index for this position.
     *
     * @return the column index (0–7)
     */
    public int getCol() {
        return col;
    }

    /**
     * Returns a string representation of this position.
     * Example: {@code (6, 4)} for row=6 and col=4.
     *
     * @return a string showing row and column in the form "(row, col)"
     */
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
