package com.gorka.rssjarioa;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;

public class Elkarteak extends Activity {

    DbEgokitua db = new DbEgokitua(this);
    public ArrayList<List_Sarrera> arr_data = new ArrayList<List_Sarrera>();
    int ekintza_id= -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_elkarteak);

        Bundle bundle=getIntent().getExtras();
        if (bundle != null) {
            ekintza_id = bundle.getInt("ekintza_id");
        }

        db.zabaldu();
        if(ekintza_id<0){
            try{
                int logo = R.drawable.rsslogo;
                Cursor cursor = db.autorLortuDanak();
                do {
                    arr_data.add(new List_Sarrera(logo, cursor.getString(0), cursor.getString(1), cursor.getString(2)));

                } while(cursor.moveToNext());

            }catch (Exception ex){
                Log.e("arr_data-datubasetik-Elkarteak", ex.toString());
            }
        }else {
            try{
                int logo = R.drawable.rsslogo;
                Cursor c = db.autorLortuId(ekintza_id);
                do{
                    Cursor cautor = db.autorLortu(Integer.parseInt(c.getString(0)));
                    do {
                        arr_data.add(new List_Sarrera(logo, cautor.getString(0), cautor.getString(1), cautor.getString(2)));

                    } while(cautor.moveToNext());
                }while (c.moveToNext());



            }catch (Exception ex){
                Log.e("arr_data-datubasetik-Elkarteak",ex.toString());
            }
        }



        db.zarratu();

        ListView lv = (ListView) findViewById(R.id.elkarteak_listview);
        lv.setAdapter(new List_adaptador(this, R.layout.layout_elkarteak, arr_data){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView logo = (ImageView) view.findViewById(R.id.layout_elkarteak_logo);
                    if (logo != null){
                        logo.setImageResource(((List_Sarrera) entrada).get_idImagen());
                    }
                    TextView nor = (TextView) view.findViewById(R.id.layout_elkarteak_nor);
                    if (nor != null) {
                        nor.setText(((List_Sarrera) entrada).get_nor());
                    }
                    TextView email = (TextView) view.findViewById(R.id.layout_elkarteak_email);
                    if (email != null) {
                        email.setText(((List_Sarrera) entrada).get_email());
                    }
                    TextView web = (TextView) view.findViewById(R.id.layout_elkarteak_web);
                    if (web != null) {
                        web.setText(((List_Sarrera) entrada).get_web());
                    }

                }
            }
        });

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