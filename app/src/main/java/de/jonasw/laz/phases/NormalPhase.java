package de.jonasw.laz.phases;

/**
 * Created by thriqon on 5/1/15.
 */
public class NormalPhase extends Phase {

    private static final String FORMAT_NORMAL = "%d s";

    public NormalPhase(int title, int colorId, int start) {
        super(title, colorId, start);
    }

    @Override
    public String formatTime(int time) {
        return String.format(FORMAT_NORMAL, time);
    }

    @Override
    public long[] getBeginningVibrationPattern() {
        return new long[] {0, 200, 300};
    }
}
