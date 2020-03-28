package org.endcoronavirus.outreach.models;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.endcoronavirus.outreach.dao.CommunityDetailsDao;
import org.endcoronavirus.outreach.dao.ContactDetailsDao;
import org.endcoronavirus.outreach.dao.LogEntryDao;
import org.endcoronavirus.outreach.data.Converters;

@Database(entities = {
        CommunityDetails.class,
        ContactDetails.class,
        LogEntry.class
}, version = 6)
@TypeConverters({Converters.class})
public abstract class DataStorageDatabase extends RoomDatabase {
    public abstract CommunityDetailsDao communityDao();

    public abstract ContactDetailsDao contactDetailsDao();

    public abstract LogEntryDao logEntriesDao();

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE " + ContactDetails.TABLE_NAME +
                            " ADD COLUMN " + ContactDetails.COLUMN_LAST_CONTACTED + " Integer "
            );
        }
    };
}
