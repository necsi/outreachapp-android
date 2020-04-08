package org.endcoronavirus.outreach.models;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

public class DataStorage extends ViewModel {
    private static final String TAG = "DataStorage";

    private static final String DB_NAME = "Outreach";
    private static final int DB_VERSION = 3;

    private static final String TABLE_COMMUNITY = "Communities";
    private static final String TABLE_CONTACTS = "Contacts";

    private static final String FLD_COMMUNITY_ID = "id";
    private static final String FLD_COMMUNITY_NAME = "name";
    private static final String FLD_COMMUNITY_DESC = "description";
    private static final String FLD_CONTACTS_COMMUNITY_ID = "community";

    private static final String FLD_CONTACTS_ID = "id";
    private static final String FLD_CONTACTS_CTS_ID = "contacts_id";
    private static final String FLD_CONTACTS_CTS_KEY = "contacts_key";
    private static final String FLD_CONTACTS_CACHED_URI = "contacts_uri";
    private static final String FLD_CONTACTS_CACHED_NAME = "name";
    private static final String FLD_CONTACTS_NOTES = "notes";

    DataStorageObject mDataStorage = null;

    public void open(@Nullable Context context) {
        mDataStorage = DataStorageObject.getInstance(context);
        Log.d(TAG, "Database Opened");
    }

    public DataStorageObject ds() {
        return mDataStorage;
    }
}
