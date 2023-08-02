package temp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class CircleAndSquareGame extends JPanel implements ActionListener {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BIG_CIRCLE_RADIUS = 100;
    private static final int SMALL_CIRCLE_RADIUS = 50;
    private static final int SQUARE_SIZE = 10;
    int direction = 0;
    private int squareX;
    private int squareY;
    private Timer timer;

    public CircleAndSquareGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        squareX = 400;
        squareY = 375;
        timer = new Timer(10, this);
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Circles and Square Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new CircleAndSquareGame());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBigCircle(g);
        drawSmallCircle(g);
        drawSquare(g);
    }

    private void drawBigCircle(Graphics g) {
        int centerX = WINDOW_WIDTH / 2;
        int centerY = WINDOW_HEIGHT / 2;
        g.drawOval(centerX - BIG_CIRCLE_RADIUS, centerY - BIG_CIRCLE_RADIUS, 2 * BIG_CIRCLE_RADIUS, 2 * BIG_CIRCLE_RADIUS);
    }

    private void drawSmallCircle(Graphics g) {
        int centerX = WINDOW_WIDTH / 2;
        int centerY = WINDOW_HEIGHT / 2;
        g.drawOval(centerX - SMALL_CIRCLE_RADIUS, centerY - SMALL_CIRCLE_RADIUS, 2 * SMALL_CIRCLE_RADIUS, 2 * SMALL_CIRCLE_RADIUS);
    }

    private void drawSquare(Graphics g) {
        g.fillRect(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void moveSquare() {
        Random random = new Random();
        //int direction = random.nextInt(4); // 0: up, 1: right, 2: down, 3: left

        switch (direction) {
            case 0:
                squareY -= 1;
                break;
            case 1:
                squareX += 1;
                break;
            case 2:
                squareY += 1;
                break;
            case 3:
                squareX -= 1;
                break;
        }
    }

    private boolean isSquareInsideCircles() {
        int bigCircleCenterX = WINDOW_WIDTH / 2;
        int bigCircleCenterY = WINDOW_HEIGHT / 2;
        int bigCircleRadiusSquared = BIG_CIRCLE_RADIUS * BIG_CIRCLE_RADIUS;

        int smallCircleCenterX = WINDOW_WIDTH / 2;
        int smallCircleCenterY = WINDOW_HEIGHT / 2;
        int smallCircleRadiusSquared = SMALL_CIRCLE_RADIUS * SMALL_CIRCLE_RADIUS;

        // Kare'nin köşeleri
        int squareLeft = squareX;
        int squareRight = squareX + SQUARE_SIZE;
        int squareTop = squareY;
        int squareBottom = squareY + SQUARE_SIZE;

        // Kare'nin sol üst köşesi
        int distanceSquared1 = (bigCircleCenterX - squareLeft) * (bigCircleCenterX - squareLeft)
                + (bigCircleCenterY - squareTop) * (bigCircleCenterY - squareTop);
        boolean isInsideBigCircle = distanceSquared1 <= bigCircleRadiusSquared;
        boolean isOutsideSmallCircle = distanceSquared1 > smallCircleRadiusSquared;

        if (!(isInsideBigCircle && isOutsideSmallCircle)) {
            return false;
        }

        // Kare'nin sağ üst köşesi
        int distanceSquared2 = (bigCircleCenterX - squareRight) * (bigCircleCenterX - squareRight)
                + (bigCircleCenterY - squareTop) * (bigCircleCenterY - squareTop);
        isInsideBigCircle = distanceSquared2 <= bigCircleRadiusSquared;
        isOutsideSmallCircle = distanceSquared2 > smallCircleRadiusSquared;

        if (!(isInsideBigCircle && isOutsideSmallCircle)) {
            return false;
        }

        // Kare'nin sol alt köşesi
        int distanceSquared3 = (bigCircleCenterX - squareLeft) * (bigCircleCenterX - squareLeft)
                + (bigCircleCenterY - squareBottom) * (bigCircleCenterY - squareBottom);
        isInsideBigCircle = distanceSquared3 <= bigCircleRadiusSquared;
        isOutsideSmallCircle = distanceSquared3 > smallCircleRadiusSquared;

        if (!(isInsideBigCircle && isOutsideSmallCircle)) {
            return false;
        }

        // Kare'nin sağ alt köşesi
        int distanceSquared4 = (bigCircleCenterX - squareRight) * (bigCircleCenterX - squareRight)
                + (bigCircleCenterY - squareBottom) * (bigCircleCenterY - squareBottom);
        isInsideBigCircle = distanceSquared4 <= bigCircleRadiusSquared;
        isOutsideSmallCircle = distanceSquared4 > smallCircleRadiusSquared;

        return isInsideBigCircle && isOutsideSmallCircle;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveSquare();
        if (!isSquareInsideCircles()) {
            direction = (direction == 0) ? 2 :0;
        }
        repaint();
    }
}


