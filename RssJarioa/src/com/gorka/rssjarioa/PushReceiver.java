package com.gorka.rssjarioa;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

public class PushReceiver extends BroadcastReceiver {

    private static final String TAG = "PushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ParseAnalytics.trackAppOpened(intent);
        try {

            /**
             * { "action": "com.gorka.rssjarioa.UPDATE_STATUS”,
             * "alert": "",
             * "tex”:"",
             * "url": ""}
             */
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Intent resultIntent;
            String url = json.getString("url");
            if(url.isEmpty()){
                resultIntent = new Intent(context, Menua.class);
            }else{
                Log.i(TAG,url);
                resultIntent =new Intent("webnavigation");
                Bundle bundle =new Bundle();
                bundle.putString("weblink", url);
                resultIntent.putExtras(bundle);
            }
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.rsslogo)
                            .setContentTitle(json.getString("alert"))
                            .setContentText(json.getString("tex"))
                            .setContentIntent(resultPendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
}