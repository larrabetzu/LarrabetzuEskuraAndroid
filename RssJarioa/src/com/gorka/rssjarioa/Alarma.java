package com.gorka.rssjarioa;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class Alarma extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){

        try {
            Toast.makeText(context,"Alarma",Toast.LENGTH_LONG).show();
            Vibrator vibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] vibra= {1000,1000,1000,1000,1000,1000};
            vibrator.vibrate(vibra,5);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            Log.e("Alarma",e.toString());
        }
    }
}
