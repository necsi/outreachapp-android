package org.endcoronavirus.outreach.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.adapters.SelectContactsListAdapter;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

import java.util.Set;

public class SelectContactsFromPhonebookFragment extends Fragment {
    private static final int REQUEST_READ_CONTACTS = 79;
    private static final String TAG = "SelectContactsFrg";

    private View view;
    private SelectContactsListAdapter adapter;
    private RecyclerView recyclerView;
    private DataStorage mDataStorage;
    private long communityId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        communityId = getArguments().getLong("community_id");
        Log.d(TAG, "Community ID: " + communityId);

        view = inflater.inflate(R.layout.fragment_select_contacts_from_phonebook, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.contacts_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);
        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);

        // FIXME: After granting permissions, the contacts are NOT displayed.
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
        inflater.inflate(R.menu.menu_community_create, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.command_menu_confirm) {
            // add all selected contacts to community
            Set<ContactDetails> details = adapter.getSelectedContacts();
            Log.d(TAG, "Contacts added: " + details.size());
            for (ContactDetails contactDetails : details) {
                contactDetails.communityId = communityId;
                mDataStorage.addContact(contactDetails);
            }

            // and move to the community screen
            Bundle bundle = new Bundle();
            bundle.putLong("community_id", communityId);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_done, bundle);
            return true;
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
                    // CHECKTHIS: probably the bug lies here.
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
        adapter = new SelectContactsListAdapter();
        adapter.startReadContacts(getActivity());
        recyclerView.setAdapter(adapter);
    }
}
