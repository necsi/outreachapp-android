package org.endcoronavirus.outreach.models;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(indices = {
        @Index(ContactDetails.COLUMN_NAME),
        @Index(ContactDetails.COLUMN_COMMUNITY_ID),
        @Index(value = {ContactDetails.COLUMN_CONTACT_ID, ContactDetails.COLUMN_COMMUNITY_ID}, unique = true)},
        foreignKeys = {
                @ForeignKey(entity = CommunityDetails.class,
                        parentColumns = CommunityDetails.COLUMN_ID,
                        childColumns = ContactDetails.COLUMN_COMMUNITY_ID,
                        onDelete = CASCADE)
        }
)
public class ContactDetails {
    public static final String TABLE_NAME = "ContactDetails";

    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_COMMUNITY_ID = "community";
    public static final String COLUMN_CONTACT_ID = "contacts_id";
    public static final String COLUMN_CONTACT_KEY = "contacts_key";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_LAST_CONTACTED = "last_contacted";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    public long id;

    @ColumnInfo(name = COLUMN_COMMUNITY_ID)
    public long communityId;

    @ColumnInfo(name = COLUMN_CONTACT_ID)
    public long contactId;

    @ColumnInfo(name = COLUMN_CONTACT_KEY)
    public String contactKey;

    @ColumnInfo(name = COLUMN_NAME)
    public String name;

    @ColumnInfo(name = COLUMN_NOTES)
    public String notes;

    @ColumnInfo(name = COLUMN_LAST_CONTACTED)
    public Date lastContacted;

    @Ignore
    public Uri getContactUri() {
        return ContactsContract.Contacts.getLookupUri(contactId, contactKey);
    }
}
