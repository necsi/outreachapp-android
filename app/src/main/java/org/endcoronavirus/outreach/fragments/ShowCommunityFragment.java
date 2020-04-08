package org.endcoronavirus.outreach.fragments;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.adapters.CommunityContactsListAdapter;
import org.endcoronavirus.outreach.adapters.SelectContactsListAdapter;
import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

public class ShowCommunityFragment extends Fragment {
    private static final String TAG = "ShowCommunityFragment";

    private View view;
    private RecyclerView recyclerView;
    private DataStorage mDataStorage;
    private CommunityContactsListAdapter adapter;
    private AppState mAppState;
    private String communityName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAppState = new ViewModelProvider(requireActivity()).get(AppState.class);
        view = inflater.inflate(R.layout.fragment_show_community, container, false);
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

        AsyncTask<Void, Void, Boolean> loadTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                adapter = new CommunityContactsListAdapter(mDataStorage, mAppState.currentCommunityId());
                communityName = mDataStorage.ds().getCommunityById(mAppState.currentCommunityId()).name;
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
                            NavHostFragment.findNavController(ShowCommunityFragment.this)
                                    .navigate(R.id.action_show_contact, null);
                        }

                        // Second method needed for long taps
                        @Override
                        public boolean onItemLongClicked(int position) {
                            long contactID = adapter.getContactAtPosition(position).contactId;
                            Bundle bundle = new Bundle();
                            // Pass forward the ID of the contact to be selected
                            bundle.putLong("trueID", contactID);
                            NavHostFragment.findNavController(ShowCommunityFragment.this)
                                    .navigate(R.id.action_select_contacts_in_community, bundle);
                            return true;
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
        inflater.inflate(R.menu.menu_show_community, menu);
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
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.action_import_contacts:
                NavHostFragment.findNavController(ShowCommunityFragment.this)
                        .navigate(R.id.action_import_contacts, null);
                return true;

            case R.id.action_delete:
                return doActionDelete();
            case R.id.action_edit:
                return doActionEdit();
            // Select all is the same as select, except sending forward a long special value
            // corresponding to select all (via a bundle)
            case R.id.action_select_all_contacts_in_community:
                bundle.putLong("trueID", SelectContactsListAdapter.SELECT_ALL);
            case R.id.action_select_contacts_in_community:
                NavHostFragment.findNavController(ShowCommunityFragment.this)
                        .navigate(R.id.action_select_contacts_in_community, bundle);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean doActionDelete() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.message_title_confirm_delete)
                .setMessage(R.string.message_confirm_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                int numdeleted = mDataStorage.ds().deleteCommunity(mAppState.currentCommunityId());
                                return numdeleted == 1;
                            }

                            @Override
                            protected void onPostExecute(Boolean ok) {
                                if (ok) {
                                    NavHostFragment.findNavController(ShowCommunityFragment.this).navigateUp();
                                } else {
                                    Snackbar.make(view, R.string.message_error_cant_delete, Snackbar.LENGTH_LONG).show();
                                }
                            }
                        };
                        task.execute();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        return true;
    }

    private boolean doActionEdit() {
        NavHostFragment.findNavController(this).navigate(R.id.action_community_edit);
        return true;
    }

    private void setFilterString(final String filterString) {
        AsyncTask<Void, Void, Boolean> refreshTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                adapter.setFilterString(filterString);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                adapter.notifyDataSetChanged();
            }
        };
        refreshTask.execute();
    }

}
