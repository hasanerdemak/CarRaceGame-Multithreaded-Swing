import java.awt.*;

public class Car {
    public static final int SIZE = 10;
    private int carID;
    private int carX, carY;
    private int lastX, lastY;
    private int speed;
    private boolean disabled;
    private Color color;
    private Color disabledColor; // New variable to store the disabled color

    private String label = "";

    public Car(int carID, int carX, int carY, int speed, Color color) {
        this.carID = carID;
        this.carX = carX;
        this.carY = carY;
        this.speed = speed;
        this.color = color;
        this.disabledColor = new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2); // Initialize the disabled color

        lastX = carX;
        lastY = carY;
    }

    public Rectangle getBounds() {
        return new Rectangle(carX, carY, SIZE, SIZE);
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

    public void disabled() {
        disabled = true;
    }

    public void enabled() {
        disabled = false;
    }

    public void moveCar(int dx, int dy) {
        lastX = carX;
        lastY = carY;
        carX += dx;
        carY += dy;
    }

    public void draw(Graphics g) {
        Color currentColor = disabled ? disabledColor : color;
        g.setColor(currentColor);
        g.fillRect(carX, carY, SIZE, SIZE);
        g.drawString(label, carX - ((label.length() * g.getFont().getSize()) / 8), carY - 5);
    }
}
