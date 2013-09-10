package com.gorka.rssjarioa;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;
import java.util.Calendar;

public class Agenda extends Activity {

    DbEgokitua db=new DbEgokitua(this);
    private ListView lista;
  
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
            Cursor cursor = db.ekitaldiakid(mYear,mMonth+1,mDay); //bi hilabete erakuzteko
            int id ;
            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getInt(0);
                    Log.d("id ",id+"");
                    Cursor cursor1=db.ekitaldiaLortu(id);
                    do{
                        datos.add(new List_Sarrera(cursor1.getString(0),cursor1.getString(1).substring(8,10) , cursor1.getString(1).substring(10,16),cursor1.getString(2),cursor1.getString(3)));
                    } while(cursor1.moveToNext());


                } while(cursor.moveToNext());
            }

            //listView sortu
            lista = (ListView) findViewById(R.id.listviewAgenda);
            lista.setAdapter(new List_adaptador(this, R.layout.layout_ekintzak, datos){
                @Override
                public void onEntrada(Object entrada, View view) {
                    if (entrada != null) {
                        TextView tituloa = (TextView) view.findViewById(R.id.tituloa_ekintzak);
                        if (tituloa != null)
                            tituloa.setText(((List_Sarrera) entrada).get_tituloa());

                        TextView lekua = (TextView) view.findViewById(R.id.lekua);
                        if (lekua != null)
                            lekua.setText(((List_Sarrera) entrada).get_lekua());

                        TextView ordue = (TextView) view.findViewById(R.id.ordue);
                        if (ordue != null)
                            ordue.setText(((List_Sarrera) entrada).get_ordue());

                        TextView egune= (TextView) view.findViewById(R.id.egune);
                        if (egune != null)
                            egune.setText(((List_Sarrera) entrada).get_egune());
                    }
                }
            });
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
                    List_Sarrera elegido = (List_Sarrera) pariente.getItemAtPosition(posicion);
                    CharSequence texto;
                    if (elegido.get_deskribapena()==null){
                        texto = "Deskribapena: ez dago erabilgarri";
                    }else{
                        texto = "Deskribapena: " + elegido.get_deskribapena();
                    }
                    Toast toast = Toast.makeText(Agenda.this, texto, Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            //mirar si cambia algo y actualizar las notificaciones en la base de datos

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
