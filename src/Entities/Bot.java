package Entities;

import Utils.RaceUtils;

import java.util.Random;

public class Bot extends AbstractPilot {
    Random random = new Random();

    public Bot(Car car, int botID) {
        super(botID, car);

        car.setLabel("Bot " + botID);
    }

    @Override
    public void handleMovement() {
        var car = getCar();
        if (RaceUtils.checkCollisionsForCar(car)) {
            setSleepTime(500);
        }

        int speed = car.getSpeed();
        int currentX = car.getCarX();
        int currentY = car.getCarY();
        int newX, newY;
        int newDX = 0, newDY = 0;
        do {
            int randomNumber = random.nextInt(8);

            if (randomNumber == 0) {
                newDY = -speed;
            } else if (randomNumber == 1) {
                newDY = speed;
            } else if (randomNumber == 2) {
                newDX = -speed;
            } else if (randomNumber == 3) {
                newDX = speed;
            } else if (randomNumber == 4) {
                newDY = -speed;
                newDX = -speed;
            } else if (randomNumber == 5) {
                newDY = -speed;
                newDX = speed;
            } else if (randomNumber == 6) {
                newDY = speed;
                newDX = -speed;
            } else if (randomNumber == 7) {
                newDY = speed;
                newDX = speed;
            }

            newX = currentX + newDX;
            newY = currentY + newDY;
        } while (RaceUtils.isInOppositeDirection(currentX, currentY, newX, newY));

        car.moveCar(newDX, newDY);
    }

}