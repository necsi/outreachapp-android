package org.endcoronavirus.outreach.models;

import android.provider.BaseColumns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = LogEntry.TABLE_NAME,
        foreignKeys = @ForeignKey(
                entity = ContactDetails.class,
                parentColumns = ContactDetails.COLUMN_ID,
                childColumns = LogEntry.COLUMN_CONTACT,
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
)
public class LogEntry {
    public static final String TABLE_NAME = "LogEntries";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATE = "state";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    public long id;

    @ColumnInfo(name = COLUMN_TIMESTAMP)
    public Date timestamp;

    @ColumnInfo(name = COLUMN_CONTACT)
    public long contactId;

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    public String description;

    public static final long STATE_TODO = 1;
    public static final long STATE_DONE = 2;

    @ColumnInfo(name = COLUMN_STATE)
    public long state;

    public LogEntry() {
        timestamp = new Date();
    }
}
