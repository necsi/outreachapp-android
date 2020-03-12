package org.endcoronavirus.outreach.fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.models.DataStorage;

public class ShowContactFragment extends Fragment {
    private static final String TAG = "ShowContactFrgment";

    private DataStorage mDataStorage;
    private View view;
    private Uri contactUri;

    private ContentResolver contentResolver;
    private Cursor cursor;

    @SuppressLint("InlinedApi")
    private static final String[] CONTACT_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI
    };

    private static final int CONTACT_FIELD_ID = 0;
    private static final int CONTACT_FIELD_KEY = 1;
    private static final int CONTACT_FIELD_NAME = 2;
    private static final int CONTACT_FIELD_PIC = 3;

    private static final String[] DATA_PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE
    };
    private static final int DATA_FIELD_NUM = 0;
    private static final int DATA_FIELD_TYPE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show_contact, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);

        contactUri = getArguments().getParcelable("contact_uri");

        Log.d(TAG, "Getting contact: " + contactUri);

        contentResolver = getActivity().getContentResolver();
        cursor = contentResolver.query(contactUri, CONTACT_PROJECTION, null, null, null);

        if (cursor.moveToFirst()) {
            populatePage(cursor);
        } else {
            Snackbar snackbar = Snackbar
                    .make(view, R.string.error_cant_read_contact, Snackbar.LENGTH_LONG);

            snackbar.show();
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

    private void populatePage(Cursor cursor) {
        String id = cursor.getString(CONTACT_FIELD_ID);
        String name = cursor.getString(CONTACT_FIELD_NAME);

        Log.d(TAG, "Id: " + id + " name: " + name);

        ((TextView) view.findViewById(R.id.field_name)).setText(name);

        Cursor phones = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, DATA_PROJECTION,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);

        Log.d(TAG, "Found: " + phones.getCount());
        while (phones.moveToNext()) {
            String number = phones.getString(DATA_FIELD_NUM);
            int type = phones.getInt(DATA_FIELD_TYPE);

            Log.d(TAG, "Phone Number: " + type + " => " + number);
            ((TextView) view.findViewById(R.id.field_phone)).setText(number);
        }
    }
}
