package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main Chess GUI window.
 * Displays the chessboard, player timers, move history,
 * and provides user controls for saving, loading, and resetting the game.
 */
public class ChessGUI extends JFrame {

    private final BoardPanel boardPanel;
    private final GameHistoryPanel historyPanel;
    private final JLabel statusLabel;

    private JLabel whiteTimerLabel;
    private JLabel blackTimerLabel;

    private javax.swing.Timer swingTimer;

    // ⬇⬇⬇ NEW turn-based timer: 30 seconds per move
    private int turnSeconds = 30;

    // Track whose turn it is
    private boolean whiteTurn = true;

    /**
     * Constructs the main Chess GUI window.
     */
    public ChessGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 800);

        // -------------------------
        // CENTER: Chess Board
        // -------------------------
        boardPanel = new BoardPanel(this);
        add(boardPanel, BorderLayout.CENTER);

        // -------------------------
        // EAST: Side Panel (Undo, Exit, History)
        // -------------------------
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

        historyPanel = new GameHistoryPanel();
        boardPanel.setHistoryPanel(historyPanel);

        sidePanel.add(controlPanel, BorderLayout.NORTH);
        sidePanel.add(historyPanel, BorderLayout.CENTER);

        add(sidePanel, BorderLayout.EAST);

        // -------------------------
        // SOUTH: Status Bar
        // -------------------------
        statusLabel = new JLabel("White's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // -------------------------
        // NORTH: Timer Bar
        // -------------------------
        JPanel timerPanel = new JPanel(new GridLayout(1, 2));

        whiteTimerLabel = new JLabel("White: 00:30", SwingConstants.CENTER);
        blackTimerLabel = new JLabel("Black: --", SwingConstants.CENTER);

        whiteTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        blackTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        timerPanel.add(whiteTimerLabel);
        timerPanel.add(blackTimerLabel);

        add(timerPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);

        // -------------------------
        // MENU BAR
        // -------------------------
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        JMenuItem loadGameItem = new JMenuItem("Load Game");
        JMenuItem resetTimersItem = new JMenuItem("Reset Turn Timer");

        resetTimersItem.addActionListener(e -> resetTimers());
        newGameItem.addActionListener(e -> {
            boardPanel.resetBoard();
            resetTimers();
        });

        saveGameItem.addActionListener(e -> boardPanel.saveGame());
        loadGameItem.addActionListener(e -> boardPanel.loadGame());

        gameMenu.add(resetTimersItem);
        gameMenu.add(newGameItem);
        gameMenu.add(saveGameItem);
        gameMenu.add(loadGameItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        // -------------------------
        // Final window setup
        // -------------------------
        setLocationRelativeTo(null);
        startTurnTimer();
        setVisible(true);
    }

    /** Displays an endgame message when checkmate occurs. */
    public void showEndgameMessage(String winner) {
        if (swingTimer != null) swingTimer.stop();
        JOptionPane.showMessageDialog(this,
                winner + " wins! Game over.",
                "Checkmate",
                JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    /** Update status text. */
    public void updateStatus(String text) {
        statusLabel.setText(text);
    }

    // --------------------------------------------------------
    //   TURN TIMER (30 seconds per turn)
    // --------------------------------------------------------
    private void startTurnTimer() {
        if (swingTimer != null && swingTimer.isRunning())
            swingTimer.stop();

        turnSeconds = 30; // reset each turn
        updateTimerLabels();

        swingTimer = new javax.swing.Timer(1000, e -> {
            turnSeconds--;

            if (turnSeconds <= 0) {
                String expired = whiteTurn ? "White" : "Black";
                String next = whiteTurn ? "Black" : "White";

                // Console debug (optional)
                System.out.println("[TIMER] " + expired + " ran out of time. Switching turn to " + next + ".");

                // Flash GUI message
                flashMessage(expired + " ran out of time — " + next + " moves.");
                JOptionPane.showMessageDialog(
                        this,
                        expired + " ran out of time — " + next + " moves!",
                        "Turn Timeout",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Switch turn, but DO NOT restart timer yet!
                whiteTurn = !whiteTurn;

                // Delay the next timer start so the flashMessage can appear
                new javax.swing.Timer(150, e2 -> {
                    // DO NOT call updateStatus here; flashMessage resets it automatically
                    startTurnTimer();  // now start the next turn’s 30 seconds
                }) {{
                    setRepeats(false);
                    start();
                }};

                return; // clean exit
            }




            updateTimerLabels();
        });

        swingTimer.start();
    }

    /** Update the timer display for whichever side's turn it is. */
    private void updateTimerLabels() {
        if (whiteTurn) {
            whiteTimerLabel.setText("White: " + formatTime(turnSeconds));
            blackTimerLabel.setText("Black: --");
        } else {
            whiteTimerLabel.setText("White: --");
            blackTimerLabel.setText("Black: " + formatTime(turnSeconds));
        }
    }

    /** Reset the turn timer manually. */
    public void resetTimers() {
        if (swingTimer != null) swingTimer.stop();
        whiteTurn = true;
        startTurnTimer();
    }

    /** Formats time into MM:SS. */
    private String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    /** Called when a move changes the turn. */
    public void switchTurnTimer(boolean whiteTurnNow) {
        this.whiteTurn = whiteTurnNow;
        startTurnTimer();
    }

    /** Show a temporary message then revert. */
    public void flashMessage(String text) {
        statusLabel.setText(text);
        new javax.swing.Timer(2000, e -> statusLabel.setText(whiteTurnText())) {{
            setRepeats(false);
            start();
        }};
    }

    private String whiteTurnText() {
        return boardPanel.isWhiteTurn() ? "White's Turn" : "Black's Turn";
    }
}
