package org.endcoronavirus.outreach.models;

import android.net.Uri;
import android.provider.ContactsContract;

public class ContactDetails {
    public long id = -1;
    public long communityId;
    public long contactId;
    public String contactKey;
    public String name;

    public Uri getContactUri() {
        return ContactsContract.Contacts.getLookupUri(contactId, contactKey);
    }
}
