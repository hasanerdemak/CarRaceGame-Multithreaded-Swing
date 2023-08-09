public interface PilotInterface extends Runnable {

    int getID();

    Car getCar();

    void setFPS(int fps);

    void handleMovement();

    void setSleepTime(int sleepDuration);
}
