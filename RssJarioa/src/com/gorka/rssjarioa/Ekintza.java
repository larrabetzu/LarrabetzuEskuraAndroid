package com.gorka.rssjarioa;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.google.analytics.tracking.android.Tracker;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class Ekintza extends Activity {

    DbEgokitua db = new DbEgokitua(this);
    String url = null;
    String link = null;
    ImageView kartela = null;
    TextView ekintza_hilea = null;
    TextView ekintza_egune = null;
    TextView ekintza_tituloa = null;
    TextView ekintza_ordue = null;
    TextView ekintza_lekue = null;
    TextView ekintza_deskribapena = null;
    TextView ekintza_link = null;
    Bitmap kartelabitmap = null;
    int id= -1;
    int hilea = 0;
    private ProgressDialog progressDialog;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ekintza);

        db.zabaldu();
        Bundle bundle=getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("posicion");
        }
        Log.i("posicion",id+"");
        Cursor cursor=db.ekitaldiaLortuDana(id);
        db.zarratu();

        kartela = (ImageView)findViewById(R.id.ekintza_kartela);
        ekintza_hilea = (TextView)findViewById(R.id.ekintza_hilea);
        ekintza_egune = (TextView)findViewById(R.id.ekintza_egune);
        ekintza_tituloa = (TextView)findViewById(R.id.ekintza_tituloa);
        ekintza_ordue = (TextView)findViewById(R.id.ekintza_ordue);
        ekintza_lekue = (TextView)findViewById(R.id.ekintza_lekue);
        ekintza_deskribapena = (TextView)findViewById(R.id.ekintza_deskribapena);
        ekintza_link = (TextView)findViewById(R.id.ekintza_link);

        do{
            hilea = Integer.parseInt(cursor.getString(1).substring(5,7));
            ekintza_hilea.setText(hilea());
            ekintza_tituloa.setText(cursor.getString(0));
            ekintza_egune.setText(cursor.getString(1).substring(8, 10));
            ekintza_ordue.setText(cursor.getString(1).substring(10, 16));
            ekintza_lekue.setText(cursor.getString(2));
            ekintza_deskribapena.setText(cursor.getString(3));
            link = cursor.getString(4);
            url = cursor.getString(5);
        } while(cursor.moveToNext());

        if (link != null){
            Log.i("link", " "+link);
            ekintza_link.setText(link);
            ekintza_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserAction = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserAction);
                }
            });
        }
        if(url != null ){
            if(networkAvailable()){
                try {
                    Log.i("Kartela-url",url);
                    dowsnloadbitmap();
                } catch (Exception e) {
                    Log.e("kartela",e.toString());
                }
            }else {
                Drawable myDrawable = getResources().getDrawable(R.drawable.warning);
                Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
                kartela.setImageBitmap(anImage);
                Toast.makeText(Ekintza.this, "EZ zaude internetari konektatuta. Kartela ezin da deskargatu.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        EasyTracker tracker = EasyTracker.getInstance(this);
        tracker.send(MapBuilder.createEvent("Ekintzak","menua","menua" ,(long) id).build());
        getMenuInflater().inflate(R.menu.menu_ekintzak, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_elkarbanatu:
                elkarbanatu();
                return true;
            case R.id.menu_alarma:
                startalert();
                return true;
            case R.id.menu_antolatzaileak:
                if(id>0){
                    Intent intent = new Intent("elkarteak");
                    Bundle bundle = new Bundle();
                    bundle.putInt("ekintza_id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dowsnloadbitmap(){
        progressDialog = ProgressDialog.show(Ekintza.this, "", "Mesedez itxaron kartela kargatu arte...", true);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                    final int maxSize = 600;
                    int outWidth;
                    int outHeight;
                    int inWidth = bitmap.getWidth();
                    int inHeight = bitmap.getHeight();
                    if(inWidth > inHeight){
                        outWidth = maxSize;
                        outHeight = (inHeight * maxSize) / inWidth;
                    } else {
                        outHeight = maxSize;
                        outWidth = (inWidth * maxSize) / inHeight;
                    }
                    kartelabitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
                }catch (Exception e){
                    Log.e("Ekintza-dowsnloadbitmap",e.toString());
                    Tracker myTracker = EasyTracker.getInstance(Ekintza.this);
                    myTracker.send(MapBuilder.createException(new StandardExceptionParser(Ekintza.this, null)
                            .getDescription(Thread.currentThread().getName(), e), false).build());
                    Drawable myDrawable = getResources().getDrawable(R.drawable.warning);
                    kartelabitmap = ((BitmapDrawable) myDrawable).getBitmap();
                }
                Message msg = progressHandler.obtainMessage();
                progressHandler.sendMessage(msg);
            }
        }).start();
    }
    private final Handler progressHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            kartela.setImageBitmap(kartelabitmap);
            progressDialog.dismiss();
        }
    };

    public String hilea (){
        String hileanizena = " ";
        switch (hilea){
            case 1: hileanizena="Urtarrilak"; break;
            case 2: hileanizena="Otsailak"; break;
            case 3: hileanizena="Martxoak"; break;
            case 4: hileanizena="Apirilak"; break;
            case 5: hileanizena="Maiatzak"; break;
            case 6: hileanizena="Ekainak"; break;
            case 7: hileanizena="Uztailak"; break;
            case 8: hileanizena="Abuztuak"; break;
            case 9: hileanizena="Irailak"; break;
            case 10: hileanizena="Urriak"; break;
            case 11: hileanizena="Azaroak"; break;
            case 12: hileanizena="Abenduak"; break;
        }
        return hileanizena;
    }

    public boolean networkAvailable() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void elkarbanatu(){
        String tituloa = ekintza_tituloa.getText().toString();
        if (tituloa.length()>76){
            tituloa = tituloa.substring(0,73)+"...";
        }
        String testua = tituloa+"\n"+ekintza_hilea.getText()+ekintza_egune.getText()+", "+ekintza_ordue.getText()+"-etan @larrabetzu #eskura";
        final String title = "Aukeratu aplikazioa Ekintza elkarbanatzeko";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, testua );
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, title));
    }

    private void startalert(){

        EasyTracker tracker = EasyTracker.getInstance(this);
        tracker.send(MapBuilder.createEvent("Ekintzak","alarma","alarma" ,(long) id).build());
        db.zabaldu();
        boolean aktibatuta = db.ekitaldiaAlarmaLortu(id);

        if(aktibatuta){
            Toast.makeText(this,"Ekitaldiaren alarma aktibatuta daukazu", Toast.LENGTH_SHORT).show();
        }else{
            long denboradiferentzia = 0;
            try{
                final String ekitaldiegune = "2014-"+hilea+"-"+ekintza_egune.getText()+" "+ekintza_ordue.getText();
                Log.i("ekitaldiegune", ekitaldiegune);
                DateFormat formatoa = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                java.util.Date noz = formatoa.parse(ekitaldiegune , new ParsePosition(0));
                denboradiferentzia = noz.getTime()-System.currentTimeMillis();
                Log.i("denboradiferentzia", denboradiferentzia + "");
            }catch (Exception e){
                Log.e("ekitaldieguna", e.toString());
                Tracker myTracker = EasyTracker.getInstance(this);
                myTracker.send(MapBuilder.createException(new StandardExceptionParser(this, null)
                        .getDescription(Thread.currentThread().getName(),e), false).build());
            }
            Intent intent=new Intent(this, Alarma.class);
            PendingIntent pendingIntent= PendingIntent.getBroadcast(this.getApplicationContext(),20000, intent, 0);
            AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    denboradiferentzia,
                    pendingIntent);
            Toast.makeText(this,"Ekitaldiaren alarma jarrita",Toast.LENGTH_SHORT).show();
            db.ekitaldiaAlarmaAktualizatu(id);
        }
        db.zarratu();
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