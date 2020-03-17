package org.endcoronavirus.outreach.fragments;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
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

import com.google.android.material.snackbar.Snackbar;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.CommunityDetails;
import org.endcoronavirus.outreach.models.DataStorage;

public class CreateCommunityFragment extends Fragment {

    private static final String TAG = "CreateCommunityFragment";
    private View mView;
    private DataStorage mDataStorage;
    private AppState mAppState;

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
        mAppState = new ViewModelProvider(requireActivity()).get(AppState.class);
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
            AsyncTask<Void, Void, Boolean> updateTask = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    CommunityDetails community = new CommunityDetails();
                    if (mAppState.isCommunityIdAvailable())
                        community.id = mAppState.currentCommunityId();
                    community.name = ((EditText) mView.findViewById(R.id.community_name)).getText().toString();
                    community.description = ((EditText) mView.findViewById(R.id.community_description)).getText().toString();

                    try {
                        if (!mAppState.isCommunityIdAvailable()) {
                            long commid = mDataStorage.addCommunity(community);
                            mAppState.selectCommunity(commid);
                            community.id = commid;
                            Log.d(TAG, "Community Created with ID: " + commid);
                        } else {
                            mDataStorage.updateCommunity(community);
                        }
                    } catch (SQLiteConstraintException x) {
                        Log.e(TAG, "Insert failed: " + x.toString());
                        return false;
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean ok) {
                    Log.d(TAG, "Create Community returned: " + ok);
                    if (ok) {
                        NavHostFragment.findNavController(CreateCommunityFragment.this)
                                .navigate(R.id.action_confirm_community_create, null);
                    } else {
                        Snackbar.make(mView, R.string.error_cant_create_community, Snackbar.LENGTH_LONG).show();
                    }
                }
            };
            updateTask.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
