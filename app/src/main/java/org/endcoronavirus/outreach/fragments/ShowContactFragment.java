package org.endcoronavirus.outreach.fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.data.ContactDetailsParser;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;

public class ShowContactFragment extends Fragment {
    private static final String TAG = "ShowContactFragment";

    private DataStorage mDataStorage;
    private View view;
    private ContactDetails contactDetails;
    private String communityName;

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

    private Uri contactUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show_contact, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupView();

        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);

        long contactId = getArguments().getLong("contact_id");
        Log.d(TAG, "Contact ID: " + contactId);

        contactDetails = mDataStorage.getContactById(contactId);

        if (contactDetails == null) {
            Snackbar.make(view, R.string.error_contact_not_found, Snackbar.LENGTH_LONG);
            return;
        }

        communityName = mDataStorage.getCommunityById(contactDetails.communityId).name;
        Log.d(TAG, "Community Name= " + communityName);

        contactUri = contactDetails.getContactUri();

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

    private void setupView() {
        Button b = view.findViewById(R.id.action_call);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactDetailsParser parser = new ContactDetailsParser(getActivity(), contactDetails.contactId);
                Uri phone = Uri.parse("tel:" + parser.getPhoneNumber(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE));
                Intent i = new Intent(Intent.ACTION_CALL, phone);
                startActivity(i);
            }
        });
    }

    private void populatePage(Cursor cursor) {
        String id = cursor.getString(CONTACT_FIELD_ID);
        String name = cursor.getString(CONTACT_FIELD_NAME);

        Log.d(TAG, "Id: " + id + " name: " + name + " Community ID: " + contactDetails.communityId);

        ((TextView) view.findViewById(R.id.field_name)).setText(name);
        ((TextView) view.findViewById(R.id.field_community)).setText(communityName);
    }
}
