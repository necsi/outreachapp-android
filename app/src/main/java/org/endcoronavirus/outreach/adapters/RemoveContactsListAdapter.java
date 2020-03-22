package org.endcoronavirus.outreach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

import java.util.HashSet;
import java.util.Set;

public class RemoveContactsListAdapter extends RecyclerView.Adapter<RemoveContactsListAdapter.ViewHolder> {

    private ContactDetails[] contacts;
    private OnItemClickedListener listener;
    private Set<ContactDetails> selectedContacts = new HashSet<ContactDetails>();
    private Set<Integer> selectedContactsPosition = new HashSet<>();

    public interface OnItemClickedListener {
        public void onItemClicked(int position);
    }

    public RemoveContactsListAdapter(DataStorage dataStorage, long communityId) {
        contacts = dataStorage.getAllContacts(communityId);
    }

    public ContactDetails getContactAtPosition(int position) { return contacts[position]; }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.listener = listener;
    }

    public Set<ContactDetails> getSelectedContacts() {
        return selectedContacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_contact_select, parent, false);

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
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
            checkBox = itemView.findViewById(R.id.selected_checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    ContactDetails contactDetails = new ContactDetails();
                    if (isChecked) {
                        selectedContacts.add(contacts[position]);
                        selectedContactsPosition.add(position);
                    } else {
                        selectedContacts.remove(contactDetails);
                        selectedContactsPosition.remove(Long.valueOf(position));
                    }
                }
            });
        }

        public void setText(String name) {
            textView.setText(name);
        }

        public void setCheckmarkIf(boolean checked) {
            checkBox.setChecked(checked);
        }
    }
}
