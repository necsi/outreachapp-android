package org.endcoronavirus.outreach.service;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.endcoronavirus.outreach.helpers.NotificationHelper;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorageObject;

import java.util.Date;

public class AlarmBroardcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DataStorageObject mDb = DataStorageObject.getInstance(context);

        long lastseen_millis = (new Date()).getTime() - 2 * AlarmManager.INTERVAL_DAY;
        ContactDetails[] contacts = mDb.contacts().getContactsOlderThan(lastseen_millis);

        if (contacts.length > 0) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.setTitle("Necsi Outreach");
            notificationHelper.setContent("Remember to reach " + contacts.length + " contacts!");

            notificationHelper.createNotification();
        }
    }
}

