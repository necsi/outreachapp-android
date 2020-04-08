package org.endcoronavirus.outreach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import org.endcoronavirus.outreach.models.AppState;
import org.endcoronavirus.outreach.models.DataStorage;
import org.endcoronavirus.outreach.service.BackendService;
import org.endcoronavirus.outreach.service.BackendServiceInterface;
import org.endcoronavirus.outreach.service.BackendServiceListener;

public class MainActivity extends AppCompatActivity implements BackendServiceListener {
    private static final String TAG = "MainActivity";

    private BackendServiceInterface mService;

    private DataStorage mDataStorage;
    private AppState mAppState;

    public BackendServiceInterface getService() {
        return mService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataStorage = new ViewModelProvider(this).get(DataStorage.class);
        mDataStorage.open(this);

        mAppState = new ViewModelProvider(this).get(AppState.class);

        // NOTE service has been removed. Check also onStart()
        //startService(new Intent(this, BackendService.class));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startService(new Intent(this, BackendService.class));
    }

    @Override
    protected void onStart() {
        // NOTE service was removed from here
        //mServiceConnection = BackendService.bindService(this, this);
        super.onStart();
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
