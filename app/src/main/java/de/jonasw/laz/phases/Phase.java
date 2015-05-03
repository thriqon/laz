package de.jonasw.laz.phases;

import java.util.LinkedList;
import java.util.List;

import de.jonasw.laz.R;

/**
 * Created by thriqon on 4/30/15.
 */
public abstract class Phase {
    private final int title;
    private final int colorId;
    private final int start;

    Phase(int title, int colorId, int start) {
        this.title = title;
        this.colorId = colorId;
        this.start = start;
    }

    public int getTitle() {
        return title;
    }

    public int getColorId() {
        return colorId;
    }

    public int getStart() {
        return start;
    }

    private static final List<Phase> phases = new LinkedList<>();

    public abstract String formatTime(int time);
    public abstract long[] getBeginningVibrationPattern();


    public static final int MAX_TIME = 420;
    static {
        phases.add(new NormalPhase(R.string.phase_preparation, android.R.color.holo_purple, 0));
        phases.add(new NormalPhase(R.string.phase_first_switch, android.R.color.holo_green_dark, 100));
        phases.add(new NormalPhase(R.string.phase_extinguish_safe, android.R.color.holo_green_dark, 181));
        phases.add(new OverflowPhase(R.string.phase_overflow, android.R.color.holo_red_dark, 421));
    }

    public static Phase getPhaseFor(int pos) {
        Phase currentResult = null;
        for (Phase x : phases) {
            if (x.getStart() <= pos) {
                currentResult = x;
            }
        }
        return currentResult;
    }
}
