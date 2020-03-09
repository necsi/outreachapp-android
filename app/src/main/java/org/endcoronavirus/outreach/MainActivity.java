package org.endcoronavirus.outreach;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.endcoronavirus.outreach.service.BackendService;
import org.endcoronavirus.outreach.service.BackendServiceInterface;
import org.endcoronavirus.outreach.service.BackendServiceListener;

public class MainActivity extends AppCompatActivity implements BackendServiceListener {

    private BackendService.BackendServiceConnection mServiceConnection;
    private BackendServiceInterface mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, BackendService.class));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
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
        mService = service;
    }

    @Override
    public void onServiceUnbound(BackendServiceInterface i) {
        mService = null;
    }
}
