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

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class SelectContactsListAdapter extends RecyclerView.Adapter<SelectContactsListAdapter.ThisViewHolder> {

    private static final String TAG = "SelectContactsListAdapt";

    private Set<ContactDetails> selectedContacts = new HashSet<ContactDetails>();
    private ContentResolver contentResolver;
    private Cursor cursor;

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

    public void startReadContacts(Context context) {
        contentResolver = context.getContentResolver();
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                PROJECTION, null, null, null);
        Log.d(TAG, "Cursor ready: " + cursor.getCount());
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

    }

    @Override
    public int getItemCount() {
        int count = (cursor == null ? 0 : cursor.getCount());
        return count;
    }

    public Set<ContactDetails> getSelectedContacts() {
        return selectedContacts;
    }

    class ThisViewHolder extends RecyclerView.ViewHolder {
        private int position;
        private TextView textView;

        public ThisViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);

            CheckBox cb = itemView.findViewById(R.id.selected_checkbox);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (cursor == null)
                        return;
                    cursor.moveToPosition(position);
                    ContactDetails contactDetails = new ContactDetails();
                    contactDetails.contactId = cursor.getLong(CONTACT_ID_INDEX);
                    contactDetails.contactKey = cursor.getString(CONTACT_KEY_INDEX);
                    contactDetails.name = cursor.getString(CONTACT_NAME);
                    if (isChecked)
                        selectedContacts.add(contactDetails);
                    else
                        selectedContacts.remove(contactDetails);
                }
            });
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
