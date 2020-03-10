package org.endcoronavirus.outreach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.endcoronavirus.outreach.service.BackendService;
import org.endcoronavirus.outreach.service.BackendServiceInterface;
import org.endcoronavirus.outreach.service.BackendServiceListener;

public class MainActivity extends AppCompatActivity implements BackendServiceListener {
    private static final String TAG = "MainActivity";

    private BackendService.BackendServiceConnection mServiceConnection;
    private BackendServiceInterface mService;
    private Fragment mCurrentFragment;
    private NavController navController;

    public BackendServiceInterface getService() {
        return mService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, BackendService.class));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        Log.d(TAG, "Fragment Attach");
        super.onAttachFragment(fragment);
        mCurrentFragment = fragment;
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

        Log.d(TAG, "Current Fragment: " + mCurrentFragment.getClass().getSimpleName());

        if (mCurrentFragment != null && mCurrentFragment instanceof RequiresServiceAccess) {
            RequiresServiceAccess ac = (RequiresServiceAccess) mCurrentFragment;
            ac.setService(mService);
        }
    }

    @Override
    public void onServiceUnbound(BackendServiceInterface i) {
        mService = null;
    }
}
