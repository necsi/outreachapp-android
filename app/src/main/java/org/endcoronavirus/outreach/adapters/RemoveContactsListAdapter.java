package org.endcoronavirus.outreach.adapters;

import android.content.ContentResolver;
import android.database.Cursor;
import android.view.View;
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

public class RemoveContactsListAdapter extends RecyclerView.Adapter<RemoveContactsListAdapter.ThisViewHolder> {
    public static class Filter {
        public boolean starredFirst;
        public boolean withPhonesOnly;
        public String filterString;
    }

    private static final int CONTACT_ID_INDEX = 0;
    private static final int CONTACT_KEY_INDEX = 1;
    private static final int CONTACT_NAME = 2;

    private ContactDetails[] contacts;
    private Cursor cursor;
    private Set<ContactDetails> selectedContacts = new HashSet<ContactDetails>();
    private Set<Long> selectedContactsPosition = new HashSet<>();

    private static final String TAG = "RemoveContactsListAdapter";

    public RemoveContactsListAdapter(DataStorage dataStorage, long communityId) {
        contacts = dataStorage.getAllContacts(communityId);
    }

    class ThisViewHolder extends RecyclerView.ViewHolder {
        private int position;
        private TextView textView;
        private CheckBox checkBox;

        public ThisViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);

            checkBox = itemView.findViewById(R.id.selected_checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (cursor == null)
                        return;
                    cursor.moveToPosition(position);
                    ContactDetails contactDetails = new ContactDetails();
                    contactDetails.contactId = cursor.getLong(CONTACT_ID_INDEX);
                    contactDetails.contactKey = cursor.getString(CONTACT_KEY_INDEX);
                    contactDetails.name = cursor.getString(CONTACT_NAME);
                    if (isChecked) {
                        selectedContacts.add(contactDetails);
                        selectedContactsPosition.add(Long.valueOf(position));
                    } else {
                        selectedContacts.remove(contactDetails);
                        selectedContactsPosition.remove(Long.valueOf(position));
                    }
                }
            });
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setCheckmarkIf(boolean checked) {
            checkBox.setChecked(checked);
        }
    }
}
