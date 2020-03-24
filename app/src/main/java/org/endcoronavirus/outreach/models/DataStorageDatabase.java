package org.endcoronavirus.outreach.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.endcoronavirus.outreach.dao.CommunityDetailsDao;
import org.endcoronavirus.outreach.dao.ContactDetailsDao;
import org.endcoronavirus.outreach.dao.LogEntryDao;
import org.endcoronavirus.outreach.data.Converters;

@Database(entities = {
        CommunityDetails.class,
        ContactDetails.class,
        LogEntry.class
}, version = 5)
@TypeConverters({Converters.class})
public abstract class DataStorageDatabase extends RoomDatabase {
    public abstract CommunityDetailsDao communityDao();

    public abstract ContactDetailsDao contactDetailsDao();

    public abstract LogEntryDao logEntriesDao();
}
