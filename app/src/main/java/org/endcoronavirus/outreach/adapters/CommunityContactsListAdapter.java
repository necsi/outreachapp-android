package org.endcoronavirus.outreach.adapters;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

public class CommunityContactsListAdapter extends RecyclerView.Adapter<CommunityContactsListAdapter.ViewHolder> {

    private ContactDetails[] contacts;
    private OnItemClickedListener listener;

    private DataStorage dataStorage;
    private long communityId;
    private String filter;

    public interface OnItemClickedListener {
        public void onItemClicked(int position);
    }

    public CommunityContactsListAdapter(DataStorage dataStorage, long communityId) {
        this.dataStorage = dataStorage;
        this.communityId = communityId;
        refresh();
    }

    private void refresh() {
        if (filter == null || filter.isEmpty())
            contacts = dataStorage.getAllContacts(communityId);
        else
            contacts = dataStorage.searchContactsForPattern(filter);
    }

    public ContactDetails getContactAtPosition(int position) {
        return contacts[position];
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
        holder.setText(contacts[position].name);
    }

    @Override
    public int getItemCount() {
        return contacts.length;
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

    public void setFilterString(String filter) {
        this.filter = "%" + filter + "%";
        refresh();

        if (Looper.myLooper() == Looper.getMainLooper())
            notifyDataSetChanged();
    }
}
