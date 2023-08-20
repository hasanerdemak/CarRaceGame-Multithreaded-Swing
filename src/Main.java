import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //SwingUtilities.invokeLater(() -> new CarRace().game(150));
        int speed = 100;
        new CarRace().game(speed);
    }
}
