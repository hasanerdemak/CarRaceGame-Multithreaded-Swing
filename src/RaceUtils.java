import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class RaceUtils {

    private static final Random random = new Random();

    // Returns true if the new position is in the opposite direction of clockwise movement
    public static boolean isInOppositeDirection(int currentX, int currentY, int newX, int newY) {
        var parkour = RacePanel.getInstance().getParkour();
        double angleCurrent = Math.atan2(currentY - parkour.OUTER_CIRCLE_Y - (double) parkour.OUTER_CIRCLE_DIAMETER / 2, currentX - parkour.OUTER_CIRCLE_X - (double) parkour.OUTER_CIRCLE_DIAMETER / 2);
        double angleNew = Math.atan2(newY - parkour.OUTER_CIRCLE_Y - (double) parkour.OUTER_CIRCLE_DIAMETER / 2, newX - parkour.OUTER_CIRCLE_X - (double) parkour.OUTER_CIRCLE_DIAMETER / 2);

        angleCurrent = Math.toDegrees(angleCurrent);
        angleNew = Math.toDegrees(angleNew);

        if (angleCurrent < 0) {
            angleCurrent += 360;
        }
        if (angleNew < 0) {
            angleNew += 360;
        }

        double clockwiseDifference = (angleNew - angleCurrent + 360) % 360;
        return clockwiseDifference > 180;
    }

    public static boolean checkCollisionsForCar(Car car) {
        if (!isCarInsideParkour(car)) {
            return true;
        }

        for (var tempCar : RacePanel.getInstance().getCars()) {
            if (!car.equals(tempCar)) {
                if (checkCollision(car, tempCar)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isEmptyPlace(Car car, int newX, int newY) {
        var rect = new Rectangle(newX, newY, Car.SIZE, Car.SIZE);
        for (var tempCar : RacePanel.getInstance().getCars()) {
            if (!car.equals(tempCar)) {
                if (rect.intersects(tempCar.getBounds())) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isCarInsideParkour(Car car) {
        int carLeft = car.getCarX();
        int carRight = car.getCarX() + Car.SIZE;
        int carTop = car.getCarY();
        int carBottom = car.getCarY() + Car.SIZE;

        return isAllCornersInsideParkour(carLeft, carRight, carTop, carBottom);
    }

    // Returns true if all four corners of the car are inside the parkour
    private static boolean isAllCornersInsideParkour(int carLeft, int carRight, int carTop, int carBottom) {
        var parkour = RacePanel.getInstance().getParkour();

        int parkourCenterX = parkour.PARKOUR_CENTER_X;
        int parkourCenterY = parkour.PARKOUR_CENTER_Y;
        int outerCircleRadiusSquared = (parkour.OUTER_CIRCLE_DIAMETER / 2) * (parkour.OUTER_CIRCLE_DIAMETER / 2);
        int innerCircleRadiusSquared = (parkour.INNER_CIRCLE_DIAMETER / 2) * (parkour.INNER_CIRCLE_DIAMETER / 2);

        return isInsideCircle(carLeft, carTop, parkourCenterX, parkourCenterY, outerCircleRadiusSquared) && isOutsideCircle(carLeft, carTop, parkourCenterX, parkourCenterY, innerCircleRadiusSquared) && isInsideCircle(carRight, carTop, parkourCenterX, parkourCenterY, outerCircleRadiusSquared) && isOutsideCircle(carRight, carTop, parkourCenterX, parkourCenterY, innerCircleRadiusSquared) && isInsideCircle(carLeft, carBottom, parkourCenterX, parkourCenterY, outerCircleRadiusSquared) && isOutsideCircle(carLeft, carBottom, parkourCenterX, parkourCenterY, innerCircleRadiusSquared) && isInsideCircle(carRight, carBottom, parkourCenterX, parkourCenterY, outerCircleRadiusSquared) && isOutsideCircle(carRight, carBottom, parkourCenterX, parkourCenterY, innerCircleRadiusSquared);
    }

    // Returns true if x and y coordinates are inside the specified circle
    private static boolean isInsideCircle(int x, int y, int centerX, int centerY, int radiusSquared) {
        int distanceSquared = (centerX - x) * (centerX - x) + (centerY - y) * (centerY - y);
        return distanceSquared <= radiusSquared;
    }

    // Returns true if x and y coordinates are outside the specified circle
    private static boolean isOutsideCircle(int x, int y, int centerX, int centerY, int radiusSquared) {
        int distanceSquared = (centerX - x) * (centerX - x) + (centerY - y) * (centerY - y);
        return distanceSquared > radiusSquared;
    }

    private static boolean checkCollision(Car car1, Car car2) {
        Rectangle rect1 = car1.getBounds();
        Rectangle rect2 = car2.getBounds();
        return rect1.intersects(rect2);
    }

    // Returns a random and available point at the same angle to the parkour center as the car.
    public static Point getRandomPointWithSameAngle(Car car) {
        var parkour = RacePanel.getInstance().getParkour();

        int carCenterX = car.getCarX() + Car.SIZE / 2;
        int carCenterY = car.getCarY() + Car.SIZE / 2;

        double angle = calculateAngleToCenter(car);

        int newX, newY;
        int coefficient = 1;
        do {
            int distanceFromOriginalPos = coefficient * random.nextInt(1, parkour.PARKOUR_WIDTH / 2);

            newX = carCenterX + (int) (distanceFromOriginalPos * Math.cos(angle)) - Car.SIZE / 2;
            newY = carCenterY + (int) (distanceFromOriginalPos * Math.sin(angle)) - Car.SIZE / 2;

            coefficient *= -1;
        } while (!isAllCornersInsideParkour(newX, newX + Car.SIZE, newY, newY + Car.SIZE) || !isEmptyPlace(car, newX, newY));

        return new Point(newX, newY);
    }

    public static boolean isPilotPassedFinishLine(AbstractPilot pilot) {
        var car = pilot.getCar();
        var parkour = RacePanel.getInstance().getParkour();
        var finishLineRect = new Rectangle(parkour.OUTER_CIRCLE_X, parkour.FINISH_LINE_Y, parkour.PARKOUR_WIDTH, 1);

        if (car.getBounds().intersects(finishLineRect) &&
                car.getLastY() + Car.SIZE / 2 > parkour.FINISH_LINE_Y &&
                car.getCarY() + Car.SIZE / 2 <= parkour.FINISH_LINE_Y) {
            if (!pilot.hasCompletedTour()) {
                pilot.setHasCompletedTour(true);
                return true;
            }
        } else {
            pilot.setHasCompletedTour(false);
        }

        return false;
    }

    public static boolean checkCheat(Car car, int dy) {
        var parkour = RacePanel.getInstance().getParkour();
        var finishLineRect = new Rectangle(parkour.OUTER_CIRCLE_X, parkour.FINISH_LINE_Y, parkour.PARKOUR_WIDTH, 1);

        return car.getBounds().intersects(finishLineRect) && dy > 0;
    }

    private static double calculateAngleToCenter(Car car) {
        var parkour = RacePanel.getInstance().getParkour();

        int parkourCenterX = parkour.OUTER_CIRCLE_X + parkour.OUTER_CIRCLE_DIAMETER / 2;
        int parkourCenterY = parkour.OUTER_CIRCLE_Y + parkour.OUTER_CIRCLE_DIAMETER / 2;
        int carCenterX = car.getCarX() + Car.SIZE / 2;
        int carCenterY = car.getCarY() + Car.SIZE / 2;

        double angle = Math.atan2(carCenterY - parkourCenterY, carCenterX - parkourCenterX);
        if (angle == Math.PI) {
            //todo açıda sıkıntı var.
            boolean a = isInOppositeDirection(car.getLastX(), car.getLastY(), car.getCarX(), car.getCarY());
//            if (car.getLabel().equals("Player 1"))
//                System.out.println(a);
            if (a || (car.getLastX() == car.getCarX() && car.getLastY() == car.getCarY())) {
                angle = -Math.PI;
            }
        }

        return angle;
    }

    public static void updateRankingLabel() {
        // Arabaları tamamladıkları tur sayısına göre sıralayın
        var racePanel = RacePanel.getInstance();
        ArrayList<AbstractPilot> pilots = racePanel.getPilots();

        pilots.sort((pilot1, pilot2) -> {
            int compareByTours = Integer.compare(pilot2.getCompletedTours(), pilot1.getCompletedTours());
            if (compareByTours == 0) {
                int compareByPosition = compareByPosition(pilot1.getCar(), pilot2.getCar());
                return compareByPosition != 0 ? compareByPosition : Integer.compare(pilot1.getID(), pilot2.getID());
            } else {
                return compareByTours;
            }
        });

        StringBuilder rankingText = new StringBuilder("<html><div style='text-align: left;'>Ranking:<br>");
        for (int i = 0; i < pilots.size(); i++) {
            AbstractPilot pilot = pilots.get(i);
            String colorHex = "#" + Integer.toHexString(pilot.getCar().getColor().getRGB()).substring(2);
            String playerName = "<font color='" + colorHex + "'>" + pilot.getCar().getLabel() + "</font>";

            rankingText.append(i + 1).append(": ").append(playerName).append(" (").append(pilot.getCompletedTours()).append("/").append(racePanel.getTotalTourCount()).append(")");
            if (i < pilots.size() - 1) {
                rankingText.append("<br>");
            }
        }
        rankingText.append("</div></html>");

        racePanel.getRankingLabel().setText(rankingText.toString());
    }


    private static int compareByPosition(Car car1, Car car2) {
        double angle1 = calculateAngleToCenter(car1);
        double angle2 = calculateAngleToCenter(car2);

        // Açıları karşılaştırırken pozitif veya negatif sonuçlara göre sıralama yapabilirsiniz
        // Eğer saat yönünün tersine hareket ediliyorsa, tersine çevirebilirsiniz.
        int result = Double.compare(angle2, angle1);

        // Eğer iki açı eşitse, başka bir ölçüte göre karşılaştırma yapabilirsiniz.
        /*if (result == 0) {
            // Örneğin, arabaların mesafelerini karşılaştırmak için başka bir metot kullanabilirsiniz.
            double distance1 = calculateDistanceToCenter(car1);
            double distance2 = calculateDistanceToCenter(car2);
            result = Double.compare(distance1, distance2);
        }*/

        return result;
    }

    private static double calculateDistanceToCenter(Car car) {
        var parkour = RacePanel.getInstance().getParkour();

        int parkourCenterX = parkour.OUTER_CIRCLE_X + parkour.OUTER_CIRCLE_DIAMETER / 2;
        int parkourCenterY = parkour.OUTER_CIRCLE_Y + parkour.OUTER_CIRCLE_DIAMETER / 2;

        int carCenterX = car.getCarX() + Car.SIZE / 2;
        int carCenterY = car.getCarY() + Car.SIZE / 2;

        // Euclidean mesafesini hesapla
        double distance = Math.sqrt(Math.pow(carCenterX - parkourCenterX, 2) + Math.pow(carCenterY - parkourCenterY, 2));

        return distance;
    }
}
