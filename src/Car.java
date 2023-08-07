import javax.swing.*;
import java.awt.*;

public class Car {
    public static final int SIZE = 10;
    private static final int BLINK_INTERVAL_INITIAL = 100;
    private static final int BLINK_INTERVAL_DECREMENT = 10;
    private static final int BLINK_INTERVAL_MINIMUM = 5;
    private final Color color;
    private final Color disabledColor; // New variable to store the disabled color
    private final Timer blinkingTimer;
    private int carID;
    private int carX, carY;
    private int lastX, lastY;
    private int speed;
    private boolean disabled;
    private String label = "";
    private boolean isBlinking = false;

    public Car(int carID, int carX, int carY, int speed, Color color) {
        this.carID = carID;
        this.carX = this.lastX = carX;
        this.carY = this.lastY = carY;
        this.speed = speed;
        this.color = color;
        this.disabledColor = new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2); // Initialize the disabled color

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
        if (!RaceUtils.checkCheat(this, dy)) {
            if (dx != 0) lastX = carX;
            if (dy != 0) lastY = carY;
            carX += dx;
            carY += dy;
        }
    }

    public void draw(Graphics g) {
        if (!isBlinking) {
            Color currentColor = disabled ? disabledColor : color;
            g.setColor(currentColor);
            g.fillRect(carX, carY, SIZE, SIZE);
            g.drawString(label, carX - ((label.length() * g.getFont().getSize()) / 8), carY - 5);
        }
    }

}
