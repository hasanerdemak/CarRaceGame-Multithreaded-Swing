public abstract class AbstractPilot implements Runnable {
    private int id;
    private Car car;
    private int fps = 5;

    public AbstractPilot(int id, Car car) {
        this.id = id;
        this.car = car;
    }

    public int getID() {
        return id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }

    public int getFps() {
        return fps;
    }

    @Override
    public void run() {
        while (!RacePanel.getInstance().isGameOver()) {
            handleMovement();
            try {
                Thread.sleep(1000 / getFps());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void handleMovement();

    public void setSleepTime(int sleepDuration) {
        var car = getCar();
        car.disabled();

        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var point = RaceUtils.getRandomPointWithSameAngle(car);
        car.setCarX(point.x);
        car.setCarY(point.y);

        car.enabled();
    }

}
