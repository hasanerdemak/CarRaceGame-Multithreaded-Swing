import javax.swing.*;
import java.awt.*;

public class Car extends JPanel {
    public static final int SIZE = 10;
    private int carID;
    private int carX, carY;
    private int lastX, lastY;
    private int speed;
    private Color color;
    private boolean disabled;

    public Car(int carID, int carX, int carY, int speed, Color color) {
        this.carID = carID;
        this.carX = carX;
        this.carY = carY;
        this.speed = speed;
        this.color = color;

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
        //g.setColor(new Color(236, 236, 236));
        //g.fillRect(lastX, lastY, SIZE, SIZE);

        Color currentColor = color;
        if (disabled) {
            int paleRed = currentColor.getRed() / 2;
            int paleGreen = currentColor.getGreen() / 2;
            int paleBlue = currentColor.getBlue() / 2;
            currentColor = new Color(paleRed, paleGreen, paleBlue);
        }
        g.setColor(currentColor);
        g.fillRect(carX, carY, SIZE, SIZE);

        String str = (speed == 1) ? "Bot" : "Player";
        str += " " + carID;
        g.drawString(str, carX - str.length()/2, carY - 5);
    }
}
