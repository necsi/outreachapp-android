package org.endcoronavirus.outreach.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackendService extends Service {
    private static final String TAG = "BackendService";

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroardcastReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 22);
//        calendar.set(Calendar.MINUTE, 20);
//
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                3 * AlarmManager.INTERVAL_HOUR, alarmIntent);

        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 10 * 1000, 3 * AlarmManager.INTERVAL_HOUR, alarmIntent);

        Log.d(TAG, "Backend Service Started");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
