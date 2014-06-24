package com.gorka.rssjarioa;


import android.app.Application;

import com.parse.Parse;
import com.parse.PushService;

public class AppLehen extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, password.YOUR_APP_ID, password.YOUR_CLIENT_KEY);
        PushService.setDefaultPushCallback(this, Menua.class);
    }

}
