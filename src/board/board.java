package board;

import pieces.Piece;

public class Board {
    private Piece[][] squares = new Piece[8][8];

    public Board() {
        // TODO: set up initial board
    }

    public Piece getPiece(Position pos) {
        return squares[pos.getRow()][pos.getCol()];
    }

    public void movePiece(Position from, Position to) {
        // TODO: implement move logic
    }

    public void display() {
        // TODO: print the 8x8 board with "wP", "bR", etc.
    }
}
