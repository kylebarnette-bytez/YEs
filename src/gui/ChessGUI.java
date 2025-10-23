package gui;

import javax.swing.*;
import java.awt.*;

public class ChessGUI extends JFrame {

    private BoardPanel boardPanel;

    public ChessGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 800);

        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null); // center window
        setVisible(true);
    }
}
