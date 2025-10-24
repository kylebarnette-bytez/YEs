package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import gui.*;

public class SquarePanel extends JPanel {

    private final int row, col;
    private final BoardPanel board;
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);
    private final JLabel pieceLabel;

    private String pieceKey = null;
    private boolean highlighted = false;

    public SquarePanel(int row, int col, BoardPanel board) {
        this.row = row;
        this.col = col;
        this.board = board;

        setLayout(new BorderLayout());
        setBackground((row + col) % 2 == 0 ? lightColor : darkColor);
        setPreferredSize(new Dimension(80, 80));

        pieceLabel = new JLabel("", SwingConstants.CENTER);
		//pieceLabel.setFont(getChessFont());
		pieceLabel.setFont(new Font("Serif", Font.BOLD, 48));
        add(pieceLabel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                board.handleSquareClick(SquarePanel.this);
            }
        });
    }

    public void setPiece(String pieceKey) {
        this.pieceKey = pieceKey;
        String symbol = PieceIcons.getIcon(pieceKey);
        pieceLabel.setText(symbol != null ? symbol : "");
    }

    public void clearPiece() {
        this.pieceKey = null;
        pieceLabel.setText("");
    }

    public boolean hasPiece() {
        return pieceKey != null;
    }

    public String getPieceKey() {
        return pieceKey;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        if (highlighted) {
            setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        } else {
            setBorder(null);
        }
        repaint();
    }
}