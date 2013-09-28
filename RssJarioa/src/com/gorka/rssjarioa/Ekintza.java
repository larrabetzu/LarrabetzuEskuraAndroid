package com.gorka.rssjarioa;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Ekintza extends Activity {

    DbEgokitua db=new DbEgokitua(this);
    String url=null;
    ImageView kartela=null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ekintza);

        db.zabaldu();
        Bundle bundle=getIntent().getExtras();
        int id=bundle.getInt("posicion");
        Log.i("posicion",id+"");
        Cursor cursor=db.ekitaldiaLortuDana(id);

        kartela=(ImageView)findViewById(R.id.ekintza_kartela);
        TextView ekintza_egune= (TextView)findViewById(R.id.ekintza_egune);
        TextView ekintza_tituloa= (TextView)findViewById(R.id.ekintza_tituloa);
        TextView ekintza_ordue= (TextView)findViewById(R.id.ekintza_ordue);
        TextView ekintza_lekue= (TextView)findViewById(R.id.ekintza_lekue);
        TextView ekintza_deskribapena= (TextView)findViewById(R.id.ekintza_deskribapena);

        do{

            ekintza_tituloa.setText(cursor.getString(0));
            ekintza_egune.setText(cursor.getString(1).substring(8, 10));
            ekintza_ordue.setText(cursor.getString(1).substring(10, 16));
            ekintza_lekue.setText(cursor.getString(2));
            ekintza_deskribapena.setText(cursor.getString(3));
            url=cursor.getString(5);
        } while(cursor.moveToNext());

        if(url!=null ){
           kartela.setImageBitmap(downloadBitmap(url,100,500));

        }
        db.zarratu();
    }
    private Bitmap downloadBitmap(String url, int width, int height) {
        try {
            //bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            return BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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