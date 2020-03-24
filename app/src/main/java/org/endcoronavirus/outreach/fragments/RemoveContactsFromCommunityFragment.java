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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.adapters.CommunityContactsListAdapter;
import org.endcoronavirus.outreach.adapters.PhonebookContactListAdapter;
import org.endcoronavirus.outreach.adapters.RemoveContactsListAdapter;
import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

import java.util.Set;

public class RemoveContactsFromCommunityFragment extends Fragment {
    private static final String TAG = "RemoveCommunityFragment";

    private View view;
    private RecyclerView recyclerView;
    private RemoveContactsListAdapter adapter;
    private DataStorage mDataStorage;
    private AppState mAppState;
    private String communityName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAppState = new ViewModelProvider(requireActivity()).get(AppState.class);
        view = inflater.inflate(R.layout.fragment_remove_contacts, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = view.findViewById(R.id.action_remove_confirm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContacts();
            }
        });
        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);

        recyclerView = (RecyclerView) view.findViewById(R.id.contacts_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        AsyncTask<Void, Void, Boolean> loadTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                adapter = new RemoveContactsListAdapter(mDataStorage, mAppState.currentCommunityId());
                communityName = mDataStorage.getCommunityById(mAppState.currentCommunityId()).name;
                return true;
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                if (ok) {
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickedListener(new RemoveContactsListAdapter.OnItemClickedListener() {
                        @Override
                        public void onItemClicked(int position) {
                            ContactDetails contact = adapter.getContactAtPosition(position);
                            Log.d(TAG, "Contact Check/Uncheck: " + contact.id + " uri: " + contact.getContactUri());
                        }
                    });
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(communityName);
                }
            }
        };
        loadTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_remove_contacts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void removeContacts() {
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
                // and move to the community screen
                NavHostFragment.findNavController(RemoveContactsFromCommunityFragment.this)
                        .navigate(R.id.action_remove_confirm, null);
            }
        };
        task.execute();
    }
}
