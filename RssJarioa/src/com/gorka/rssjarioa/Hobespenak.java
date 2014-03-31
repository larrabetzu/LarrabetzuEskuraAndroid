package com.gorka.rssjarioa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.parse.PushService;

import java.util.List;

public class Hobespenak extends PreferenceActivity {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

    Boolean kultura;
    Boolean kirola;
    Boolean udalgaiak;
    Boolean albisteak;
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.aukerak);

            kultura= sharedPrefs.getBoolean("11", false);
            kirola= sharedPrefs.getBoolean("12", false);
            udalgaiak=sharedPrefs.getBoolean("13", false);
            albisteak= sharedPrefs.getBoolean("14", false);
        }
        @Override
        public void onBuildHeaders(List<Header> target) {
            // loadHeadersFromResource(R.xml.preference_headers, target);
        }

    @Override
    public void onStart() {
        super.onStart();
        // The rest of your onStart() code.
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // The rest of your onStop() code.
        EasyTracker.getInstance(this).activityStop(this);

        if (kultura != (sharedPrefs.getBoolean("11", false))){
            if(!kultura){
                PushService.subscribe(this, "kultura", Menua.class);
            }else{
                PushService.unsubscribe(this, "kultura");
            }
        }
        if (kirola != (sharedPrefs.getBoolean("12", false))){
            if(!kirola){
                PushService.subscribe(this, "kirola", Menua.class);
            }else{
                PushService.unsubscribe(this, "kirola");
            }
        }
        if (udalgaiak != (sharedPrefs.getBoolean("13", false))){
            if(!udalgaiak){
                PushService.subscribe(this, "udalgaiak", Menua.class);
            }else{
                PushService.unsubscribe(this, "udalgaiak");
            }
        }
        if (albisteak != (sharedPrefs.getBoolean("14", false))){
            if(!albisteak){
                PushService.subscribe(this, "albisteak", Menua.class);
            }else{
                PushService.unsubscribe(this, "albisteak");
            }
        }
    }
}
