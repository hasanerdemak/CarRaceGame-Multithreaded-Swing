import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RacePanel extends JPanel {

    private static RacePanel instance;
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private Parkour parkour = new Parkour();
    private ArrayList<Car> cars = new ArrayList<>();
    private ArrayList<AbstractPilot> pilots = new ArrayList<>();
    private boolean gameOver = false;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private int playerCount = 2;
    private int botCount = 5;
    private Player player1;
    private Player player2;
    private Bot[] bots;
    private Timer stopwatchTimer;
    private Timer paintRaceTimer;
    private long startTime;
    private JLabel timerLabel;
    private JLabel rankingLabel;
    private int fps = 5;
    private int totalTourCount = 2;

    private RacePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        //setLayout(new FlowLayout(FlowLayout.LEFT));
        setFocusable(true);

        rankingLabel = new JLabel("Sıralama: ");
        rankingLabel.setHorizontalAlignment(JLabel.RIGHT);

        setLayout(new BorderLayout());
        add(rankingLabel, BorderLayout.NORTH);

        initializeTimers();
    }

    public static RacePanel getInstance() {
        if (instance == null) {
            instance = new RacePanel();
        }
        return instance;
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public ArrayList<AbstractPilot> getPilots() {
        return pilots;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Parkour getParkour() {
        return parkour;
    }

    public Bot[] getBots() {
        return bots;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }

    public JLabel getRankingLabel() {
        return rankingLabel;
    }

    public int getTotalTourCount() {
        return totalTourCount;
    }

    public void startGame() {
        getPlayerAndBotCount();
        initializePlayers(playerCount);
        initializeBots(botCount);

        repaint();

        showDifficultyChooser(Difficulty.MEDIUM);


        for (var bot : bots) {
            bot.getCar().setSpeed(difficulty.ordinal() + 1);
        }

        startTime = System.currentTimeMillis();
        paintRaceTimer.setDelay(1000 / fps);

        Thread[] threads = new Thread[pilots.size()];
        for (int i = 0; i < threads.length; i++) {
            var pilot = pilots.get(i);
            pilot.setFPS(fps);
            threads[i] = new Thread(pilot);
        }
        for (var thread : threads) {
            thread.start();
        }

        stopwatchTimer.start();
        paintRaceTimer.start();
    }

    private void resetGame() {
        cars.clear();
        pilots.clear();
        gameOver = false;
        difficulty = Difficulty.MEDIUM;
        playerCount = 2;
        botCount = 5;
        removeKeyListener(player1);
        removeKeyListener(player2);
        player1 = null;
        player2 = null;
    }

    private void initializePlayers(int count) {
        if (count == 1) {
            initializePlayer1();
        } else if (count == 2) {
            initializePlayer1();
            initializePlayer2();
        }
    }

    private void initializePlayer1() {
        var redCar = new Car(1, 25, 385, 1, Color.RED);
        player1 = new Player(redCar, 1, 'W', 'S', 'A', 'D');
        cars.add(redCar);
        pilots.add(player1);
        addKeyListener(player1);
    }

    private void initializePlayer2() {
        var greenCar = new Car(2, 45, 385, 1, Color.GREEN);
        player2 = new Player(greenCar, 2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        cars.add(greenCar);
        pilots.add(player2);
        addKeyListener(player2);
    }

    private void initializeBots(int count) {
        bots = new Bot[count];
        for (int i = 0; i < bots.length; i++) {
            int startX = 45 + (i + 1) * 20;
            int startY = 385;
            var newCar = new Car(3 + i, startX, startY, difficulty.ordinal() + 1, Color.BLACK);
            bots[i] = new Bot(newCar, i + 1);
            cars.add(newCar);
            pilots.add(bots[i]);
        }
    }

    private void initializeTimers() {
        timerLabel = new JLabel("00:00:00");
        timerLabel.setBounds(10, 10, 100, 30);
        timerLabel.setHorizontalAlignment(JLabel.LEFT);
        //add(timerLabel, BorderLayout.NORTH);

        stopwatchTimer = new Timer(10, e -> {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;

            int minutes = (int) (elapsedTime / 60000);
            int seconds = (int) ((elapsedTime % 60000) / 1000);
            int millis = (int) (elapsedTime % 1000);

            String timeStr = String.format("%02d:%02d:%02d", minutes, seconds, millis / 10);
            timerLabel.setText(timeStr);
        });

        paintRaceTimer = new Timer(10, e -> {
            repaint();
            checkWinner();
            RaceUtils.updateRankingLabel();
        });
    }

    public void checkWinner() {
        if (!gameOver) {
            for (var pilot : pilots) {
                if (RaceUtils.isPilotPassedFinishLine(pilot)) {
                    pilot.increaseCompletedTours();
                    if (pilot.getCompletedTours() == totalTourCount){
                        gameOver = true;
                        stopwatchTimer.stop();
                        paintRaceTimer.stop();

                        showGameOverDialog(pilot);
                        break;
                    }
                }
            }
        }
    }

    public void getPlayerAndBotCount() {
        JSlider botCountSlider = new JSlider(JSlider.HORIZONTAL, 0, 5, botCount);
        botCountSlider.setMajorTickSpacing(1);
        botCountSlider.setPaintTicks(true);
        botCountSlider.setPaintLabels(true);

        JSlider playerCountSlider = new JSlider(JSlider.HORIZONTAL, 0, 2, playerCount);
        playerCountSlider.setMajorTickSpacing(1);
        playerCountSlider.setPaintTicks(true);
        playerCountSlider.setPaintLabels(true);

        String[] options = {"OK"};
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(new JLabel("Select Player Count:"));
        panel.add(playerCountSlider);
        panel.add(new JLabel("Select Bot Count:"));
        panel.add(botCountSlider);

        int choice;
        do {
            choice = JOptionPane.showOptionDialog(
                    null,
                    panel,
                    "Player&Bot Count Selection",
                    JOptionPane.OK_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );
        } while (choice == JOptionPane.CLOSED_OPTION);

        playerCount = playerCountSlider.getValue();
        botCount = botCountSlider.getValue();
    }

    public void showDifficultyChooser(Difficulty initialDifficulty) {
        JPanel panel = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton easyRadioButton = new JRadioButton("Easy");
        JRadioButton mediumRadioButton = new JRadioButton("Medium");
        JRadioButton hardRadioButton = new JRadioButton("Hard");

        buttonGroup.add(easyRadioButton);
        buttonGroup.add(mediumRadioButton);
        buttonGroup.add(hardRadioButton);

        panel.add(easyRadioButton);
        panel.add(mediumRadioButton);
        panel.add(hardRadioButton);

        // Set the initial selected button
        switch (initialDifficulty) {
            case EASY -> easyRadioButton.setSelected(true);
            case MEDIUM -> mediumRadioButton.setSelected(true);
            case HARD -> hardRadioButton.setSelected(true);
        }

        int choice;
        do {
            choice = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Select Difficulty",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );
        } while (choice == JOptionPane.CLOSED_OPTION);

        if (choice == JOptionPane.OK_OPTION) {
            if (easyRadioButton.isSelected()) {
                difficulty = Difficulty.EASY;
            } else if (mediumRadioButton.isSelected()) {
                difficulty = Difficulty.MEDIUM;
            } else if (hardRadioButton.isSelected()) {
                difficulty = Difficulty.HARD;
            }
        }
    }

    public void showGameOverDialog(AbstractPilot pilot) {
        String pilotType = (pilot instanceof Player) ? "Player" : "Bot";
        String message = pilotType + " " + pilot.getID() + " won. Finishing time is " + timerLabel.getText();

        int choice;
        do {
            choice = JOptionPane.showOptionDialog(
                    null,
                    message,
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Restart", "Quit Game"},
                    "Restart"
            );
        } while (choice == JOptionPane.CLOSED_OPTION);

        if (choice == JOptionPane.YES_OPTION) { // Restart
            resetGame();
            startGame();
        } else if (choice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y = parkour.FINISH_LINE_Y;
        int x = parkour.OUTER_CIRCLE_X;
        g.drawLine(x, y, x + parkour.PARKOUR_WIDTH, y);
        drawParkour(g);
        drawCars(g);
    }

    private void drawParkour(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawOval(parkour.OUTER_CIRCLE_X, parkour.OUTER_CIRCLE_Y, parkour.OUTER_CIRCLE_DIAMETER, parkour.OUTER_CIRCLE_DIAMETER);
        g.drawOval(parkour.INNER_CIRCLE_X, parkour.INNER_CIRCLE_Y, parkour.INNER_CIRCLE_DIAMETER, parkour.INNER_CIRCLE_DIAMETER);
    }

    private void drawCars(Graphics g) {
        for (var car : cars) {
            g.drawLine(parkour.PARKOUR_CENTER_X, parkour.PARKOUR_CENTER_Y, car.getCarX()+Car.SIZE/2, car.getCarY()+Car.SIZE/2);
            car.draw(g);

        }
    }

    private enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    public static class Parkour {
        public final int OUTER_CIRCLE_X = 20;
        public final int OUTER_CIRCLE_Y = 40;
        public final int OUTER_CIRCLE_DIAMETER = 700;
        public final int INNER_CIRCLE_X = 170;
        public final int INNER_CIRCLE_Y = 190;
        public final int INNER_CIRCLE_DIAMETER = 400;
        public final int PARKOUR_CENTER_X = OUTER_CIRCLE_X + OUTER_CIRCLE_DIAMETER / 2;
        public final int PARKOUR_CENTER_Y = OUTER_CIRCLE_Y + OUTER_CIRCLE_DIAMETER / 2;
        public final int FINISH_LINE_Y = PARKOUR_CENTER_Y;
        public final int PARKOUR_WIDTH = (OUTER_CIRCLE_DIAMETER - INNER_CIRCLE_DIAMETER) / 2;

    }

}
