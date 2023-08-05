import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
        while (!RacePanel.gameOver) {
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
        int centerX = RacePanel.OUTER_CIRCLE_X + RacePanel.OUTER_CIRCLE_DIAMETER / 2;
        int centerY = RacePanel.OUTER_CIRCLE_Y + RacePanel.OUTER_CIRCLE_DIAMETER / 2;
        int collisionX = car.getCarX();
        int collisionY = car.getCarY();

        // Define the distance from the collision point to the starting point
        int distanceFromCollision = 50; // Adjust this value as desired

        // Calculate the angle between the collision point and the center of the track
        double angle = Math.atan2(collisionY - centerY, collisionX - centerX);

        // Calculate the new position for the car
        int startX = collisionX + (int) (distanceFromCollision * Math.cos(angle));
        int startY = collisionY + (int) (distanceFromCollision * Math.sin(angle));

        // Check if the new position is inside the inner circle
        int innerCircleCenterX = RacePanel.INNER_CIRCLE_X + RacePanel.INNER_CIRCLE_DIAMETER / 2;
        int innerCircleCenterY = RacePanel.INNER_CIRCLE_Y + RacePanel.INNER_CIRCLE_DIAMETER / 2;
        int distanceSquaredToInnerCircle = (startX - innerCircleCenterX) * (startX - innerCircleCenterX)
                + (startY - innerCircleCenterY) * (startY - innerCircleCenterY);
        int innerCircleRadiusSquared = (RacePanel.INNER_CIRCLE_DIAMETER / 2) * (RacePanel.INNER_CIRCLE_DIAMETER / 2);

        // Adjust the new position if it is inside the inner circle
        if (distanceSquaredToInnerCircle < innerCircleRadiusSquared) {
            startX = (int) (innerCircleCenterX + Math.cos(angle) * RacePanel.INNER_CIRCLE_DIAMETER / 2);
            startY = (int) (innerCircleCenterY + Math.sin(angle) * RacePanel.INNER_CIRCLE_DIAMETER / 2);
        }

        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        car.enabled();

        // Move the car to the new starting position
        car.setCarX(startX);
        car.setCarY(startY);
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