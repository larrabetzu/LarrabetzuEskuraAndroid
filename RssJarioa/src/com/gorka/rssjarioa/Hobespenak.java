package com.gorka.rssjarioa;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

public class Hobespenak extends PreferenceActivity {
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                addPreferencesFromResource(R.xml.aukerak);
                }

        }
        @Override
        public void onBuildHeaders(List<Header> target) {
            // loadHeadersFromResource(R.xml.preference_headers, target);
        }

}
