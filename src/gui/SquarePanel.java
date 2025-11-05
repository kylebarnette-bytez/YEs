package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import gui.*;

/**
 * Represents a single square on the chessboard.
 * Handles rendering, piece display, highlighting, and click interactions.
 */
public class SquarePanel extends JPanel {

    private final int row, col;
    private final BoardPanel board;
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);
    private final JLabel pieceLabel;
    private String pieceKey = null;
    private boolean highlighted = false;

    /**
     * Constructs a square panel with a given board reference and position.
     *
     * @param row   the row index of this square
     * @param col   the column index of this square
     * @param board the parent board this square belongs to
     */
    public SquarePanel(int row, int col, BoardPanel board) {
        this.row = row;
        this.col = col;
        this.board = board;

        setLayout(new BorderLayout());
        setBackground((row + col) % 2 == 0 ? lightColor : darkColor);
        setPreferredSize(new Dimension(80, 80));

        pieceLabel = new JLabel("", SwingConstants.CENTER);
        pieceLabel.setFont(new Font("Serif", Font.BOLD, 48));
        add(pieceLabel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                board.handleSquareClick(SquarePanel.this);
            }
        });
    }

    /** Returns the row index of this square. */
    public int getRow() {
        return row;
    }

    /** Returns the column index of this square. */
    public int getCol() {
        return col;
    }

    /**
     * Sets a chess piece on this square.
     *
     * @param pieceKey the piece identifier (e.g., "WHITE_KING")
     */
    public void setPiece(String pieceKey) {
        this.pieceKey = pieceKey;
        String symbol = PieceIcons.getIcon(pieceKey);
        pieceLabel.setText(symbol != null ? symbol : "");
    }

    /** Clears the square of any piece. */
    public void clearPiece() {
        this.pieceKey = null;
        pieceLabel.setText("");
    }

    /** Returns true if this square currently contains a piece. */
    public boolean hasPiece() {
        return pieceKey != null;
    }

    /** Returns the piece key (e.g., "BLACK_QUEEN") on this square. */
    public String getPieceKey() {
        return pieceKey;
    }

    /**
     * Highlights or unhighlights the square, used for move indication.
     *
     * @param highlighted true to highlight the square, false to clear it
     */
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        setBorder(highlighted ? BorderFactory.createLineBorder(Color.GREEN, 3) : null);
        repaint();
    }
}
