package org.endcoronavirus.outreach.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.endcoronavirus.outreach.models.DataStorage;

public class BackendService extends Service implements BackendServiceInterface {
    private static final String TAG = "BackendService";
    final IBinder mBinder = new LocalBinder();

    DataStorage mDataStorage;

    public DataStorage getDataStorage() {
        return mDataStorage;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDataStorage = new DataStorage(this);

        Log.d(TAG, "Backend Service Started");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static BackendServiceConnection bindService(Context context, BackendServiceListener listener) {
        BackendServiceConnection connection = new BackendServiceConnection(listener);
        BackendService.getInstance(context, connection);

        return connection;
    }

    public static BackendServiceInterface get(IBinder binder) {
        if (binder instanceof LocalBinder) {
            return ((LocalBinder) binder).getService();
        }
        return null;
    }

    public static void getInstance(Context context, ServiceConnection connection) {
        Intent service = new Intent(context, BackendService.class);
        context.bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    public static class BackendServiceConnection implements ServiceConnection {
        private BackendServiceListener mListener;
        private BackendServiceInterface mService;

        public BackendServiceConnection(BackendServiceListener listener) {
            mListener = listener;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mService = BackendService.get(binder);
            mListener.onServiceBound(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BackendServiceInterface service = mService;
            mService = null;
            mListener.onServiceUnbound(service);
        }
    }

    /**
     * Binder di interfacciamento servizio con client
     */
    public class LocalBinder extends Binder {
        /**
         * Ritorna l'oggetto BackendService associato al servizio
         *
         * @return
         */
        public BackendService getService() {
            return BackendService.this;
        }
    }
}
