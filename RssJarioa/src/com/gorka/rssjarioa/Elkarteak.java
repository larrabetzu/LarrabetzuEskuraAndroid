package com.gorka.rssjarioa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.google.analytics.tracking.android.Tracker;

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
                Cursor cursor = db.autorLortuDanak();
                do {
                    int logo = R.drawable.rsslogo;
                    String izena = cursor.getString(0).replace(" ","");
                    if (izena.equalsIgnoreCase("aretxabala")) {
                        logo = R.drawable.aretxabala;
                    }else if (izena.equalsIgnoreCase("arrekikili")) {
                        logo = R.drawable.arrekikili;
                    }else if (izena.equalsIgnoreCase("garmitxa")) {
                        logo = R.drawable.garmitxa;
                    }else if (izena.equalsIgnoreCase("gaztelumendiabesbatza")) {
                        logo = R.drawable.gaztelumendiabesbatza;
                    }else if (izena.equalsIgnoreCase("gureetxea")) {
                        logo = R.drawable.guretxea;
                    }else if (izena.equalsIgnoreCase("gurpide")) {
                        logo = R.drawable.gurpide;
                    }else if (izena.equalsIgnoreCase("intxurretajaibatzordea")) {
                        logo = R.drawable.intxurreta;
                    }else if (izena.equalsIgnoreCase("itallenbertsoeskola")) {
                        logo = R.drawable.itallen;
                    }else if (izena.equalsIgnoreCase("larrabetzukoeskola")) {
                        logo = R.drawable.eskola;
                    }else if (izena.equalsIgnoreCase("larrabetzukoudala")) {
                        logo = R.drawable.udala;
                    }else if (izena.equalsIgnoreCase("literaturia")) {
                        logo = R.drawable.literaturia;
                    }else if (izena.equalsIgnoreCase("horibai")) {
                        logo = R.drawable.horibai;
                    }else if (izena.equalsIgnoreCase("kukubel")) {
                        logo = R.drawable.kukubel;
                    }else if (izena.equalsIgnoreCase("tantaztanta")) {
                        logo = R.drawable.tantaztanta;
                    }
                    arr_data.add(new List_Sarrera(logo, cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                } while(cursor.moveToNext());
            }catch (Exception e){
                Log.e("arr_data-datubasetik-Elkarteak", e.toString());
                Tracker myTracker = EasyTracker.getInstance(Elkarteak.this);
                myTracker.send(MapBuilder.createException(new StandardExceptionParser(Elkarteak.this, null)
                        .getDescription(Thread.currentThread().getName(), e), false).build());
            }
        }else {
            try{
                Cursor c = db.autorLortuId(ekintza_id);
                do{
                    Cursor cautor = db.autorLortu(Integer.parseInt(c.getString(0)));
                    do {
                        int logo = R.drawable.rsslogo;
                        String izena = cautor.getString(0).replace(" ","");
                        if (izena.equalsIgnoreCase("aretxabala")) {
                            logo = R.drawable.aretxabala;
                        }else if (izena.equalsIgnoreCase("arrekikili")) {
                            logo = R.drawable.arrekikili;
                        }else if (izena.equalsIgnoreCase("garmitxa")) {
                            logo = R.drawable.garmitxa;
                        }else if (izena.equalsIgnoreCase("gaztelumendiabesbatza")) {
                            logo = R.drawable.gaztelumendiabesbatza;
                        }else if (izena.equalsIgnoreCase("gureetxea")) {
                            logo = R.drawable.guretxea;
                        }else if (izena.equalsIgnoreCase("gurpide")) {
                            logo = R.drawable.gurpide;
                        }else if (izena.equalsIgnoreCase("intxurretajaibatzordea")) {
                            logo = R.drawable.intxurreta;
                        }else if (izena.equalsIgnoreCase("itallenbertsoeskola")) {
                            logo = R.drawable.itallen;
                        }else if (izena.equalsIgnoreCase("larrabetzukoeskola")) {
                            logo = R.drawable.eskola;
                        }else if (izena.equalsIgnoreCase("larrabetzukoudala")) {
                            logo = R.drawable.udala;
                        }else if (izena.equalsIgnoreCase("literaturia")) {
                            logo = R.drawable.literaturia;
                        }else if (izena.equalsIgnoreCase("horibai")) {
                            logo = R.drawable.horibai;
                        }else if (izena.equalsIgnoreCase("kukubel")) {
                            logo = R.drawable.kukubel;
                        }else if (izena.equalsIgnoreCase("tantaztanta")) {
                            logo = R.drawable.tantaztanta;
                        }
                        arr_data.add(new List_Sarrera(logo, cautor.getString(0), cautor.getString(1), cautor.getString(2)));
                    } while(cautor.moveToNext());
                }while (c.moveToNext());
            }catch (Exception e){
                Log.e("arr_data-datubasetik-Elkarteak",e.toString());
                Tracker myTracker = EasyTracker.getInstance(Elkarteak.this);
                myTracker.send(MapBuilder.createException(new StandardExceptionParser(Elkarteak.this, null)
                        .getDescription(Thread.currentThread().getName(), e), false).build());
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
                }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogWebEmail(position);
            }
        });
    }

    private void dialogWebEmail(final int position){
        final CharSequence[] items = {"Email", "web"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aukeratu nahi dozuna");
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        Log.i("email", arr_data.get(position).get_email());
                        String[] to = { arr_data.get(position).get_email()};
                        Intent itSend = new Intent(Intent.ACTION_SEND);
                        itSend.putExtra(Intent.EXTRA_EMAIL,to);
                        itSend.setType("message/rfc822");
                        try {
                            startActivity(Intent.createChooser(itSend, "Aukeratu e-posta bezeroa"));
                        }catch (android.content.ActivityNotFoundException ex){
                            Toast.makeText(Elkarteak.this, "Posta bezeroa ez dago instalatuta.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        String link = arr_data.get(position).get_web();
                        Intent intent=new Intent("webnavigation");
                        Bundle bundle =new Bundle();
                        bundle.putString("weblink", link);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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