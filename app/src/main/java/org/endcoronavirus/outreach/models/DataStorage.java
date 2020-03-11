package org.endcoronavirus.outreach.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class DataStorage extends ViewModel {
    private static final String TAG = "DataStorage";

    private static final String DB_NAME = "Outreach";
    private static final int DB_VERSION = 2;

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

    private SQLiteOpenHelper mOpenHelper;
    private SQLiteDatabase mDb;

    public void open(@Nullable Context context) {
        Log.d(TAG, "Database Opened");

        if (mOpenHelper == null) {
            mOpenHelper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

                @Override
                public void onCreate(SQLiteDatabase db) {
                    Log.d(TAG, "Database Created");
                    mDb = db;
                    createCommunityTable(false);
                    createContactsTable(false);
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    mDb = db;
                    if (oldVersion < 2) {
                        createCommunityTable(true);
                        createContactsTable(true);
                    }
                }
            };
        }

        mDb = mOpenHelper.getWritableDatabase();
    }

    private void createCommunityTable(boolean drop) {
        if (drop) {
            mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMUNITY);
        }

        String sql = "create table " + TABLE_COMMUNITY +
                "(" + FLD_COMMUNITY_ID + " INTEGER constraint " + TABLE_COMMUNITY + "_pk primary key autoincrement," +
                FLD_COMMUNITY_NAME + " TEXT not null," +
                FLD_COMMUNITY_DESC + " TEXT" +
                ");" +
                " create unique index " + TABLE_COMMUNITY + "_" + FLD_COMMUNITY_NAME + "_uindex " +
                "on " + TABLE_COMMUNITY + " (" + FLD_COMMUNITY_NAME + ");";

        mDb.execSQL(sql);
    }

    private void createContactsTable(boolean drop) {
        if (drop) {
            mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        }

        String sql = "create table " + TABLE_CONTACTS +
                "(" + FLD_CONTACTS_ID + " INTEGER constraint " + TABLE_CONTACTS + "_pl primary key autoincrement," +
                FLD_CONTACTS_COMMUNITY_ID + " INTEGER not null," +
                FLD_CONTACTS_CTS_ID + " INTEGER not null," +
                FLD_CONTACTS_CTS_KEY + " TEXT not null," +
                FLD_CONTACTS_CACHED_URI + " TEXT not null," +
                FLD_CONTACTS_CACHED_NAME + " TEXT not null" +
                ");" +
                " create index " + TABLE_CONTACTS + "_" + FLD_CONTACTS_CACHED_NAME + "_index " +
                "on " + TABLE_CONTACTS + " (" + FLD_CONTACTS_CACHED_NAME + ");" +
                " create index " + TABLE_CONTACTS + "_" + FLD_CONTACTS_COMMUNITY_ID + "_index " +
                "on " + TABLE_CONTACTS + " (" + FLD_CONTACTS_COMMUNITY_ID + ");";
        mDb.execSQL(sql);
    }

    public long addCommunity(CommunityDetails community) {
        ContentValues value = new ContentValues();
        value.put(FLD_COMMUNITY_NAME, community.name);
        value.put(FLD_COMMUNITY_DESC, community.description);
        long id = mDb.insert(TABLE_COMMUNITY, null, value);

        Log.d(TAG, "Record inserted (" + community.name + ")");
        return id;
    }

    public ArrayList<CommunityDetails> getAllCommunitiesNames() {
        ArrayList<CommunityDetails> list = new ArrayList<>();
        String[] fields = {FLD_COMMUNITY_ID, FLD_COMMUNITY_NAME};
        Cursor c = mDb.query(TABLE_COMMUNITY, fields, null, null, null, null, null);
        while (c.moveToNext()) {
            CommunityDetails d = new CommunityDetails();
            d.id = c.getInt(0);
            d.name = c.getString(1);
            list.add(d);
        }
        Log.d(TAG, "Community Query: (" + list.size() + " records)");
        return list;
    }

    public void addContact(ContactDetails contact) {
        ContentValues value = new ContentValues();
        value.put(FLD_CONTACTS_CTS_ID, contact.Id);
        value.put(FLD_CONTACTS_CTS_KEY, contact.Key);
        value.put(FLD_CONTACTS_COMMUNITY_ID, contact.communityId);
        value.put(FLD_CONTACTS_CACHED_URI, contact.getContactUri().toString());
        value.put(FLD_CONTACTS_CACHED_NAME, contact.Name);
        mDb.insert(TABLE_CONTACTS, null, value);

        Log.d(TAG, "Record inserted (" + contact.getContactUri().toString() + ")");
    }

    public ArrayList<ContactDetails> getAllContacts(long communityId) {
        ArrayList<ContactDetails> list = new ArrayList<>();
        String[] fields = {FLD_CONTACTS_CACHED_NAME, FLD_COMMUNITY_ID, FLD_CONTACTS_CTS_KEY};
        String where = FLD_CONTACTS_COMMUNITY_ID + " =?";

        Cursor c = mDb.query(TABLE_CONTACTS, fields, where, new String[]{Long.toString(communityId)},
                null, null, null);
        while (c.moveToNext()) {
            ContactDetails d = new ContactDetails();
            d.Name = c.getString(0);
            d.Id = c.getLong(1);
            d.Key = c.getString(2);
            list.add(d);
        }
        Log.d(TAG, "Contacts Query[" + communityId + "]: (" + list.size() + " records)");
        return list;
    }
}
