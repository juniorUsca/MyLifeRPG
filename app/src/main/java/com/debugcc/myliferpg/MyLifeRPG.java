package com.debugcc.myliferpg;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyLifeRPG extends Application{
    private static final String TAG = "MyLifeRPGAPP INICIO";

    @Override
    public void onCreate() {
        super.onCreate();

        /// Initialize Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.debugcc.myliferepg",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "onCreate1: ", e);

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "onCreate2: ", e);
        }

    }
}
