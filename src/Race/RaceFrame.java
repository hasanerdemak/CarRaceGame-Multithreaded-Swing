package Race;

import javax.swing.*;

public class RaceFrame extends JFrame {

    public static final int WIDTH = 900;
    public static final int HEIGHT = 800;
    private RacePanel racePanel;

    public RaceFrame() {
        setTitle("Car Race");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        racePanel = RacePanel.getInstance();
        add(racePanel);
        pack();

        setVisible(true);
    }

    public void game() {
        racePanel.startGame();
        racePanel.requestFocus();
    }
}