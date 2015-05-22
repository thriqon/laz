package de.jonasw.laz.phases;

import java.util.Locale;

/**
 * Created by thriqon on 5/1/15.
 */
public class OverflowPhase extends Phase {

    public OverflowPhase(int title, int colorId, int start) {
        super(title, colorId, start);
    }

    private static final String FORMAT_OVERFLOW = "+ %d s";

    @Override
    public String formatTime(int time, Locale locale) {
        return String.format(locale, FORMAT_OVERFLOW, time - getStart() + 1);
    }

    @Override
    public long[] getBeginningVibrationPattern() {
        return new long[] {0, 500, 200, 500, 200, 500, 200};
    }
}
