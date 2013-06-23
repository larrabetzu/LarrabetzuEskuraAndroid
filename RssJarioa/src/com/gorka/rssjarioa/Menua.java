package com.gorka.rssjarioa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Menua extends Activity {

	DbEgokitua db=new DbEgokitua(this);
	boolean networkAvailable=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.menua);
            networkAvailable=networkAvailable();
            if(networkAvailable) {
                // Badago Interneta
                    Log.d("INTERNET", "Badago");
                    hilo1.run();
            }else{
                // Ez dago internetik
                    Toast.makeText(Menua.this,"EZ zaude intenetari konektatuta",Toast.LENGTH_LONG).show();
                    Log.d("INTERNET", "EZ dago");
                }

    }

    public void onclickbtnberriak(View view){
            if (networkAvailable) {
                startActivity(new Intent("berriak"));
            }else{
                Toast.makeText(Menua.this,"EZ zaude intenetari konektatuta",Toast.LENGTH_LONG).show();
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

            Log.i("gaurko data", "" + mYear + "-" + String.format("%02d",mMonth) + "-" + mDay +" "+mhour);
            db.zabaldu();
            try{
                db.garbitu(mYear, String.format("%02d",mMonth), mDay, mhour);
                }catch (Exception e){
                Log.e("garbitu",e.toString());
            }
            try{
                db.eguneratuEkintzak();
                }catch (Exception e){
                Log.e("eguneratu",e.toString());
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
            Context context = getApplicationContext();
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectMgr != null) {
                NetworkInfo[] netInfo = connectMgr.getAllNetworkInfo();
                if (netInfo != null) {
                    for (NetworkInfo net : netInfo) {
                        if (net.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
            else {
                Log.d("INTERNET", "EZ dago internetik");
            }
            return false;
    }
}
