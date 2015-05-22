package de.jonasw.laz;

/**
 * Created by thriqon on 5/1/15.
 */
public class Utils {
    private Utils() {}

    public static <T extends Comparable<T>> T min(T a, T b) {
        return ((a != null) && a.compareTo(b) < 0) ? a : b;
    }
}
