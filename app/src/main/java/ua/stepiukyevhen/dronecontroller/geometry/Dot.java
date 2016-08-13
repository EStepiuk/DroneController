package ua.stepiukyevhen.dronecontroller.geometry;

/**
 * @author : Yevhen Stepiuk
 */

public class Dot {

    private int x, y;

    public Dot(Dot dot) {
        this(dot.x, dot.y);
    }

    public Dot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
