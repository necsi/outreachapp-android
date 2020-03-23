package org.endcoronavirus.outreach.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.endcoronavirus.outreach.dao.CommunityDetailsDao;
import org.endcoronavirus.outreach.dao.ContactDetailsDao;

@Database(entities = {CommunityDetails.class, ContactDetails.class}, version = 5)
public abstract class DataStorageDatabase extends RoomDatabase {
    public abstract CommunityDetailsDao communityDao();

    public abstract ContactDetailsDao contactDetailsDao();
}
