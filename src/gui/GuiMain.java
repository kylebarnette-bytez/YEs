package gui;

import javax.swing.SwingUtilities;

/**
 * GuiMain.java
 * ------------------------------------------------------------
 * A separate entry point used solely for testing GUI components.
 * This allows GUI development without interfering with
 * the core game logic in Main.java.
 *
 * Run this file to launch the GUI without starting the game loop.
 * ------------------------------------------------------------
 */
public class GuiMain {

    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI();
            gui.setVisible(true);
        });
    }
}
