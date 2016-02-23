package com.chronix.databaseparsing;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by chronix on 2/23/16.
 */
public class Database {

    private Context context;

    public Database(Context context) {
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
