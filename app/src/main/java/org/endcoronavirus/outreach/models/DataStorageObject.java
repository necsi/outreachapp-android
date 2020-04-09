package org.endcoronavirus.outreach.models;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import androidx.room.Room;

import org.endcoronavirus.outreach.dao.ContactDetailsDao;
import org.endcoronavirus.outreach.dao.LogEntryDao;

public class DataStorageObject {
    static private DataStorageDatabase mDb = null;

    static public DataStorageObject getInstance(Context context) {
        if (mDb == null)
            mDb = Room.databaseBuilder(context,
                    DataStorageDatabase.class, "outreach.db")
                    .fallbackToDestructiveMigrationFrom(4)
                    .addMigrations(DataStorageDatabase.MIGRATION_5_6)
                    .build();

        return new DataStorageObject();
    }

    private DataStorageObject() {

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

    public enum Sorting {
        Unsorted, Name, LastContacted
    }

    public ContactDetails[] getAllContacts(long communityId, Sorting sorting) {
        switch (sorting) {
            case Unsorted:
                return mDb.contactDetailsDao().getAllContactsByCommunity(communityId);
            case Name:
                return mDb.contactDetailsDao().getAllContactsByCommunitySortedByName(communityId);
            case LastContacted:
                return mDb.contactDetailsDao().getAllContactsByCommunitySortedByLastContacted(communityId);
            default:
                throw new RuntimeException("Sorting field not implemented");
        }
    }

}
