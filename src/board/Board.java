package board;

import pieces.Piece;

/**
 * Represents the 8x8 chess board.
 * Handles setup, movement, and display of chess pieces.
 */
public class Board {
    private Piece[][] squares; // 8x8 grid

    /**
     * Constructs a new chess board and initializes it.
     */
    public Board() {
        squares = new Piece[8][8];
        initializeBoard();
    }

    /**
     * Initializes the board.
     * For Phase 1: fill every square with null (placeholders).
     * Later: partner will provide real piece classes to populate.
     */
    private void initializeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = null;
            }
        }

        // 🔒 Future: uncomment when pieces are ready
        // squares[1][0] = new Pawn("black", new Position(1, 0));
        // squares[6][0] = new Pawn("white", new Position(6, 0));
        // ... etc.
    }

    /**
     * Gets the piece at a given position.
     * @param pos the position on the board
     * @return the piece at that square, or null if empty
     */
    public Piece getPiece(Position pos) {
        return squares[pos.getRow()][pos.getCol()];
    }

    /**
     * Moves a piece from one position to another.
     * Overwrites the destination if occupied.
     * @param from the starting position
     * @param to the ending position
     */
    public void movePiece(Position from, Position to) {
        Piece moving = squares[from.getRow()][from.getCol()];
        if (moving == null) {
            System.out.println("No piece at source square.");
            return;
        }

        squares[to.getRow()][to.getCol()] = moving;

        // 🔒 Future: partner's Piece class will support this
        // moving.setPosition(to);

        squares[from.getRow()][from.getCol()] = null;
    }

    /**
     * Displays the board in the console.
     * Empty squares = "##", occupied = "??" until piece classes are ready.
     */
    public void display() {
        System.out.println("   A  B  C  D  E  F  G  H");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " "); // rank label
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece == null) {
                    System.out.print("## ");
                } else {
                    System.out.print("?? "); // placeholder until real pieces exist
                }
            }
            System.out.println((8 - row));
        }
        System.out.println("   A  B  C  D  E  F  G  H");
    }
}
