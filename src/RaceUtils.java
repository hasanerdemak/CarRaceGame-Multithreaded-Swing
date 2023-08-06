import java.awt.*;
import java.util.Random;

public class RaceUtils {

    public static boolean isInOppositeDirection(int currentX, int currentY, int newX, int newY) {
        // Calculate the angle (in radians) between the current position and the new position
        RacePanel.Parkour parkour = RacePanel.getInstance().getParkour();
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

        int outerCircleCenterX = parkour.OUTER_CIRCLE_X + (parkour.OUTER_CIRCLE_DIAMETER / 2);
        int outerCircleCenterY = parkour.OUTER_CIRCLE_Y + (parkour.OUTER_CIRCLE_DIAMETER / 2);
        int outerCircleRadiusSquared = (parkour.OUTER_CIRCLE_DIAMETER / 2) * (parkour.OUTER_CIRCLE_DIAMETER / 2);
        int innerCircleCenterX = parkour.INNER_CIRCLE_X + (parkour.INNER_CIRCLE_DIAMETER / 2);
        int innerCircleCenterY = parkour.INNER_CIRCLE_Y + (parkour.INNER_CIRCLE_DIAMETER / 2);
        int innerCircleRadiusSquared = (parkour.INNER_CIRCLE_DIAMETER / 2) * (parkour.INNER_CIRCLE_DIAMETER / 2);

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

    public static Point getRandomPointWithSameAngle(Car car) {
        var parkour = RacePanel.getInstance().getParkour();

        int centerX = parkour.OUTER_CIRCLE_X + parkour.OUTER_CIRCLE_DIAMETER / 2;
        int centerY = parkour.OUTER_CIRCLE_Y + parkour.OUTER_CIRCLE_DIAMETER / 2;
        int carX = car.getCarX();
        int carY = car.getCarY();

        // Define the distance from the collision point to the starting point
        int distanceFromOriginalPos = new Random().nextInt(5, 50); // Adjust this value as desired

        // Calculate the angle between the collision point and the center of the track
        double angle = Math.atan2(carY - centerY, carX - centerX);

        // Calculate the new position for the car
        int newX = carX - (int) (distanceFromOriginalPos * Math.cos(angle));
        int newY = carY - (int) (distanceFromOriginalPos * Math.sin(angle));
        // todo fix this
        if (!isAllCornersInsideParkour(newX, newX + Car.SIZE, newY, newY+ Car.SIZE)){
            return getRandomPointWithSameAngle(car);
        } else {
            return new Point(newX, newY);
        }
    }


    public static boolean isCarPassedFinishLine(Car car) {
        var parkour = RacePanel.getInstance().getParkour();

        int finishLineY = 390;
        int outerCircleLeft = 20;
        int outerCircleRight = 20 + ((parkour.OUTER_CIRCLE_DIAMETER - parkour.INNER_CIRCLE_DIAMETER) / 2);

        int carY = car.getCarY();
        int lastY = car.getLastY();
        int speed = car.getSpeed();
        int carX = car.getCarX();

        return (carY <= finishLineY) && (lastY + speed >= finishLineY) && (carX > outerCircleLeft) && (carX < outerCircleRight);
    }
}
