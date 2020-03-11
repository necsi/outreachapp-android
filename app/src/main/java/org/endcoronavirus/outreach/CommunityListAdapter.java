package org.endcoronavirus.outreach;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.models.CommunityDetails;
import org.endcoronavirus.outreach.models.DataStorage;

import java.util.ArrayList;

public class CommunityListAdapter extends RecyclerView.Adapter<CommunityListAdapter.ThisViewHolder> {
    private static final String TAG = "CommunityListAdapter";
    ArrayList<CommunityDetails> communities;
    private OnClickListener listener;


    public interface OnClickListener {
        public void onClick(int position);
    }

    public void loadData(DataStorage dataStorage) {
        Log.d(TAG, "Loading data");
        communities = dataStorage.getAllCommunitiesNames();
    }

    public int getIdAtPosition(int position) {
        return communities.get(position).id;
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
        holder.setText(communities.get(position).name);
    }

    @Override
    public int getItemCount() {
        if (communities != null)
            return communities.size();
        return 0;
    }

    public void setOnItemClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    class ThisViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ThisViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null)
                        listener.onClick(position);
                }
            });
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }
}
