package org.endcoronavirus.outreach.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import org.endcoronavirus.outreach.R;
import org.endcoronavirus.outreach.data.ContactDetailsParser;
import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.ContactDetails;
import org.endcoronavirus.outreach.models.DataStorage;
import org.endcoronavirus.outreach.models.LogEntry;

import java.io.IOException;
import java.io.InputStream;

public class ShowContactFragment extends Fragment {
    private static final String TAG = "ShowContactFragment";

    private static final int REQUEST_MAKE_CALL = 78;

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
    private AppState mAppState;
    private Uri contactUri;
    private Uri numberUri;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show_contact, container, false);
        setHasOptionsMenu(true);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveContact();
                NavHostFragment.findNavController(ShowContactFragment.this).popBackStack();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mView = view;
        setupView();

        mDataStorage = new ViewModelProvider(requireActivity()).get(DataStorage.class);
        mAppState = new ViewModelProvider(requireActivity()).get(AppState.class);

        Log.d(TAG, "Contact ID: " + mAppState.currentContactId());

        AsyncTask<Void, Void, Boolean> loaderTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                if (!load())
                    return false;
                contactDetails = mDataStorage.getContactById(mAppState.currentContactId());

                if (contactDetails == null) {
                    return false;
                }
                communityName = mDataStorage.getCommunityById(contactDetails.communityId).name;
                if (communityName == null) {
                    return false;
                }

                Log.d(TAG, "Community Name= " + communityName);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                if (!ok) {
                    Snackbar.make(mView, R.string.error_contact_not_found, Snackbar.LENGTH_LONG).show();
                    return;
                }
                setupData();
            }
        };
        loaderTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_show, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch  (id) {
            case R.id.command_menu_save:
                saveContact();
                Snackbar.make(view, R.string.message_contact_save_done, Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.action_delete_contact:
                // Allow for user to delete contact straight from this page
                AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        contactDetails.communityId *= -1;
                        mDataStorage.updateContact(contactDetails);
                        return true;
                    }
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        NavHostFragment.findNavController(ShowContactFragment.this).popBackStack();
                    }
                };
                task.execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        Button b = view.findViewById(R.id.action_call);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactDetailsParser parser = new ContactDetailsParser(getActivity(), contactDetails.contactId);
                numberUri = Uri.parse("tel:" + parser.getPhoneNumber(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE));
                Log.d(TAG, "Contact: " + contactDetails.contactId + " " + contactDetails.name + "found: " + numberUri.toString());
                tryCall(numberUri);
            }
        });
    }

    private void tryCall(Uri number) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestCallPermissionsAndCall(number, getActivity());
        } else {
            makeCall(number);
        }
    }

    private void requestCallPermissionsAndCall(Uri number, FragmentActivity activity) {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.CALL_PHONE)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE},
                    REQUEST_MAKE_CALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_MAKE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall(numberUri);
                } else {
                }
                return;
            }
        }
    }

    private void makeCall(Uri Number) {
        AsyncTask<Void, Void, Boolean> addLogTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                LogEntry entry = new LogEntry();
                entry.contactId = contactDetails.id;
                entry.description = getString(R.string.log_try_call);
                mDataStorage.logEntries().add(entry);
                return null;
            }
        };
        addLogTask.execute();

        Intent i = new Intent(Intent.ACTION_CALL, Number);
        startActivity(i);
    }

    private void populatePage(Cursor cursor) {
        String id = cursor.getString(CONTACT_FIELD_ID);
        String name = cursor.getString(CONTACT_FIELD_NAME);

        Log.d(TAG, "Id: " + id + " name: " + name + " Community ID: " + contactDetails.communityId);

        ((TextView) view.findViewById(R.id.field_name)).setText(name);
        ((TextView) view.findViewById(R.id.field_community)).setText(communityName);
        ((EditText) view.findViewById(R.id.field_freetext_notes)).setText(contactDetails.notes);

        Bitmap photo = BitmapFactory.decodeResource(getContext().getResources(),
                android.R.mipmap.sym_def_app_icon);

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactDetails.contactId));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((ImageView) view.findViewById(R.id.contact_picture)).setImageBitmap(photo);
    }

    private void saveContact() {
        contactDetails.notes = ((EditText) view.findViewById(R.id.field_freetext_notes)).getText().toString();

        AsyncTask<Void, Void, Boolean> saveTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                mDataStorage.updateContact(contactDetails);
                return true;
            }
        };
        saveTask.execute();
    }

    private Boolean load() {
        return true;
    }

    private void setupData() {
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
}
