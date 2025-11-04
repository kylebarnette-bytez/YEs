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
    private final JLabel statusLabel;

    public ChessGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 800);

        // --- Center: Board ---
        boardPanel = new BoardPanel(this);
        add(boardPanel, BorderLayout.CENTER);

        // --- East: Control buttons ---
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(5, 1, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

		//New Game button implemented in menu bar
        //JButton newGameBtn = new JButton("New Game"); 
        JButton undoBtn = new JButton("Undo Move");
        JButton exitBtn = new JButton("Exit");

        //newGameBtn.addActionListener(e -> boardPanel.resetBoard());
        undoBtn.addActionListener(e -> boardPanel.undoLastMove());
        exitBtn.addActionListener(e -> System.exit(0));

        //controlPanel.add(newGameBtn);
        controlPanel.add(undoBtn);
        controlPanel.add(exitBtn);

        add(controlPanel, BorderLayout.EAST);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}
