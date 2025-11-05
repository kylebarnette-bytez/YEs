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
    // --- Turn Timers ---
    private JLabel whiteTimerLabel;
    private JLabel blackTimerLabel;
    private javax.swing.Timer swingTimer;
    private int whiteSeconds = 300; // 5 minutes
    private int blackSeconds = 300; // 5 minutes
    private boolean whiteTurn = true; // synced with BoardPanel

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
        // --- Turn Timer Display ---
        JPanel timerPanel = new JPanel(new GridLayout(1, 2));
        whiteTimerLabel = new JLabel("White: 05:00", SwingConstants.CENTER);
        blackTimerLabel = new JLabel("Black: 05:00", SwingConstants.CENTER);
        whiteTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        blackTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerPanel.add(whiteTimerLabel);
        timerPanel.add(blackTimerLabel);
        add(timerPanel, BorderLayout.NORTH);

        add(statusLabel, BorderLayout.SOUTH);
		
		// --- Menu Bar ---
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		JMenuItem newGameItem = new JMenuItem("New Game");
		JMenuItem saveGameItem = new JMenuItem("Save Game");
		JMenuItem loadGameItem = new JMenuItem("Load Game");
        JMenuItem resetTimers = new JMenuItem("Reset Timers");
        resetTimers.addActionListener(e -> {
            whiteSeconds = 300;
            blackSeconds = 300;
            updateTimerLabels();
        });
        gameMenu.add(resetTimers);

        newGameItem.addActionListener(e -> {
            boardPanel.resetBoard();
            resetTimers();   // 🕒 restart the clocks
        });

        saveGameItem.addActionListener(e -> boardPanel.saveGame());
		loadGameItem.addActionListener(e -> boardPanel.loadGame());

		gameMenu.add(newGameItem);
		gameMenu.add(saveGameItem);
		gameMenu.add(loadGameItem);

		menuBar.add(gameMenu);
		setJMenuBar(menuBar);

        setLocationRelativeTo(null);
        startTurnTimer();
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
    // --- Turn Timer Logic ---
    private void startTurnTimer() {
        if (swingTimer != null && swingTimer.isRunning()) swingTimer.stop();

        swingTimer = new javax.swing.Timer(1000, e -> {
            if (whiteTurn) {
                whiteSeconds--;
                if (whiteSeconds <= 0) endGameOnTimeout("Black");
            } else {
                blackSeconds--;
                if (blackSeconds <= 0) endGameOnTimeout("White");
            }
            updateTimerLabels();
        });
        swingTimer.start();
    }

    private void updateTimerLabels() {
        whiteTimerLabel.setText("White: " + formatTime(whiteSeconds));
        blackTimerLabel.setText("Black: " + formatTime(blackSeconds));
    }
    // --- Reset timers to full time and restart for White ---
    public void resetTimers() {
        if (swingTimer != null) swingTimer.stop();
        whiteSeconds = 300;  // 5:00 for white
        blackSeconds = 300;  // 5:00 for black
        whiteTurn = true;    // White always starts
        updateTimerLabels();
        startTurnTimer();    // restart countdown
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void endGameOnTimeout(String winner) {
        swingTimer.stop();
        JOptionPane.showMessageDialog(this,
                "⏰ Time's up! " + winner + " wins!",
                "Timeout",
                JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
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
    public void switchTurnTimer(boolean whiteTurnNow) {
        this.whiteTurn = whiteTurnNow;
        startTurnTimer();
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}
