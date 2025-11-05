package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel displaying the move history and captured pieces for both players.
 * Used in conjunction with {@link BoardPanel} to provide real-time updates.
 */
public class GameHistoryPanel extends JPanel {

    private DefaultListModel<String> moveListModel;
    private JList<String> moveList;
    private JLabel whiteCapturedLabel;
    private JLabel blackCapturedLabel;
    private List<String> whiteCaptured = new ArrayList<>();
    private List<String> blackCaptured = new ArrayList<>();

    /** Constructs the game history panel with layout and display setup. */
    public GameHistoryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Game History"));
        setPreferredSize(new Dimension(200, 0));

        moveListModel = new DefaultListModel<>();
        moveList = new JList<>(moveListModel);
        JScrollPane moveScrollPane = new JScrollPane(moveList);

        JPanel capturedPanel = new JPanel(new GridLayout(2, 1));
        whiteCapturedLabel = new JLabel("White captured: ");
        blackCapturedLabel = new JLabel("Black captured: ");
        capturedPanel.add(whiteCapturedLabel);
        capturedPanel.add(blackCapturedLabel);

        add(capturedPanel, BorderLayout.NORTH);
        add(moveScrollPane, BorderLayout.CENTER);
    }

    /** Adds a new move to the move history list. */
    public void addMove(String move) {
        moveListModel.addElement(move);
    }

    /**
     * Adds a captured piece to the appropriate player's captured list.
     *
     * @param player the player who captured the piece
     * @param piece  the piece captured
     */
    public void addCapturedPiece(String player, String piece) {
        if (player.equalsIgnoreCase("white")) {
            whiteCaptured.add(piece);
            whiteCapturedLabel.setText("White captured: " + whiteCaptured);
        } else {
            blackCaptured.add(piece);
            blackCapturedLabel.setText("Black captured: " + blackCaptured);
        }
    }

    /** Removes the most recent move from the history list. */
    public void removeLastMove() {
        if (!moveListModel.isEmpty()) {
            moveListModel.remove(moveListModel.size() - 1);
        }
    }

    /** Clears all move history and captured piece lists. */
    public void reset() {
        moveListModel.clear();
        whiteCaptured.clear();
        blackCaptured.clear();
        whiteCapturedLabel.setText("White captured: ");
        blackCapturedLabel.setText("Black captured: ");
    }
}
