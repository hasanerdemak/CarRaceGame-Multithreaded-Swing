import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RacePanel extends JPanel {
    public static final int OUTER_CIRCLE_X = 20;
    public static final int OUTER_CIRCLE_Y = 40;
    public static final int OUTER_CIRCLE_DIAMETER = 700;
    public static final int INNER_CIRCLE_X = 170;
    public static final int INNER_CIRCLE_Y = 190;
    public static final int INNER_CIRCLE_DIAMETER = 400;
    public static ArrayList<Car> cars = new ArrayList<>();
    public static ArrayList<PilotInterface> pilots = new ArrayList<>();
    public static ArrayList<Runnable> runnables = new ArrayList<>();
    public static boolean gameOver = false;
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private Player player1;
    private Player player2;
    private Bot[] bots;
    private Timer timer;
    private long startTime;
    private JLabel timerLabel;

    public RacePanel(int n) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setFocusable(true);

        var redCar = new Car(0, 25, 385, 4, Color.RED);
        var greenCar = new Car(1, 45, 385, 2, Color.GREEN);
        cars.add(redCar);
        cars.add(greenCar);
        player1 = new Player(this, redCar, 1, 'W', 'S', 'A', 'D');
        player2 = new Player(this, greenCar, 2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        pilots.add(player1);
        pilots.add(player2);
        runnables.add(player1);
        runnables.add(player2);
        addKeyListener(player1);
        addKeyListener(player2);

        bots = new Bot[5];
        for (int i = 0; i < bots.length; i++) {
            int startX = 45 + (i + 1) * 20;
            int startY = 385;
            var newCar = new Car(2 + i, startX, startY, 1, Color.BLACK);
            bots[i] = new Bot(this, newCar, i + 1, n);
            add(newCar);
            cars.add(newCar);
            pilots.add(bots[i]);
            runnables.add(bots[i]);
        }

        timerLabel = new JLabel("00:00:00");
        timerLabel.setBounds(10, 10, 100, 30);
        add(timerLabel, FlowLayout.LEFT);
        startTime = System.currentTimeMillis();
        timer = new Timer(10, new TimerListener());

        //drawParkour(getGraphics());
    }

    public void startRace() {
        if (timer.isRunning()) {
            return;
        }

        for (var runnable: runnables) {
            var newThread = new Thread(runnable);
            newThread.start();
        }

        timer.start();

        setVisible(true);
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
        g.drawLine(20, 390, 20 + (OUTER_CIRCLE_DIAMETER - INNER_CIRCLE_DIAMETER) / 2, 390);
        drawParkour(g);
        drawSquares(g);

        checkWinner();
    }

    private void drawParkour(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawOval(OUTER_CIRCLE_X, OUTER_CIRCLE_Y, OUTER_CIRCLE_DIAMETER, OUTER_CIRCLE_DIAMETER);
        g.drawOval(INNER_CIRCLE_X, INNER_CIRCLE_Y, INNER_CIRCLE_DIAMETER, INNER_CIRCLE_DIAMETER);
    }

    public void drawSquares(Graphics g) {
        for (var car : cars) {
            car.draw(g);
        }
    }

    public void checkWinner() {
        if (!gameOver) {
            for (var pilot : pilots) {
                var car = pilot.getCar();
                if (RaceUtils.isCarPassedFinishLine(car)) {
                    gameOver = true;
                    timer.stop();
                    String pilotType = (pilot instanceof Player) ? "Oyuncu" : "Bot";
                    String message = pilot.getID() + ". " + pilotType + " Kazandı! Süresi " + timerLabel.getText();
                    JOptionPane.showMessageDialog(this, message, "Oyun Bitti", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;

            int minutes = (int) (elapsedTime / 60000);
            int seconds = (int) ((elapsedTime % 60000) / 1000);
            int millis = (int) (elapsedTime % 1000);

            String timeStr = String.format("%02d:%02d:%02d", minutes, seconds, millis / 10);
            timerLabel.setText(timeStr);
        }
    }
}
