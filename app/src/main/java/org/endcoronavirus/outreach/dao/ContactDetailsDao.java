package org.endcoronavirus.outreach.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.endcoronavirus.outreach.models.ContactDetails;

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

    @Update()
    void updateContact(ContactDetails community);

}
