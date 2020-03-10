package org.endcoronavirus.outreach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.endcoronavirus.outreach.models.DataStorage;
import org.endcoronavirus.outreach.service.BackendService;
import org.endcoronavirus.outreach.service.BackendServiceInterface;
import org.endcoronavirus.outreach.service.BackendServiceListener;

public class MainActivity extends AppCompatActivity implements BackendServiceListener {
    private static final String TAG = "MainActivity";

    private BackendService.BackendServiceConnection mServiceConnection;
    private BackendServiceInterface mService;

    private DataStorage mDataStorage;

    public BackendServiceInterface getService() {
        return mService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataStorage = new ViewModelProvider(this).get(DataStorage.class);
        mDataStorage.open(this);

        startService(new Intent(this, BackendService.class));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        Log.d(TAG, "Fragment Attach");
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Binding");
        mServiceConnection = BackendService.bindService(this, this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void onServiceBound(BackendServiceInterface service) {
        Log.d(TAG, "Service Bound");
        mService = service;
    }

    @Override
    public void onServiceUnbound(BackendServiceInterface i) {
        mService = null;
    }
}
