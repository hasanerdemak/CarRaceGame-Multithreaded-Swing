import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RacePanel extends JPanel {
    private static RacePanel instance;
    private Parkour parkour = new Parkour();
    private ArrayList<Car> cars = new ArrayList<>();
    private ArrayList<PilotInterface> pilots = new ArrayList<>();
    private boolean gameOver = false;
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private Player player1;
    private Player player2;
    private Bot[] bots;
    private Timer stopwatchTimer;
    private Timer paintRaceTimer;
    private long startTime;
    private JLabel timerLabel;
    private int fps = 5;

    private RacePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setFocusable(true);

        var redCar = new Car(1, 25, 385, 2, Color.RED);
        var greenCar = new Car(2, 45, 385, 2, Color.GREEN);
        cars.add(redCar);
        cars.add(greenCar);
        player1 = new Player(redCar, 1, 'W', 'S', 'A', 'D');
        player2 = new Player(greenCar, 2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        pilots.add(player1);
        pilots.add(player2);
        addKeyListener(player1);
        addKeyListener(player2);

        bots = new Bot[5];
        for (int i = 0; i < bots.length; i++) {
            int startX = 45 + (i + 1) * 20;
            int startY = 385;
            var newCar = new Car(3 + i, startX, startY, 1, Color.BLACK);
            bots[i] = new Bot(newCar, i + 1);
            cars.add(newCar);
            pilots.add(bots[i]);
        }

        timerLabel = new JLabel("00:00:00");
        timerLabel.setBounds(10, 10, 100, 30);
        add(timerLabel, FlowLayout.LEFT);

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
        });

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

    public ArrayList<PilotInterface> getPilots() {
        return pilots;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Parkour getParkour() {
        return parkour;
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }

    public void startGame() {
        startTime = System.currentTimeMillis();

        for (var pilot : pilots) {
            pilot.setFPS(fps);
            var newThread = new Thread(pilot);
            newThread.start();
        }

        paintRaceTimer.setDelay(1000 / fps);

        stopwatchTimer.start();
        paintRaceTimer.start();
    }

    private void resetGame() {
        gameOver = false;
        for (int i = 0; i < pilots.size(); i++) {
            var car = pilots.get(i).getCar();
            int x = 25 + i * 20;
            int y = parkour.FINISH_LINE_Y- Car.SIZE/2;
            car.reset(x, y);
        }
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
            car.draw(g);
        }
    }

    public synchronized void checkWinner() {
        if (!gameOver) {
            for (var pilot : pilots) {
                var car = pilot.getCar();
                if (RaceUtils.isCarPassedFinishLine(car)) {
                    gameOver = true;
                    stopwatchTimer.stop();
                    paintRaceTimer.stop();
                    String pilotType = (pilot instanceof Player) ? "Oyuncu" : "Bot";
                    String message = pilot.getID() + ". " + pilotType + " Kazandı! Süresi " + timerLabel.getText();
                    //JOptionPane.showMessageDialog(this, message, "Oyun Bitti", JOptionPane.INFORMATION_MESSAGE);
                    String[] options = {"Yeniden Başla", "Oyundan Çık"};

                    int choice = JOptionPane.showOptionDialog(null, message, "Oyun Bitti", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                    if (choice == JOptionPane.YES_OPTION) { // Restart
                        resetGame();
                        startGame();
                    } else if (choice == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    } else if (choice == JOptionPane.CLOSED_OPTION) {
                        System.exit(0);
                    }
                    break;
                }
            }
        }
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
