package board;

public class Position {
    private final int row;    // 0–7
    private final int col;    // 0–7

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
}
