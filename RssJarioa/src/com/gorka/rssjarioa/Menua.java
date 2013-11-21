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
    boolean hariaEginda = false;

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
                    haria();
            }else{
                // Ez dago internetik
                    networkNoAvailableDialog();
                    Log.d("INTERNET", "EZ dago");
                }

    }

    public void onclickbtnberriak(@SuppressWarnings("UnusedParameters")View view){
            if (networkAvailable) {
                startActivity(new Intent("berriak"));
            }else{
                Toast.makeText(Menua.this,"EZ zaude internetari konektatuta",Toast.LENGTH_LONG).show();
            }
    }
	    
    public void onclickbtnagenda(@SuppressWarnings("UnusedParameters")View view){
        if(hariaEginda || !networkAvailable){
            startActivity(new Intent("agenda"));
        }else {
            Toast.makeText(this,"zerbitzariarekin konektatzen",Toast.LENGTH_LONG).show();
        }
    }

    public void onclickbtnkontaktua(@SuppressWarnings("UnusedParameters")View view){
            startActivity(new Intent("kontaktua"));
    }

    public void onclickbtnhobespenak(@SuppressWarnings("UnusedParameters")View view){
            startActivity(new Intent("hobespenak"));
    }

    private void haria(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                Log.e("hilo1", "on");
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH)+1;   //urtarrila=0
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                int mhour = c.get(Calendar.HOUR_OF_DAY);
                int numdbekitaldi = 0;
                int numwebekitaldi = 0;

                Log.i("gaurko data", "" + mYear + "-" + String.format("%02d",mMonth) + "-" + String.format("%02d",mDay)  +" "+String.format("%02d",mhour));
                db.zabaldu();
                try{
                    numwebekitaldi = db.eguneratuEkintzak();
                    Log.i("numwebekitaldi",numwebekitaldi+"");
                    }catch (Exception e){
                    Log.e("eguneratu",e.toString());
                }
                try{
                    db.garbitu(mYear, String.format("%02d",mMonth),String.format("%02d",mDay), String.format("%02d",mhour));
                    }catch (Exception e){
                    Log.e("garbitu",e.toString());
                }
                try{
                    numdbekitaldi = db.ekitaldikzenbat();
                    Log.i("numdbekitaldi",numdbekitaldi+"");
                }catch (Exception e){
                    Log.e("ekitaldikzenbat",e.toString());
                }
                if(numwebekitaldi<numdbekitaldi && numwebekitaldi!=0){
                    try {
                        Log.e("berAktualizatu","");
                        db.ekitaldiguztiakkendu();
                        db.eguneratuEkintzak();
                        db.garbitu(mYear, String.format("%02d",mMonth),String.format("%02d",mDay), String.format("%02d",mhour));
                    }catch (Exception e){
                        Log.e("ekitaldiguztiakkendu",e.toString());
                    }
                }
                try {
                    int id= db.azkenId();
                    Log.d("azkenID",id+"");
                    }catch (Exception e){
                    Log.e("azkenID",e.toString());
                }
                db.zarratu();
                Log.e("hilo1", "off");
                hariaEginda = true;
            }

        }).start();
    }

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
