package com.gorka.rssjarioa;

import android.os.Bundle;
import android.preference.PreferenceActivity;

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

}
