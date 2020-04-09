package org.endcoronavirus.outreach.service;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.helpers.NotificationHelper;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorageObject;

import java.util.Date;

public class AlarmBroardcastReceiver extends BroadcastReceiver {
    static final String TAG = "AlarmBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        AsyncTask<Void, Void, Integer> checkTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                DataStorageObject mDb = DataStorageObject.getInstance(context);

                long lastseen_millis = (new Date()).getTime() - 2 * AlarmManager.INTERVAL_DAY;
                ContactDetails[] contacts = mDb.contacts().getContactsOlderThan(lastseen_millis);

                Log.d(TAG, "Alarm triggered: " + contacts.length);

                return contacts.length;
            }

            @Override
            protected void onPostExecute(Integer contactsCount) {
                if (contactsCount > 0) {
                    NotificationHelper notificationHelper = new NotificationHelper(context);
                    notificationHelper.setTitle(context
                            .getResources().getString(R.string.format_notification_title));
                    notificationHelper.setContent(context
                            .getResources()
                            .getString(R.string.format_notification_message, contactsCount));

                    notificationHelper.createNotification();
                }
            }
        };

        checkTask.execute();
    }
}

