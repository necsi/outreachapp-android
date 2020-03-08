package org.endcoronavirus.outreach;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class CommunityListAdapter extends RecyclerView.Adapter<CommunityListAdapter.ThisViewHolder> {
    ArrayList<String> samples = new ArrayList<String>(Arrays.asList(
            "Family", "Colleagues"
    ));

    @NonNull
    @Override
    public ThisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_community_list, parent, false);

        return new ThisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, int position) {
        holder.setText(samples.get(position));
    }

    @Override
    public int getItemCount() {
        return samples.size();
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
