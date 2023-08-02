import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player implements KeyListener, PilotInterface {
    private int playerID;
    private RacePanel racePanel;
    private Car car;
    private int upKey;
    private int downKey;
    private int leftKey;
    private int rightKey;
    private boolean upKeyPressed;
    private boolean downKeyPressed;
    private boolean leftKeyPressed;
    private boolean rightKeyPressed;

    public Player(RacePanel racePanel, Car car, int playerID, int upKey, int downKey, int leftKey, int rightKey) {
        this.racePanel = racePanel;
        this.car = car;
        this.playerID = playerID;
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
    }

    @Override
    public int getID() {
        return playerID;
    }

    @Override
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        while (!RacePanel.gameOver) {
            handleMovement();
            try {
                Thread.sleep(50); // Adjust this value for player speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            racePanel.repaint();
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
        if (upKeyPressed && !downKeyPressed) {
            dy -= speed;
        } else if (downKeyPressed && !upKeyPressed) {
            dy += speed;
        }

        if (leftKeyPressed && !rightKeyPressed) {
            dx -= speed;
        } else if (rightKeyPressed && !leftKeyPressed) {
            dx += speed;
        }

        car.moveCar(dx, dy);
    }

    @Override
    public void setSleepTime(int sleepDuration) {
        car.disabled();
        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        car.enabled();
        int dx = car.getLastX() - car.getCarX();
        int dy = car.getLastY() - car.getCarY();
        car.moveCar(dx, dy);
    }

}