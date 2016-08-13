package ua.stepiukyevhen.dronecontroller.geometry;

import ua.stepiukyevhen.dronecontroller.util.MathUtils;

/**
 * @author : Yevhen Stepiuk
 */

public class Circle {

    private int centerX, centerY, radius;

    public Circle(int centerX, int centerY, int radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public boolean isInside(Dot dot) {
        return radius > MathUtils.distance(centerX, centerY, dot.getX(), dot.getY());
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getRadius() {
        return radius;
    }
}
