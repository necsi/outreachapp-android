package org.endcoronavirus.outreach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.endcoronavirus.outreach.models.DataStorage;

public class CommunitiesBrowse extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CommunityListAdapter dataAdapter;
    private DataStorage mDataStorage;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_browse, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        dataAdapter = new CommunityListAdapter();
        dataAdapter.setOnItemClickListener(new CommunityListAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                int id = dataAdapter.getIdAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putLong("community_id", id);
                NavHostFragment.findNavController(CommunitiesBrowse.this)
                        .navigate(R.id.action_select_community, bundle);
            }
        });

        recyclerView.setAdapter(dataAdapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(CommunitiesBrowse.this)
                        .navigate(R.id.add_community_action);
            }
        });

        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);
        dataAdapter.loadData(mDataStorage);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
