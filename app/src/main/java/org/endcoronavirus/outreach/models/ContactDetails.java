package org.endcoronavirus.outreach.models;

import android.net.Uri;
import android.provider.ContactsContract;

public class ContactDetails {
    public long Id;
    public String Key;
    public String Name;

    public Uri getContactUri() {
        return ContactsContract.Contacts.getLookupUri(Id, Key);
    }
}
