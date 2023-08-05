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

        racePanel = new RacePanel();
        add(racePanel);
        pack();

        setVisible(true);
    }

    public void game(int n) {
        racePanel.setFPS(n);
        racePanel.startGame();
        racePanel.requestFocus();
    }
}