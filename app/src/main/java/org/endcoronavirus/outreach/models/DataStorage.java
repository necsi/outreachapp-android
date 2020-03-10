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
    private static final int DB_VERSION = 1;

    private static final String TABLE_COMMUNITY = "Communities";
    private static final String TABLE_CONTACTS = "Contacts";

    private static final String FLD_COMMUNITY_ID = "id";
    private static final String FLD_COMMUNITY_NAME = "name";
    private static final String FLD_COMMUNITY_DESC = "description";

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
                    createCommunityTable();
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                }
            };
        }

        mDb = mOpenHelper.getWritableDatabase();
    }

    private void createCommunityTable() {
        String sql = "create table " + TABLE_COMMUNITY + "\n" +
                "(\n" +
                "\t" + FLD_COMMUNITY_ID + " INTEGER\n" +
                "\t\tconstraint Communities_pk\n" +
                "\t\t\tprimary key autoincrement,\n" +
                "\t" + FLD_COMMUNITY_NAME + " TEXT not null,\n" +
                "\t" + FLD_COMMUNITY_DESC + " TEXT\n" +
                ");\n" +
                "\n" +
                "create unique index " + TABLE_COMMUNITY + "_" + FLD_COMMUNITY_NAME + "_uindex\n" +
                "\ton " + TABLE_COMMUNITY + " (" + FLD_COMMUNITY_NAME + ");\n" +
                "\n";

        mDb.execSQL(sql);
    }

    public void addCommunity(CommunityDetails community) {
        ContentValues value = new ContentValues();
        value.put(FLD_COMMUNITY_NAME, community.name);
        value.put(FLD_COMMUNITY_DESC, community.description);
        mDb.insert(TABLE_COMMUNITY, null, value);

        Log.d(TAG, "Record inserted (" + community.name + ")");
    }

    public ArrayList<String> getAllCommunitiesNames() {
        ArrayList<String> list = new ArrayList<>();
        String[] fields = {FLD_COMMUNITY_NAME};
        Cursor c = mDb.query(TABLE_COMMUNITY, fields, null, null, null, null, null);
        while (c.moveToNext()) {
            list.add(c.getString(0));
        }
        Log.d(TAG, "Community Query: (" + list.size() + " records)");
        return list;
    }
}
