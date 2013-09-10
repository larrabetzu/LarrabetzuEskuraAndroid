package com.gorka.rssjarioa;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.List;

public class Hobespenak extends PreferenceActivity {
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.aukerak);
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
    }
}
