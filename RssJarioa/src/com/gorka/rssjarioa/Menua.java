package com.gorka.rssjarioa;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.Calendar;

public class Menua extends Activity {

	DbEgokitua db=new DbEgokitua(this);
	boolean networkAvailable=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.menua);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);

            networkAvailable=networkAvailable();
            if(networkAvailable) {
                // Badago Interneta
                    Log.d("INTERNET", "Badago");
                    hilo1.run();
            }else{
                // Ez dago internetik
                    networkNoAvailableDialog();
                    Log.d("INTERNET", "EZ dago");
                }

    }

    public void onclickbtnberriak(View view){
            if (networkAvailable) {
                startActivity(new Intent("berriak"));
            }else{
                Toast.makeText(Menua.this,"EZ zaude internetari konektatuta",Toast.LENGTH_LONG).show();
            }
    }
	    
    public void onclickbtnagenda(View view){
            startActivity(new Intent("agenda"));
    }

    public void onclickbtnkontaktua(View view){
            startActivity(new Intent("kontaktua"));
    }

    public void onclickbtnhobespenak(View view){
            startActivity(new Intent("hobespenak"));
    }
	    
    Thread hilo1 = new Thread(new Runnable(){

        @Override
        public void run() {
            Log.e("hilo1", "on");
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH)+1;   //urtarrila=0
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            int mhour = c.get(Calendar.HOUR_OF_DAY);

            Log.i("gaurko data", "" + mYear + "-" + String.format("%02d",mMonth) + "-" + String.format("%02d",mDay)  +" "+mhour);
            db.zabaldu();
            try{
                db.eguneratuEkintzak();
                }catch (Exception e){
                Log.e("eguneratu",e.toString());
            }
            try{
                db.garbitu(mYear, String.format("%02d",mMonth),String.format("%02d",mDay), mhour);
                }catch (Exception e){
                Log.e("garbitu",e.toString());
            }
            try {
                int id= db.azkenId();
                Log.d("azkenID",id+"");
                }catch (Exception e){
                Log.e("azkenID",e.toString());
            }
            db.zarratu();
            Log.e("hilo1", "off");
        }

    });

    public boolean networkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }else{
            Log.d("INTERNET", "EZ dago internetik");
        }
        return false;
    }

    /**
     * public static String getConnectivityStatusString(Context context) {
     int conn = NetworkUtil.getConnectivityStatus(context);
     String status = null;
     if (conn == NetworkUtil.TYPE_WIFI) {
     status = "Wifi enabled";
     } else if (conn == NetworkUtil.TYPE_MOBILE) {
     status = "Mobile data enabled";
     } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
     status = "Not connected to Internet";
     }
     return status;
     }
     */

    public void networkNoAvailableDialog(){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setIcon(R.drawable.warning);
        alertbox.setTitle("EZ zaude internetari konektatuta");
        alertbox.setPositiveButton("Bale", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {}
        });
        alertbox.show();
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
