package gui;

import javax.swing.*;
import java.awt.*;
import gui.*;

public class BoardPanel extends JPanel {

    private static final int BOARD_SIZE = 8;
    private final SquarePanel[][] squares = new SquarePanel[BOARD_SIZE][BOARD_SIZE];
    private SquarePanel selectedSquare = null;

    public BoardPanel() {
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
        initializePieces();
    }

    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquarePanel square = new SquarePanel(row, col, this);
                squares[row][col] = square;
                add(square);
            }
        }
    }

    private void initializePieces() {
        // Black pieces
        for (int col = 0; col < BOARD_SIZE; col++) {
            squares[1][col].setPiece("BLACK_PAWN");
        }
        squares[0][0].setPiece("BLACK_ROOK");
        squares[0][7].setPiece("BLACK_ROOK");
        squares[0][1].setPiece("BLACK_KNIGHT");
        squares[0][6].setPiece("BLACK_KNIGHT");
        squares[0][2].setPiece("BLACK_BISHOP");
        squares[0][5].setPiece("BLACK_BISHOP");
        squares[0][3].setPiece("BLACK_QUEEN");
        squares[0][4].setPiece("BLACK_KING");

        // White pieces
        for (int col = 0; col < BOARD_SIZE; col++) {
            squares[6][col].setPiece("WHITE_PAWN");
        }
        squares[7][0].setPiece("WHITE_ROOK");
        squares[7][7].setPiece("WHITE_ROOK");
        squares[7][1].setPiece("WHITE_KNIGHT");
        squares[7][6].setPiece("WHITE_KNIGHT");
        squares[7][2].setPiece("WHITE_BISHOP");
        squares[7][5].setPiece("WHITE_BISHOP");
        squares[7][3].setPiece("WHITE_QUEEN");
        squares[7][4].setPiece("WHITE_KING");
    }

    public void handleSquareClick(SquarePanel clicked) {
        if (selectedSquare == null) {
            if (clicked.hasPiece()) {
                selectedSquare = clicked;
                clicked.setHighlighted(true);
            }
            return;
        }

        if (clicked == selectedSquare) {
            selectedSquare.setHighlighted(false);
            selectedSquare = null;
            return;
        }

        String movingPiece = selectedSquare.getPieceKey();
        clicked.setPiece(movingPiece);
        selectedSquare.clearPiece();
        selectedSquare.setHighlighted(false);
        selectedSquare = null;
    }
}
