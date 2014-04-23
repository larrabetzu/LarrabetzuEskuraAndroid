package com.gorka.rssjarioa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.google.analytics.tracking.android.EasyTracker;
import com.parse.PushService;


public class Hobespenak extends PreferenceActivity {


    Boolean kultura;
    Boolean kirola;
    Boolean udalgaiak;
    Boolean albisteak;
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.aukerak);

            final PreferenceScreen pab = (PreferenceScreen) findPreference("preferencescreen_aukeratzeko_blogak");
            pab.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    PreferenceScreen a = (PreferenceScreen) preference;
                    a.getDialog().getWindow().setBackgroundDrawableResource(R.color.zuria);
                    return false;
                }
            });
            final PreferenceScreen paa = (PreferenceScreen) findPreference("preferencescreen_abisuak_aukeran");
            paa.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    PreferenceScreen a = (PreferenceScreen) preference;
                    a.getDialog().getWindow().setBackgroundDrawableResource(R.color.zuria);
                    return false;
                }
            });

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            kultura= sharedPrefs.getBoolean("11", false);
            kirola= sharedPrefs.getBoolean("12", false);
            udalgaiak=sharedPrefs.getBoolean("13", false);
            albisteak= sharedPrefs.getBoolean("14", false);
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

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
