import javax.swing.*;

public class CarRace extends JFrame {

    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private RacePanel racePanel;

    public CarRace() {
        setTitle("Car Race");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void game(int n) {
        racePanel = new RacePanel(n);
        add(racePanel);
        pack();

        racePanel.startRace();
        racePanel.requestFocus();

        setVisible(true);
    }
}