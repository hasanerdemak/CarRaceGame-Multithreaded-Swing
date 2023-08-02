package temp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ArabaYarisi extends JFrame {
    private final int WIDTH = 1000;
    private final int HEIGHT = 800;
    private final int CAR_SIZE = 40;
    private final int OUTER_CIRCLE_X = 20;
    private final int OUTER_CIRCLE_Y = 40;
    private final int OUTER_CIRCLE_WIDTH = 700;
    private final int OUTER_CIRCLE_HEIGHT = 700;
    private final int INNER_CIRCLE_X = 170;
    private final int INNER_CIRCLE_Y = 190;
    private final int INNER_CIRCLE_WIDTH = 400;
    private final int INNER_CIRCLE_HEIGHT = 400;
    private int carX, carY;
    private Timer timer;

    public ArabaYarisi() {
        setTitle("Araba Yarışı");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        carX = 550;//WIDTH / 2 - CAR_SIZE / 2;
        carY = 550;//HEIGHT / 2 - CAR_SIZE / 2;

        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveCar();
                repaint();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ArabaYarisi().setVisible(true);
            }
        });
    }

    private void moveCar() {
        int direction = (int) (Math.random() * 4); // 0: up, 1: down, 2: left, 3: right

        switch (direction) {
            case 0:
                if (carY - 5 >= INNER_CIRCLE_Y - CAR_SIZE) {
                    carY -= 5;
                }
                break;
            case 1:
                if (carY + 5 + CAR_SIZE <= INNER_CIRCLE_Y + INNER_CIRCLE_HEIGHT) {
                    carY += 5;
                }
                break;
            case 2:
                if (carX - 5 >= OUTER_CIRCLE_X - CAR_SIZE) {
                    carX -= 5;
                }
                break;
            case 3:
                if (carX + 5 + CAR_SIZE <= OUTER_CIRCLE_X + OUTER_CIRCLE_WIDTH) {
                    carX += 5;
                }
                break;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.drawOval(OUTER_CIRCLE_X, OUTER_CIRCLE_Y, OUTER_CIRCLE_WIDTH, OUTER_CIRCLE_HEIGHT);
        g.drawOval(INNER_CIRCLE_X, INNER_CIRCLE_Y, INNER_CIRCLE_WIDTH, INNER_CIRCLE_HEIGHT);

        g.setColor(Color.RED);
        g.fillRect(carX, carY, CAR_SIZE, CAR_SIZE);
    }
}

