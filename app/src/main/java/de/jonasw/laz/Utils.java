package de.jonasw.laz;

/**
 * Created by thriqon on 5/1/15.
 */
public class Utils {
    private Utils() {}

    public static <T extends Comparable<T>> T min(T... a) {
        return extreme(1, a);
    }

    public static <T extends Comparable<T>> T max(T... a) {
        return extreme(-1, a);
    }

    private static <T extends Comparable<T>> T extreme(int comparisonSignum, T[] a) {
        if (a != null && a.length > 0) {
            T curExtreme = a[0];
            for (T x : a) {
                if (Integer.signum(curExtreme.compareTo(x)) == comparisonSignum) {
                    curExtreme = x;
                }
            }
            return curExtreme;
        } else {
            throw new IllegalArgumentException("Needs at least one parameter");
        }
    }
}
