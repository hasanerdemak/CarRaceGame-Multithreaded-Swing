package temp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Kare extends JPanel implements KeyListener, ActionListener {
    private int x = 200;
    private int y = 200;
    private int kareBoyutu = 50;
    private boolean wPressed = false;
    private boolean aPressed = false;
    private boolean sPressed = false;
    private boolean dPressed = false;
    private Timer timer;

    public Kare() {
        timer = new Timer(10, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.fillRect(x, y, kareBoyutu, kareBoyutu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        handleMovement();
        repaint();
    }

    private void handleMovement() {
        int speed = 5;
        if (wPressed && !sPressed) {
            y -= speed;
        } else if (sPressed && !wPressed) {
            y += speed;
        }

        if (aPressed && !dPressed) {
            x -= speed;
        } else if (dPressed && !aPressed) {
            x += speed;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) {
            wPressed = true;
        } else if (keyCode == KeyEvent.VK_S) {
            sPressed = true;
        } else if (keyCode == KeyEvent.VK_A) {
            aPressed = true;
        } else if (keyCode == KeyEvent.VK_D) {
            dPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) {
            wPressed = false;
        } else if (keyCode == KeyEvent.VK_S) {
            sPressed = false;
        } else if (keyCode == KeyEvent.VK_A) {
            aPressed = false;
        } else if (keyCode == KeyEvent.VK_D) {
            dPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
