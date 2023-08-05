import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RaceUtils {
    private static Lock lock = new ReentrantLock();

    public static boolean isInOppositeDirection(int currentX, int currentY, int newX, int newY) {
        // Calculate the angle (in radians) between the current position and the new position
        double angleCurrent = Math.atan2(currentY - RacePanel.OUTER_CIRCLE_Y - (double) RacePanel.OUTER_CIRCLE_DIAMETER / 2,
                currentX - RacePanel.OUTER_CIRCLE_X - (double) RacePanel.OUTER_CIRCLE_DIAMETER / 2);
        double angleNew = Math.atan2(newY - RacePanel.OUTER_CIRCLE_Y - (double) RacePanel.OUTER_CIRCLE_DIAMETER / 2,
                newX - RacePanel.OUTER_CIRCLE_X - (double) RacePanel.OUTER_CIRCLE_DIAMETER / 2);

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

        for (var tempCar : RacePanel.cars) {
            if (!car.equals(tempCar)) {
                if (checkCollision(car, tempCar)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isCarInsideParkour(Car car) {
        int outerCircleCenterX = RacePanel.OUTER_CIRCLE_X + (RacePanel.OUTER_CIRCLE_DIAMETER / 2);
        int outerCircleCenterY = RacePanel.OUTER_CIRCLE_Y + (RacePanel.OUTER_CIRCLE_DIAMETER / 2);
        int outerCircleRadiusSquared = (RacePanel.OUTER_CIRCLE_DIAMETER / 2) * (RacePanel.OUTER_CIRCLE_DIAMETER / 2);
        int innerCircleCenterX = RacePanel.INNER_CIRCLE_X + (RacePanel.INNER_CIRCLE_DIAMETER / 2);
        int innerCircleCenterY = RacePanel.INNER_CIRCLE_Y + (RacePanel.INNER_CIRCLE_DIAMETER / 2);
        int innerCircleRadiusSquared = (RacePanel.INNER_CIRCLE_DIAMETER / 2) * (RacePanel.INNER_CIRCLE_DIAMETER / 2);

        int carLeft = car.getCarX();
        int carRight = car.getCarX() + Car.SIZE;
        int carTop = car.getCarY();
        int carBottom = car.getCarY() + Car.SIZE;

        // Check if all four corners of the car are inside the parkour
        return isInsideCircle(carLeft, carTop, outerCircleCenterX, outerCircleCenterY, outerCircleRadiusSquared)
                && isOutsideCircle(carLeft, carTop, innerCircleCenterX, innerCircleCenterY, innerCircleRadiusSquared)
                && isInsideCircle(carRight, carTop, outerCircleCenterX, outerCircleCenterY, outerCircleRadiusSquared)
                && isOutsideCircle(carRight, carTop, innerCircleCenterX, innerCircleCenterY, innerCircleRadiusSquared)
                && isInsideCircle(carLeft, carBottom, outerCircleCenterX, outerCircleCenterY, outerCircleRadiusSquared)
                && isOutsideCircle(carLeft, carBottom, innerCircleCenterX, innerCircleCenterY, innerCircleRadiusSquared)
                && isInsideCircle(carRight, carBottom, outerCircleCenterX, outerCircleCenterY, outerCircleRadiusSquared)
                && isOutsideCircle(carRight, carBottom, innerCircleCenterX, innerCircleCenterY, innerCircleRadiusSquared);
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


    public static boolean isCarPassedFinishLine(Car car) {
        int finishLineY = 390;
        int outerCircleLeft = 20;
        int outerCircleRight = 20 + ((RacePanel.OUTER_CIRCLE_DIAMETER - RacePanel.INNER_CIRCLE_DIAMETER) / 2);

        int carY = car.getCarY();
        int lastY = car.getLastY();
        int speed = car.getSpeed();
        int carX = car.getCarX();

        return (carY <= finishLineY) && (lastY + speed >= finishLineY) && (carX > outerCircleLeft) && (carX < outerCircleRight);
    }
}
