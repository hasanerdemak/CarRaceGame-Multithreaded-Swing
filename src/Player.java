import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Player implements KeyListener, PilotInterface {
    private int playerID;
    private Car car;
    private int upKey;
    private int downKey;
    private int leftKey;
    private int rightKey;
    private boolean upKeyPressed;
    private boolean downKeyPressed;
    private boolean leftKeyPressed;
    private boolean rightKeyPressed;
    private int fps = 5;

    public Player(Car car, int playerID, int upKey, int downKey, int leftKey, int rightKey) {
        this.car = car;
        this.playerID = playerID;
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;

        car.setLabel("Player " + playerID);
    }

    @Override
    public int getID() {
        return playerID;
    }

    @Override
    public Car getCar() {
        return car;
    }

    @Override
    public void setFPS(int fps) {
        this.fps = fps;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        upKeyPressed = downKeyPressed = rightKeyPressed = leftKeyPressed = false;
        while (!RacePanel.getInstance().isGameOver()) {
            handleMovement();
            try {
                Thread.sleep(1000 / fps); // Adjust this value for player speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == upKey) {
            upKeyPressed = true;
        } else if (keyCode == downKey) {
            downKeyPressed = true;
        } else if (keyCode == leftKey) {
            leftKeyPressed = true;
        } else if (keyCode == rightKey) {
            rightKeyPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == upKey) {
            upKeyPressed = false;
        } else if (keyCode == downKey) {
            downKeyPressed = false;
        } else if (keyCode == leftKey) {
            leftKeyPressed = false;
        } else if (keyCode == rightKey) {
            rightKeyPressed = false;
        }
    }

    @Override
    public void handleMovement() {
        if (RaceUtils.checkCollisionsForCar(car)) {
            setSleepTime(500);
        }

        int speed = car.getSpeed();
        int dx = 0, dy = 0;

        if (upKeyPressed) dy -= speed;
        if (downKeyPressed) dy += speed;
        if (leftKeyPressed) dx -= speed;
        if (rightKeyPressed) dx += speed;

        car.moveCar(dx, dy);
    }

    @Override
    public void setSleepTime(int sleepDuration) {
        car.disabled();

        var point = RaceUtils.getRandomPointWithSameAngle(car);

        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        car.enabled();

        // Move the car to the new starting position
        car.setCarX(point.x);
        car.setCarY(point.y);
    }



}


/*

@Override
    public void setSleepTime(int sleepDuration) {
        car.disabled();
        int startX = car.getLastX();
        int startY = car.getLastY();

        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        car.enabled();

        // Move the car back to the starting position (empty point)
        car.setCarX(startX);
        car.setCarY(startY);
    }

 */