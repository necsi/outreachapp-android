package org.endcoronavirus.outreach.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.endcoronavirus.outreach.R;

/**
 *
 */
public class SelectContactsListAdapter extends RecyclerView.Adapter<SelectContactsListAdapter.ThisViewHolder> {

    private static final String TAG = "SelectContactsListAdapt";

    private ContentResolver contentResolver;
    Cursor cursor;

    public void startReadContacts(Context context) {
        contentResolver = context.getContentResolver();
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Log.d(TAG, "Cursor ready: " + cursor.getCount());
    }

    @NonNull
    @Override
    public ThisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_contact, parent, false);

        return new ThisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        holder.setText(name);
    }

    @Override
    public int getItemCount() {
        int count = (cursor == null ? 0 : cursor.getCount());
        return count;
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
