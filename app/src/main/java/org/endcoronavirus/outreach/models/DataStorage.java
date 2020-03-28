package org.endcoronavirus.outreach.models;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import org.endcoronavirus.outreach.dao.ContactDetailsDao;
import org.endcoronavirus.outreach.dao.LogEntryDao;

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

    private DataStorageDatabase mDb;

    public void open(@Nullable Context context) {
        Log.d(TAG, "Database Opened");

        mDb = Room.databaseBuilder(context,
                DataStorageDatabase.class, "outreach.db")
                .fallbackToDestructiveMigrationFrom(4)
                .addMigrations(DataStorageDatabase.MIGRATION_5_6)
                .build();
    }

    public long addCommunity(CommunityDetails community) throws SQLiteConstraintException {
        if (community.id != 0)
            throw new RuntimeException("Community ID is set.");

        community.id = mDb.communityDao().addCommunity(community);
        return community.id;
    }

    public long updateCommunity(CommunityDetails community) {
        if (community.id == 0)
            throw new RuntimeException("Community ID is not set.");

        mDb.communityDao().updateCommunity(community);
        return community.id;
    }

    public int deleteCommunity(long id) {
        return mDb.communityDao().deleteCommunity(getCommunityById(id));
    }

    public CommunityDetails[] getAllCommunitiesNames() {
        return mDb.communityDao().getAllCommunitiesNames();
    }

    public long addContact(ContactDetails contact) {
        return mDb.contactDetailsDao().addContact(contact);
    }

    public ContactDetails[] getAllContacts(long communityId) {
        return mDb.contactDetailsDao().getAllContactsByCommunity(communityId);
    }

    public ContactDetails[] searchContactsForPattern(String pattern) {
        return mDb.contactDetailsDao().searchContactsForPattern(pattern);
    }

    public ContactDetails getContactById(long contactId) {
        return mDb.contactDetailsDao().getContactById(contactId);
    }

    public CommunityDetails getCommunityById(long communityId) {
        return mDb.communityDao().getCommunityById(communityId);
    }

    public void updateContact(ContactDetails contactDetails) {
        mDb.contactDetailsDao().updateContact(contactDetails);
    }

    public ContactDetailsDao contacts() {
        return mDb.contactDetailsDao();
    }

    public LogEntryDao logEntries() {
        return mDb.logEntriesDao();
    }
}
