package com.gorka.rssjarioa;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Alarma extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){

        Intent resultIntent = new Intent(context, Menua.class);
        try {

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
                            .setContentTitle("Ekitaldiaren Alarma")
                            .setContentText("15 min barru zuk nahi zenuen ekitaldia egongo da.")
                            .setContentIntent(resultPendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());

        } catch (Exception e) {
            Log.e("Alarma",e.toString());
        }
    }
}
