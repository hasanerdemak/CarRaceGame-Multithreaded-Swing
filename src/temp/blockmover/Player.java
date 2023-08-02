package temp.blockmover;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player implements KeyListener, Runnable {
    private Block block;
    private boolean running;
    private int dx, dy;

    public Player(Block block) {
        this.block = block;
        this.running = true;
        this.dx = 0;
        this.dy = 0;
    }

    public void run() {
        while (running) {
            System.out.println(block.x);
            try {
                Thread.sleep(1); // Adjust this value for player speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            block.move(dx, dy);
        }
    }

    public void stopPlayer() {
        running = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) {
            dy = -1;
        } else if (key == KeyEvent.VK_S) {
            dy = 1;
        } else if (key == KeyEvent.VK_A) {
            dx = -1;
        } else if (key == KeyEvent.VK_D) {
            dx = 1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_S) {
            dy = 0;
        } else if (key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
            dx = 0;
        }
    }
}