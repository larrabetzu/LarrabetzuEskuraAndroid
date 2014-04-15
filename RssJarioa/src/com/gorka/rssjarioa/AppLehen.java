package com.gorka.rssjarioa;


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class AppLehen extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "uucnk4RbZSP3o41Ntzp9Ju6o3i99ENAJqPsZYOsB", "ABhgQKHR1NNNmXqVxslL2HI1jYQNKu948MZnPapr");
        PushService.setDefaultPushCallback(this, Menua.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
