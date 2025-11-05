package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {

    private DefaultListModel<String> moveListModel;
    private JList<String> moveList;
    private JLabel whiteCapturedLabel;
    private JLabel blackCapturedLabel;

    private List<String> whiteCaptured = new ArrayList<>();
    private List<String> blackCaptured = new ArrayList<>();

    public GameHistoryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Game History"));
        setPreferredSize(new Dimension(200, 0));

        // Move history area
        moveListModel = new DefaultListModel<>();
        moveList = new JList<>(moveListModel);
        JScrollPane moveScrollPane = new JScrollPane(moveList);

        // Captured pieces
        JPanel capturedPanel = new JPanel(new GridLayout(2, 1));
        whiteCapturedLabel = new JLabel("White captured: ");
        blackCapturedLabel = new JLabel("Black captured: ");
        capturedPanel.add(whiteCapturedLabel);
        capturedPanel.add(blackCapturedLabel);

        // Layout assembly
        add(capturedPanel, BorderLayout.NORTH);
        add(moveScrollPane, BorderLayout.CENTER);
    }

    // ---- public helper methods ----
    public void addMove(String move) {
        moveListModel.addElement(move);
    }

    public void addCapturedPiece(String player, String piece) {
        if (player.equalsIgnoreCase("white")) {
            whiteCaptured.add(piece);
            whiteCapturedLabel.setText("White captured: " + whiteCaptured);
        } else {
            blackCaptured.add(piece);
            blackCapturedLabel.setText("Black captured: " + blackCaptured);
        }
    }

    public void removeLastMove() {
        if (!moveListModel.isEmpty()) {
            moveListModel.remove(moveListModel.size() - 1);
        }
    }

    public void reset() {
        moveListModel.clear();
        whiteCaptured.clear();
        blackCaptured.clear();
        whiteCapturedLabel.setText("White captured: ");
        blackCapturedLabel.setText("Black captured: ");
    }
}
