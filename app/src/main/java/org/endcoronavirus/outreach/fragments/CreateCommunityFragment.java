package org.endcoronavirus.outreach.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.models.CommunityDetails;
import org.endcoronavirus.outreach.models.DataStorage;

public class CreateCommunityFragment extends Fragment {

    private static final String TAG = "CreateCommunityFragment";
    private View mView;
    private DataStorage mDataStorage;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_community_create, container, false);
        return mView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_community_create, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.command_menu_confirm) {
            CommunityDetails community = new CommunityDetails();
            community.name = ((EditText) mView.findViewById(R.id.community_name)).getText().toString();
            community.description = ((EditText) mView.findViewById(R.id.community_description)).getText().toString();
            long communityId = mDataStorage.addCommunity(community);

            Log.d(TAG, "Community Created with ID: " + communityId);
            Bundle bundle = new Bundle();
            bundle.putLong("community_id", communityId);
            NavHostFragment.findNavController(CreateCommunityFragment.this)
                    .navigate(R.id.action_confirm_community_create, bundle);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
