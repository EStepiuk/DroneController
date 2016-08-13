package ua.stepiukyevhen.dronecontroller.util;

/**
 * @author : Yevhen Stepiuk
 */

public class MathUtils {

    public static int distance(int fromX, int fromY, int toX, int toY) {
        return (int) Math.sqrt((toX - fromX) * (toX - fromX) + (toY - fromY) * (toY - fromY));
    }
}
