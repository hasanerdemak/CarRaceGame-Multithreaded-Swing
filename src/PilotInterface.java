public interface PilotInterface extends Runnable {
    int getID();

    Car getCar();

    void setFPS(int fps);

    void handleMovement();

    default void setSleepTime(int sleepDuration) {
        var car = getCar();
        car.disabled();

        var point = RaceUtils.getRandomPointWithSameAngle(car);

        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        car.enabled();

        car.setCarX(point.x);
        car.setCarY(point.y);
    }
}
