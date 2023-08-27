import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Car {
    public static final int SIZE = 15;
    private static final int BLINK_INTERVAL_INITIAL = 100;
    private static final int BLINK_INTERVAL_DECREMENT = 10;
    private static final int BLINK_INTERVAL_MINIMUM = 5;
    private final Color color;
    private final Color disabledColor;
    private final Timer blinkingTimer;
    private int carID;
    private int carX, carY;
    private int lastX, lastY;
    private int speed;
    private boolean disabled;
    private String label = "";
    private boolean isBlinking = false;

    private BufferedImage carImage;

    private double rotationAngle = 0.0;

    public Car(int carID, int carX, int carY, int speed, Color color) {
        this.carID = carID;
        this.carX = this.lastX = carX;
        this.carY = this.lastY = carY;
        this.speed = speed;
        this.color = color;
        this.disabledColor = new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2);


        try {
            carImage = ImageIO.read(new File("C:\\Users\\HasanErdemAK\\IdeaProjects\\CarRaceGame-Multithreaded-Swing\\car-image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        blinkingTimer = new Timer(BLINK_INTERVAL_INITIAL, e -> toggleBlinking());
    }

    public Rectangle getBounds() {
        return new Rectangle(carX, carY, SIZE, SIZE);
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public int getCarX() {
        return carX;
    }

    public void setCarX(int carX) {
        this.carX = carX;
    }

    public int getCarY() {
        return carY;
    }

    public void setCarY(int carY) {
        this.carY = carY;
    }

    public int getLastX() {
        return lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void reset(int x, int y) {
        carX = lastX = x;
        carY = lastY = y;
        disabled = false;
        isBlinking = false;
    }

    public void disabled() {
        disabled = true;
    }

    public void enabled() {
        disabled = false;

        startBlinking();
    }

    private void toggleBlinking() {
        isBlinking = !isBlinking;
        if (blinkingTimer.getDelay() >= BLINK_INTERVAL_MINIMUM) {
            blinkingTimer.setDelay(blinkingTimer.getDelay() - BLINK_INTERVAL_DECREMENT);
        } else {
            stopBlinking();
        }
    }

    private void startBlinking() {
        isBlinking = true;
        blinkingTimer.setDelay(BLINK_INTERVAL_INITIAL);
        blinkingTimer.start();
    }

    private void stopBlinking() {
        isBlinking = false;
        blinkingTimer.stop();
    }

    public void moveCar(int dx, int dy) {
        if (!disabled && !RaceUtils.checkCheat(this, dy)) {
            if (dx != 0) lastX = carX;
            if (dy != 0) lastY = carY;
            carX += dx;
            carY += dy;
        }

        if (dx != 0 || dy != 0){
            rotationAngle = Math.atan2(dy, dx);
        }

    }

    /*public void draw(Graphics g) {
        if (!isBlinking || RacePanel.getInstance().isGameOver()) {
            Color currentColor = disabled ? disabledColor : color;
            g.setColor(currentColor);
            g.fillRect(carX, carY, SIZE, SIZE);
            g.drawString(label, carX - ((label.length() * g.getFont().getSize()) / 8), carY - 5);
        }
    }*/

    public void draw(Graphics g) {
        if (!isBlinking || RacePanel.getInstance().isGameOver()) {
            // Draw the rotated image
            g.drawString(label, carX - ((label.length() * g.getFont().getSize()) / 8), carY - 5);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.rotate(rotationAngle, carX + SIZE / 2, carY + SIZE / 2);
            g2d.drawImage(carImage, carX, carY, SIZE, SIZE, null);
            g2d.dispose();
        }
    }

}
