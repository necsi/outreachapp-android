package org.endcoronavirus.outreach.models;

import androidx.lifecycle.ViewModel;

public class AppState extends ViewModel {

    private static final String TAG = "AppState";

    private long mCommunityId = -1;
    private long mContactId = -1;

    public boolean isCommunityIdAvailable() {
        return mCommunityId > -1;
    }

    public void selectCommunity(long id) {
        mCommunityId = id;
    }

    public void clearCommunity() {
        mCommunityId = -1;
    }

    public long currentCommunityId() {
        if (isCommunityIdAvailable())
            return mCommunityId;
        throw new RuntimeException("Community ID is not available");
    }

    public boolean isContactIdAvailable() {
        return mContactId > -1;
    }

    public void selectContact(long contactId) {
        mContactId = contactId;
    }

    public void clearContact() {
        mContactId = -1;
    }

    public long currentContactId() {
        if (isContactIdAvailable())
            return mContactId;
        throw new RuntimeException("Contact ID in not available");
    }
}
