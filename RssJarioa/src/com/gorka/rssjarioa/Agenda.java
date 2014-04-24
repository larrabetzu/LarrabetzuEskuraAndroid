package com.gorka.rssjarioa;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;
import java.util.Calendar;

public class Agenda extends Activity {

    DbEgokitua db=new DbEgokitua(this);
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {        
            super.onCreate(savedInstanceState);
            setContentView(R.layout.agenda);
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH)+1; //urtarrila=0
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            ArrayList<List_Sarrera> datos = new ArrayList<List_Sarrera>();
            db.zabaldu();
            if(mMonth == 12){
                mYear = mYear + 1;
                mMonth = 0;
            }
            Cursor cursor = db.ekitaldiakid(mYear,mMonth+1,mDay); // hilabete bat erakuzteko
            int id ;
            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getInt(0);
                    Log.d("id ",id+"");
                    Cursor cursor1=db.ekitaldiaLortu(id);
                    do{
                        datos.add(new List_Sarrera(cursor1.getString(0),cursor1.getString(1).substring(8,10) , cursor1.getString(1).substring(10, 16),cursor1.getString(2),id));
                    } while(cursor1.moveToNext());


                } while(cursor.moveToNext());
            }
            db.zarratu();
            //listView sortu
            final ListView lista = (ListView) findViewById(R.id.agenda_listview);
            lista.setAdapter(new List_adaptador(this, R.layout.layout_ekintzak, datos){
                @Override
                public void onEntrada(Object entrada, View view) {
                    if (entrada != null) {
                        TextView tituloa = (TextView) view.findViewById(R.id.ekintzak_tituloa_agenda);
                        if (tituloa != null)
                            tituloa.setText(((List_Sarrera) entrada).get_tituloa());

                        TextView lekua = (TextView) view.findViewById(R.id.ekintzak_lekua);
                        if (lekua != null)
                            lekua.setText(((List_Sarrera) entrada).get_lekua());

                        TextView ordue = (TextView) view.findViewById(R.id.ekintzak_ordue);
                        if (ordue != null)
                            ordue.setText(((List_Sarrera) entrada).get_ordue());

                        TextView egune= (TextView) view.findViewById(R.id.ekintzak_egune);
                        if (egune != null)
                            egune.setText(((List_Sarrera) entrada).get_egune());
                    }
                }
            });

            lista.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
                    List_Sarrera hautatua = (List_Sarrera) pariente.getItemAtPosition(posicion);
                    Intent intent=new Intent("ekintza");
                    Bundle bundle =new Bundle();
                    if (hautatua != null) {
                        bundle.putInt("posicion", hautatua.get_id());
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> pariente, View arg1, int pos, long id) {
                    List_Sarrera hautatua = (List_Sarrera) pariente.getItemAtPosition(pos);
                    String testua = "";
                    if (hautatua != null) {
                        testua = hautatua.get_tituloa()+"\n"+hautatua.get_egune()+" "+hautatua.get_ordue()+" "+hautatua.get_lekua()+ " @larrabetzu #eskura";
                        if (testua.length()>120){
                            testua = hautatua.get_tituloa()+"\n"+hautatua.get_egune()+" "+hautatua.get_ordue();
                            try{
                                testua = testua.substring(0,120);
                            }catch (Exception e){
                                Log.e("testua-Agenda",e.toString());
                            }
                        }
                    }
                    String title = "Aukeratu aplikazioa Ekintza elkarbanatzeko";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, testua );
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, title));
                    return true;
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
