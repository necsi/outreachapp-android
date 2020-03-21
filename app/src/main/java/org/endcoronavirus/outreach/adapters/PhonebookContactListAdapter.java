package org.endcoronavirus.outreach.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PhonebookContactListAdapter extends RecyclerView.Adapter<PhonebookContactListAdapter.ThisViewHolder> {

    public static class Filter {
        public boolean starredFirst;
        public boolean withPhonesOnly;
        public String filterString;
    }

    private static final String TAG = "SelectContactsListAdapt";

    private Map<Long, ContactDetails> selectedContactsID = new HashMap<>();

    private ContentResolver contentResolver;
    private Cursor cursor;

    private Filter filter;

    public PhonebookContactListAdapter(Context context) {
        contentResolver = context.getContentResolver();
    }

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME

    };

    private static final int CONTACT_ID_INDEX = 0;
    private static final int CONTACT_KEY_INDEX = 1;
    private static final int CONTACT_NAME = 2;

    @SuppressLint("InlinedApi")
    private static final String FIELD_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

    private static final String SELECTION =
            FIELD_NAME + " LIKE ?";

    public void startReadContacts() {
        refresh();
    }

    private void refresh() {
        String selectionQuery = null;
        String[] selectionArgs = null;
        if (filter != null && filter.filterString != null) {
            selectionQuery = SELECTION;
            selectionArgs = new String[]{"%" + filter.filterString + "%"};
        }

        String sortOrder = FIELD_NAME;
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                PROJECTION, selectionQuery, selectionArgs, sortOrder);

        Log.d(TAG, "Cursor ready: " + cursor.getCount());
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
        refresh();
        notifyDataSetChanged();
    }

    public void clearFilter() {
        filter = null;
        refresh();
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public ThisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_contact_select, parent, false);

        return new ThisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String name = cursor.getString(CONTACT_NAME);

        holder.setText(name);
        holder.setPosition(position);

        holder.setCheckmarkIf(selectedContactsID.containsKey(Long.valueOf(cursor.getLong(CONTACT_ID_INDEX))));
    }

    @Override
    public int getItemCount() {
        int count = (cursor == null ? 0 : cursor.getCount());
        return count;
    }

    public Collection<ContactDetails> getSelectedContacts() {
        return selectedContactsID.values();
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
                        selectedContactsID.put(Long.valueOf(contactDetails.contactId), contactDetails);
                        Log.d(TAG, "Added: " + contactDetails.contactId + " num: " + selectedContactsID.size());
                    } else {
                        selectedContactsID.remove(Long.valueOf(contactDetails.contactId));
                        Log.d(TAG, "Removed: " + contactDetails.contactId + " num: " + selectedContactsID.size());
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
