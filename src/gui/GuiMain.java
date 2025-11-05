package gui;

import javax.swing.SwingUtilities;

/**
 * Standalone entry point for testing or launching the Chess GUI.
 * Runs independently from any game logic or backend processes.
 */
public class GuiMain {

    /**
     * Launches the Chess GUI using the Swing event dispatch thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI();
            gui.setVisible(true);
        });
    }
}
