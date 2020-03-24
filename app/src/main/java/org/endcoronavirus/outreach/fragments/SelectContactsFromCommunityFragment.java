package org.endcoronavirus.outreach.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.adapters.SelectContactsListAdapter;
import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

import java.util.Set;

public class SelectContactsFromCommunityFragment extends Fragment {
    private static final String TAG = "RemoveCommunityFragment";

    private View view;
    private RecyclerView recyclerView;
    private SelectContactsListAdapter adapter;
    private DataStorage mDataStorage;
    private AppState mAppState;
    private String communityName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAppState = new ViewModelProvider(requireActivity()).get(AppState.class);
        view = inflater.inflate(R.layout.fragment_select_contacts_in_community, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);

        recyclerView = (RecyclerView) view.findViewById(R.id.contacts_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        // Get optional trueID, a long value that corresponds to what contacts should be pre-selected
        // if any (or all)
        long trueID = getArguments().getLong("trueID");

        AsyncTask<Void, Void, Boolean> loadTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // trueID - passing forward the ID of the contact (or all contacts) that should be selected
                adapter = new SelectContactsListAdapter(mDataStorage, mAppState.currentCommunityId(), trueID);
                communityName = mDataStorage.getCommunityById(mAppState.currentCommunityId()).name;
                return true;
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                if (ok) {
                    recyclerView.setAdapter(adapter);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(communityName);
                }
            }
        };
        loadTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_select_contacts_in_community, menu);
        // Busy waiting in case this function is called before the adapter is created
        while (adapter == null);
        adapter.setMenuItems(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_cancel_selection:
                NavHostFragment.findNavController(SelectContactsFromCommunityFragment.this).popBackStack();
                return true;
            case R.id.action_delete_selection:
                return removeContacts();
            case R.id.action_select_all_contacts_in_community:
                return selectAll();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean removeContacts() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // remove all selected contacts to community
                Set<ContactDetails> details = adapter.getSelectedContacts();
                Log.d(TAG, "Contacts removed: " + details.size());
                for (ContactDetails contactDetails : details) {
                    contactDetails.communityId = mAppState.currentCommunityId();
                    contactDetails.communityId *= -1;
                    mDataStorage.updateContact(contactDetails);
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                // and move back to the community page
                NavHostFragment.findNavController(SelectContactsFromCommunityFragment.this).popBackStack();
            }
        };
        task.execute();
        return true;
    }

    public boolean  selectAll() {
        // Busy wait in case adapter is not created yet
        while (adapter == null);
        return adapter.selectAll();
    }
}
