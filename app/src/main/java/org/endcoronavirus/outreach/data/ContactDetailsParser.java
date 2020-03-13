package org.endcoronavirus.outreach.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactDetailsParser {

    private static final String TAG = "ContactDetailsParser";

    private final ContentResolver contentProvider;
    //private Uri uri;
    private long contact_ID;

    private static final String[] DATA_PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE
    };
    private static final int DATA_FIELD_NUM = 0;
    private static final int DATA_FIELD_TYPE = 1;

    public ContactDetailsParser(Context context, long contactid) {
        //this.uri = uri;
        this.contact_ID = contactid;
        contentProvider = context.getContentResolver();
    }

    public String getPhoneNumber(int desired_type) {
        Cursor phones = contentProvider.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, DATA_PROJECTION,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contact_ID, null, null);

        Log.d(TAG, "Found: " + phones.getCount());
        while (phones.moveToNext()) {
            String number = phones.getString(DATA_FIELD_NUM);
            int type = phones.getInt(DATA_FIELD_TYPE);

            if (type == desired_type)
                return number;
        }

        return null;
    }
}
