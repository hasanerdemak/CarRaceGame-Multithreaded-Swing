package Race;

import Entities.*;
import Utils.RaceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RacePanel extends JPanel {

    // region Global Variables
    private static RacePanel instance;
    private final static int WIDTH = 900;
    private final static int HEIGHT = 800;
    private final Parkour parkour = new Parkour();
    private final ArrayList<Car> cars = new ArrayList<>();
    private final ArrayList<AbstractPilot> pilots = new ArrayList<>();
    private JLabel rankingLabel;
    private JLabel timerLabel;
    private boolean gameOver = false;
    private Difficulty difficulty = Difficulty.Medium;
    private int playerCount = 2;
    private int botCount = 0;
    private Player player1;
    private Player player2;
    private Bot[] bots;
    private Timer stopwatchTimer;
    private Timer paintRaceTimer;
    private long startTime;
    private long pauseTime;
    private boolean paused = false;
    private int fps = 100;
    private int totalTourCount = 2;

    // endregion

    private RacePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setFocusable(true);

        createTimerLabel();
        createRankingLabel();
        createBottomPanel();

        initializeTimers();
    }

    public static RacePanel getInstance() {
        if (instance == null) {
            instance = new RacePanel();
        }
        return instance;
    }

    private void initializeTimers() {
        stopwatchTimer = new Timer(10, e -> {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;

            int minutes = (int) (elapsedTime / 60000);
            int seconds = (int) ((elapsedTime % 60000) / 1000);
            int millis = (int) (elapsedTime % 1000);

            String timeStr = String.format("%02d:%02d:%02d", minutes, seconds, millis / 10);
            timerLabel.setText(timeStr);
        });

        paintRaceTimer = new Timer(1000 / fps, e -> {
            repaint();
            checkWinner();
            updateRankingLabel();
        });
    }

    // region Getters and Setters

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

    // endregion

    // region Manage Game
    public void startGame() {
        makeInitialConfigurations();

        pilots.stream().map(Thread::new).forEach(Thread::start);

        stopwatchTimer.start();
        paintRaceTimer.start();

        requestFocusInWindow();
    }

    private void makeInitialConfigurations() {
        showOptionsDialog();
        initializePlayers(playerCount);
        initializeBots(botCount);

        repaint();

        for (var bot : bots) {
            bot.getCar().setSpeed(difficulty.ordinal() + 1);
        }

        startTime = System.currentTimeMillis();
        paintRaceTimer.setDelay(1000 / fps);

        pilots.forEach(x -> x.setFPS(fps));
    }

    private void resetGame() {
        cars.clear();
        pilots.clear();
        gameOver = false;
        paused = false;
        difficulty = Difficulty.Medium;
        playerCount = 2;
        botCount = 0;
        removeKeyListener(player1);
        removeKeyListener(player2);
        player1 = null;
        player2 = null;
        timerLabel.setText("00:00:00");
        rankingLabel.setText("");
    }

    private void pauseGame() {
        pauseTime = System.currentTimeMillis();
        pilots.forEach(AbstractPilot::pauseMovement);
        player1.resetKeyPresses();
        player2.resetKeyPresses();

        stopwatchTimer.stop();
        paintRaceTimer.stop();
        paused = true;

        repaint();
    }

    private void resumeGame() {
        startTime += (System.currentTimeMillis() - pauseTime);
        paused = false;

        stopwatchTimer.start();
        paintRaceTimer.start();

        pilots.forEach(AbstractPilot::resumeMovement);
        requestFocusInWindow();
    }

    // endregion

    // region Initialize Players and Bots

    private void initializePlayers(int count) {
        if (count == 1) {
            initializePlayer1();
        } else if (count == 2) {
            initializePlayer1();
            initializePlayer2();
        }
    }

    private void initializePlayer1() {
        var yPosition = parkour.OUTER_CIRCLE_Y + parkour.OUTER_CIRCLE_RADIUS - Car.SIZE / 2;
        var xPosition = parkour.OUTER_CIRCLE_X + 5;
        var redCar = new Car(1, xPosition, yPosition, 2, Color.RED);
        player1 = new Player(redCar, 1, 'W', 'S', 'A', 'D');
        cars.add(redCar);
        pilots.add(player1);
        addKeyListener(player1);
    }

    private void initializePlayer2() {
        var yPosition = parkour.OUTER_CIRCLE_Y + parkour.OUTER_CIRCLE_RADIUS - Car.SIZE / 2;
        var xPosition = parkour.OUTER_CIRCLE_X + 25;
        var greenCar = new Car(2, xPosition, yPosition, 2, Color.GREEN);
        player2 = new Player(greenCar, 2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        cars.add(greenCar);
        pilots.add(player2);
        addKeyListener(player2);
    }

    private void initializeBots(int count) {
        var startY = parkour.OUTER_CIRCLE_Y + parkour.OUTER_CIRCLE_RADIUS - Car.SIZE / 2;
        bots = new Bot[count];
        for (int i = 0; i < bots.length; i++) {
            int startX = parkour.OUTER_CIRCLE_X + 5 + (i + 2) * 20;
            var newCar = new Car(3 + i, startX, startY, difficulty.ordinal() + 1, Color.BLACK);
            bots[i] = new Bot(newCar, i + 1);
            cars.add(newCar);
            pilots.add(bots[i]);
        }
    }

    // endregion

    private void checkWinner() {
        if (!gameOver) {
            for (var pilot : pilots) {
                if (RaceUtils.isPilotPassedFinishLine(pilot)) {
                    pilot.increaseCompletedTours();
                    if (pilot.getCompletedTours() == totalTourCount) {
                        gameOver = true;
                        stopwatchTimer.stop();

                        updateRankingLabel();
                        showGameOverDialog(pilot);
                        break;
                    }
                }
            }
        }
    }

    // region Dialogs

    public void showOptionsDialog() {
        JComboBox<Integer> playerCountComboBox = new JComboBox<>(new Integer[]{1, 2});
        JComboBox<Integer> botCountComboBox = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4, 5});
        JComboBox<Integer> totalTourCountComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JComboBox<Difficulty> difficultyComboBox = new JComboBox<>(Difficulty.values());
        JComboBox<String> gameSpeedComboBox = new JComboBox<>(new String[]{"x0.5", "x1", "x2", "x3"});

        playerCountComboBox.setSelectedIndex(1);
        botCountComboBox.setSelectedIndex(0);
        totalTourCountComboBox.setSelectedIndex(1);
        difficultyComboBox.setSelectedIndex(1);
        gameSpeedComboBox.setSelectedIndex(1);

        playerCountComboBox.setPreferredSize(new Dimension(100, 25));
        botCountComboBox.setPreferredSize(new Dimension(100, 25));
        totalTourCountComboBox.setPreferredSize(new Dimension(100, 25));
        difficultyComboBox.setPreferredSize(new Dimension(100, 25));
        gameSpeedComboBox.setPreferredSize(new Dimension(100, 25));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Select Player Count:"), gbc);
        gbc.gridx = 1;
        panel.add(playerCountComboBox, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 10);

        gbc.gridx = 0;
        panel.add(new JLabel("Select Bot Count:"), gbc);
        gbc.gridx = 1;
        panel.add(botCountComboBox, gbc);

        gbc.gridy++;

        gbc.gridx = 0;
        panel.add(new JLabel("Select Tour Count:"), gbc);
        gbc.gridx = 1;
        panel.add(totalTourCountComboBox, gbc);

        gbc.gridy++;

        gbc.gridx = 0;
        var difficultyLabel = new JLabel("Select Difficulty:");
        panel.add(difficultyLabel, gbc);
        gbc.gridx = 1;
        panel.add(difficultyComboBox, gbc);
        difficultyComboBox.setEnabled(botCount > 0);
        difficultyLabel.setEnabled(botCount > 0);

        gbc.gridy++;

        gbc.gridx = 0;
        panel.add(new JLabel("Select Game Speed:"), gbc);
        gbc.gridx = 1;
        panel.add(gameSpeedComboBox, gbc);

        botCountComboBox.addActionListener(_ -> {
            boolean enabled = (int) botCountComboBox.getSelectedItem() > 0;
            difficultyComboBox.setEnabled(enabled);
            difficultyLabel.setEnabled(enabled);
        });

        int choice;
        do {
            choice = JOptionPane.showOptionDialog(
                    null,
                    panel,
                    "Options",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"Ok", "Exit"},
                    "Ok"
            );
        } while (choice == JOptionPane.CLOSED_OPTION);

        if (choice == 0) {
            playerCount = (int) playerCountComboBox.getSelectedItem();
            botCount = (int) botCountComboBox.getSelectedItem();
            totalTourCount = (int) totalTourCountComboBox.getSelectedItem();
            difficulty = (Difficulty) difficultyComboBox.getSelectedItem();
            fps = (int) (100 * (gameSpeedComboBox.getSelectedIndex() == 0 ? 0.5 : gameSpeedComboBox.getSelectedIndex()));
        } else {
            int exitChoice = showExitConfirmationDialog();
            if (exitChoice == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                showOptionsDialog();
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
        } else {
            int exitChoice = showExitConfirmationDialog();
            if (exitChoice == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                showGameOverDialog(pilot);
            }
        }
    }

    private int showExitConfirmationDialog() {
        return JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
    }

    // endregion

    // region Create Label and Buttons
    private void createTimerLabel() {
        timerLabel = new JLabel("00:00:00");
        timerLabel.setFont(new Font("Calibre", Font.BOLD, 20));
        timerLabel.setBounds(10, 10, 100, 30);
        timerLabel.setHorizontalAlignment(JLabel.LEFT);
        timerLabel.setVerticalAlignment(JLabel.NORTH);
        add(timerLabel, BorderLayout.WEST);
    }

    private void createRankingLabel() {
        rankingLabel = new JLabel();
        rankingLabel.setFont(new Font("Calibre", Font.BOLD, 18));
        rankingLabel.setVerticalAlignment(JLabel.TOP);

        add(rankingLabel, BorderLayout.EAST);
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // region Center panel for buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        JButton pauseResumeButton = new JButton("Pause");
        JButton restartButton = new JButton("Restart");
        JButton exitButton = new JButton("Exit");

        pauseResumeButton.addActionListener(_ -> {
            if (paused) {
                pauseResumeButton.setText("Pause");
                resumeGame();
            } else {
                pauseResumeButton.setText("Resume");
                pauseGame();
            }
        });

        restartButton.addActionListener(_ -> {
            pauseGame();
            var choice = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to restart the game?",
                    "Restart Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) { // Restart
                resetGame();
                startGame();
            } else {
                resumeGame();
            }
        });

        exitButton.addActionListener(_ -> {
            pauseGame();
            int choice = showExitConfirmationDialog();

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                resumeGame();
            }
        });

        buttonsPanel.add(pauseResumeButton);
        buttonsPanel.add(restartButton);
        buttonsPanel.add(exitButton);

        bottomPanel.add(buttonsPanel, BorderLayout.WEST);

        // endregion

        // region Panel for key images
        JPanel keyImagePanel = new JPanel(new GridLayout(2, 2, 20, -7));

        ImageIcon image1 = RaceUtils.createImageIcon("../resources/images/wasd_keys.png", 0.09F);
        ImageIcon image2 = RaceUtils.createImageIcon("../resources/images/arrow_keys.png", 0.09F);

        keyImagePanel.add(new JLabel("Player 1 Keys"));
        keyImagePanel.add(new JLabel("Player 2 Keys"));
        keyImagePanel.add(new JLabel(image1));
        keyImagePanel.add(new JLabel(image2));

        bottomPanel.add(keyImagePanel, BorderLayout.EAST);

        //endregion

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // endregion

    // region Rendering
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
            // Line from center to car
            //g.drawLine(parkour.PARKOUR_CENTER_X, parkour.PARKOUR_CENTER_Y, car.getCarX()+Car.SIZE/2, car.getCarY()+Car.SIZE/2);
            car.draw(g);
        }
    }

    // Rank the cars according to the number of laps they have completed and their position
    private void updateRankingLabel() {
        getPilots().sort((pilot1, pilot2) -> {
            int compareByTours = Integer.compare(pilot2.getCompletedTours(), pilot1.getCompletedTours());
            if (compareByTours == 0) {
                int compareByPosition = RaceUtils.compareByPosition(pilot1.getCar(), pilot2.getCar());
                return compareByPosition != 0 ? compareByPosition : Integer.compare(pilot1.getID(), pilot2.getID());
            } else {
                return compareByTours;
            }
        });

        StringBuilder rankingText = new StringBuilder("<html><div style='text-align: left;'>Ranking:<br>");
        for (int i = 0; i < getPilots().size(); i++) {
            AbstractPilot pilot = getPilots().get(i);
            Color color = pilot.getCar().getColor();
            String playerName = String.format("<font color='#%02x%02x%02x'>%s</font>", color.getRed(), color.getGreen(), color.getBlue(), pilot.getCar().getLabel());

            rankingText.append(i + 1).append(": ").append(playerName).append(" (").append(pilot.getCompletedTours()).append("/").append(getTotalTourCount()).append(")");
            if (i < getPilots().size() - 1) {
                rankingText.append("<br>");
            }
        }
        rankingText.append("</div></html>");

        getRankingLabel().setText(rankingText.toString());
    }

    // endregion

    private enum Difficulty {
        Easy,
        Medium,
        Hard,
        Expert
    }

    public static class Parkour {
        public final int OUTER_CIRCLE_DIAMETER = 700;
        public final int INNER_CIRCLE_DIAMETER = 400;
        public final int OUTER_CIRCLE_X = RacePanel.WIDTH / 2 - OUTER_CIRCLE_DIAMETER / 2;
        public final int OUTER_CIRCLE_Y = RacePanel.HEIGHT / 2 - OUTER_CIRCLE_DIAMETER / 2 - 20;
        public final int OUTER_CIRCLE_RADIUS = OUTER_CIRCLE_DIAMETER / 2;
        public final int INNER_CIRCLE_RADIUS = INNER_CIRCLE_DIAMETER / 2;
        public final int INNER_CIRCLE_X = OUTER_CIRCLE_X + (OUTER_CIRCLE_RADIUS - INNER_CIRCLE_RADIUS);
        public final int INNER_CIRCLE_Y = OUTER_CIRCLE_Y + (OUTER_CIRCLE_RADIUS - INNER_CIRCLE_RADIUS);
        public final int PARKOUR_CENTER_X = OUTER_CIRCLE_X + OUTER_CIRCLE_DIAMETER / 2;
        public final int PARKOUR_CENTER_Y = OUTER_CIRCLE_Y + OUTER_CIRCLE_DIAMETER / 2;
        public final int FINISH_LINE_Y = PARKOUR_CENTER_Y;
        public final int PARKOUR_WIDTH = (OUTER_CIRCLE_DIAMETER - INNER_CIRCLE_DIAMETER) / 2;

    }

}
