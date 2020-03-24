package org.endcoronavirus.outreach.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.adapters.LogEntriesListAdapter;
import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.DataStorage;
import org.endcoronavirus.outreach.models.LogEntry;

import java.util.List;

public class BrowseLogEntriesFragment extends Fragment {
    private static final String TAG = "BrowseLogEntriesFragmen";
    private View view;
    private RecyclerView recyclerView;
    private LogEntriesListAdapter adapter;
    private DataStorage mDataStorage;
    private AppState mAppState;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse_log_entries, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.history_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);
        mAppState = new ViewModelProvider(requireActivity()).get(AppState.class);

        adapter = new LogEntriesListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                List<LogEntry> entries = mDataStorage.logEntries().getLogsForContact(mAppState.currentContactId());
                Log.d(TAG, "Entries for " + mAppState.currentContactId() + ": " + entries.size());
                adapter.setLogEntries(entries);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                adapter.notifyDataSetChanged();
            }
        };
        task.execute();
    }
}
