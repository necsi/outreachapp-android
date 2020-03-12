package org.endcoronavirus.outreach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.adapters.CommunityContactsListAdapter;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

public class ShowCommunityFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private DataStorage mDataStorage;
    private long communityId;
    private CommunityContactsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        communityId = getArguments().getLong("community_id");
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

        adapter = new CommunityContactsListAdapter(mDataStorage, communityId);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickedListener(new CommunityContactsListAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int position) {
                ContactDetails contact = adapter.getContactAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("contact_uri", contact.getContactUri());
                NavHostFragment.findNavController(ShowCommunityFragment.this)
                        .navigate(R.id.action_show_contact, bundle);
            }
        });
    }
}
