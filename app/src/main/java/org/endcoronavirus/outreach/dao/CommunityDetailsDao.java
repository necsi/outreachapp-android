package org.endcoronavirus.outreach.dao;

import android.provider.BaseColumns;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.endcoronavirus.outreach.models.CommunityDetails;

@Dao
public interface CommunityDetailsDao {

    @Query("SELECT * FROM CommunityDetails")
    CommunityDetails[] getAllCommunitiesNames();

    @Query("SELECT * FROM CommunityDetails WHERE " + BaseColumns._ID + " = :id")
    CommunityDetails getCommunityById(long id);

    @Insert
    long addCommunity(CommunityDetails community);

    @Update
    void updateCommunity(CommunityDetails community);

    @Delete
    int deleteCommunity(CommunityDetails details);
}
