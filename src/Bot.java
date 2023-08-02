import java.util.Random;

public class Bot implements Runnable, PilotInterface {
    Random random = new Random();
    private int botID;
    private RacePanel racePanel;
    private Car car;
    private int dx, dy;
    private int n;

    public Bot(RacePanel racePanel, Car car, int botID, int n) {
        this.car = car;
        this.racePanel = racePanel;
        this.botID = botID;
        this.n = n;
    }

    @Override
    public int getID() {
        return botID;
    }

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
            racePanel.repaint();
            try {
                Thread.sleep(1000 / n);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMovement() {
        if (RaceUtils.checkCollisionsForCar(car)) {
            setSleepTime(500);
        }

        int speed = car.getSpeed();
        int currentX = car.getCarX();
        int currentY = car.getCarY();
        int newX, newY;
        int newDX = 0, newDY = 0;
        do {
            int randomNumber = random.nextInt(4);

            if (randomNumber == 0) {
                newDY = -speed;
            } else if (randomNumber == 1) {
                newDY = speed;
            } else if (randomNumber == 2) {
                newDX = -speed;
            } else {
                newDX = speed;
            }

            newX = currentX + newDX;
            newY = currentY + newDY;
        } while (RaceUtils.isInOppositeDirection(currentX, currentY, newX, newY));

        car.moveCar(newDX, newDY);
    }


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
        car.moveCar(3 * dx, 3 * dy);
    }

}



/*
    private void handleMovement() {
        if (RaceUtils.checkCollisionsForCar(car)) {
            setSleepTime(500);
        }

        do {

        } while (RaceUtils.isInOppositeDirection(currentX, currentY, newX, newY))

        int randomNumber = random.nextInt(4);
        int currentX = car.getCarX();
        int currentY = car.getCarY();

        int speed = car.getSpeed();
        int newDX = 0;
        int newDY = 0;

        if (randomNumber == 0) {
            newDY = -speed;
        } else if (randomNumber == 1) {
            newDY = speed;
        } else if (randomNumber == 2) {
            newDX = -speed;
        } else {
            newDX = speed;
        }

        int newX = currentX + newDX;
        int newY = currentY + newDY;

        // Check if the new position is in the opposite direction of clockwise movement
        if (RaceUtils.isInOppositeDirection(currentX, currentY, newX, newY)) {
            handleMovement(); // Choose a new random direction
            return;
        }

        car.moveCar(newDX, newDY);
    }
 */