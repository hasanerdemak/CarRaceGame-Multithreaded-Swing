package temp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TwoPlayerGame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private Square player1Square;
    private Square player2Square;

    public TwoPlayerGame() {
        setTitle("Two-Player Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        player1Square = new Square(50, 50, Color.RED);
        player2Square = new Square(300, 300, Color.BLUE);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                moveSquare(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    private void moveSquare(KeyEvent e) {
        int step = 10;
        int player1SquareSpeed = 5;
        int player2SquareSpeed = 5;

        switch (e.getKeyCode()) {
            // Player 1 controls using arrow keys
            case KeyEvent.VK_UP:
                player1Square.move(0, -player1SquareSpeed);
                break;
            case KeyEvent.VK_DOWN:
                player1Square.move(0, player1SquareSpeed);
                break;
            case KeyEvent.VK_LEFT:
                player1Square.move(-player1SquareSpeed, 0);
                break;
            case KeyEvent.VK_RIGHT:
                player1Square.move(player1SquareSpeed, 0);
                break;

            // Player 2 controls using WASD keys
            case KeyEvent.VK_W:
                player2Square.move(0, -player2SquareSpeed);
                break;
            case KeyEvent.VK_S:
                player2Square.move(0, player2SquareSpeed);
                break;
            case KeyEvent.VK_A:
                player2Square.move(-player2SquareSpeed, 0);
                break;
            case KeyEvent.VK_D:
                player2Square.move(player2SquareSpeed, 0);
                break;
        }

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.drawOval(20, 40, 700, 700);
        g.drawOval(170, 190, 400, 400);

        player1Square.draw(g);
        player2Square.draw(g);
    }

    private static class Square {
        private int x;
        private int y;
        private int size;
        private Color color;

        public Square(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.size = 50;
            this.color = color;
        }

        public void move(int dx, int dy) {
            x += dx;
            y += dy;
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, size, size);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TwoPlayerGame game = new TwoPlayerGame();
            game.setVisible(true);
        });
    }
}

