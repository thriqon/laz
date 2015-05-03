package de.jonasw.laz;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;

import java.util.Calendar;

import de.jonasw.laz.phases.Phase;

public class TimerService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    private NotificationManager notificationManager;
    private PowerManager.WakeLock wakeLock;

    private Handler handler;

    private Phase previousPhase;

    private static final int RECALCULATE_INTERVAL = 500;

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LAZ_TIMERSERVICE");

        handler = new Handler();
        prefs = Preferences.getPreferencesFor(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final int NOTIFICATION_ID = 128508213;

    private void refreshNotification(int diff) {
        Phase current = Phase.getPhaseFor(diff);

        Intent launchMain = new Intent(this, Timetaking.class);
        launchMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingLaunchMain = PendingIntent.getActivity(this, 0, launchMain, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(current.formatTime(diff) + " - " + getString(current.getTitle()))
                .setOngoing(true)
                .setContentIntent(pendingLaunchMain)
                .setAutoCancel(false)
                .setOnlyAlertOnce(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder.setProgress(Phase.MAX_TIME, Utils.min(diff, Phase.MAX_TIME), false);
        }

        if (current != previousPhase) {
            builder
                    .setVibrate(current.getBeginningVibrationPattern());
            previousPhase = current;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder
                    .setColor(getResources().getColor(current.getColorId()))
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setStartTime(Calendar.getInstance().getTimeInMillis());

        prefs.edit().putBoolean(RUNNING, true).apply();

        prefs.registerOnSharedPreferenceChangeListener(this);

        wakeLock.acquire(100);
        handler.post(tick);

        return Service.START_STICKY;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        prefs.edit().putBoolean(RUNNING, false).commit();
        notificationManager.cancel(NOTIFICATION_ID);
        handler.removeCallbacks(tick);

        super.onDestroy();
    }

    private long computeTimeDifferenceInMilliseconds() {
        return Calendar.getInstance().getTimeInMillis() - getStartTime();
    }

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            long difference = computeTimeDifferenceInMilliseconds();

            int differenceS = (int) (difference / 1000l);
            float ratioPercent = (((float) differenceS) / ((float) Phase.MAX_TIME)) * 100f;

            prefs
                    .edit()
                    .putLong(DIFF_MS_KEY, difference)
                    .putInt(DIFF_S_KEY, differenceS)
                    .putFloat(RATIO_PERCENT_KEY, Utils.min((float) Phase.MAX_TIME, ratioPercent))
                    .apply();

            handler.postDelayed(tick, RECALCULATE_INTERVAL);
            wakeLock.acquire(RECALCULATE_INTERVAL * 2);
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(DIFF_S_KEY)) {
            refreshNotification(sharedPreferences.getInt(DIFF_S_KEY, 0));
        }
    }

    final static String START_TIME_KEY = "service.starttime";
    final static String DIFF_MS_KEY = "service.diffms";
    final static String DIFF_S_KEY = "service.diffs";
    final static String RATIO_PERCENT_KEY = "service.ratioPercent";
    final static String RUNNING = "service.running";

    public long getStartTime() {
        return prefs.getLong(START_TIME_KEY, 0);
    }
    public void setStartTime(long v) {
        prefs
                .edit()
                .putLong(START_TIME_KEY, v)
                .apply();
    }
}
