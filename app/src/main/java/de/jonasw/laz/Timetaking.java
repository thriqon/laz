package de.jonasw.laz;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Locale;

import de.jonasw.laz.clockface.CircleView;
import de.jonasw.laz.phases.Phase;

public class Timetaking extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private CircleView circle;
    private TextView phaseDisplay;


    private boolean running = false;

    private SharedPreferences getTimerPreferences() {
        return Preferences.getPreferencesFor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getTimerPreferences().registerOnSharedPreferenceChangeListener(this);
        tick();
    }

    @Override
    protected void onPause() {
        super.onPause();

        getTimerPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private static final int ACTION_RESTART = 1;
    private static final int ACTION_STOP = 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!running) {
            menu
                    .add(Menu.NONE, ACTION_RESTART, Menu.NONE, R.string.action_start)
                    .setIcon(R.drawable.ic_play)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            menu
                    .add(Menu.NONE, ACTION_STOP, Menu.NONE, R.string.action_stop)
                    .setIcon(R.drawable.ic_stop)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item != null) {
            switch (item.getItemId()) {
                case ACTION_RESTART:
                    restartRun();
                    return true;
                case ACTION_STOP:
                    stopService(new Intent(this, TimerService.class));
                    getTimerPreferences().edit()
                            .putBoolean(TimerService.RUNNING, false)
                            .apply();
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.timetaking);

        circle = (CircleView) findViewById(R.id.circle);
        phaseDisplay = (TextView) findViewById(R.id.phase);
    }

    private int darkenColor(int c) {
        float[] hsv = new float[3];
        Color.colorToHSV(c, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    private void restartRun() {
        Intent serviceStart = new Intent(this, TimerService.class);
        startService(serviceStart);
    }

    private void tick() {
        SharedPreferences sharedPreferences = getTimerPreferences();

        circle.setProgress(Utils.min(100f, sharedPreferences.getFloat(TimerService.RATIO_PERCENT_KEY, 0)));

        running = sharedPreferences.getBoolean(TimerService.RUNNING, false);

        Locale locale = getResources().getConfiguration().locale;

        int diffS = sharedPreferences.getInt(TimerService.DIFF_S_KEY, 0);
        Phase p = Phase.getPhaseFor(diffS);
        circle.setText(p.formatTime(diffS, locale));
        phaseDisplay.setText(p.getTitle());

        if (running) {
            setColorId(p.getColorId());
        } else {
            setStoppedColor();
        }


        invalidateOptionsMenu();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        tick();
    }

    private void setStoppedColor() {
        setColorId(R.color.stopped_color);
    }

    private void setColorId(int colorId) {
        int color = getResources().getColor(colorId);
        circle.setForegroundArcColor(color);
        phaseDisplay.setTextColor(color);

        final ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setBackgroundDrawable(new ColorDrawable(color));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(darkenColor(color));
        }
    }
}
