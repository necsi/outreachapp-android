package org.endcoronavirus.outreach.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

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
                    if (oldVersion < DB_VERSION) {
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
                FLD_COMMUNITY_NAME + " TEXT UNIQUE not null," +
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
                FLD_CONTACTS_CTS_ID + " INTEGER not null ," +
                FLD_CONTACTS_CTS_KEY + " TEXT not null ," +
                FLD_CONTACTS_CACHED_URI + " TEXT not null ," +
                FLD_CONTACTS_CACHED_NAME + " TEXT not null" +
                ");" +
                " create index " + TABLE_CONTACTS + "_" + FLD_CONTACTS_CACHED_NAME + "_index " +
                "on " + TABLE_CONTACTS + " (" + FLD_CONTACTS_CACHED_NAME + ");" +
                " create index " + TABLE_CONTACTS + "_" + FLD_CONTACTS_COMMUNITY_ID + "_index " +
                "on " + TABLE_CONTACTS + " (" + FLD_CONTACTS_COMMUNITY_ID + ");";
        mDb.execSQL(sql);
    }

    public long addCommunity(CommunityDetails community) throws SQLiteConstraintException {
        if (community.id != -1)
            throw new RuntimeException("Community ID should not be set.");

        ContentValues value = new ContentValues();
        value.put(FLD_COMMUNITY_NAME, community.name);
        value.put(FLD_COMMUNITY_DESC, community.description);

        long id = mDb.insertOrThrow(TABLE_COMMUNITY, null, value);
        Log.d(TAG, "Record inserted (" + community.name + ")");
        return id;
    }

    public long updateCommunity(CommunityDetails community) {
        if (community.id == -1)
            throw new RuntimeException("Community ID is not set.");

        ContentValues value = new ContentValues();
        value.put(FLD_COMMUNITY_NAME, community.name);
        value.put(FLD_COMMUNITY_DESC, community.description);

        mDb.update(TABLE_COMMUNITY, value, FLD_COMMUNITY_ID + "=?", new String[]{
                Long.toString(community.id)
        });

        Log.d(TAG, "Record updated (" + community.name + ")");
        return community.id;
    }

    public ArrayList<CommunityDetails> getAllCommunitiesNames() {
        ArrayList<CommunityDetails> list = new ArrayList<>();
        String[] fields = {FLD_COMMUNITY_ID, FLD_COMMUNITY_NAME};
        Cursor c = mDb.query(TABLE_COMMUNITY, fields, null, null, null, null, null);
        while (c.moveToNext()) {
            CommunityDetails d = new CommunityDetails();
            d.id = c.getLong(0);
            d.name = c.getString(1);
            list.add(d);
        }
        Log.d(TAG, "Community Query: (" + list.size() + " records)");
        return list;
    }

    public void addContact(ContactDetails contact) {
        ContentValues value = new ContentValues();
        value.put(FLD_CONTACTS_CTS_ID, contact.contactId);
        value.put(FLD_CONTACTS_CTS_KEY, contact.contactKey);
        value.put(FLD_CONTACTS_COMMUNITY_ID, contact.communityId);
        value.put(FLD_CONTACTS_CACHED_URI, contact.getContactUri().toString());
        value.put(FLD_CONTACTS_CACHED_NAME, contact.name);
        mDb.insert(TABLE_CONTACTS, null, value);

        Log.d(TAG, "Record inserted (" + contact.getContactUri().toString() + ")");
    }

    public ArrayList<ContactDetails> getAllContacts(long communityId) {
        ArrayList<ContactDetails> list = new ArrayList<>();
        String[] fields = {FLD_CONTACTS_ID, FLD_CONTACTS_CACHED_NAME,
                FLD_COMMUNITY_ID, FLD_CONTACTS_CTS_KEY};
        String where = FLD_CONTACTS_COMMUNITY_ID + " =?";

        Cursor c = mDb.query(TABLE_CONTACTS, fields, where, new String[]{Long.toString(communityId)},
                null, null, null);
        while (c.moveToNext()) {
            ContactDetails d = new ContactDetails();
            d.id = c.getLong(0);
            d.name = c.getString(1);
            d.contactId = c.getLong(2);
            d.contactKey = c.getString(3);
            list.add(d);
        }
        Log.d(TAG, "Contacts Query[" + communityId + "]: (" + list.size() + " records)");
        return list;
    }

    public ContactDetails getContactById(long contactId) {
        String[] fields = {FLD_CONTACTS_CACHED_NAME, FLD_CONTACTS_CTS_ID, FLD_CONTACTS_CTS_KEY, FLD_CONTACTS_COMMUNITY_ID};
        String where = FLD_CONTACTS_ID + " =?";

        Cursor c = mDb.query(TABLE_CONTACTS, fields, where, new String[]{Long.toString(contactId)},
                null, null, null);

        if (c.getCount() > 1) {
            Log.e(TAG, "Something has happened! More than one Contact! ");
            throw new RuntimeException("This is unexpected!");
        } else if (c.getCount() == 0) {
            return null;
        }

        c.moveToFirst();

        ContactDetails details = new ContactDetails();
        details.id = contactId;
        details.name = c.getString(0);
        details.contactId = c.getLong(1);
        details.contactKey = c.getString(2);
        details.communityId = c.getInt(3);
        return details;
    }

    public CommunityDetails getCommunityById(long communityId) {
        String[] fields = {FLD_COMMUNITY_ID, FLD_COMMUNITY_NAME, FLD_COMMUNITY_DESC};
        String where = FLD_COMMUNITY_ID + " =?";

        Cursor c = mDb.query(TABLE_COMMUNITY, fields, where, new String[]{Long.toString(communityId)},
                null, null, null);

        if (c.getCount() > 1) {
            Log.e(TAG, "Something has happened! More than one Contact! ");
            throw new RuntimeException("This is unexpected!");
        } else if (c.getCount() == 0) {
            return null;
        }

        c.moveToFirst();
        CommunityDetails comm = new CommunityDetails();
        comm.id = c.getLong(0);
        comm.name = c.getString(1);
        comm.description = c.getString(2);
        return comm;
    }
}
