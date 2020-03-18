package org.endcoronavirus.outreach.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.adapters.PhonebookContactListAdapter;
import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

import java.util.Set;

public class SelectContactsFromPhonebookFragment extends Fragment {
    private static final int REQUEST_READ_CONTACTS = 79;
    private static final String TAG = "SelectContactsFrg";

    private View view;
    private PhonebookContactListAdapter adapter;
    private RecyclerView recyclerView;
    private DataStorage mDataStorage;
    private AppState mAppState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAppState = new ViewModelProvider(requireActivity()).get(AppState.class);
        Log.d(TAG, "Community ID: " + mAppState.currentCommunityId());

        view = inflater.inflate(R.layout.fragment_select_contacts_from_phonebook, container, false);

        FloatingActionButton fab = view.findViewById(R.id.action_next);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContacts();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.contacts_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);
        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(getActivity());
        } else {
            startReadContacts();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_import_contacts, menu);
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


    private void requestPermission(Activity activity) {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startReadContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void startReadContacts() {
        adapter = new PhonebookContactListAdapter();
        PhonebookContactListAdapter.Filter filter = new PhonebookContactListAdapter.Filter();
        adapter.setFilter(filter);
        adapter.startReadContacts(getActivity());
        recyclerView.setAdapter(adapter);
    }

    private void addContacts() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // add all selected contacts to community
                Set<ContactDetails> details = adapter.getSelectedContacts();
                Log.d(TAG, "Contacts added: " + details.size());
                for (ContactDetails contactDetails : details) {
                    contactDetails.communityId = mAppState.currentCommunityId();
                    mDataStorage.addContact(contactDetails);
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                // and move to the community screen
                NavHostFragment.findNavController(SelectContactsFromPhonebookFragment.this)
                        .navigate(R.id.action_done, null);
            }
        };
        task.execute();
    }

    private void setFilterString(String filterString) {
        PhonebookContactListAdapter.Filter filter = adapter.getFilter();
        filter.filterString = filterString;
        adapter.setFilter(filter);
    }

    private void manageFilter() {

    }

}
