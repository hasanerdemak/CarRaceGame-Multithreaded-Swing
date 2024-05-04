package Entities;

import Utils.RaceUtils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends AbstractPilot implements KeyListener {
    private int upKey;
    private int downKey;
    private int leftKey;
    private int rightKey;
    private boolean upKeyPressed;
    private boolean downKeyPressed;
    private boolean leftKeyPressed;
    private boolean rightKeyPressed;

    public Player(Car car, int playerID, int upKey, int downKey, int leftKey, int rightKey) {
        super(playerID, car);

        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;

        car.setLabel("Player " + playerID);
    }

    @Override
    public void run() {
        upKeyPressed = downKeyPressed = rightKeyPressed = leftKeyPressed = false;
        super.run();
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
        var car = getCar();
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

    public void resetKeyPresses() {
        upKeyPressed = false;
        downKeyPressed = false;
        leftKeyPressed = false;
        rightKeyPressed = false;
    }

}