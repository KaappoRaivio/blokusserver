package blokus;

import java.io.Serializable;

public class Position implements Serializable {
    public final int x;
    public final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double getAverage () {
        return (x + y) / 2.0f;
    }

    @Override
    public String toString() {
        return "blokus.Position(" +
                "x=" + x +
                ", y=" + y +
                ')';
    }
}
