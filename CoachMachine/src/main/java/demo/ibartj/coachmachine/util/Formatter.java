package demo.ibartj.coachmachine.util;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class Formatter {
    public static String duration(int duration) {
        int h = (int) Math.floor(duration / 60 / 60);
        int m = (int) Math.floor((duration - h * 60 * 60) / 60);
        int s = duration - h * 60 * 60 - m * 60;
        return duration(h, m, s);
    }

    public static String duration(int hours, int minutes, int seconds) {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
