import java.awt.*;
import java.util.Random;

public class RaceUtils {

    private static final Random random = new Random();

    public static boolean isInOppositeDirection(int currentX, int currentY, int newX, int newY) {
        // Calculate the angle (in radians) between the current position and the new position
        var parkour = RacePanel.getInstance().getParkour();
        double angleCurrent = Math.atan2(currentY - parkour.OUTER_CIRCLE_Y - (double) parkour.OUTER_CIRCLE_DIAMETER / 2,
                currentX - parkour.OUTER_CIRCLE_X - (double) parkour.OUTER_CIRCLE_DIAMETER / 2);
        double angleNew = Math.atan2(newY - parkour.OUTER_CIRCLE_Y - (double) parkour.OUTER_CIRCLE_DIAMETER / 2,
                newX - parkour.OUTER_CIRCLE_X - (double) parkour.OUTER_CIRCLE_DIAMETER / 2);

        // Convert the angles to degrees (between 0 and 360)
        angleCurrent = Math.toDegrees(angleCurrent);
        angleNew = Math.toDegrees(angleNew);

        // Normalize the angles to be between 0 and 360
        if (angleCurrent < 0) {
            angleCurrent += 360;
        }
        if (angleNew < 0) {
            angleNew += 360;
        }

        // Check if the new position is in the opposite direction of clockwise movement
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

    public static boolean isCarInsideParkour(Car car) {
        int carLeft = car.getCarX();
        int carRight = car.getCarX() + Car.SIZE;
        int carTop = car.getCarY();
        int carBottom = car.getCarY() + Car.SIZE;

        // Check if all four corners of the car are inside the parkour
        return isAllCornersInsideParkour(carLeft, carRight, carTop, carBottom);
    }

    private static boolean isAllCornersInsideParkour(int carLeft, int carRight, int carTop, int carBottom) {
        var parkour = RacePanel.getInstance().getParkour();

        int parkourCenterX = parkour.PARKOUR_CENTER_X;
        int parkourCenterY = parkour.PARKOUR_CENTER_Y;
        int outerCircleRadiusSquared = (parkour.OUTER_CIRCLE_DIAMETER / 2) * (parkour.OUTER_CIRCLE_DIAMETER / 2);
        int innerCircleRadiusSquared = (parkour.INNER_CIRCLE_DIAMETER / 2) * (parkour.INNER_CIRCLE_DIAMETER / 2);

        // Check if all four corners of the car are inside the parkour
        return isInsideCircle(carLeft, carTop, parkourCenterX, parkourCenterY, outerCircleRadiusSquared)
                && isOutsideCircle(carLeft, carTop, parkourCenterX, parkourCenterY, innerCircleRadiusSquared)
                && isInsideCircle(carRight, carTop, parkourCenterX, parkourCenterY, outerCircleRadiusSquared)
                && isOutsideCircle(carRight, carTop, parkourCenterX, parkourCenterY, innerCircleRadiusSquared)
                && isInsideCircle(carLeft, carBottom, parkourCenterX, parkourCenterY, outerCircleRadiusSquared)
                && isOutsideCircle(carLeft, carBottom, parkourCenterX, parkourCenterY, innerCircleRadiusSquared)
                && isInsideCircle(carRight, carBottom, parkourCenterX, parkourCenterY, outerCircleRadiusSquared)
                && isOutsideCircle(carRight, carBottom, parkourCenterX, parkourCenterY, innerCircleRadiusSquared);
    }

    private static boolean isInsideCircle(int x, int y, int centerX, int centerY, int radiusSquared) {
        int distanceSquared = (centerX - x) * (centerX - x) + (centerY - y) * (centerY - y);
        return distanceSquared <= radiusSquared;
    }

    private static boolean isOutsideCircle(int x, int y, int centerX, int centerY, int radiusSquared) {
        int distanceSquared = (centerX - x) * (centerX - x) + (centerY - y) * (centerY - y);
        return distanceSquared > radiusSquared;
    }


    public static boolean checkCollision(Car car1, Car car2) {
        Rectangle rect1 = car1.getBounds();
        Rectangle rect2 = car2.getBounds();
        return rect1.intersects(rect2);
    }

    public static Point getRandomPointWithSameAngle(Car car) {
        var parkour = RacePanel.getInstance().getParkour();

        int parkourCenterX = parkour.OUTER_CIRCLE_X + parkour.OUTER_CIRCLE_DIAMETER / 2;
        int parkourCenterY = parkour.OUTER_CIRCLE_Y + parkour.OUTER_CIRCLE_DIAMETER / 2;
        int carCenterX = car.getCarX() + Car.SIZE/2;
        int carCenterY = car.getCarY() + Car.SIZE/2;

        // Calculate the angle between the collision point and the center of the track
        double angle = Math.atan2(carCenterY - parkourCenterY, carCenterX - parkourCenterX);

        int newX, newY;
        int coefficient = 1;
        do {
            // Define the distance from the collision point to the starting point
            int distanceFromOriginalPos = coefficient * random.nextInt(1, parkour.PARKOUR_WIDTH / 2);
            // Calculate the new position for the car
            newX = carCenterX + (int) (distanceFromOriginalPos * Math.cos(angle))- Car.SIZE/2;
            newY = carCenterY + (int) (distanceFromOriginalPos * Math.sin(angle))-Car.SIZE/2;

            coefficient *= -1;
        } while (!isAllCornersInsideParkour(newX, newX + Car.SIZE, newY, newY + Car.SIZE) ||
                !isEmptyPlace(car, newX, newY));

        return new Point(newX, newY);
    }



    public static boolean isCarPassedFinishLine(Car car) {
        var parkour = RacePanel.getInstance().getParkour();
        var finishLineRect = new Rectangle(parkour.OUTER_CIRCLE_X, parkour.FINISH_LINE_Y, parkour.PARKOUR_WIDTH, 1);

        return car.getBounds().intersects(finishLineRect) && car.getLastY() + car.getSpeed() >= parkour.FINISH_LINE_Y;
        //return (carY <= finishLineY) && (lastY >= finishLineY) && (carX > outerCircleLeft) && (carX < outerCircleRight);
    }

    public static boolean checkCheat(Car car, int dy){
        var parkour = RacePanel.getInstance().getParkour();
        var finishLineRect = new Rectangle(parkour.OUTER_CIRCLE_X, parkour.FINISH_LINE_Y, parkour.PARKOUR_WIDTH, 1);

        return car.getBounds().intersects(finishLineRect) && dy > 0;
    }
}
