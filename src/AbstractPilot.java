public abstract class AbstractPilot implements PilotInterface {
    private int id;
    private Car car;
    private int fps = 5;

    public AbstractPilot(int id, Car car) {
        this.id = id;
        this.car = car;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public void setFPS(int fps) {
        this.fps = fps;
    }

    public int getFps() {
        return fps;
    }

    @Override
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
