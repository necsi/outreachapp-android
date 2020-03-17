package org.endcoronavirus.outreach.models;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ContactDetails {
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_COMMUNITY_ID = "community";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    public long id;

    @ColumnInfo(name = COLUMN_COMMUNITY_ID)
    public long communityId;

    @ColumnInfo(name = "contacts_id")
    public long contactId;

    @ColumnInfo(name = "contacts_key")
    public String contactKey;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "notes")
    public String notes;

    @Ignore
    public Uri getContactUri() {
        return ContactsContract.Contacts.getLookupUri(contactId, contactKey);
    }
}
