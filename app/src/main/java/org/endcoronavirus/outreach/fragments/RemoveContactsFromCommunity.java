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
import android.widget.SearchView;

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
import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

import java.util.Set;

public class RemoveContactsFromCommunity extends Fragment {
    private static final String TAG = "RemoveContactsFragment";

    private View view;
    private RecyclerView recyclerView;
    private DataStorage mDataStorage;
    private CommunityContactsListAdapter adapter;
    private AppState mAppState;
    private String communityName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAppState = new ViewModelProvider(requireActivity()).get(AppState.class);
        view = inflater.inflate(R.layout.fragment_remove_contacts, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);

        FloatingActionButton fab = view.findViewById(R.id.action_remove_confirm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContacts();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.contacts_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        AsyncTask<Void, Void, Boolean> loadTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                adapter = new CommunityContactsListAdapter(mDataStorage, mAppState.currentCommunityId());
                communityName = mDataStorage.getCommunityById(mAppState.currentCommunityId()).name;
                return true;
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                if (ok) {
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickedListener(new CommunityContactsListAdapter.OnItemClickedListener() {
                        @Override
                        public void onItemClicked(int position) {
                            ContactDetails contact = adapter.getContactAtPosition(position);

                            Log.d(TAG, "Contact Selected: " + contact.id + " uri: " + contact.getContactUri());
                            mAppState.selectContact(contact.id);
                            NavHostFragment.findNavController(RemoveContactsFromCommunity.this)
                                    .navigate(R.id.action_show_contact, null);
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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setFilterString(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setFilterString(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.command_menu_filter) {
            manageFilter();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setFilterString(String filterString) {
        //PhonebookContactListAdapter.Filter filter = adapter.getFilter();
        //filter.filterString = filterString;
        //adapter.setFilter(filter);
    }

    private void manageFilter() {

    }

    public void removeContacts() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // add all selected contacts to community
                //Set<ContactDetails> details = adapter.getSelectedContacts();
                //Log.d(TAG, "Contacts added: " + details.size());
                //for (ContactDetails contactDetails : details) {
                //    contactDetails.communityId = mAppState.currentCommunityId();
                //    mDataStorage.addContact(contactDetails);
                //}

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                // and move to the community screen
                NavHostFragment.findNavController(RemoveContactsFromCommunity.this)
                        .navigate(R.id.action_remove_confirm, null);
            }
        };
        task.execute();
    }
}
