package org.endcoronavirus.outreach.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.endcoronavirus.outreach.models.ContactDetails;

import java.util.Date;

@Dao
public interface ContactDetailsDao {

    @Query("SELECT * FROM ContactDetails")
    ContactDetails[] getAllContacts();

    @Insert
    long addContact(ContactDetails community);

    @Query("SELECT * FROM ContactDetails WHERE " + ContactDetails.COLUMN_ID + " = :cid")
    ContactDetails getContactById(long cid);

    @Query("SELECT * FROM ContactDetails WHERE " + ContactDetails.COLUMN_COMMUNITY_ID + " = :cid")
    ContactDetails[] getAllContactsByCommunity(long cid);

    @Query("SELECT * FROM ContactDetails WHERE "
            + ContactDetails.COLUMN_NAME + " LIKE :pat OR "
            + ContactDetails.COLUMN_NOTES + " LIKE :pat")
    ContactDetails[] searchContactsForPattern(String pat);

    @Query("SELECT * FROM ContactDetails WHERE " + ContactDetails.COLUMN_NOTES + " LIKE :pat")
    ContactDetails[] searchContactsForNotesLike(String pat);

    @Query("SELECT * FROM ContactDetails WHERE " + ContactDetails.COLUMN_COMMUNITY_ID + " = :cid AND "
            + ContactDetails.COLUMN_NOTES + " LIKE :pat")
    ContactDetails[] searchContactsInCommunityForNotesLike(long cid, String pat);

    @Update()
    void updateContact(ContactDetails community);

    @Query("UPDATE " + ContactDetails.TABLE_NAME
            + " SET " + ContactDetails.COLUMN_LAST_CONTACTED + " = :date "
            + " WHERE " + ContactDetails.COLUMN_ID + " = :id"
    )
    int updateLastContacted(long id, Date date);
}
