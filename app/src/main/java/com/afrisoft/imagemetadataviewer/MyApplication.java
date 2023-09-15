package com.afrisoft.imagemetadataviewer;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

/** The Application class that manages AppOpenManager. */
public class MyApplication extends Application {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });
        AppOpenManager appOpenManager = new AppOpenManager(this,this);
    }
}
