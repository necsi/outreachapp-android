package org.endcoronavirus.outreach.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CommunityWithContacts {
    @Embedded
    public CommunityDetails community;

    @Relation(
            parentColumn = CommunityDetails.COLUMN_ID,
            entityColumn = ContactDetails.COLUMN_COMMUNITY_ID
    )

    public List<ContactDetails> contacts;
}
