package org.endcoronavirus.outreach.models;

import android.provider.BaseColumns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "CommunityDetails",
        indices = {@Index(value = CommunityDetails.COLUMN_NAME, unique = true)
        })
public class CommunityDetails {
    static final String COLUMN_ID = BaseColumns._ID;
    static final String COLUMN_NAME = "name";
    static final String COLUMN_DESCRIPTION = "description";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    public long id;

    @ColumnInfo(name = COLUMN_NAME)
    public String name;

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    public String description;
}
