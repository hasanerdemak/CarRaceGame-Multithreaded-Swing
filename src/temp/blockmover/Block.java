package temp.blockmover;

import javax.swing.*;
import java.awt.*;

public class Block extends JPanel {

    public int x, y;

    public Block(int x, int y) {
        this.x = x; // Initial x-coordinate of the block
        this.y = y; // Initial y-coordinate of the block
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(x, y, 50, 50); // Size of the block
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400); // Set the size of the game window
    }
}
