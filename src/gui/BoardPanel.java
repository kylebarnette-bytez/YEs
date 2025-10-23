package gui;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private final int BOARD_SIZE = 8;

    public BoardPanel() {
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquarePanel square = new SquarePanel(row, col);
                add(square);
            }
        }
    }
}
