package org.endcoronavirus.outreach;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.models.DataStorage;

import java.util.ArrayList;

public class CommunityListAdapter extends RecyclerView.Adapter<CommunityListAdapter.ThisViewHolder> {
    private static final String TAG = "CommunityListAdapter";
    ArrayList<String> communities;

    public void loadData(DataStorage dataStorage) {
        Log.d(TAG, "Loading data");
        communities = dataStorage.getAllCommunitiesNames();
    }

    @NonNull
    @Override
    public ThisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_community_list, parent, false);

        return new ThisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, int position) {
        holder.setText(communities.get(position));
    }

    @Override
    public int getItemCount() {
        if (communities != null)
            return communities.size();
        return 0;
    }

    class ThisViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ThisViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }
}
