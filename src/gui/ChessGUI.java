package gui;

import javax.swing.*;
import java.awt.*;
import gui.*;

/**
 * Main Chess GUI window.
 * Displays the chessboard and provides control buttons and status updates.
 * Author: Kyle Barnette
 */

public class ChessGUI extends JFrame{
    private final BoardPanel boardPanel;
    // --- added for GameHistoryPanel ---
    private final GameHistoryPanel historyPanel;

    private final JLabel statusLabel;

    public ChessGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 800);

        // --- Center: Board ---
        boardPanel = new BoardPanel(this);
        add(boardPanel, BorderLayout.CENTER);

        // --- East: Combined side panel (controls + move history) ---
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        controlPanel.setMaximumSize(new Dimension(200, 120));
        JButton undoBtn = new JButton("Undo Move");
        JButton exitBtn = new JButton("Exit");

        undoBtn.addActionListener(e -> boardPanel.undoLastMove());
        exitBtn.addActionListener(e -> System.exit(0));

        controlPanel.add(undoBtn);
        controlPanel.add(exitBtn);

// --- added for GameHistoryPanel ---
        historyPanel = new GameHistoryPanel();
        boardPanel.setHistoryPanel(historyPanel); // link BoardPanel → HistoryPanel

        sidePanel.add(controlPanel, BorderLayout.NORTH);
        sidePanel.add(historyPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);


        // --- South: Status bar ---
        statusLabel = new JLabel("White's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.SOUTH);
		
		// --- Menu Bar ---
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		JMenuItem newGameItem = new JMenuItem("New Game");
		JMenuItem saveGameItem = new JMenuItem("Save Game");
		JMenuItem loadGameItem = new JMenuItem("Load Game");

		newGameItem.addActionListener(e -> boardPanel.resetBoard());
		saveGameItem.addActionListener(e -> boardPanel.saveGame());
		loadGameItem.addActionListener(e -> boardPanel.loadGame());

		gameMenu.add(newGameItem);
		gameMenu.add(saveGameItem);
		gameMenu.add(loadGameItem);

		menuBar.add(gameMenu);
		setJMenuBar(menuBar);

        setLocationRelativeTo(null);
        setVisible(true);
    }
    /** Displays endgame message when King is captured. */
    public void showEndgameMessage(String winner){
        JOptionPane.showMessageDialog(this,
                winner + " wins! Game over.",
                "Checkmate",
                JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
    }
    /** Updates the turn label (called by BoardPanel). */
    public void updateStatus(String text) {
        statusLabel.setText(text);
    }

    /**
     * Temporarily show a short message in the status bar.
     * Automatically restores the normal turn message after a moment.
     */
    public void flashMessage(String text) {
        statusLabel.setText(text);

        // Restore the normal status after 2 seconds
        new javax.swing.Timer(2000, e -> {
            statusLabel.setText(whiteTurnText());
        }) {{
            setRepeats(false);
            start();
        }};
    }

    /** Returns the standard turn text. */
    private String whiteTurnText() {
        return boardPanel.isWhiteTurn() ? "White's Turn" : "Black's Turn";
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}
