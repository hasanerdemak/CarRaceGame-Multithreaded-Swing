public interface PilotInterface extends Runnable {
    int getID();

    Car getCar();

    void handleMovement();

    void setSleepTime(int sleepDuration);
}
