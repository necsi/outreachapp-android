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

import java.text.DateFormat;
import java.util.Date;

public class CommunityContactsListAdapter extends RecyclerView.Adapter<CommunityContactsListAdapter.ViewHolder> {

    private ContactDetails[] contacts;
    private OnItemClickedListener listener;
    private DataStorage dataStorage;
    private long communityId;
    private String filter;
    private DataStorage.Sorting mSorting = DataStorage.Sorting.LastContacted;
    private final DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

    public interface OnItemClickedListener {
        public void onItemClicked(int position);

        // Second method needed for long taps
        public boolean onItemLongClicked(int position);
    }

    public CommunityContactsListAdapter(DataStorage dataStorage, long communityId) {
        this.dataStorage = dataStorage;
        this.communityId = communityId;
        refresh();
    }

    private void refresh() {
        if (filter == null || filter.isEmpty())
            contacts = dataStorage.getAllContacts(communityId, mSorting);
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
        holder.setLastContacted(contacts[position].lastContacted);
    }

    @Override
    public int getItemCount() {
        return contacts.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView lastContacted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.contact_name);
            lastContacted = itemView.findViewById(R.id.contact_last_contacted);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null)
                        listener.onItemClicked(position);
                }
            });

            itemView.setOnLongClickListener((v) -> {
                int position = getAdapterPosition();
                if (listener != null)
                    return listener.onItemLongClicked(position);
                return false;
            });
        }

        public void setText(String name) {
            textView.setText(name);
        }

        public void setLastContacted(Date date) {
            if (date != null)
                lastContacted.setText(dateFormatter.format(date));
            else
                lastContacted.setText(R.string.last_contact_never);
        }
    }

    public void setFilterString(String filter) {
        this.filter = "%" + filter + "%";
        refresh();

        if (Looper.myLooper() == Looper.getMainLooper())
            notifyDataSetChanged();
    }
}
