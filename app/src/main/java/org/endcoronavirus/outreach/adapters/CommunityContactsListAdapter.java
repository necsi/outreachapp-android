package org.endcoronavirus.outreach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

import java.util.ArrayList;

public class CommunityContactsListAdapter extends RecyclerView.Adapter<CommunityContactsListAdapter.ViewHolder> {

    private ArrayList<ContactDetails> contacts;
    private OnItemClickedListener listener;

    public interface OnItemClickedListener {
        public void onItemClicked(int position);
    }

    public CommunityContactsListAdapter(DataStorage dataStorage, long communityId) {
        contacts = dataStorage.getAllContacts(communityId);
    }

    public ContactDetails getContactAtPosition(int position) {
        return contacts.get(position);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_contact, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setText(contacts.get(position).name);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null)
                        listener.onItemClicked(position);
                }
            });
        }

        public void setText(String name) {
            textView.setText(name);
        }
    }
}
