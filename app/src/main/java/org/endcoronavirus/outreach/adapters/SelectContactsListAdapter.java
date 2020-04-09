package org.endcoronavirus.outreach.adapters;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import org.endcoronavirus.outreach.models.DataStorageObject;

import java.util.HashSet;
import java.util.Set;

public class SelectContactsListAdapter extends RecyclerView.Adapter<SelectContactsListAdapter.ViewHolder> {

    private ContactDetails[] contacts;
    private Set<ContactDetails> selectedContacts = new HashSet<ContactDetails>();
    private Set<Integer> selectedContactsPosition = new HashSet<>();
    private int selectedSize = 0;
    static final public long SELECT_ALL = Long.MAX_VALUE;
    boolean[] defaultState;
    private MenuItem deleteSelectionMenuItem;
    private MenuItem selectAllMenuItem;
    private ViewHolder[] viewHolders;

    public SelectContactsListAdapter(DataStorage dataStorage, long communityId, long trueID) {
        contacts = dataStorage.ds().getAllContacts(communityId, DataStorageObject.Sorting.Name);
        viewHolders = new ViewHolder[contacts.length];
        // Determine starting state of all contacts, some (or all) might need to be selected
        defaultState = new boolean[contacts.length];
        for (int i = 0; i < defaultState.length; i++) {
            if (trueID == SELECT_ALL || contacts[i].contactId == trueID)
                defaultState[i] = true;
            else
                defaultState[i] = false;
        }
    }

    public ContactDetails getContactAtPosition(int position) {
        return contacts[position];
    }

    public void setMenuItems(Menu menu) {
        deleteSelectionMenuItem = menu.findItem(R.id.action_delete_selection);
        selectAllMenuItem = menu.findItem(R.id.action_select_all_contacts_in_community);
    }

    // Update for every time a contact checkbox is changed
    private void menuUpdate() {
        if (selectedSize < contacts.length)
            selectAllMenuItem.setVisible(true);
        else
            selectAllMenuItem.setVisible(false);
        if (selectedSize > 0)
            deleteSelectionMenuItem.setVisible(true);
        else
            deleteSelectionMenuItem.setVisible(false);
    }

    public Set<ContactDetails> getSelectedContacts() {
        return selectedContacts;
    }

    public boolean selectAll() {
        for (ViewHolder viewholder : viewHolders)
            if (!viewholder.checkBox.isChecked())
                viewholder.checkBox.setChecked(true);
        return true;
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
        viewHolders[position] = holder;
        holder.checkBox.setChecked(defaultState[position]);
    }

    @Override
    public int getItemCount() {
        return contacts.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private CheckBox checkBox;

        public ViewHolder(@NonNull final View itemView) {
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
                        // HashSet size does not decrement, so need to keep track of actual size
                        selectedSize++;
                        menuUpdate();
                    } else {
                        selectedContacts.remove(contactDetails);
                        selectedContactsPosition.remove(Long.valueOf(position));
                        // HashSet size does not decrement, so need to keep track of actual size
                        selectedSize--;
                        menuUpdate();
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
