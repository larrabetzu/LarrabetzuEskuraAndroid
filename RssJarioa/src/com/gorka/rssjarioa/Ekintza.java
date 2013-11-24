package com.gorka.rssjarioa;

import android.app.Activity;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import java.io.InputStream;
import java.net.URL;

public class Ekintza extends Activity {

    DbEgokitua db = new DbEgokitua(this);
    String url = null;
    String link = null;
    ImageView kartela = null;
    TextView ekintza_link = null;
    Bitmap kartelabitmap = null;
    private ProgressDialog progressDialog;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ekintza);

        db.zabaldu();
        Bundle bundle=getIntent().getExtras();
        int id= 0;
        if (bundle != null) {
            id = bundle.getInt("posicion");
        }
        Log.i("posicion",id+"");
        Cursor cursor=db.ekitaldiaLortuDana(id);
        db.zarratu();

        kartela = (ImageView)findViewById(R.id.ekintza_kartela);
        TextView ekintza_hilea = (TextView)findViewById(R.id.ekintza_hilea);
        TextView ekintza_egune = (TextView)findViewById(R.id.ekintza_egune);
        TextView ekintza_tituloa = (TextView)findViewById(R.id.ekintza_tituloa);
        TextView ekintza_ordue = (TextView)findViewById(R.id.ekintza_ordue);
        TextView ekintza_lekue = (TextView)findViewById(R.id.ekintza_lekue);
        TextView ekintza_deskribapena = (TextView)findViewById(R.id.ekintza_deskribapena);
        ekintza_link = (TextView)findViewById(R.id.ekintza_link);

        do{
            ekintza_hilea.setText(hilea(Integer.parseInt(cursor.getString(1).substring(5,7))));
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
                    Log.e("Ekintza",e.toString());
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

    public String hilea (int hilea){
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