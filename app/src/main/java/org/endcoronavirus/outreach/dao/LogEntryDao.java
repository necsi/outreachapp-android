package org.endcoronavirus.outreach.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.endcoronavirus.outreach.models.LogEntry;

import java.util.List;

@Dao
public interface LogEntryDao {

    @Insert
    public long add(LogEntry logEntry);

    @Query("SELECT * FROM " + LogEntry.TABLE_NAME
            + " WHERE " + LogEntry.COLUMN_CONTACT + " = :contactId "
            + " ORDER BY " + LogEntry.COLUMN_TIMESTAMP + " DESC"
    )
    public List<LogEntry> getLogsForContact(long contactId);
}
