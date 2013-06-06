package com.gorka.rssjarioa;

import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class Agenda extends Activity {

    DbEgokitua db=new DbEgokitua(this);
    private ListView lista;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {        
            super.onCreate(savedInstanceState);
            setContentView(R.layout.agenda);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH)+1; //urtarrila=0
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                ArrayList<List_Sarrera> datos = new ArrayList<List_Sarrera>();
                db.zabaldu();
                Cursor cursor = db.ekitaldiakid(mYear,mMonth+2,mDay); //bi hilabete erakuzteko
                int id ;
                if (cursor.moveToFirst()) {
                    do {
                        id = cursor.getInt(0);
                        Log.d("id ",id+"");
                        Cursor cursor1=db.ekitaldiaLortu(id);
                        int logo = R.drawable.ic_launcher;
                        String autorea = db.autoreaLortu(id);
                        if(autorea.equalsIgnoreCase("hori")){
                           logo = R.drawable.horibai;
                        }
                        if(autorea.equalsIgnoreCase("herria")){
                            logo = R.drawable.iptx;
                        }
                        do{
                            datos.add(new List_Sarrera(logo,cursor1.getString(0), cursor1.getString(1),cursor1.getString(2),cursor1.getString(3)));
                        } while(cursor1.moveToNext());


                    } while(cursor.moveToNext());
                }

                //crear el listView
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

                            TextView eguna = (TextView) view.findViewById(R.id.data);
                            if (eguna != null)
                                eguna.setText(((List_Sarrera) entrada).get_egune());

                            ImageView imagen = (ImageView) view.findViewById(R.id.imagen);
                            if (imagen != null)
                                imagen.setImageResource(((List_Sarrera) entrada).get_idImagen());
                        }
                    }
                });
                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
                        List_Sarrera elegido = (List_Sarrera) pariente.getItemAtPosition(posicion);

                        CharSequence texto = "Deskribapena: " + elegido.get_deskribapena();
                        Toast toast = Toast.makeText(Agenda.this, texto, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });


            //mirar si cambia algo y actualizar las notificaciones en la base de datos


            db.zarratu();
            }
    }




}
