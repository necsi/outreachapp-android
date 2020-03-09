package org.endcoronavirus.outreach.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataStorage extends SQLiteOpenHelper {

    private static final String DB_NAME = "Outreach";
    private static final int DB_VERSION = 1;

    private static final String TABLE_COMMUNITY = "Communities";
    private static final String TABLE_CONTACTS = "Contacts";

    private static final String FLD_COMMUNITY_ID = "id";
    private static final String FLD_COMMUNITY_NAME = "name";
    private static final String FLD_COMMUNITY_DESC = "description";

    private final Context mContext;
    private SQLiteDatabase mDb;

    public DataStorage(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public void open() {
        mDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDb = db;
        createCommunityTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
    }

    public ArrayList<String> getAllCommunitiesNames() {
        ArrayList<String> list = new ArrayList<>();
        String[] fields = {FLD_COMMUNITY_NAME};
        Cursor c = mDb.query(TABLE_COMMUNITY, fields, null, null, null, null, null);
        while (c.moveToNext()) {
            list.add(c.getString(0));
        }
        return list;
    }
}
