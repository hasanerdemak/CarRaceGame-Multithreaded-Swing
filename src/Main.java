import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //new CarRace().game(2);
        SwingUtilities.invokeLater(() -> new CarRace().game(150));
    }
}
