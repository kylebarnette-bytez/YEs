package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SquarePanel extends JPanel {

    private int row, col;
    private Color lightColor = new Color(240, 217, 181);
    private Color darkColor = new Color(181, 136, 99);

    public SquarePanel(int row, int col) {
        this.row = row;
        this.col = col;
        setBackground((row + col) % 2 == 0 ? lightColor : darkColor);
        setPreferredSize(new Dimension(80, 80));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Clicked square: (" + row + ", " + col + ")");
            }
        });
    }
}
