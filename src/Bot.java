import java.util.Random;

public class Bot implements PilotInterface {
    Random random = new Random();
    private int botID;
    private RacePanel racePanel;
    private Car car;
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
            try {
                Thread.sleep(1000 / n);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            racePanel.repaint();
        }
    }

    @Override
    public void handleMovement() {
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
        car.moveCar(3 * dx, 3 * dy);
    }

}