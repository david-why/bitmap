package net.davidwhy.bitmap.logic;

public class SemiConductor {

    private static int speed = 2;

    public static int speedUp() {
        if (speed == 2)
            speed = 5;
        else if (speed < 655360) {
            speed *= 2;
        }
        return speed;
    }

    public static int speedDown() {
        if (speed == 5)
            speed = 2;
        else if (speed > 1) {
            speed /= 2;
        }
        return speed;
    }

}